-- Commerce DB 초기 설정
CREATE DATABASE IF NOT EXISTS commerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON commerce.* TO 'commerce'@'%';
FLUSH PRIVILEGES;
