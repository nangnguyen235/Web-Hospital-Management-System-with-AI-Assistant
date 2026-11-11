package vn.nhom6.his.shared.infra;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import vn.nhom6.his.shared.kernel.IStorageService;
import vn.nhom6.his.shared.kernel.Loi;

/**
 * Cloudflare R2 qua chuẩn S3 (cloudflare-r2.md mục 4): endpoint R2, region "auto", path-style,
 * tắt aws-chunked và checksum mặc định, khóa truyền tường minh (không lấy nhầm credential AWS của EC2).
 * Tạo client lười để backend vẫn khởi động khi chưa cấu hình R2.
 */
@Service
public class S3StorageWrapper implements IStorageService, DisposableBean {

    private final R2Properties properties;
    private S3Client client;
    private S3Presigner presigner;

    public S3StorageWrapper(R2Properties properties) {
        this.properties = properties;
    }

    private synchronized void khoiTao() {
        if (client != null) {
            return;
        }
        if (!properties.daCauHinh()) {
            throw new Loi("CHUA_CAU_HINH_R2", "Máy chủ chưa cấu hình lưu trữ tệp (HIS_R2_*).").ngoaiLe();
        }
        var credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()));
        var s3Config = S3Configuration.builder().pathStyleAccessEnabled(true).chunkedEncodingEnabled(false).build();
        URI endpoint = URI.create(properties.endpoint());
        presigner = S3Presigner.builder()
                .endpointOverride(endpoint).region(Region.of("auto")).credentialsProvider(credentials)
                .serviceConfiguration(s3Config).build();
        client = S3Client.builder()
                .endpointOverride(endpoint).region(Region.of("auto")).credentialsProvider(credentials)
                .serviceConfiguration(s3Config)
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();
    }

    @Override
    public void upload(InputStream noiDung, long kichThuoc, String objectKey, String contentType) {
        khoiTao();
        client.putObject(
                PutObjectRequest.builder().bucket(properties.bucket()).key(objectKey).contentType(contentType).build(),
                RequestBody.fromInputStream(noiDung, kichThuoc));
    }

    @Override
    public String presignedUrl(String objectKey, Duration han) {
        khoiTao();
        var request = GetObjectPresignRequest.builder()
                .signatureDuration(han)
                .getObjectRequest(GetObjectRequest.builder().bucket(properties.bucket()).key(objectKey).build())
                .build();
        return presigner.presignGetObject(request).url().toString();
    }

    @Override
    public void delete(String objectKey) {
        khoiTao();
        client.deleteObject(DeleteObjectRequest.builder().bucket(properties.bucket()).key(objectKey).build());
    }

    @Override
    public synchronized void destroy() {
        if (client != null) {
            client.close();
            presigner.close();
        }
    }
}
