namespace HIS.SharedKernel.Results;

public enum LoaiLoi
{
    NghiepVu,      // 400
    KhongTimThay,  // 404
    XungDot,       // 409
    KhongCoQuyen,  // 403
}

/// <summary>Lỗi nghiệp vụ trả về cho client (ProblemDetails: title = Ma, detail = MoTa).</summary>
public sealed record Error(string Ma, string MoTa, LoaiLoi Loai = LoaiLoi.NghiepVu)
{
    public static Error KhongTimThay(string doiTuong) => new("KHONG_TIM_THAY", $"Không tìm thấy {doiTuong}.", LoaiLoi.KhongTimThay);
}

/// <summary>Kết quả use case: thành công hoặc lỗi nghiệp vụ (không dùng exception cho lỗi nghiệp vụ).</summary>
public class Result
{
    protected Result(Error? error) => Error = error;

    public Error? Error { get; }
    public bool IsSuccess => Error is null;

    public static Result Success() => new(null);
    public static Result Failure(Error error) => new(error);
    public static implicit operator Result(Error error) => Failure(error);
}

public sealed class Result<T> : Result
{
    private readonly T? _value;

    private Result(T value) : base(null) => _value = value;
    private Result(Error error) : base(error) { }

    public T Value => IsSuccess ? _value! : throw new InvalidOperationException("Kết quả lỗi không có giá trị.");

    public static implicit operator Result<T>(T value) => new(value);
    public static implicit operator Result<T>(Error error) => new(error);
}

/// <summary>Trang dữ liệu (NFR-01: danh sách luôn phân trang, mặc định 20).</summary>
public sealed record PagedResult<T>(IReadOnlyList<T> Items, int TotalCount, int Page, int PageSize);
