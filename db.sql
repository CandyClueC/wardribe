-- 创建数据库
CREATE DATABASE IF NOT EXISTS `wardrobe` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `wardrobe`;

-- 1. 用户表
CREATE TABLE `user` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `openid` VARCHAR(64) NOT NULL COMMENT '微信唯一标识',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  `create_time` BIGINT NOT NULL COMMENT '创建时间戳(ms)',
  `update_time` BIGINT NOT NULL COMMENT '修改时间戳(ms)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 衣服分类表
CREATE TABLE `category` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID (0表示系统预设)',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否系统预设(0否 1是)',
  `create_time` BIGINT NOT NULL COMMENT '创建时间戳(ms)',
  `update_time` BIGINT NOT NULL COMMENT '修改时间戳(ms)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='衣服分类表';

-- 3. 衣服单品表
CREATE TABLE `item` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `category_id` BIGINT NOT NULL COMMENT '所属分类ID',
  `image_url` VARCHAR(255) NOT NULL COMMENT '衣服原图地址',
  `price` DECIMAL(10,2) DEFAULT '0.00' COMMENT '购买价格',
  `season` VARCHAR(20) DEFAULT NULL COMMENT '季节标签(如:春,夏,秋,冬)',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:在库, 1:待洗, 2:外借, 3:断舍离)',
  `create_time` BIGINT NOT NULL COMMENT '创建时间戳(ms)',
  `update_time` BIGINT NOT NULL COMMENT '修改时间戳(ms)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='衣服单品表';

-- 4. 穿搭组合表
CREATE TABLE `look` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '穿搭名称',
  `image_url` VARCHAR(255) NOT NULL COMMENT '合成后的穿搭预览图地址',
  `create_time` BIGINT NOT NULL COMMENT '创建时间戳(ms)',
  `update_time` BIGINT NOT NULL COMMENT '修改时间戳(ms)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='穿搭组合表';

-- 5. 穿搭-单品关联表
CREATE TABLE `look_item_rel` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `look_id` BIGINT NOT NULL COMMENT '穿搭ID',
  `item_id` BIGINT NOT NULL COMMENT '单品ID',
  `create_time` BIGINT NOT NULL COMMENT '创建时间戳(ms)',
  `update_time` BIGINT NOT NULL COMMENT '修改时间戳(ms)',
  PRIMARY KEY (`id`),
  KEY `idx_look_id` (`look_id`),
  KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='穿搭单品关联表';

-- 6. 穿搭日历表
CREATE TABLE `wearing_log` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `look_id` BIGINT DEFAULT NULL COMMENT '关联穿搭ID(可为空)',
  `wear_date` DATE NOT NULL COMMENT '穿着日期',
  `create_time` BIGINT NOT NULL COMMENT '创建时间戳(ms)',
  `update_time` BIGINT NOT NULL COMMENT '修改时间戳(ms)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id_date` (`user_id`, `wear_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='穿搭日历表';

-- 7. 日历-单品记录表
CREATE TABLE `wearing_log_item_rel` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `log_id` BIGINT NOT NULL COMMENT '关联日历记录ID',
  `item_id` BIGINT NOT NULL COMMENT '单品ID',
  `create_time` BIGINT NOT NULL COMMENT '创建时间戳(ms)',
  `update_time` BIGINT NOT NULL COMMENT '修改时间戳(ms)',
  PRIMARY KEY (`id`),
  KEY `idx_log_id` (`log_id`),
  KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日历单品记录表';

ALTER TABLE item
ADD COLUMN name VARCHAR(100) COMMENT '物品名称',
ADD COLUMN purchase_date BIGINT DEFAULT 0 COMMENT '购买日期';

ALTER TABLE category
ADD COLUMN parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0为一级大类';

ALTER TABLE category
ADD COLUMN level INT NOT NULL DEFAULT 1 COMMENT '分类层级: 1-一级分类, 2-二级分类',
ADD COLUMN image_url VARCHAR(255) DEFAULT NULL COMMENT '分类图片/图标',
ADD COLUMN sort INT NOT NULL DEFAULT 0 COMMENT '排序权重';

ALTER TABLE item
ADD COLUMN is_shipped TINYINT DEFAULT 0 COMMENT '发货状态: 0-未发货, 1-已发货',
ADD COLUMN is_final_paid TINYINT DEFAULT 0 COMMENT '尾款状态: 0-未补, 1-已补';

