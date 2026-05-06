package com.zk.wardrobe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.wardrobe.dto.CategoryDTO;
import com.zk.wardrobe.dto.CategorySortDTO;
import com.zk.wardrobe.entity.Category;
import com.zk.wardrobe.mapper.CategoryMapper;
import com.zk.wardrobe.service.CategoryService;
import com.zk.wardrobe.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    @Transactional
    public void addCategory(CategoryDTO categoryDTO) {
        Long userId = UserContext.getUserId();
        log.info("用户 {} 新增分类：{}", userId, categoryDTO);
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUserId(userId);

        // 如果前端不传 parentId，或者传了 0，说明这是一级分类（娃娃）
        if (category.getParentId() == null || category.getParentId() == 0L) {
            category.setLevel(1);     // 强制设置为 1 级
            category.setParentId(0L); // 数据库里用 0 表示没有父节点，防止存入 null
        } else {
            // 如果传了具体的 parentId，说明是给某个娃娃添加部位，那就是二级分类
            category.setLevel(2);     // 强制设置为 2 级
        }

        // 2. 自动计算排序 sort (获取当前同级最大的 sort + 1)
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, category.getParentId())
                .eq(Category::getUserId, userId)
                .orderByDesc(Category::getSort)
                .last("limit 1");

        Category maxSortCategory = this.getOne(queryWrapper);
        if (maxSortCategory != null && maxSortCategory.getSort() != null) {
            category.setSort(maxSortCategory.getSort() + 1);
        } else {
            category.setSort(1);
        }

        // 3. 执行插入
        this.save(category);
    }

    @Override
    @Transactional
    public void updateCategory(CategoryDTO categoryDTO) {
        Long userId = UserContext.getUserId();
        Category category = this.getById(categoryDTO.getId());

        // 极其纯粹的权限校验：必须存在且归属当前用户
        if (category == null || !category.getUserId().equals(userId)) {
            throw new RuntimeException("分类不存在或无权修改");
        }

        // 可以加一个同名校验（如果改的名字和自己已有的其他分类重名了）
        long count = this.count(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, userId)
                .eq(Category::getName, categoryDTO.getName())
                .ne(Category::getId, categoryDTO.getId())); // 排除自己
        if (count > 0) {
            throw new RuntimeException("分类名称已存在");
        }

        category.setName(categoryDTO.getName());
        this.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Long userId = UserContext.getUserId();
        Category category = this.getById(id);

        if (category == null) return;
        
        // 权限校验
        if (!category.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此分类");
        }

        // 进阶校验：这里强烈建议后续保留对 Item 的查询，如果分类里还有衣服，直接删分类会导致衣服变成“孤儿数据”
        // 伪代码：
        // long itemCount = itemService.count(new LambdaQueryWrapper<Item>().eq(Item::getCategoryId, id));
        // if (itemCount > 0) throw new RuntimeException("请先清空或转移该分类下的衣服");

        this.removeById(id);
    }

    @Override
    public List<Category> getCategoryList(Integer level, Long parentId) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        // 1. 用户隔离：查自己的，或者系统预设的(0)
        // ⚠️ 注意：这里必须用 and 把它包起来，否则后面的条件会被 or 冲垮！
        wrapper.and(w -> w.eq(Category::getUserId, userId).or().eq(Category::getUserId, 0L));

        // 2. 动态追加查询条件
        if (level != null) {
            wrapper.eq(Category::getLevel, level); // 比如：只查 level=1 的娃娃
        }
        if (parentId != null) {
            wrapper.eq(Category::getParentId, parentId); // 比如：只查属于某个娃娃的部位
        }

        // 按照 sort 升序排列
        wrapper.orderByAsc(Category::getSort);
        // 其次按照创建时间排序
        wrapper.orderByDesc(Category::getCreateTime);


        return this.list(wrapper);
    }

    @Override
    public Category getCategoryDetail(Long id) {
        Long userId = UserContext.getUserId();
        Category category = this.getById(id);

        // 只能看自己的详情
        if (category == null || !category.getUserId().equals(userId)) {
            throw new RuntimeException("分类不存在或无权访问");
        }
        return category;
    }

    @Override
    public String updateSort(List<CategorySortDTO> sortList) {
        // 组装成 Category 实体列表
        List<Category> updateList = sortList.stream().map(dto -> {
            Category c = new Category();
            c.setId(dto.getId());
            c.setSort(dto.getSort());
            return c;
        }).collect(Collectors.toList());

        // MyBatis-Plus 批量更新 (只会更新传了值的 id 和 sort 字段，不会覆盖别的)
        this.updateBatchById(updateList);
        return "更新成功";
    }
}