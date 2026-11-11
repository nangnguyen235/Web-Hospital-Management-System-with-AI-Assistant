package vn.nhom6.his.danhmuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** {@code dm_dan_toc} (Nation) — seed 54 dân tộc trong Flyway, chỉ đọc. */
@Entity
@Table(name = "dm_dan_toc")
@Getter
@NoArgsConstructor
public class DanToc {

    @Id
    private Integer id;

    @Column(nullable = false, length = 50)
    private String tenDanToc;
}
