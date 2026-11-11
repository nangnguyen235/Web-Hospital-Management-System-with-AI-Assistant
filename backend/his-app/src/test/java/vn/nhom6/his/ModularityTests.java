package vn.nhom6.his;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Kiểm ranh giới module bằng Spring Modulith (modular-monolith.md mục 2.2): không vòng phụ thuộc, không chạm package
 * nội bộ của module khác. Sinh sơ đồ module (PlantUML) vào target/spring-modulith-docs cho báo cáo.
 */
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(HisApplication.class);

    @Test
    void ranhGioiModuleHopLe() {
        modules.verify();
    }

    @Test
    void sinhSoDoModule() {
        new Documenter(modules).writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
    }
}
