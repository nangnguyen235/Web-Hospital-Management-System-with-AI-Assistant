package vn.nhom6.his.lichkham.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom6.his.lichkham.dto.SoDangGoiDto;
import vn.nhom6.his.lichkham.services.SoDangGoiService;
import vn.nhom6.his.shared.kernel.CongKhai;

@RestController
@RequestMapping("/api/lich-kham")
@RequiredArgsConstructor
public class SoDangGoiController {

    private final SoDangGoiService soDangGoiService;

    /** Màn hình số đang gọi công khai: chỉ số + phòng + khoa, không họ tên (BR-15, NFR-03). */
    @GetMapping("/{lichKhamId}/so-dang-goi")
    @CongKhai
    public SoDangGoiDto soDangGoi(@PathVariable long lichKhamId) {
        return soDangGoiService.lay(lichKhamId);
    }
}
