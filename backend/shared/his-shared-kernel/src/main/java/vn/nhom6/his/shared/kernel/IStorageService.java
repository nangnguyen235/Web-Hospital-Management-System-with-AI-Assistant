package vn.nhom6.his.shared.kernel;

import java.io.InputStream;
import java.time.Duration;

/** Lưu tệp trên Cloudflare R2 (cloudflare-r2.md). CSDL chỉ lưu object key. */
public interface IStorageService {

    Duration HAN_MAC_DINH = Duration.ofMinutes(15); // NFR-04

    void upload(InputStream noiDung, long kichThuoc, String objectKey, String contentType);

    String presignedUrl(String objectKey, Duration han);

    default String presignedUrl(String objectKey) {
        return presignedUrl(objectKey, HAN_MAC_DINH);
    }

    void delete(String objectKey);
}
