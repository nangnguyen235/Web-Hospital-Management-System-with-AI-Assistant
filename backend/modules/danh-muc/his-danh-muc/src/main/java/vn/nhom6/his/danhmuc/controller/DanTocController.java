package vn.nhom6.his.danhmuc.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom6.his.danhmuc.dto.DanTocDto;
import vn.nhom6.his.danhmuc.services.DanTocService;
import vn.nhom6.his.shared.kernel.CongKhai;

@RestController
@RequestMapping("/api/danh-muc/dan-toc")
@RequiredArgsConstructor
public class DanTocController {

    private final DanTocService danTocService;

    @GetMapping
    @CongKhai
    public List<DanTocDto> danhSach() {
        return danTocService.danhSach();
    }
}
