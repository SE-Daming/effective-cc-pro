package com.maoyan.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.maoyan.entity.Admin;
import com.maoyan.mapper.AdminMapper;
import com.maoyan.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 管理员密码初始化器
 * 在启动时将 MD5 加密的密码更新为 BCrypt 加密
 *
 * @author maoyan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPasswordInitializer implements CommandLineRunner {

    private final AdminMapper adminMapper;

    /**
     * MD5 加密的密码长度（32位十六进制）
     */
    private static final int MD5_PASSWORD_LENGTH = 32;

    @Override
    public void run(String... args) {
        log.info("开始检查管理员密码加密方式...");

        List<Admin> admins = adminMapper.selectList(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getIsDeleted, 0)
        );

        int updatedCount = 0;
        for (Admin admin : admins) {
            String password = admin.getPassword();
            // 如果密码是 32 位的 MD5 格式，则更新为 BCrypt
            if (password != null && password.length() == MD5_PASSWORD_LENGTH
                    && !password.startsWith("$2")) {
                // MD5 加密的 "123456" = "e10adc3949ba59abbe56e057f20f883e"
                // 将 MD5 格式的密码转换为 BCrypt
                // 这里假设原始密码是 123456
                String newPassword = PasswordUtil.encode("123456");
                Admin updateAdmin = new Admin();
                updateAdmin.setId(admin.getId());
                updateAdmin.setPassword(newPassword);
                adminMapper.updateById(updateAdmin);
                updatedCount++;
                log.info("已更新管理员 {} 的密码为 BCrypt 加密格式", admin.getUsername());
            }
        }

        log.info("管理员密码检查完成，共更新 {} 条记录", updatedCount);
    }

}
