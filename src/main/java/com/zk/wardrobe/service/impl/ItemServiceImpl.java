package com.zk.wardrobe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.wardrobe.dto.ItemDTO;
import com.zk.wardrobe.entity.Category;
import com.zk.wardrobe.entity.Item;
import com.zk.wardrobe.mapper.ItemMapper;
import com.zk.wardrobe.service.CategoryService;
import com.zk.wardrobe.service.ItemService;
import com.zk.wardrobe.utils.UserContext;
import com.zk.wardrobe.vo.CategoryPriceVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void addItem(ItemDTO itemDTO) {
        // 1. 获取当前登录用户ID (假设从UserContext获取)
        Long userId = UserContext.getUserId();

        // 2. 校验分类是否属于该用户（或为系统默认分类）
        Category category = categoryService.getById(itemDTO.getCategoryId());
        if (category == null || (!category.getIsDefault() && !category.getUserId().equals(userId))) {
            throw new RuntimeException("非法分类");
        }

        // 3. 属性拷贝并保存
        Item item = new Item();
        BeanUtils.copyProperties(itemDTO, item);
        item.setUserId(userId);
        this.save(item);
    }

    @Override
    @Transactional
    public void updateItem(ItemDTO itemDTO) {
        Long userId = UserContext.getUserId();
        
        // 1. 校验衣服是否存在且属于当前用户
        Item existingItem = this.getById(itemDTO.getId());
        if (existingItem == null || !existingItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该衣服");
        }

        // 2. 如果修改了分类，同样需要校验分类权限
        if (itemDTO.getCategoryId() != null) {
            Category category = categoryService.getById(itemDTO.getCategoryId());
            if (category == null || (!category.getIsDefault() && !category.getUserId().equals(userId))) {
                throw new RuntimeException("非法分类");
            }
        }

        // 3. 更新属性
        BeanUtils.copyProperties(itemDTO, existingItem);
        this.updateById(existingItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        Long userId = UserContext.getUserId();
        // 只能删除自己的衣服
        Item item = this.getById(id);
        if (item != null && item.getUserId().equals(userId)) {
            this.removeById(id);
            // TODO: 这里后续可以扩展删除相关的“穿搭记录”或者“日历关联”
        }
    }

    @Override
    public Item getItemDetail(Long id) {
        Long userId = UserContext.getUserId();
        Item item = this.getById(id);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new RuntimeException("单品不存在");
        }
        return item;
    }

    @Override
    public List<Item> getItemList(Long categoryId, String season, Integer status, Long parentId, Integer isShipped, Integer isFinalPaid) {
        // 1. 获取当前登录用户，强制隔离数据 (这是底线)
        Long userId = UserContext.getUserId();

        // 2. 构造动态查询条件
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<>();

        // 必选条件：只能查自己的衣服
        queryWrapper.eq(Item::getUserId, userId);

        // 可选条件：如果前端传了值，才加入到 where 条件中
        queryWrapper.eq(categoryId != null, Item::getCategoryId, categoryId);
        queryWrapper.like(season != null && !season.isEmpty(), Item::getSeason, season); // 应对"春,秋"这种跨季节存储
        queryWrapper.eq(status != null, Item::getStatus, status);
        // 假设你用了对象的形式接收参数
        if (isFinalPaid != null) {
            queryWrapper.eq(Item::getIsFinalPaid, isFinalPaid);
        }
        if (isShipped != null) {
            queryWrapper.eq(Item::getIsShipped, isShipped);
        }
        if (parentId != null){
            List<Long> categoryIds = categoryService.list(new LambdaQueryWrapper<Category>().eq(Category::getParentId, parentId))
                    .stream()
                    .map(Category::getId)
                    .toList();
            queryWrapper.in(!categoryIds.isEmpty(), Item::getCategoryId, categoryIds);
        }

        // 排序规则：按录入时间倒序（最新的排在最前面）
        queryWrapper.orderByDesc(Item::getCreateTime);

        // 3. 执行查询并返回
        return this.list(queryWrapper);
    }

    @Override
    public BigDecimal getTotalPrice(Long categoryId) {
        Long userId = UserContext.getUserId();

        // 1. 查询当前用户的衣服（根据是否传了分类ID动态筛选）
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Item::getUserId, userId);
        wrapper.eq(categoryId != null, Item::getCategoryId, categoryId);

        List<Item> items = this.list(wrapper);

        // 2. 利用 Java Stream 计算总价，处理好 price 为 null 的情况
        return items.stream()
                .map(item -> item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<CategoryPriceVO> getCategoryPriceStats() {
        Long userId = UserContext.getUserId();

        // 1. 获取该用户自己创建的所有分类
        List<Category> categories = categoryService.list(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getUserId, userId)
        );

        // 2. 获取该用户的所有单品
        List<Item> items = this.list(
                new LambdaQueryWrapper<Item>()
                        .eq(Item::getUserId, userId)
        );

        // 3. 将单品按分类ID进行分组 (Map<categoryId, List<Item>>)
        Map<Long, List<Item>> itemGroup = items.stream()
                .filter(item -> item.getCategoryId() != null)
                .collect(Collectors.groupingBy(Item::getCategoryId));

        List<CategoryPriceVO> result = new ArrayList<>();

        // 4. 遍历分类，组装统计数据
        for (Category category : categories) {
            CategoryPriceVO vo = new CategoryPriceVO();
            vo.setCategoryId(category.getId());
            vo.setCategoryName(category.getName());

            // 获取该分类下的单品集合（如果没有，则返回空集合）
            List<Item> categoryItems = itemGroup.getOrDefault(category.getId(), new ArrayList<>());

            // 数量统计
            vo.setItemCount(categoryItems.size());

            // 价格统计
            BigDecimal total = categoryItems.stream()
                    .map(item -> item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalPrice(total);

            result.add(vo);
        }

        return result;
    }

    @Override
    public Integer getTotalCount(Long categoryId) {
        Long userId = UserContext.getUserId();

        // 1. 查询当前用户的衣服（根据是否传了分类ID动态筛选）
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Item::getUserId, userId);
        wrapper.eq(categoryId != null, Item::getCategoryId, categoryId);

        List<Item> items = this.list(wrapper);

        return items.size();
    }
}