using Amazon;
using Amazon.Runtime;
using Amazon.S3;
using Amazon.S3.Model;
using HIS.SharedKernel.Abstractions;
using Microsoft.Extensions.Options;

namespace HIS.SharedKernel.Infrastructure.Storage;

public sealed class R2Options
{
    public string ServiceURL { get; set; } = "";
    public string AccessKey { get; set; } = "";
    public string SecretKey { get; set; } = "";
    public string Bucket { get; set; } = "";
}

/// <summary>
/// Cloudflare R2 qua chuẩn S3 (cloudflare-r2.md mục 4): truyền khóa tường minh, region "auto",
/// tắt payload signing và checksum mặc định mà R2 chưa hỗ trợ.
/// </summary>
public sealed class S3StorageWrapper : IStorageService, IDisposable
{
    private static readonly TimeSpan HanMacDinh = TimeSpan.FromMinutes(15); // NFR-04
    private readonly R2Options _options;
    private readonly Lazy<AmazonS3Client> _client;

    public S3StorageWrapper(IOptions<R2Options> options)
    {
        _options = options.Value;
        _client = new Lazy<AmazonS3Client>(TaoClient);
    }

    private AmazonS3Client TaoClient()
    {
        if (string.IsNullOrWhiteSpace(_options.ServiceURL) || string.IsNullOrWhiteSpace(_options.Bucket))
        {
            throw new InvalidOperationException("Chưa cấu hình R2 (R2__ServiceURL, R2__Bucket, R2__AccessKey, R2__SecretKey).");
        }

        var config = new AmazonS3Config
        {
            ServiceURL = _options.ServiceURL,
            AuthenticationRegion = "auto",
            ForcePathStyle = true,
            RequestChecksumCalculation = RequestChecksumCalculation.WHEN_REQUIRED,
            ResponseChecksumValidation = ResponseChecksumValidation.WHEN_REQUIRED,
        };
        return new AmazonS3Client(new BasicAWSCredentials(_options.AccessKey, _options.SecretKey), config);
    }

    public async Task UploadAsync(Stream content, string objectKey, string contentType, CancellationToken cancellationToken = default)
    {
        var request = new PutObjectRequest
        {
            BucketName = _options.Bucket,
            Key = objectKey,
            InputStream = content,
            ContentType = contentType,
            DisablePayloadSigning = true,
        };
        await _client.Value.PutObjectAsync(request, cancellationToken);
    }

    public Task<string> GetPresignedUrlAsync(string objectKey, TimeSpan? expiresIn = null) =>
        _client.Value.GetPreSignedURLAsync(new GetPreSignedUrlRequest
        {
            BucketName = _options.Bucket,
            Key = objectKey,
            Verb = HttpVerb.GET,
            Expires = DateTime.UtcNow.Add(expiresIn ?? HanMacDinh),
        });

    public async Task DeleteAsync(string objectKey, CancellationToken cancellationToken = default) =>
        await _client.Value.DeleteObjectAsync(_options.Bucket, objectKey, cancellationToken);

    public void Dispose()
    {
        if (_client.IsValueCreated)
        {
            _client.Value.Dispose();
        }
    }
}
