package vn.nhom6.his;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom6.his.shared.kernel.CongKhai;

/** ArchUnit — 5 luật tầng và tham chiếu chéo module (modular-monolith.md mục 2.2) + luật phân quyền endpoint. */
class KienTrucTests {

    static final List<String> MODULE = List.of("nguoidung", "danhmuc", "lichkham", "hosobenhan", "vienphi", "trolyai");

    private static JavaClasses lop;

    @BeforeAll
    static void nap() {
        // Gồm cả jar: khi chạy cả reactor, lớp của module con nằm trong jar của chúng.
        lop = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("vn.nhom6.his");
    }

    static List<String> module() {
        return MODULE;
    }

    /** Luật 1: Domain (entity, constant) không dùng tầng khác, Spring, AWS SDK, mail. */
    @Test
    void domainKhongPhuThuocHaTang() {
        noClasses().that().resideInAnyPackage("vn.nhom6.his.*.entity..", "vn.nhom6.his.*.constant..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "vn.nhom6.his.*.services..", "vn.nhom6.his.*.repository..", "vn.nhom6.his.*.controller..",
                        "vn.nhom6.his.*.config..", "org.springframework..", "software.amazon..", "jakarta.mail..")
                .allowEmptyShould(true)
                .check(lop);
    }

    /** Luật 2: Application (services, dto) không dùng controller, không dùng shared.infra/web (chỉ qua interface kernel). */
    @Test
    void applicationKhongDungPresentationVaHaTangChung() {
        noClasses().that().resideInAnyPackage("vn.nhom6.his.*.services..", "vn.nhom6.his.*.dto..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "vn.nhom6.his.*.controller..", "vn.nhom6.his.shared.infra..", "vn.nhom6.his.shared.web..")
                .check(lop);
    }

    /** Luật 3: lớp của module A không dùng package của module B (chỉ qua contracts). */
    @ParameterizedTest
    @MethodSource("module")
    void khongThamChieuCheoModule(String a) {
        String[] khac = MODULE.stream().filter(b -> !b.equals(a)).map(b -> "vn.nhom6.his." + b + "..").toArray(String[]::new);
        noClasses().that().resideInAPackage("vn.nhom6.his." + a + "..")
                .should().dependOnClassesThat().resideInAnyPackage(khac)
                .check(lop);
    }

    /** Luật 4: contracts không dùng package module nào, không dùng Spring. */
    @Test
    void contractsDocLap() {
        String[] cam = MODULE.stream().map(b -> "vn.nhom6.his." + b + "..").toArray(String[]::new);
        // package-info (khai báo module mở cho Spring Modulith, nằm ở his-app) không phải mã contracts.
        noClasses().that().resideInAPackage("vn.nhom6.his.contracts..").and().doNotHaveSimpleName("package-info")
                .should().dependOnClassesThat().resideInAnyPackage(cam)
                .orShould().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                .check(lop);
    }

    /** Luật 5: shared không dùng module nào; kernel không dùng Spring, AWS SDK, mail. */
    @Test
    void sharedDocLap() {
        String[] cam = MODULE.stream().map(b -> "vn.nhom6.his." + b + "..").toArray(String[]::new);
        noClasses().that().resideInAPackage("vn.nhom6.his.shared..")
                .should().dependOnClassesThat().resideInAnyPackage(cam)
                .check(lop);
        noClasses().that().resideInAPackage("vn.nhom6.his.shared.kernel..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "software.amazon..", "jakarta.mail..")
                .check(lop);
    }

    /** Controller chỉ nằm trong package controller (hoặc thongke của his-app). */
    @Test
    void controllerDungCho() {
        classes().that().areAnnotatedWith(RestController.class)
                .should().resideInAnyPackage("vn.nhom6.his.*.controller..", "vn.nhom6.his.thongke..")
                .check(lop);
    }

    /** Mọi endpoint PHẢI có @PreAuthorize hoặc được đánh dấu @CongKhai (rule 3 — không quên phân quyền). */
    @Test
    void moiEndpointDeuCoPhanQuyen() {
        methods().that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                .and().areAnnotatedWith(GetMapping.class)
                .or().areAnnotatedWith(PostMapping.class)
                .or().areAnnotatedWith(PutMapping.class)
                .or().areAnnotatedWith(PatchMapping.class)
                .or().areAnnotatedWith(DeleteMapping.class)
                .or().areAnnotatedWith(RequestMapping.class)
                .should(coPhanQuyen())
                .check(lop);
    }

    private static ArchCondition<JavaMethod> coPhanQuyen() {
        return new ArchCondition<>("có @PreAuthorize hoặc @CongKhai") {
            @Override
            public void check(JavaMethod m, ConditionEvents events) {
                boolean ok = m.isAnnotatedWith(PreAuthorize.class) || m.isAnnotatedWith(CongKhai.class)
                        || m.getOwner().isAnnotatedWith(PreAuthorize.class) || m.getOwner().isAnnotatedWith(CongKhai.class);
                if (!ok) {
                    events.add(SimpleConditionEvent.violated(m, m.getFullName() + " thiếu @PreAuthorize/@CongKhai"));
                }
            }
        };
    }
}
