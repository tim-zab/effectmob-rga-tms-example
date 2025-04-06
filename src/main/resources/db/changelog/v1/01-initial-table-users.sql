CREATE TABLE IF NOT EXISTS users (
    id              INT PRIMARY KEY NOT NULL,
    email           VARCHAR (30) UNIQUE NOT NULL,
    password        VARCHAR (100) UNIQUE NOT NULL,
    role            VARCHAR(30)
    );

COMMENT ON TABLE users IS 'Таблица пользователей';
COMMENT ON COLUMN users.id IS 'id пользователя';
COMMENT ON COLUMN users.email IS 'E-mail пользователя';
COMMENT ON COLUMN users.password IS 'Пароль пользователя';
COMMENT ON COLUMN users.role IS 'Роль пользователя';

CREATE SEQUENCE USER_SEQUENCE_ID START WITH 4;
