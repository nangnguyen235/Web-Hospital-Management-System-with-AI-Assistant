package vn.nhom6.his.danhmuc.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.danhmuc.dto.DanTocDto;
import vn.nhom6.his.danhmuc.repository.DanTocRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DanTocService {

    private final DanTocRepository danTocRepository;

    public List<DanTocDto> danhSach() {
        return danTocRepository.findAll(Sort.by("id")).stream()
                .map(d -> new DanTocDto(d.getId(), d.getTenDanToc()))
                .toList();
    }
}
