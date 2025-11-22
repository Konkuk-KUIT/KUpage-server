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

INSERT INTO role (role_id, batch, name, discord_role_id, position, created_at, modified_at)
VALUES (1,'SIXTH', '6th PM 부원', NULL, 0, NOW(), NOW()),
       (2, 'FIFTH', '5th 운영진', NULL, 0, NOW(), NOW()),
       (3, 'SIXTH', '6th PM 부원', NULL, 0, NOW(), NOW()),
       (4, 'SIXTH', '6th Server 튜터', NULL, 0, NOW(), NOW()),
       (5, 'SIXTH', '6th 운영진', NULL, 0, NOW(), NOW());

INSERT INTO member_role (member_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 1),
       (6, 2),
       (7, 3),
       (8, 4),
       (9, 1),
       (10, 5);


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
    team_applicant_id,
    applied_part,
    portfolio_url,
    motivation,
    status,
    member_id,
    team_id,
    created_at,
    modified_at
) VALUES
      (1, 'Android',
       'https://github.com/jwkim/Android-portfolio',
       'Android Studio로 앱을 개발한 경험이 있습니다.',
       ROUND1_APPLYING,
       2, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      (2, 'Android',
       'https://github.com/hwlee/Android-toy',
       'Kotlin과 Firebase를 활용한 프로젝트 경험이 있습니다.',
       ROUND1_APPLYING,
       3, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      (3, 'iOS',
       'https://github.com/sylee/ios-portfolio',
       'UIKit, SwiftUI 모두 사용해봤습니다.',
       ROUND1_APPLYING,
       4, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      (4, 'Web',
       'https://github.com/mspark/web-portfolio',
       'React와 Next.js 기반 프로젝트 경험이 있습니다.',
       ROUND1_APPLYING,
       5, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      (5, 'Web',
       'https://github.com/yjjeong/web-demo',
       'TypeScript와 Zustand를 이용한 상태 관리 경험이 있습니다.',
       ROUND1_APPLYING,
       6, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      (6, 'Server',
       'https://github.com/haeun/server-portfolio',
       'Spring Boot, JPA 기반 REST API 개발 경험이 있습니다.',
       ROUND1_APPLYING,
       7, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      (7, 'Design',
       'https://behance.net/mjchoi-design',
       'Figma와 Adobe XD를 활용한 UI/UX 디자인 경험이 있습니다.',
       ROUND1_APPLYING,
       8, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655'),

      (8, 'Design',
       'https://dribbble.com/sbhan',
       '브랜딩과 인터랙션 디자인 프로젝트를 진행했습니다.',
       ROUND1_APPLYING,
       9, 1, '2025-11-02 22:40:50.709655', '2025-11-02 22:40:50.709655');

SELECT * FROM MEMBER;

SELECT * FROM ROLE ;

SELECT * FROM MEMBER_ROLE ;

SELECT * FROM TEAM  ;

