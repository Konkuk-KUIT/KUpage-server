INSERT INTO detail
(name, student_number, depart_name, grade, github_id, email, phone_number, birthday)
VALUES
    ('김지훈(6기부원)', '202012301', '스마트ICT융합공학과', 'THIRD_YEAR', 'jihoon-dev', 'jihoon@example.com', '010-1111-1111', '2001-03-23'),
    ('이서연', '202112302', '컴퓨터공학부',       'SECOND_YEAR', 'seoyeon-dev', 'seoyeon@example.com', '010-2222-2222', '2002-07-15'),
    ('박민수', '202012303', '소프트웨어학과',     'FOURTH_YEAR', 'minsu-backend', 'minsu@example.com', '010-3333-3333', '2000-11-02'),
    ('최유진', '202212304', '스마트ICT융합공학과', 'FIRST_YEAR',  'yujin-fe', 'yujin@example.com', '010-4444-4444', '2004-05-30'),
    ('정하늘', '202012305', '전자정보공학부',     'THIRD_YEAR', 'haneul-dev', 'haneul@example.com', '010-5555-5555', '2001-01-10'),
    ('오세훈', '202012306', '스마트ICT융합공학과', 'FOURTH_YEAR', 'sehun-dev', 'sehun@example.com', '010-6666-6666', '2000-08-21'),
    ('한수민', '202112307', '컴퓨터공학부',       'SECOND_YEAR', 'sumin-dev', 'sumin@example.com', '010-7777-7777', '2002-02-14'),
    ('류지호', '202012308', '소프트웨어학과',     'THIRD_YEAR', 'jiho-dev', 'jiho@example.com', '010-8888-8888', '2001-09-09'),
    ('서민재', '202012309', '스마트ICT융합공학과', 'FOURTH_YEAR', 'minjae-dev', 'minjae@example.com', '010-9999-9999', '2000-12-01'),
    ('윤아람', '202112310', '컴퓨터공학부',       'SECOND_YEAR', 'aram-dev', 'aram@example.com', '010-0000-0000', '2002-04-18');


INSERT INTO member
(name, detail_id, discord_id, discord_login_id, profile_image, created_at, modified_at)
VALUES ('김지훈', 1, 'discord_1001', 'jihoon#1234', 'https://cdn.discordapp.com/embed/avatars/1.png', NOW(), NOW()),
       ('이서연', 2, 'discord_1002', 'seoyeon#5678', 'https://cdn.discordapp.com/embed/avatars/2.png', NOW(), NOW()),
       ('박민수', 3, 'discord_1003', 'minsu#4321', 'https://cdn.discordapp.com/embed/avatars/3.png', NOW(), NOW()),
       ('최유진', 4, 'discord_1004', 'yujin#8765', 'https://cdn.discordapp.com/embed/avatars/4.png', NOW(), NOW()),
       ('정하늘', 5, 'discord_1005', 'haneul#2345', 'https://cdn.discordapp.com/embed/avatars/5.png', NOW(), NOW()),
       ('오세훈', 6, 'discord_1006', 'sehun#9876', 'https://cdn.discordapp.com/embed/avatars/0.png', NOW(), NOW()),
       ('한수민', 7, 'discord_1007', 'sumin#6543', 'https://cdn.discordapp.com/embed/avatars/1.png', NOW(), NOW()),
       ('류지호', 8, 'discord_1008', 'jiho#2468', 'https://cdn.discordapp.com/embed/avatars/2.png', NOW(), NOW()),
       ('서민재', 9, 'discord_1009', 'minjae#1357', 'https://cdn.discordapp.com/embed/avatars/3.png', NOW(), NOW()),
       ('윤아람', 10, 'discord_1010', 'aram#8642', 'https://cdn.discordapp.com/embed/avatars/4.png', NOW(), NOW());

INSERT INTO role (role_id, batch, name, discord_role_id, position, created_at, modified_at)
VALUES (1,'SIXTH', '6th PM 스터디장', 1, 0, NOW(), NOW()),
       (2,'SIXTH', '6th PM 부원', 1, 0, NOW(), NOW()),
       (3, 'SIXTH', '6th Server 파트장', 2, 0, NOW(), NOW()),
       (4, 'SIXTH', '6th Server 튜터', 3, 0, NOW(), NOW()),
       (5, 'SIXTH', '6th Server 부원', 2, 0, NOW(), NOW()),
       (6, 'SIXTH', '6th Web 파트장', 4, 0, NOW(), NOW()),
       (7, 'SIXTH', '6th Web 튜터', 5, 0, NOW(), NOW()),
       (8, 'SIXTH', '6th Web 부원', 4, 0, NOW(), NOW()),
       (9, 'SIXTH', '6th Android 파트장', 6, 0, NOW(), NOW()),
       (10, 'SIXTH', '6th Android 튜터', 7, 0, NOW(), NOW()),
       (11, 'SIXTH', '6th Android 부원', 7, 0, NOW(), NOW()),
       (12, 'SIXTH', '6th 회장', 8, 0, NOW(), NOW()),
       (13, 'SIXTH', '6th 운영진', 8, 0, NOW(), NOW()),
       (14, 'FIFTH', '5th 운영진', 9, 0, NOW(), NOW()),
       (15, 'SIXTH', '6th 부회장', 8, 0, NOW(), NOW()),
       (16, 'SIXTH', '6th PM 파트장', 1, 0, NOW(), NOW());



-- Member-Role mapping summary
-- 김지훈 (member_id=1, discord_1001): 6th 부회장, 5th 운영진
-- 이서연 (member_id=2, discord_1002): 6th PM 파트장
-- 박민수 (member_id=3, discord_1003): 6th Server 파트장, 6th Web 부원
-- 최유진 (member_id=4, discord_1004): 6th Server 튜터
-- 정하늘 (member_id=5, discord_1005): 6th Web 튜터
-- 오세훈 (member_id=6, discord_1006): 6th Android 튜터
-- 한수민 (member_id=7, discord_1007): 6th PM 부원
-- 류지호 (member_id=8, discord_1008): 6th Web 부원
-- 서민재 (member_id=9, discord_1009): 6th Android 부원
-- 윤아람 (member_id=10, discord_1010): 6th Server 부원

INSERT INTO member_role (member_discord_id, role_id, member_id)
VALUES
       ('discord_1001', 15, 1),
       ('discord_1001', 14, 1),

       ('discord_1002', 16, 2),

       ('discord_1003', 3, 3),
       ('discord_1003', 8, 3),

       ('discord_1004', 4, 4),

       ('discord_1005', 7, 5),

       ('discord_1006', 10, 6),

       ('discord_1007', 2, 7),

       ('discord_1008', 8, 8),

       ('discord_1009', 11, 9),

       ('discord_1010', 5, 10);

INSERT INTO team
(CREATED_AT, MODIFIED_AT, OWNER_ID,
 SERVICE_NAME, SERVICE_INTRO_FILE, IMAGE_URL,
 TOPIC_SUMMARY, FEATURE_REQUIREMENTS, PREFERRED_DEVELOPER,
 OWNER_NAME, APP_TYPE, BATCH)
VALUES (NOW(), NOW(), 3,
        '글로방',
        'https://cdn.kupage.com/team1-intro.pdf',
        'https://cdn.kupage.com/team1-thumbnail.jpg',
        '외국인 대상 부동산 매칭 서비스',
        '지도 기반의 실시간 매물 탐색, 채팅 문의, 언어별 번역 기능 제공',
        '이런 개발자분이 오시면 좋겠습니다!',
        '박민수',
        'Android',
        'SIXTH'),

       (NOW(), NOW(), 2,
        '쿠페이지(Kupage)',
        'https://cdn.kupage.com/team4.pdf',
        'https://cdn.kupage.com/team4-thumbnail.jpg',
        '대학생 프로젝트 포트폴리오 공유 플랫폼',
        '팀-지원자 간 매칭, 파일 업로드, 상세 페이지 구현',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Web',
        'SIXTH'),

       (NOW(), NOW(), 2,
        '디어캠퍼스',
        'https://cdn.kupage.com/team5.pdf',
        'https://cdn.kupage.com/team5-thumbnail.jpg',
        '대학 캠퍼스 기반 커뮤니티 플랫폼',
        '위치 기반 동아리 홍보, 실시간 소통 게시판 제공, 실시간 알림, 이미지 업로드, 권한 기반 접근 제어',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Web',
        'SIXTH');


SELECT * FROM MEMBER;

SELECT * FROM DETAIL;

SELECT * FROM ROLE ;

SELECT * FROM MEMBER_ROLE ;

SELECT * FROM TEAM  ;
