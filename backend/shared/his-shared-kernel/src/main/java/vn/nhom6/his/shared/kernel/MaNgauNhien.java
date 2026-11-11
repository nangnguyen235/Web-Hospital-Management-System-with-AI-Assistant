package vn.nhom6.his.shared.kernel;

import java.util.Locale;
import java.util.UUID;

/** Mã hiển thị sinh ngẫu nhiên từ UUID, chữ hoa (mo-hinh-du-lieu.md mục 6). Trùng thì sinh lại ở service. */
public final class MaNgauNhien {

    private MaNgauNhien() {
    }

    public static String hex(int doDai) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, doDai).toUpperCase(Locale.ROOT);
    }
}
