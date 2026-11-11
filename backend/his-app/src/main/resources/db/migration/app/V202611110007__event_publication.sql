-- Event Publication Registry của Spring Modulith (spring-modulith-events-jdbc 2.1.2, schemas/v2/schema-mysql.sql).
-- Sự kiện ghi cùng transaction bên phát; handler lỗi thì phát lại khi khởi động (modular-monolith.md mục 3.1).
-- Giữ nguyên tên VIẾT HOA như truy vấn của thư viện (MySQL trên Linux phân biệt hoa thường tên bảng).
CREATE TABLE IF NOT EXISTS EVENT_PUBLICATION
(
  ID                     VARCHAR(36) NOT NULL,
  LISTENER_ID            VARCHAR(512) NOT NULL,
  EVENT_TYPE             VARCHAR(512) NOT NULL,
  SERIALIZED_EVENT       VARCHAR(4000) NOT NULL,
  PUBLICATION_DATE       TIMESTAMP(6) NOT NULL,
  COMPLETION_DATE        TIMESTAMP(6) DEFAULT NULL NULL,
  STATUS                 VARCHAR(20),
  COMPLETION_ATTEMPTS    INT,
  LAST_RESUBMISSION_DATE TIMESTAMP(6) DEFAULT NULL NULL,
  PRIMARY KEY (ID),
  INDEX EVENT_PUBLICATION_BY_COMPLETION_DATE_IDX (COMPLETION_DATE)
);
