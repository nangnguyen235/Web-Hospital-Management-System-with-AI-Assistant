# syntax=docker/dockerfile:1.7-labs
# Image backend HIS (multi-stage) — 4-docker-compose/docker-compose.md mục 3.
# Build ở GitHub Actions, KHÔNG build trên EC2 1 GiB.

FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
# Chép riêng các file project trước để cache bước restore
COPY global.json Directory.Build.props Directory.Packages.props ./
COPY --parents src/**/*.csproj ./
RUN dotnet restore src/HIS.Api/HIS.Api.csproj
COPY src/ ./src/
RUN dotnet publish src/HIS.Api/HIS.Api.csproj -c Release -o /app --no-restore

FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime
# curl cho healthcheck; image mặc định (Ubuntu 24.04) có sẵn tzdata và ICU
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app ./
ENV ASPNETCORE_HTTP_PORTS=8080 \
    DOTNET_gcServer=0 \
    TZ=Asia/Ho_Chi_Minh
EXPOSE 8080
USER app
ENTRYPOINT ["dotnet", "HIS.Api.dll"]
