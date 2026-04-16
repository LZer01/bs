package com.example.house;

import com.example.house.utils.PasswordUtil; // ⚠️ 导入你写的工具类

public class Test {
    public static void main(String[] args) {
        System.out.println("========== BCrypt 密码测试开始 ==========");

        // 1. 模拟注册：生成密文
        String rawPassword = "123456";
        String encoded = PasswordUtil.encode(rawPassword);

        System.out.println("🔑 原始明文密码: " + rawPassword);
        System.out.println("🔒 生成的新密文: " + encoded);
        System.out.println("------------------------------------------");

        // 2. 模拟登录：验证密码

        // ⚠️【关键步骤】请把你数据库 user 表里查出来的 password 字段值，粘贴到这里
        String dbPasswordFromDatabase = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        System.out.println("🗄️ 数据库存的密文: " + dbPasswordFromDatabase);

        // 调用工具类进行比对
        boolean isMatch = PasswordUtil.matches(rawPassword, dbPasswordFromDatabase);

        System.out.println("✅ 比对结果 (true=成功, false=失败): " + isMatch);

        if (!isMatch) {
            System.out.println("❌ 警告：比对失败！请检查数据库里的密文是否对应明文 '123456'");
            System.out.println("💡 建议：复制上面‘生成的新密文’，去数据库执行 UPDATE 语句更新密码。");
        } else {
            System.out.println("🎉 成功！密码逻辑正常。");
        }

        System.out.println("==========================================");
    }
}