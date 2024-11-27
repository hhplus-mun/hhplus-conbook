-- Load Test 진행을 위한 테스트 유저 생성 프로시저
-- PLAN : 30,000 명
DELIMITER //
CREATE PROCEDURE generate_test_users()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 30000 DO
        INSERT INTO users (name, uuid)
        VALUES (
            CONCAT('user_', i),
            UUID()
        );
        SET i = i + 1;
END WHILE;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE generate_test_user_point()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 30000 DO
            INSERT INTO user_point (point, updated_time, user_id)
            VALUES (
                       10000000,
                       NOW(),
                    i
                   );
            SET i = i + 1;
        END WHILE;
END //
DELIMITER ;

-- Load Test 진행을 위한 테스트 좌석 생성 프로시저
-- PLAN : 20,000 좌석 (수용인원을 2만명이라 가정)
DELIMITER //
CREATE PROCEDURE generate_test_seats()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 20000 DO
        INSERT INTO seat (
            is_occupied,
            price,
            seat_no,
            concert_schedule_id,
            row_name
        )
        VALUES (
            0,  -- is_occupied: 초기값 0 (비어있음)
            25000,
                (i % 100), -- seat_no: 순차적인 번호
            1,  -- concert_sched_id: 임의의 concert_schedule ID (실제 존재하는 ID로 변경 필요)
            CASE FLOOR(i / 100)
                WHEN 0 THEN 'A'
                WHEN 1 THEN 'B'
                WHEN 2 THEN 'C'
                WHEN 3 THEN 'D'
                WHEN 4 THEN 'E'
                WHEN 5 THEN 'F'
                WHEN 6 THEN 'G'
                WHEN 7 THEN 'H'
                WHEN 8 THEN 'I'
                WHEN 9 THEN 'J'
                WHEN 10 THEN 'K'
                WHEN 11 THEN 'L'
                WHEN 12 THEN 'M'
                WHEN 13 THEN 'N'
                WHEN 14 THEN 'O'
                WHEN 15 THEN 'P'
                WHEN 16 THEN 'Q'
                WHEN 17 THEN 'R'
                WHEN 18 THEN 'S'
                WHEN 19 THEN 'T'
                ELSE 'U'
            END
        );
        SET i = i + 1;
END WHILE;
END //
DELIMITER ;

-- MariaDB(MySQL) 프로시저 실행
CALL generate_test_users();
CALL generate_test_seats();

-- 필요 시 생성한 프로시저 제거
-- DROP PROCEDURE IF EXISTS generate_test_users;
-- DROP PROCEDURE IF EXISTS generate_test_user_point;
-- DROP PROCEDURE IF EXISTS generate_test_seats;