package com.zk.wardrobe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.wardrobe.dto.LookDTO;
import com.zk.wardrobe.entity.Category;
import com.zk.wardrobe.entity.Item;
import com.zk.wardrobe.entity.Look;
import com.zk.wardrobe.entity.LookItemRel;
import com.zk.wardrobe.mapper.LookMapper;
import com.zk.wardrobe.service.CategoryService;
import com.zk.wardrobe.service.ItemService;
import com.zk.wardrobe.service.LookItemRelService;
import com.zk.wardrobe.service.LookService;
import com.zk.wardrobe.utils.UserContext;
import com.zk.wardrobe.vo.LookDetailVO;
import com.zk.wardrobe.vo.LookListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LookServiceImpl extends ServiceImpl<LookMapper, Look> implements LookService {

    @Autowired
    private LookItemRelService lookItemRelService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addLook(LookDTO lookDTO) {
        Long userId = UserContext.getUserId();

        // 1. 校验安全性：检查传过来的单品ID是否都属于当前用户
        if (lookDTO.getItemIds() != null && !lookDTO.getItemIds().isEmpty()) {
            long count = itemService.count(new LambdaQueryWrapper<Item>()
                    .eq(Item::getUserId, userId)
                    .in(Item::getId, lookDTO.getItemIds()));
            
            if (count != lookDTO.getItemIds().size()) {
                throw new RuntimeException("包含非法或不存在的单品");
            }
        }

        // 2. 保存穿搭主体表 (look)
        Look look = new Look();
        look.setUserId(userId);
        look.setName(lookDTO.getName());
        look.setImageUrl(lookDTO.getImageUrl());
        this.save(look); // 保存后，MyBatis-Plus 会自动把生成的 ID 塞回 look.id 中

        // 3. 批量保存关联关系 (look_item_rel)
        if (lookDTO.getItemIds() != null && !lookDTO.getItemIds().isEmpty()) {
            List<LookItemRel> relList = lookDTO.getItemIds().stream().map(itemId -> {
                LookItemRel rel = new LookItemRel();
                rel.setLookId(look.getId()); // 拿到刚才生成的穿搭ID
                rel.setItemId(itemId);
                return rel;
            }).collect(Collectors.toList());

            lookItemRelService.saveBatch(relList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLook(LookDTO lookDTO) {
        Long userId = UserContext.getUserId();

        // 1. 权限校验
        Look existingLook = this.getById(lookDTO.getId());
        if (existingLook == null || !existingLook.getUserId().equals(userId)) {
            throw new RuntimeException("穿搭不存在或无权修改");
        }

        // 2. 同样的安全性校验：单品是否属于该用户
        if (lookDTO.getItemIds() != null && !lookDTO.getItemIds().isEmpty()) {
            long count = itemService.count(new LambdaQueryWrapper<Item>()
                    .eq(Item::getUserId, userId)
                    .in(Item::getId, lookDTO.getItemIds()));
            if (count != lookDTO.getItemIds().size()) {
                throw new RuntimeException("包含非法或不存在的单品");
            }
        }

        // 3. 更新穿搭主体信息
        existingLook.setName(lookDTO.getName());
        existingLook.setImageUrl(lookDTO.getImageUrl());
        this.updateById(existingLook);

        // 4. 更新关联关系：最简单的做法是先删后插 (Delete & Insert)
        lookItemRelService.remove(new LambdaQueryWrapper<LookItemRel>().eq(LookItemRel::getLookId, lookDTO.getId()));

        if (lookDTO.getItemIds() != null && !lookDTO.getItemIds().isEmpty()) {
            List<LookItemRel> relList = lookDTO.getItemIds().stream().map(itemId -> {
                LookItemRel rel = new LookItemRel();
                rel.setLookId(existingLook.getId());
                rel.setItemId(itemId);
                return rel;
            }).collect(Collectors.toList());
            lookItemRelService.saveBatch(relList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLook(Long id) {
        Long userId = UserContext.getUserId();
        
        Look look = this.getById(id);
        if (look == null) return;
        if (!look.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此穿搭");
        }

        // 1. 删除穿搭主体
        this.removeById(id);

        // 2. 删除关联关系表数据
        lookItemRelService.remove(new LambdaQueryWrapper<LookItemRel>().eq(LookItemRel::getLookId, id));
        
        // TODO: 如果后期接入了日历表(wearing_log)，可能还需要处理日历里的旧数据
    }

    @Override
    public List<LookListVO> getLookList() {
        Long userId = UserContext.getUserId();

        // 1. 查询该用户所有的穿搭记录
        List<Look> looks = this.list(new LambdaQueryWrapper<Look>()
                .eq(Look::getUserId, userId)
                .orderByDesc(Look::getCreateTime));

        if (looks.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 遍历组装 VO，并提取 Tags
        List<LookListVO> voList = new ArrayList<>();
        for (Look look : looks) {
            LookListVO vo = new LookListVO();
            BeanUtils.copyProperties(look, vo);

            // 查询该穿搭关联了哪些单品
            List<LookItemRel> rels = lookItemRelService.list(
                    new LambdaQueryWrapper<LookItemRel>().eq(LookItemRel::getLookId, look.getId())
            );

            List<String> tags = new ArrayList<>();
            if (!rels.isEmpty()) {
                // 拿到所有单品 ID
                List<Long> itemIds = rels.stream().map(LookItemRel::getItemId).collect(Collectors.toList());
                // 查出这些单品实体
                List<Item> items = itemService.listByIds(itemIds);

                // 提取单品所属的 categoryId (去重)
                List<Long> categoryIds = items.stream()
                        .map(Item::getCategoryId)
                        .distinct()
                        .collect(Collectors.toList());

                // 根据 categoryId 查出分类名称（比如：连衣裙、草帽）
                if (!categoryIds.isEmpty()) {
                    List<Category> categories = categoryService.listByIds(categoryIds);
                    tags = categories.stream().map(Category::getName).collect(Collectors.toList());
                }
            }
            vo.setTags(tags);
            voList.add(vo);
        }

        return voList;
    }

    @Override
    public LookDetailVO getLookDetail(Long id) {
        Long userId = UserContext.getUserId();
        Look look = this.getById(id);

        if (look == null || !look.getUserId().equals(userId)) {
            throw new RuntimeException("穿搭不存在或无权访问");
        }

        LookDetailVO vo = new LookDetailVO();
        BeanUtils.copyProperties(look, vo);

        // 查询该穿搭关联了哪些单品
        List<LookItemRel> rels = lookItemRelService.list(
                new LambdaQueryWrapper<LookItemRel>().eq(LookItemRel::getLookId, id)
        );

        if (!rels.isEmpty()) {
            // 提取出所有的 itemId
            List<Long> itemIds = rels.stream().map(LookItemRel::getItemId).collect(Collectors.toList());
            // 用 itemIds 批量查询衣服详情
            List<Item> items = itemService.listByIds(itemIds);
            vo.setItemList(items);
        } else {
            vo.setItemList(new ArrayList<>());
        }

        return vo;
    }
}