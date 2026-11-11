package vn.nhom6.his.shared.infra;

import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.nhom6.his.shared.kernel.ThoiGian;

/** Bean dùng chung: đồng hồ giờ Việt Nam (NFR-06) — service tiêm Clock, không gọi now() không tham số. */
@Configuration
@EnableConfigurationProperties({JwtProperties.class, R2Properties.class})
public class InfraConfig {

    @Bean
    Clock clock() {
        Clock clock = Clock.system(ThoiGian.MUI_GIO);
        ThoiGian.dat(clock);
        return clock;
    }
}
