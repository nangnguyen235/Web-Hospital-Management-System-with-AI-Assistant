package vn.nhom6.his.shared.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Swagger UI (/swagger-ui.html) — bật bằng HIS_SWAGGER_ENABLED; nút Authorize nhận JWT. */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "HIS API — TheHos Hospital", version = "1.0", description = "Hệ thống quản lý bệnh viện, Nhóm 6"),
        security = @SecurityRequirement(name = "bearer"))
@SecurityScheme(name = "bearer", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
