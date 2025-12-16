-- =========================================================
-- V1 Init Schema
-- (ordered to satisfy foreign-key dependencies)
-- =========================================================

-- ----------------------------
-- Base tables (no FK deps)
-- ----------------------------
CREATE TABLE detail (
                        detail_id bigint NOT NULL AUTO_INCREMENT,
                        birthday date DEFAULT NULL,
                        depart_name varchar(255) DEFAULT NULL,
                        email varchar(255) DEFAULT NULL,
                        github_id varchar(255) DEFAULT NULL,
                        grade enum('EXTENDED_SEMESTER','FIFTH_YEAR','FIRST_YEAR','FOURTH_YEAR','GRADUATED','GRADUATION_DEFERRED','LEAVE_OF_ABSENCE','SECOND_YEAR','SIXTH_YEAR','THIRD_YEAR') DEFAULT NULL,
                        name varchar(255) DEFAULT NULL,
                        phone_number varchar(255) DEFAULT NULL,
                        student_number varchar(255) DEFAULT NULL,
                        PRIMARY KEY (detail_id)
);


CREATE TABLE project (
                         project_id bigint NOT NULL AUTO_INCREMENT,
                         created_at datetime(6) DEFAULT NULL,
                         modified_at datetime(6) DEFAULT NULL,
                         app_fields varchar(255) DEFAULT NULL,
                         app_type enum('Web','Android','iOS','PC_프로그램','크로스플랫','하이브리드앱','Web_App','Native_App') DEFAULT NULL,
                         batch enum('FIRST','SECOND','THIRD','FOURTH','FIFTH','SIXTH','ALL','UNKNOWN') DEFAULT NULL,
                         description varchar(255) DEFAULT NULL,
                         detail_file_url varchar(1024) DEFAULT NULL,
                         detail_url varchar(1024) DEFAULT NULL,
                         main_image_path varchar(255) DEFAULT NULL,
                         name varchar(255) DEFAULT NULL,
                         service_links varchar(255) DEFAULT NULL,
                         summary varchar(255) DEFAULT NULL,
                         tech_stacks varchar(255) DEFAULT NULL,
                         PRIMARY KEY (project_id)
);

CREATE TABLE roles (
                       role_id bigint NOT NULL AUTO_INCREMENT,
                       created_at datetime(6) DEFAULT NULL,
                       modified_at datetime(6) DEFAULT NULL,
                       batch enum('ALL','FIFTH','FIRST','FOURTH','SECOND','SIXTH','THIRD','UNKNOWN') DEFAULT NULL,
                       discord_role_id varchar(255) DEFAULT NULL,
                       name varchar(255) DEFAULT NULL,
                       position int DEFAULT NULL,
                       PRIMARY KEY (role_id)
);

CREATE TABLE tag (
                     tag_id bigint NOT NULL AUTO_INCREMENT,
                     created_at datetime(6) DEFAULT NULL,
                     modified_at datetime(6) DEFAULT NULL,
                     name varchar(255) DEFAULT NULL,
                     PRIMARY KEY (tag_id)
);

CREATE TABLE team (
                      team_id bigint NOT NULL AUTO_INCREMENT,
                      created_at datetime(6) DEFAULT NULL,
                      modified_at datetime(6) DEFAULT NULL,
                      app_type enum('Android','Native_App','PC_프로그램','Web','Web_App','iOS','크로스플랫','하이브리드앱') DEFAULT NULL,
                      batch enum('ALL','FIFTH','FIRST','FOURTH','SECOND','SIXTH','THIRD','UNKNOWN') NOT NULL,
                      feature_requirements varchar(1000) DEFAULT NULL,
                      image_url varchar(1024) DEFAULT NULL,
                      owner_id bigint DEFAULT NULL,
                      owner_name varchar(255) DEFAULT NULL,
                      preferred_developer varchar(1000) DEFAULT NULL,
                      service_intro_file varchar(500) DEFAULT NULL,
                      service_name varchar(100) NOT NULL,
                      topic_summary varchar(500) DEFAULT NULL,
                      PRIMARY KEY (team_id)
);

-- ----------------------------
-- Dependent tables
-- ----------------------------

CREATE TABLE member (
                        member_id bigint NOT NULL AUTO_INCREMENT,
                        created_at datetime(6) DEFAULT NULL,
                        modified_at datetime(6) DEFAULT NULL,
                        access_token varchar(255),
                        refresh_token varchar(255),
                        discord_id varchar(255) NOT NULL,
                        discord_login_id varchar(255) NOT NULL,
                        discord_access_token varchar(255),
                        discord_expires_in bigint DEFAULT NULL,
                        discord_refresh_token varchar(255),
                        name varchar(255) NOT NULL,
                        profile_image varchar(255),
                        detail_id bigint DEFAULT NULL,
                        CONSTRAINT pk_member PRIMARY KEY (member_id),
                        CONSTRAINT uk_member_discord_id UNIQUE (discord_id),
                        CONSTRAINT uk_member_discord_login_id UNIQUE (discord_login_id),
                        CONSTRAINT uk_member_detail_id UNIQUE (detail_id),
                        CONSTRAINT fk_member_detail FOREIGN KEY (detail_id) REFERENCES detail (detail_id)
);

CREATE TABLE article (
                         article_id bigint NOT NULL AUTO_INCREMENT,
                         created_at datetime(6) DEFAULT NULL,
                         modified_at datetime(6) DEFAULT NULL,
                         thumbnail_image_path varchar(1024) DEFAULT NULL,
                         title varchar(255) DEFAULT NULL,
                         member_member_id bigint DEFAULT NULL,
                         PRIMARY KEY (article_id),
                         KEY idx_article_member_member_id (member_member_id),
                         CONSTRAINT fk_article_member FOREIGN KEY (member_member_id) REFERENCES member (member_id)
);



CREATE TABLE block (
                       block_id bigint NOT NULL AUTO_INCREMENT,
                       created_at datetime(6) DEFAULT NULL,
                       modified_at datetime(6) DEFAULT NULL,
                       position int DEFAULT NULL,
                       properties varchar(255) DEFAULT NULL,
                       type enum('CODE','FILE','IMAGE','SUB_TITLE','TEXT','URL') DEFAULT NULL,
                       article_id bigint DEFAULT NULL,
                       PRIMARY KEY (block_id),
                       KEY idx_block_article_id (article_id),
                       CONSTRAINT fk_block_article FOREIGN KEY (article_id) REFERENCES article (article_id)
);


CREATE TABLE category (
                          category_id bigint NOT NULL AUTO_INCREMENT,
                          created_at datetime(6) DEFAULT NULL,
                          modified_at datetime(6) DEFAULT NULL,
                          name varchar(255) DEFAULT NULL,
                          project_id bigint DEFAULT NULL,
                          PRIMARY KEY (category_id),
                          KEY idx_category_project_id (project_id),
                          CONSTRAINT fk_category_project FOREIGN KEY (project_id) REFERENCES project (project_id)
);


CREATE TABLE review (
                        review_id bigint NOT NULL AUTO_INCREMENT,
                        created_at datetime(6) DEFAULT NULL,
                        modified_at datetime(6) DEFAULT NULL,
                        member_desc varchar(255) DEFAULT NULL,
                        review varchar(255) DEFAULT NULL,
                        project_id bigint DEFAULT NULL,
                        PRIMARY KEY (review_id),
                        KEY idx_review_project_id (project_id),
                        CONSTRAINT fk_review_project FOREIGN KEY (project_id) REFERENCES project (project_id)
);

-- ----------------------------
-- Mapping / join tables
-- ----------------------------

CREATE TABLE member_role (
                             id bigint NOT NULL AUTO_INCREMENT,
                             member_discord_id varchar(50),
                             member_id bigint DEFAULT NULL,
                             role_id bigint DEFAULT NULL,
                             CONSTRAINT pk_member_role PRIMARY KEY (id),
                             CONSTRAINT uk_member_role_discord_id_role_id UNIQUE (member_discord_id, role_id),
                             CONSTRAINT fk_member_role_member FOREIGN KEY (member_id) REFERENCES member (member_id),
                             CONSTRAINT fk_member_role_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE TABLE member_project (
                                member_project_id bigint NOT NULL AUTO_INCREMENT,
                                created_at datetime(6) DEFAULT NULL,
                                modified_at datetime(6) DEFAULT NULL,
                                member_id bigint DEFAULT NULL,
                                project_id bigint DEFAULT NULL,
                                PRIMARY KEY (member_project_id),
                                KEY idx_member_project_member_id (member_id),
                                KEY idx_member_project_project_id (project_id),
                                CONSTRAINT fk_member_project_project FOREIGN KEY (project_id) REFERENCES project (project_id),
                                CONSTRAINT fk_member_project_member FOREIGN KEY (member_id) REFERENCES member (member_id)
);


CREATE TABLE member_team (
                             id bigint NOT NULL AUTO_INCREMENT,
                             created_at datetime(6) DEFAULT NULL,
                             modified_at datetime(6) DEFAULT NULL,
                             member_id bigint DEFAULT NULL,
                             team_id bigint DEFAULT NULL,
                             PRIMARY KEY (id),
                             KEY idx_member_team_member_id (member_id),
                             KEY idx_member_team_team_id (team_id),
                             CONSTRAINT fk_member_team_team FOREIGN KEY (team_id) REFERENCES team (team_id),
                             CONSTRAINT fk_member_team_member FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE team_applicant (
                                team_applicant_id bigint NOT NULL AUTO_INCREMENT,
                                created_at datetime(6) DEFAULT NULL,
                                modified_at datetime(6) DEFAULT NULL,
                                applied_part enum('Android','Design','PM','Server','Web','iOS') NOT NULL,
                                batch enum('ALL','FIFTH','FIRST','FOURTH','SECOND','SIXTH','THIRD','UNKNOWN') NOT NULL,
                                motivation varchar(1000) DEFAULT NULL,
                                portfolio_url varchar(1024) DEFAULT NULL,
                                slot_no INT NOT NULL,
                                status enum('FINAL_CONFIRMED','ROUND1_APPLYING','ROUND1_FAILED','ROUND2_APPLYING','ROUND2_FAILED') NOT NULL,
                                member_id bigint DEFAULT NULL,
                                team_id bigint DEFAULT NULL,
                                PRIMARY KEY (team_applicant_id),
                                UNIQUE KEY uk_team_applicant_status_member_team (status,member_id,team_id),
                                UNIQUE KEY uk_team_applicant_member_slot_batch (member_id,slot_no,batch),
                                KEY idx_team_applicant_team_id (team_id),
                                CONSTRAINT fk_team_applicant_member FOREIGN KEY (member_id) REFERENCES member (member_id),
                                CONSTRAINT fk_team_applicant_team FOREIGN KEY (team_id) REFERENCES team (team_id),
                                CONSTRAINT chk_team_applicant_slot_no CHECK ((slot_no in (1,2)))
);

CREATE TABLE article_tag (
                             article_tag_id bigint NOT NULL AUTO_INCREMENT,
                             created_at datetime(6) DEFAULT NULL,
                             modified_at datetime(6) DEFAULT NULL,
                             article_id bigint DEFAULT NULL,
                             tag_id bigint DEFAULT NULL,
                             PRIMARY KEY (article_tag_id),
                             KEY idx_article_tag_article_id (article_id),
                             KEY idx_article_tag_tag_id (tag_id),
                             CONSTRAINT fk_article_tag_article FOREIGN KEY (article_id) REFERENCES article (article_id),
                             CONSTRAINT fk_article_tag_tag FOREIGN KEY (tag_id) REFERENCES tag (tag_id)
);
