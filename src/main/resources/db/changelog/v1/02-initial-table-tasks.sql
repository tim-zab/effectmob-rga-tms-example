CREATE TABLE IF NOT EXISTS tasks (
    id              INT PRIMARY KEY NOT NULL,
    title           VARCHAR (255) NOT NULL,
    description     VARCHAR (1000) NOT NULL,
    status          VARCHAR (255) NOT NULL,
    priority        VARCHAR (255) NOT NULL,
    author_id       INT
    CONSTRAINT fk_tasks_author_id
    REFERENCES users (id),
    executor_id     INT
    CONSTRAINT fk_tasks_executor_id
    REFERENCES users (id),
    timestamp       TIMESTAMP
    );

COMMENT ON TABLE tasks IS 'Таблица задач';
COMMENT ON COLUMN tasks.id IS 'id задачи';
COMMENT ON COLUMN tasks.title IS 'Заголовок задачи';
COMMENT ON COLUMN tasks.description IS 'Описание задачи';
COMMENT ON COLUMN tasks.status IS 'Статус задачи';
COMMENT ON COLUMN tasks.priority IS 'Приоритет задачи';
COMMENT ON COLUMN tasks.author_id IS 'id автора задачи';
COMMENT ON COLUMN tasks.executor_id IS 'id исполнителя задачи';
COMMENT ON COLUMN tasks.timestamp IS 'Время создания задачи';

CREATE SEQUENCE TASK_SEQUENCE_ID START WITH 4;