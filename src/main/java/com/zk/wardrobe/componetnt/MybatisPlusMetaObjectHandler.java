package com.zk.wardrobe.componetnt;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        long now = System.currentTimeMillis();
        // 自动填充 createTime 和 updateTime
        this.strictInsertFill(metaObject, "createTime", Long.class, now);
        this.strictInsertFill(metaObject, "updateTime", Long.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        long now = System.currentTimeMillis();
        // 更新时只填充 updateTime
        this.strictUpdateFill(metaObject, "updateTime", Long.class, now);
    }
}