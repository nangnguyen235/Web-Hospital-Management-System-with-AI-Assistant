package vn.nhom6.his.shared.kernel;

/** Ném từ service khi vi phạm quy tắc nghiệp vụ; GlobalExceptionHandler đổi thành ProblemDetail. */
public class LoiNghiepVuException extends RuntimeException {

    private final transient Loi loi;

    public LoiNghiepVuException(Loi loi) {
        super(loi.ma() + ": " + loi.moTa());
        this.loi = loi;
    }

    public Loi getLoi() {
        return loi;
    }
}
