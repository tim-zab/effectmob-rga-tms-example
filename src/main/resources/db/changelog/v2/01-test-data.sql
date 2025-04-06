INSERT INTO users (id, email, password, role)
VALUES
    (1, 'ivanov123@mail.ru', '$2a$12$OW2W44tJkHh7Aj0zm9p1ROQWZM/Sm5rzrTl5iwV5WnEAoYhp32iha', 'ROLE_ADMIN'),
    (2, 'pet89rov@mail.ru', '$2a$12$fEkGtMISLsorcTMc2TRRd.gMK3TGtfmwawf/eGyHODXRRo.CO0BHu', 'ROLE_USER'),
    (3, 'si78dorov@mail.ru', '$2a$12$llfj2sESFa4GT2BRo5oUe.WDnYflfkqHEnRl2R6l.36dmDWGrGYly', 'ROLE_USER');

INSERT INTO tasks (id, title, description, status, priority, author_id, executor_id)
VALUES
    (1, 'Task 1', 'Description 1', 'PENDING', 'LOW', 1, 2),
    (2, 'Task 2', 'Description 2', 'IN_PROGRESS', 'MEDIUM', 1, 3),
    (3, 'Task 3', 'Description 3', 'FINISHED', 'HIGH', 1, 2);

INSERT INTO comments (id, content, task_id, commentator_id)
VALUES
    (1, 'Content 1', 3, 2),
    (2, 'Content 2', 2, 3),
    (3, 'Content 3', 1, 2),
    (4, 'Content 4', 1, 1),
    (5, 'Content 5', 3, 2);


