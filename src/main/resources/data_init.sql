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
VALUES (1,NULL, '부원', NULL, 0, NOW(), NOW()),
       (2, NULL, '운영진', NULL, 0, NOW(), NOW()),
       (3, NULL, '튜터', NULL, 0, NOW(), NOW());

INSERT INTO member_role (member_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 1),
       (5, 2),
       (6, 3),
       (7, 1),
       (8, 2),
       (9, 3),
       (10, 3);


INSERT INTO team
(service_name, app_type, topic_summary, image_url, service_intro_file, feature_requirements, preferred_developer, created_at, modified_at)
VALUES
    ('글로방', 'Web',
     '외국인 대상 부동산 매칭 서비스',
     'https://cdn.kupage.com/team1-thumbnail.jpg',
     'https://cdn.kupage.com/team1-thumbnail.pdf',
     '지도 기반의 실시간 매물 탐색, 채팅 문의, 언어별 번역 기능 제공 / 다국어 지원, 실시간 매물 등록, 중개인 매칭 기능',
     '안녕하세요, 저희는 외국인 거주 문제를 해결하고자 합니다. 지역별 법률 차이로 인한 중개 절차 복잡성이었습니다.',
     NOW(), NOW()),

    ('쿠페이지(Kupage)', 'Web',
     '대학생 프로젝트 포트폴리오 공유 플랫폼',
     'https://cdn.kupage.com/team4-thumbnail.jpg',
     'https://cdn.kupage.com/team4-thumbnail.pdf',
     '팀 매칭, 프로젝트 홍보, 지원서 관리 기능 제공 / 팀-지원자 간 매칭, 파일 업로드, 상세 페이지 구현',
     '실무 감각을 기르고 싶은 대학생들을 위한 플랫폼입니다. 다양한 전공자 간 커뮤니케이션 조율이었습니다.',
     NOW(), NOW()),

    ('디어캠퍼스', 'Web',
     '대학 캠퍼스 기반 커뮤니티 플랫폼',
     'https://cdn.kupage.com/team5-thumbnail.jpg',
     'https://cdn.kupage.com/team5-thumbnail.pdf',
     '위치 기반 동아리 홍보, 실시간 소통 게시판 제공 / 실시간 알림, 이미지 업로드, 권한 기반 접근 제어',
     '대학 생활을 조금 더 즐겁게 만들고 싶습니다. 서비스 첫 게시물이 올라왔던 날입니다.',
     NOW(), NOW()),

    ('그린체인(GreenChain)', 'Web',
     '친환경 소비 유도 앱 서비스',
     'https://cdn.kupage.com/team6-thumbnail.jpg',
     'https://cdn.kupage.com/team6-thumbnail.pdf',
     '탄소 절감 포인트 시스템, 마켓 연동, 랭킹 표시 / API 통신, 포인트 계산 로직, 사용자 인증',
     '환경 보호를 재밌게 만들어보자는 목표로 시작했습니다. Spring Boot, React, AWS, MySQL을 사용했습니다.',
     NOW(), NOW()),

    ('핏업(FitUp)', 'Web',
     'AI 기반 개인 맞춤 운동 루틴 추천 서비스',
     'https://cdn.kupage.com/team7-thumbnail.jpg',
     'https://cdn.kupage.com/team7-thumbnail.pdf',
     'AI 운동 추천, 실시간 자세 분석, 통계 리포트 제공 / TensorFlow 모델, 영상 인식, BLE 기기 연동',
     '운동의 재미를 데이터로 증명하고 싶습니다. 데이터 기반으로 개인화가 가능한 점입니다.',
     NOW(), NOW()),

    ('페이온(PayOn)', 'Web',
     'QR 기반 간편 결제 시스템',
     'https://cdn.kupage.com/team8-thumbnail.jpg',
     'https://cdn.kupage.com/team8-thumbnail.pdf',
     'QR 결제, 정산 관리, 통계 리포트 제공 / 결제 보안, 세션 인증, DB 트랜잭션 관리',
     '모든 사용자가 빠르고 안전하게 결제하길 바랍니다. PM 1명, BE 2명, FE 2명, DESIGN 1명입니다.',
     NOW(), NOW()),

    ('스테이링크(StayLink)', 'Web',
     '숙박 예약 및 후기 통합 플랫폼',
     'https://cdn.kupage.com/team9-thumbnail.jpg',
     'https://cdn.kupage.com/team9-thumbnail.pdf',
     '숙소 검색, 후기 작성, 예약 관리 기능 제공 / 검색 필터링, 사용자 인증, 결제 연동',
     '여행의 경험을 하나로 잇는 플랫폼을 만들고 싶습니다. 매일 stand-up 미팅으로 진행 상황을 공유합니다.',
     NOW(), NOW()),

    ('코드메이트(CodeMate)', 'Web',
     '프로그래밍 학습 및 코드 리뷰 플랫폼',
     'https://cdn.kupage.com/team10-thumbnail.jpg',
     'https://cdn.kupage.com/team10-thumbnail.pdf',
     '실시간 코드 리뷰, 문제 풀이, 개인 학습 기록 제공 / 코드 실행 API, 리뷰 시스템, 알림 기능',
     '개발자 성장의 든든한 파트너가 되고 싶습니다. 코드는 협업의 언어라고 생각합니다.',
     NOW(), NOW()),

    ('푸디잇(FoodEat)', 'Web',
     'AI 기반 개인 맞춤 식단 추천 서비스',
     'https://cdn.kupage.com/team11-thumbnail.jpg',
     'https://cdn.kupage.com/team11-thumbnail.pdf',
     '사용자 알레르기 및 식습관 기반 맞춤 식단 추천, 음식 이미지 인식 기능 / 음식 데이터 크롤링, AI 추천 알고리즘, 사용자 피드백 반영 기능',
     '건강한 식습관을 더 쉽게 만들기 위해 시작했습니다. 식단 데이터를 구조화하는 과정이었습니다.',
     NOW(), NOW()),

    ('에듀링크(EduLink)', 'Web',
     '온라인 학습 관리 및 멘토링 플랫폼',
     'https://cdn.kupage.com/team12-thumbnail.jpg',
     'https://cdn.kupage.com/team12-thumbnail.pdf',
     '학생-멘토 매칭, 학습 진행 현황 대시보드, 실시간 피드백 기능 / 멘토링 세션 예약, 화상 채팅 API, 학습 진도 관리 기능',
     '학습의 지속 가능성을 높이기 위한 교육 서비스를 만들고 싶습니다. 지속적인 성장과 맞춤형 학습입니다.',
     NOW(), NOW());

SELECT * FROM MEMBER;

SELECT * FROM ROLE ;

SELECT * FROM MEMBER_ROLE ;

SELECT * FROM TEAM  ;

