INSERT INTO member
(name, discord_id, discord_login_id, profile_image, created_at, modified_at)
VALUES ('김지훈', 'discord_1001', 'jihoon#1234', 'https://cdn.discordapp.com/embed/avatars/1.png', NOW(), NOW()),
       ('이서연', 'discord_1002', 'seoyeon#5678', 'https://cdn.discordapp.com/embed/avatars/2.png', NOW(), NOW()),
       ('박민수', 'discord_1003', 'minsu#4321', 'https://cdn.discordapp.com/embed/avatars/3.png', NOW(), NOW()),
       ('최유진', 'discord_1004', 'yujin#8765', 'https://cdn.discordapp.com/embed/avatars/4.png', NOW(), NOW()),
       ('정하늘', 'discord_1005', 'haneul#2345', 'https://cdn.discordapp.com/embed/avatars/5.png', NOW(), NOW()),
       ('오세훈', 'discord_1006', 'sehun#9876', 'https://cdn.discordapp.com/embed/avatars/0.png', NOW(), NOW()),
       ('한수민', 'discord_1007', 'sumin#6543', 'https://cdn.discordapp.com/embed/avatars/1.png', NOW(), NOW()),
       ('류지호', 'discord_1008', 'jiho#2468', 'https://cdn.discordapp.com/embed/avatars/2.png', NOW(), NOW()),
       ('서민재', 'discord_1009', 'minjae#1357', 'https://cdn.discordapp.com/embed/avatars/3.png', NOW(), NOW()),
       ('윤아람', 'discord_1010', 'aram#8642', 'https://cdn.discordapp.com/embed/avatars/4.png', NOW(), NOW());

INSERT INTO detail
(name, student_number, depart_name, grade, github_id, email, phone_number, birthday)
VALUES
    ('김지훈', '202012301', '스마트ICT융합공학과', 'THIRD_YEAR', 'jihoon-dev', 'jihoon@example.com', '010-1111-1111', '2001-03-23'),
    ('이서연', '202112302', '컴퓨터공학부',       'SECOND_YEAR', 'seoyeon-dev', 'seoyeon@example.com', '010-2222-2222', '2002-07-15'),
    ('박민수', '202012303', '소프트웨어학과',     'FOURTH_YEAR', 'minsu-backend', 'minsu@example.com', '010-3333-3333', '2000-11-02'),
    ('최유진', '202212304', '스마트ICT융합공학과', 'FIRST_YEAR',  'yujin-fe', 'yujin@example.com', '010-4444-4444', '2004-05-30'),
    ('정하늘', '202012305', '전자정보공학부',     'THIRD_YEAR', 'haneul-dev', 'haneul@example.com', '010-5555-5555', '2001-01-10'),
    ('오세훈', '202012306', '스마트ICT융합공학과', 'FOURTH_YEAR', 'sehun-dev', 'sehun@example.com', '010-6666-6666', '2000-08-21'),
    ('한수민', '202112307', '컴퓨터공학부',       'SECOND_YEAR', 'sumin-dev', 'sumin@example.com', '010-7777-7777', '2002-02-14'),
    ('류지호', '202012308', '소프트웨어학과',     'THIRD_YEAR', 'jiho-dev', 'jiho@example.com', '010-8888-8888', '2001-09-09'),
    ('서민재', '202012309', '스마트ICT융합공학과', 'FOURTH_YEAR', 'minjae-dev', 'minjae@example.com', '010-9999-9999', '2000-12-01'),
    ('윤아람', '202112310', '컴퓨터공학부',       'SECOND_YEAR', 'aram-dev', 'aram@example.com', '010-0000-0000', '2002-04-18');


INSERT INTO role (role_id, batch, name, discord_role_id, position, created_at, modified_at)
VALUES (1,'SIXTH', '6th PM 부원', 1, 0, NOW(), NOW()),
       (2, 'FIFTH', '5th 운영진', 2, 0, NOW(), NOW()),
       (3, 'SIXTH', '6th Server 튜터', 3, 0, NOW(), NOW()),
       (4, 'SIXTH', '6th 운영진', 4, 0, NOW(), NOW());

UPDATE member SET detail_id = 1 WHERE discord_id = 'discord_1001';
UPDATE member SET detail_id = 2 WHERE discord_id = 'discord_1002';
UPDATE member SET detail_id = 3 WHERE discord_id = 'discord_1003';
UPDATE member SET detail_id = 4 WHERE discord_id = 'discord_1004';
UPDATE member SET detail_id = 5 WHERE discord_id = 'discord_1005';
UPDATE member SET detail_id = 6 WHERE discord_id = 'discord_1006';
UPDATE member SET detail_id = 7 WHERE discord_id = 'discord_1007';
UPDATE member SET detail_id = 8 WHERE discord_id = 'discord_1008';
UPDATE member SET detail_id = 9 WHERE discord_id = 'discord_1009';
UPDATE member SET detail_id = 10 WHERE discord_id = 'discord_1010';

INSERT INTO member_role (member_discord_id, role_id, member_id)
VALUES ('discord_1001', 1, 1),
       ('discord_1001', 2, 1),
       ('discord_1001', 4, 1),
       ('discord_1002', 2, 2),
       ('discord_1003', 1, 3),
       ('discord_1004', 3, 4),
       ('discord_1005', 1, 5),
       ('discord_1006', 2, 6),
       ('discord_1007', 1, 7),
       ('discord_1008', 3, 8),
       ('discord_1009', 1, 9),
       ('discord_1010', 4, 10);


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
        'SIXTH'),

       (NOW(), NOW(), 2,
        '그린체인(GreenChain)',
        'https://cdn.kupage.com/team6.pdf',
        'https://cdn.kupage.com/team6-thumbnail.jpg',
        '친환경 소비 유도 앱 서비스',
        '탄소 절감 포인트 시스템, 마켓 연동, 랭킹 표시, 포인트 계산 로직, 사용자 인증',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Web',
        'SIXTH'),

       (NOW(), NOW(), 2,
        '핏업(FitUp)',
        'https://cdn.kupage.com/team7.pdf',
        'https://cdn.kupage.com/team7-thumbnail.jpg',
        'AI 기반 개인 맞춤 운동 루틴 추천 서비스',
        'AI 운동 추천, 실시간 자세 분석, 통계 리포트 제공, TensorFlow 모델, 영상 인식, BLE 기기 연동',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Web',
        'SIXTH'),

       (NOW(), NOW(), 2,
        '페이온(PayOn)',
        'https://cdn.kupage.com/team8.pdf',
        'https://cdn.kupage.com/team8-thumbnail.jpg',
        'QR 기반 간편 결제 시스템',
        'QR 결제, 정산 관리, 통계 리포트 제공, 결제 보안, 세션 인증, DB 트랜잭션 관리',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Web',
        'SIXTH'),

       (NOW(), NOW(), 2,
        '스테이링크(StayLink)',
        'https://cdn.kupage.com/team9.pdf',
        'https://cdn.kupage.com/team9-thumbnail.jpg',
        '숙박 예약 및 후기 통합 플랫폼',
        '숙소 검색, 후기 작성, 예약 관리 기능 제공, 검색 필터링, 사용자 인증, 결제 연동',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Web',
        'SIXTH'),

       (NOW(), NOW(), 2,
        '코드메이트(CodeMate)',
        'https://cdn.kupage.com/team10.pdf',
        'https://cdn.kupage.com/team10-thumbnail.jpg',
        '프로그래밍 학습 및 코드 리뷰 플랫폼',
        '실시간 코드 리뷰, 문제 풀이, 개인 학습 기록 제공, 코드 실행 API, 리뷰 시스템, 알림 기능',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Web',
        'SIXTH'),

       (NOW(), NOW(), 2,
        '푸디잇(FoodEat)',
        'https://cdn.kupage.com/team11.pdf',
        'https://cdn.kupage.com/team11-thumbnail.jpg',
        'AI 기반 개인 맞춤 식단 추천 서비스',
        '음식 데이터 크롤링, AI 추천 알고리즘, 사용자 피드백 반영 기능',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Web',
        'SIXTH'),

       (NOW(), NOW(), 2,
        '에듀링크(EduLink)',
        'https://cdn.kupage.com/team12.pdf',
        'https://cdn.kupage.com/team12-thumbnail.jpg',
        '온라인 학습 관리 및 멘토링 플랫폼',
        '멘토링 세션 예약, 화상 채팅 API, 학습 진도 관리 기능',
        '이런 개발자분이 오시면 좋겠습니다!',
        '이서연',
        'Android',
        'SIXTH');

INSERT INTO team_applicant (
    applied_part,
    portfolio_url,
    motivation,
    status,
    member_id,
    team_id,
    created_at,
    modified_at
) VALUES
      ('Android',
       'https://github.com/jwkim/Android-portfolio',
       'Android Studio로 앱을 개발한 경험이 있습니다.',
       'ROUND1_APPLYING',
       2, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      ('Android',
       'https://github.com/hwlee/Android-toy',
       'Kotlin과 Firebase를 활용한 프로젝트 경험이 있습니다.',
       'ROUND1_APPLYING',
       3, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      ('iOS',
       'https://github.com/sylee/ios-portfolio',
       'UIKit, SwiftUI 모두 사용해봤습니다.',
       'ROUND1_APPLYING',
       4, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      ('Web',
       'https://github.com/mspark/web-portfolio',
       'React와 Next.js 기반 프로젝트 경험이 있습니다.',
       'ROUND1_APPLYING',
       5, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      ('Web',
       'https://github.com/yjjeong/web-demo',
       'TypeScript와 Zustand를 이용한 상태 관리 경험이 있습니다.',
       'ROUND1_APPLYING',
       6, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      ('Server',
       'https://github.com/haeun/server-portfolio',
       'Spring Boot, JPA 기반 REST API 개발 경험이 있습니다.',
       'ROUND1_APPLYING',
       7, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      ('Design',
       'https://behance.net/mjchoi-design',
       'Figma와 Adobe XD를 활용한 UI/UX 디자인 경험이 있습니다.',
       'ROUND1_APPLYING',
       8, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      ('Design',
       'https://dribbble.com/sbhan',
       '브랜딩과 인터랙션 디자인 프로젝트를 진행했습니다.',
       'ROUND1_APPLYING',
       9, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655');

SELECT * FROM MEMBER;

SELECT * FROM DETAIL;

SELECT * FROM ROLE ;

SELECT * FROM MEMBER_ROLE ;

SELECT * FROM TEAM  ;

