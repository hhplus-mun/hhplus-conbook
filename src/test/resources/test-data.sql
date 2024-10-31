INSERT INTO concert(concert_id, artist, title, place) VALUES
    (1, 'buz', 'sorry', 'seoul'),
    (2, 'position', 'i love you', 'Busan');

INSERT INTO concert_schedule(concert_id, concert_date, occupied_count, capacity) VALUES
     (1, '2024-10-01', 1, 50),
     (1, '2024-10-02', 0, 50),
     (1, '2024-10-03', 35, 50),
     (1, '2024-10-04', 22, 50),
     (2, '2024-11-13', 14, 50),
     (2, '2024-11-14', 22, 50),
     (2, '2024-11-15', 50, 50),
     (2, '2024-11-16', 43, 50);

INSERT INTO users(name, uuid) VALUES
    ('admin', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
    ('manager', '266cab91-1e74-46ad-b475-4fc101ad83cf');

INSERT user_point(user_id, updated_time, `point`)
SELECT u.user_id, NOW(), 0 FROM users u ORDER BY user_id;

-- 좌석
INSERT INTO seat(concert_schedule_id, price, is_occupied, row_name, seat_no) VALUES
    (1, 1000, 0, "A", 1),
    (1, 1000, 0, "A", 2),
    (1, 1000, 0, "A", 3),
    (1, 1000, 0, "A", 4),
    (1, 1000, 0, "A", 5),
    (1, 1000, 0, "A", 6),
    (1, 1000, 0, "A", 7),
    (1, 1000, 0, "A", 8),
    (1, 1000, 0, "A", 9),
    (1, 1000, 0, "A", 10),
    (1, 1000, 0, "B", 1),
    (1, 1000, 0, "B", 2),
    (1, 1000, 0, "B", 3),
    (1, 1000, 0, "B", 4),
    (1, 1000, 0, "B", 5),
    (1, 1000, 0, "B", 6),
    (1, 1000, 0, "B", 7),
    (1, 1000, 0, "B", 8),
    (1, 1000, 0, "B", 9),
    (1, 1000, 0, "B", 10),
    (1, 1000, 0, "C", 1),
    (1, 1000, 0, "C", 2),
    (1, 1000, 0, "C", 3),
    (1, 1000, 0, "C", 4),
    (1, 1000, 0, "C", 5),
    (1, 1000, 0, "C", 6),
    (1, 1000, 0, "C", 7),
    (1, 1000, 0, "C", 8),
    (1, 1000, 0, "C", 9),
    (1, 1000, 0, "C", 10),
    (1, 1000, 0, "D", 1),
    (1, 1000, 0, "D", 2),
    (1, 1000, 0, "D", 3),
    (1, 1000, 0, "D", 4),
    (1, 1000, 0, "D", 5),
    (1, 1000, 0, "D", 6),
    (1, 1000, 0, "D", 7),
    (1, 1000, 0, "D", 8),
    (1, 1000, 0, "D", 9),
    (1, 1000, 0, "D", 10),
    (1, 1000, 0, "E", 1),
    (1, 1000, 0, "E", 2),
    (1, 1000, 0, "E", 3),
    (1, 1000, 0, "E", 4),
    (1, 1000, 0, "E", 5),
    (1, 1000, 0, "E", 6),
    (1, 1000, 0, "E", 7),
    (1, 1000, 0, "E", 8),
    (1, 1000, 0, "E", 9),
    (1, 1000, 0, "E", 10),

    (2, 1000, 0, "A", 1),
    (2, 1000, 0, "A", 2),
    (2, 1000, 0, "A", 3),
    (2, 1000, 0, "A", 4),
    (2, 1000, 0, "A", 5),
    (2, 1000, 0, "A", 6),
    (2, 1000, 0, "A", 7),
    (2, 1000, 0, "A", 8),
    (2, 1000, 0, "A", 9),
    (2, 1000, 0, "A", 10),
    (2, 1000, 0, "B", 1),
    (2, 1000, 0, "B", 2),
    (2, 1000, 0, "B", 3),
    (2, 1000, 0, "B", 4),
    (2, 1000, 0, "B", 5),
    (2, 1000, 0, "B", 6),
    (2, 1000, 0, "B", 7),
    (2, 1000, 0, "B", 8),
    (2, 1000, 0, "B", 9),
    (2, 1000, 0, "B", 10),
    (2, 1000, 0, "C", 1),
    (2, 1000, 0, "C", 2),
    (2, 1000, 0, "C", 3),
    (2, 1000, 0, "C", 4),
    (2, 1000, 0, "C", 5),
    (2, 1000, 0, "C", 6),
    (2, 1000, 0, "C", 7),
    (2, 1000, 0, "C", 8),
    (2, 1000, 0, "C", 9),
    (2, 1000, 0, "C", 10),
    (2, 1000, 0, "D", 1),
    (2, 1000, 0, "D", 2),
    (2, 1000, 0, "D", 3),
    (2, 1000, 0, "D", 4),
    (2, 1000, 0, "D", 5),
    (2, 1000, 0, "D", 6),
    (2, 1000, 0, "D", 7),
    (2, 1000, 0, "D", 8),
    (2, 1000, 0, "D", 9),
    (2, 1000, 0, "D", 10),
    (2, 1000, 0, "E", 1),
    (2, 1000, 0, "E", 2),
    (2, 1000, 0, "E", 3),
    (2, 1000, 0, "E", 4),
    (2, 1000, 0, "E", 5),
    (2, 1000, 0, "E", 6),
    (2, 1000, 0, "E", 7),
    (2, 1000, 0, "E", 8),
    (2, 1000, 0, "E", 9),
    (2, 1000, 0, "E", 10),

    (3, 1000, 0, "A", 1),
    (3, 1000, 0, "A", 2),
    (3, 1000, 0, "A", 3),
    (3, 1000, 0, "A", 4),
    (3, 1000, 0, "A", 5),
    (3, 1000, 0, "A", 6),
    (3, 1000, 0, "A", 7),
    (3, 1000, 0, "A", 8),
    (3, 1000, 0, "A", 9),
    (3, 1000, 0, "A", 10),
    (3, 1000, 0, "B", 1),
    (3, 1000, 0, "B", 2),
    (3, 1000, 0, "B", 3),
    (3, 1000, 0, "B", 4),
    (3, 1000, 0, "B", 5),
    (3, 1000, 0, "B", 6),
    (3, 1000, 0, "B", 7),
    (3, 1000, 0, "B", 8),
    (3, 1000, 0, "B", 9),
    (3, 1000, 0, "B", 10),
    (3, 1000, 0, "C", 1),
    (3, 1000, 0, "C", 2),
    (3, 1000, 0, "C", 3),
    (3, 1000, 0, "C", 4),
    (3, 1000, 0, "C", 5),
    (3, 1000, 0, "C", 6),
    (3, 1000, 0, "C", 7),
    (3, 1000, 0, "C", 8),
    (3, 1000, 0, "C", 9),
    (3, 1000, 0, "C", 10),
    (3, 1000, 0, "D", 1),
    (3, 1000, 0, "D", 2),
    (3, 1000, 0, "D", 3),
    (3, 1000, 0, "D", 4),
    (3, 1000, 0, "D", 5),
    (3, 1000, 0, "D", 6),
    (3, 1000, 0, "D", 7),
    (3, 1000, 0, "D", 8),
    (3, 1000, 0, "D", 9),
    (3, 1000, 0, "D", 10),
    (3, 1000, 0, "E", 1),
    (3, 1000, 0, "E", 2),
    (3, 1000, 0, "E", 3),
    (3, 1000, 0, "E", 4),
    (3, 1000, 0, "E", 5),
    (3, 1000, 0, "E", 6),
    (3, 1000, 0, "E", 7),
    (3, 1000, 0, "E", 8),
    (3, 1000, 0, "E", 9),
    (3, 1000, 0, "E", 10),

    (4, 1000, 0, "A", 1),
    (4, 1000, 0, "A", 2),
    (4, 1000, 0, "A", 3),
    (4, 1000, 0, "A", 4),
    (4, 1000, 0, "A", 5),
    (4, 1000, 0, "A", 6),
    (4, 1000, 0, "A", 7),
    (4, 1000, 0, "A", 8),
    (4, 1000, 0, "A", 9),
    (4, 1000, 0, "A", 10),
    (4, 1000, 0, "B", 1),
    (4, 1000, 0, "B", 2),
    (4, 1000, 0, "B", 3),
    (4, 1000, 0, "B", 4),
    (4, 1000, 0, "B", 5),
    (4, 1000, 0, "B", 6),
    (4, 1000, 0, "B", 7),
    (4, 1000, 0, "B", 8),
    (4, 1000, 0, "B", 9),
    (4, 1000, 0, "B", 10),
    (4, 1000, 0, "C", 1),
    (4, 1000, 0, "C", 2),
    (4, 1000, 0, "C", 3),
    (4, 1000, 0, "C", 4),
    (4, 1000, 0, "C", 5),
    (4, 1000, 0, "C", 6),
    (4, 1000, 0, "C", 7),
    (4, 1000, 0, "C", 8),
    (4, 1000, 0, "C", 9),
    (4, 1000, 0, "C", 10),
    (4, 1000, 0, "D", 1),
    (4, 1000, 0, "D", 2),
    (4, 1000, 0, "D", 3),
    (4, 1000, 0, "D", 4),
    (4, 1000, 0, "D", 5),
    (4, 1000, 0, "D", 6),
    (4, 1000, 0, "D", 7),
    (4, 1000, 0, "D", 8),
    (4, 1000, 0, "D", 9),
    (4, 1000, 0, "D", 10),
    (4, 1000, 0, "E", 1),
    (4, 1000, 0, "E", 2),
    (4, 1000, 0, "E", 3),
    (4, 1000, 0, "E", 4),
    (4, 1000, 0, "E", 5),
    (4, 1000, 0, "E", 6),
    (4, 1000, 0, "E", 7),
    (4, 1000, 0, "E", 8),
    (4, 1000, 0, "E", 9),
    (4, 1000, 0, "E", 10),

    (5, 1000, 0, "A", 1),
    (5, 1000, 0, "A", 2),
    (5, 1000, 0, "A", 3),
    (5, 1000, 0, "A", 4),
    (5, 1000, 0, "A", 5),
    (5, 1000, 0, "A", 6),
    (5, 1000, 0, "A", 7),
    (5, 1000, 0, "A", 8),
    (5, 1000, 0, "A", 9),
    (5, 1000, 0, "A", 10),
    (5, 1000, 0, "B", 1),
    (5, 1000, 0, "B", 2),
    (5, 1000, 0, "B", 3),
    (5, 1000, 0, "B", 4),
    (5, 1000, 0, "B", 5),
    (5, 1000, 0, "B", 6),
    (5, 1000, 0, "B", 7),
    (5, 1000, 0, "B", 8),
    (5, 1000, 0, "B", 9),
    (5, 1000, 0, "B", 10),
    (5, 1000, 0, "C", 1),
    (5, 1000, 0, "C", 2),
    (5, 1000, 0, "C", 3),
    (5, 1000, 0, "C", 4),
    (5, 1000, 0, "C", 5),
    (5, 1000, 0, "C", 6),
    (5, 1000, 0, "C", 7),
    (5, 1000, 0, "C", 8),
    (5, 1000, 0, "C", 9),
    (5, 1000, 0, "C", 10),
    (5, 1000, 0, "D", 1),
    (5, 1000, 0, "D", 2),
    (5, 1000, 0, "D", 3),
    (5, 1000, 0, "D", 4),
    (5, 1000, 0, "D", 5),
    (5, 1000, 0, "D", 6),
    (5, 1000, 0, "D", 7),
    (5, 1000, 0, "D", 8),
    (5, 1000, 0, "D", 9),
    (5, 1000, 0, "D", 10),
    (5, 1000, 0, "E", 1),
    (5, 1000, 0, "E", 2),
    (5, 1000, 0, "E", 3),
    (5, 1000, 0, "E", 4),
    (5, 1000, 0, "E", 5),
    (5, 1000, 0, "E", 6),
    (5, 1000, 0, "E", 7),
    (5, 1000, 0, "E", 8),
    (5, 1000, 0, "E", 9),
    (5, 1000, 0, "E", 10),

    (6, 1000, 0, "A", 1),
    (6, 1000, 0, "A", 2),
    (6, 1000, 0, "A", 3),
    (6, 1000, 0, "A", 4),
    (6, 1000, 0, "A", 5),
    (6, 1000, 0, "A", 6),
    (6, 1000, 0, "A", 7),
    (6, 1000, 0, "A", 8),
    (6, 1000, 0, "A", 9),
    (6, 1000, 0, "A", 10),
    (6, 1000, 0, "B", 1),
    (6, 1000, 0, "B", 2),
    (6, 1000, 0, "B", 3),
    (6, 1000, 0, "B", 4),
    (6, 1000, 0, "B", 5),
    (6, 1000, 0, "B", 6),
    (6, 1000, 0, "B", 7),
    (6, 1000, 0, "B", 8),
    (6, 1000, 0, "B", 9),
    (6, 1000, 0, "B", 10),
    (6, 1000, 0, "C", 1),
    (6, 1000, 0, "C", 2),
    (6, 1000, 0, "C", 3),
    (6, 1000, 0, "C", 4),
    (6, 1000, 0, "C", 5),
    (6, 1000, 0, "C", 6),
    (6, 1000, 0, "C", 7),
    (6, 1000, 0, "C", 8),
    (6, 1000, 0, "C", 9),
    (6, 1000, 0, "C", 10),
    (6, 1000, 0, "D", 1),
    (6, 1000, 0, "D", 2),
    (6, 1000, 0, "D", 3),
    (6, 1000, 0, "D", 4),
    (6, 1000, 0, "D", 5),
    (6, 1000, 0, "D", 6),
    (6, 1000, 0, "D", 7),
    (6, 1000, 0, "D", 8),
    (6, 1000, 0, "D", 9),
    (6, 1000, 0, "D", 10),
    (6, 1000, 0, "E", 1),
    (6, 1000, 0, "E", 2),
    (6, 1000, 0, "E", 3),
    (6, 1000, 0, "E", 4),
    (6, 1000, 0, "E", 5),
    (6, 1000, 0, "E", 6),
    (6, 1000, 0, "E", 7),
    (6, 1000, 0, "E", 8),
    (6, 1000, 0, "E", 9),
    (6, 1000, 0, "E", 10);