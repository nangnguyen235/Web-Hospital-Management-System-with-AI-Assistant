package vn.nhom6.his.danhmuc.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.nhom6.his.danhmuc.dto.KhoaDto;
import vn.nhom6.his.danhmuc.dto.LuuKhoaRequest;
import vn.nhom6.his.danhmuc.entity.Khoa;
import vn.nhom6.his.danhmuc.repository.KhoaRepository;
import vn.nhom6.his.shared.kernel.LoaiLoi;
import vn.nhom6.his.shared.kernel.LoiNghiepVuException;

/** Lát mẫu Khoa (FR-AD-04, BR-04). */
@ExtendWith(MockitoExtension.class)
class KhoaServiceTest {

    @Mock
    KhoaRepository repository;

    @InjectMocks
    KhoaService service;

    @Test
    void taoKhoaHopLeThiLuuVaTraDto() {
        when(repository.existsByTenKhoaAndDaXoaFalse("Tim mạch")).thenReturn(false);
        when(repository.save(any(Khoa.class))).thenAnswer(i -> {
            Khoa k = i.getArgument(0);
            k.setId(1);
            return k;
        });

        KhoaDto dto = service.tao(new LuuKhoaRequest("  Tim mạch ", "Khoa tim", null));

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.tenKhoa()).isEqualTo("Tim mạch");
    }

    @Test
    void taoTrungTenKhoaChuaXoaThiBaoXungDot() {
        when(repository.existsByTenKhoaAndDaXoaFalse("Tim mạch")).thenReturn(true);

        assertThatThrownBy(() -> service.tao(new LuuKhoaRequest("Tim mạch", null, null)))
                .isInstanceOfSatisfying(LoiNghiepVuException.class,
                        e -> assertThat(e.getLoi().loai()).isEqualTo(LoaiLoi.XUNG_DOT));
        verify(repository, never()).save(any());
    }

    @Test
    void capNhatKhoaKhongTonTaiThiBaoKhongTimThay() {
        when(repository.findById(9)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.capNhat(9, new LuuKhoaRequest("X", null, null)))
                .isInstanceOfSatisfying(LoiNghiepVuException.class,
                        e -> assertThat(e.getLoi().loai()).isEqualTo(LoaiLoi.KHONG_TIM_THAY));
    }

    @Test
    void xoaMemVaKhoiPhuc() {
        Khoa khoa = new Khoa("Nhi", null, null);
        when(repository.findById(2)).thenReturn(Optional.of(khoa));

        service.xoa(2);
        assertThat(khoa.isDaXoa()).isTrue();

        service.khoiPhuc(2);
        assertThat(khoa.isDaXoa()).isFalse();
    }

    @Test
    void timKiemChuanHoaPhanTrang() {
        when(repository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var kq = service.timKiem(null, 0, 500);

        assertThat(kq.page()).isEqualTo(1);
        assertThat(kq.pageSize()).isEqualTo(100);
        assertThat(kq.items()).isEmpty();
    }
}
