# syntax=docker/dockerfile:1
# Backend HIS — multi-stage (4-docker-compose/docker-compose.md mục 3). Build context: backend/.
# Test chạy ở CI (Testcontainers không chạy trong docker build) nên ở đây bỏ test.

# ---- Stage 1: build bằng JDK + Maven Wrapper ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q -DskipTests package
# Tách layer: thư viện ít đổi nằm layer dưới, mã nguồn layer trên cùng → image đổi ít khi chỉ sửa code.
RUN java -Djarmode=tools -jar his-app/target/his-app.jar extract --layers --launcher --destination /extracted

# ---- Stage 2: chạy bằng JRE (Ubuntu, có tzdata cho giờ Việt Nam) ----
FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system his && useradd --system --gid his --home-dir /app his
WORKDIR /app
COPY --from=build /extracted/dependencies/ ./
COPY --from=build /extracted/spring-boot-loader/ ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/ ./
USER his
ENV TZ=Asia/Ho_Chi_Minh
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
