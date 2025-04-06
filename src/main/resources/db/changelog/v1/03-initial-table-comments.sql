CREATE TABLE IF NOT EXISTS comments (
    id              INT PRIMARY KEY NOT NULL,
    content         VARCHAR (255) NOT NULL,
    task_id         INT
    CONSTRAINT fk_comments_task_id
    REFERENCES tasks (id),
    commentator_id       INT
    CONSTRAINT fk_comments_commentator_id
    REFERENCES users (id),
    timestamp TIMESTAMP
    );

COMMENT ON TABLE comments IS 'Таблица комментариев';
COMMENT ON COLUMN comments.id IS 'id коммента';
COMMENT ON COLUMN comments.content IS 'Содержание коммента';
COMMENT ON COLUMN comments.task_id IS 'id задачи, в которой этот коммент';
COMMENT ON COLUMN comments.commentator_id IS 'id автора этого коммента';
COMMENT ON COLUMN comments.timestamp IS 'Время создания этого коммента';

CREATE SEQUENCE COMMENT_SEQUENCE_ID START WITH 6;
