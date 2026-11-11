package vn.nhom6.his;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

/**
 * HIS — TheHos Hospital. Modular Monolith: mỗi package con trực tiếp của {@code vn.nhom6.his} là một module
 * (nguoidung, danhmuc, lichkham, hosobenhan, vienphi, trolyai, thongke); {@code shared} và {@code contracts}
 * là module dùng chung, mở (modular-monolith.md mục 1, 2).
 */
@SpringBootApplication
@Modulithic(systemName = "HIS - TheHos", sharedModules = {"shared", "contracts"})
public class HisApplication {

    public static void main(String[] args) {
        SpringApplication.run(HisApplication.class, args);
    }
}
