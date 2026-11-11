package vn.nhom6.his.shared.kernel;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Đồng hồ dùng cho callback JPA (không tiêm bean được). Service vẫn tiêm bean {@link Clock}
 * (modular-monolith.md mục 4); bean đó gọi {@link #dat(Clock)} khi khởi động nên hai nơi cùng một đồng hồ.
 */
public final class ThoiGian {

    public static final ZoneId MUI_GIO = ZoneId.of("Asia/Ho_Chi_Minh");

    private static volatile Clock dongHo = Clock.system(MUI_GIO);

    private ThoiGian() {
    }

    public static void dat(Clock clock) {
        dongHo = clock;
    }

    public static LocalDateTime bayGio() {
        return LocalDateTime.now(dongHo);
    }

    public static LocalDate homNay() {
        return LocalDate.now(dongHo);
    }
}
