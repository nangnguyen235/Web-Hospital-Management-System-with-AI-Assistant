package vn.nhom6.his.shared.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.LoiNghiepVuException;

/**
 * Xử lý ngoại lệ tập trung (tiêu chí Exemplary 2): mọi lỗi thành ProblemDetail với {@code title} = mã lỗi,
 * {@code detail} = câu tiếng Việt, lỗi validation có thêm {@code errors: {field: [..]}} — đúng định dạng frontend đọc.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(LoiNghiepVuException.class)
    ProblemDetail loiNghiepVu(LoiNghiepVuException e) {
        Loi loi = e.getLoi();
        HttpStatus status = switch (loi.loai()) {
            case KHONG_TIM_THAY -> HttpStatus.NOT_FOUND;
            case XUNG_DOT -> HttpStatus.CONFLICT;
            case KHONG_CO_QUYEN -> HttpStatus.FORBIDDEN;
            case NGHIEP_VU -> HttpStatus.BAD_REQUEST;
        };
        return problem(status, loi.ma(), loi.moTa());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException e) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(fe.getField(), k -> new ArrayList<>()).add(fe.getDefaultMessage());
        }
        e.getBindingResult().getGlobalErrors()
                .forEach(ge -> errors.computeIfAbsent(ge.getObjectName(), k -> new ArrayList<>()).add(ge.getDefaultMessage()));
        return loiDuLieu(errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ProblemDetail validationThamSo(HandlerMethodValidationException e) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        e.getParameterValidationResults().forEach(r -> errors
                .computeIfAbsent(r.getMethodParameter().getParameterName(), k -> new ArrayList<>())
                .addAll(r.getResolvableErrors().stream().map(x -> x.getDefaultMessage()).toList()));
        return loiDuLieu(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail validationRangBuoc(ConstraintViolationException e) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        e.getConstraintViolations().forEach(v -> errors
                .computeIfAbsent(v.getPropertyPath().toString(), k -> new ArrayList<>()).add(v.getMessage()));
        return loiDuLieu(errors);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail xungDotPhienBan(ObjectOptimisticLockingFailureException e) {
        LOG.warn("Xung đột phiên bản: {}", e.getMessage());
        return problem(HttpStatus.CONFLICT, "DU_LIEU_DA_THAY_DOI", "Dữ liệu vừa bị người khác thay đổi, vui lòng thử lại.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail quaKichThuoc(MaxUploadSizeExceededException e) {
        return problem(HttpStatus.BAD_REQUEST, "QUA_KICH_THUOC", "Tệp tải lên vượt quá kích thước cho phép.");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class, MissingServletRequestPartException.class})
    ProblemDetail yeuCauSai(Exception e) {
        return problem(HttpStatus.BAD_REQUEST, "YEU_CAU_KHONG_HOP_LE", "Yêu cầu không hợp lệ.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ProblemDetail khongCoDuongDan(NoResourceFoundException e) {
        return problem(HttpStatus.NOT_FOUND, "KHONG_TIM_THAY", "Không tìm thấy đường dẫn.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ProblemDetail saiPhuongThuc(HttpRequestMethodNotSupportedException e) {
        return problem(HttpStatus.METHOD_NOT_ALLOWED, "SAI_PHUONG_THUC", "Phương thức HTTP không được hỗ trợ.");
    }

    /** Để Spring Security trả 401 (chưa đăng nhập) hoặc 403 (sai vai trò) — ném lại cho ExceptionTranslationFilter. */
    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    void baoMat(RuntimeException e) {
        throw e;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail loiHeThong(Exception e, HttpServletRequest request) {
        LOG.error("Lỗi chưa xử lý tại {} {}", request.getMethod(), request.getRequestURI(), e);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "LOI_HE_THONG", "Có lỗi xảy ra, vui lòng thử lại sau.");
    }

    private static ProblemDetail loiDuLieu(Map<String, List<String>> errors) {
        String dauTien = errors.values().stream().flatMap(List::stream).findFirst().orElse("Dữ liệu không hợp lệ.");
        ProblemDetail p = problem(HttpStatus.BAD_REQUEST, "DU_LIEU_KHONG_HOP_LE", dauTien);
        p.setProperty("errors", errors);
        return p;
    }

    private static ProblemDetail problem(HttpStatus status, String ma, String moTa) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, moTa);
        p.setTitle(ma);
        return p;
    }
}
