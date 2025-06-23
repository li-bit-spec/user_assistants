-- 修复数据库字符集，支持emoji表情符号
-- 使用utf8mb4字符集和utf8mb4_unicode_ci排序规则

-- 修改数据库字符集
ALTER DATABASE user_assistants CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 修改现有表的字符集
ALTER TABLE ass_manual_articles CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE ass_user_feedbacks CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE ass_feedback_images CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE ass_support_articles CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 如果表名不同，请使用以下命令查看当前表名
-- SHOW TABLES;

-- 验证字符集修改结果
SELECT 
    TABLE_NAME,
    TABLE_COLLATION 
FROM 
    information_schema.TABLES 
WHERE 
    TABLE_SCHEMA = 'user_assistants' 
    AND TABLE_TYPE = 'BASE TABLE';

-- 显示列的字符集信息
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CHARACTER_SET_NAME,
    COLLATION_NAME,
    DATA_TYPE,
    COLUMN_TYPE
FROM 
    information_schema.COLUMNS 
WHERE 
    TABLE_SCHEMA = 'user_assistants' 
    AND DATA_TYPE IN ('varchar', 'text', 'longtext', 'mediumtext', 'char')
ORDER BY 
    TABLE_NAME, 
    ORDINAL_POSITION; 