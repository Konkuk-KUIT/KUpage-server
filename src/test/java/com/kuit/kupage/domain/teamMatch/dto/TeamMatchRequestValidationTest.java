package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.teamMatch.Part;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TeamMatchRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("지원 파트가 enum에 없는 값이면 검증 실패")
    void invalidEnumValue_shouldFail() {
        TeamMatchRequest request = new TeamMatchRequest(
                null,
                "안드로이드 개발에 참여하고 싶습니다.",
                "https://portfolio.com/seoyeon"
        );

        Set<ConstraintViolation<TeamMatchRequest>> violations = validator.validate(request);

        assertThat(violations).extracting("message")
                .contains("지원 파트는 필수 입력 항목입니다.");
    }

    @Test
    @DisplayName("지원 파트 필드에 null이 들어가면 검증 실패")
    void nullFields_shouldFail() {
        TeamMatchRequest request = new TeamMatchRequest(
                null,
                null,
                "https://portfolio.com/test"
        );

        Set<ConstraintViolation<TeamMatchRequest>> violations = validator.validate(request);

        assertThat(violations).extracting("message").contains(
                "지원 파트는 필수 입력 항목입니다."
        );
    }

    @Test
    @DisplayName("글자수가 초과된 경우 검증 실패")
    void exceededFieldLength_shouldFail() {
        String longMotivation = "M".repeat(2000);
        String longPortfolioUrl = "https://".concat("a".repeat(600));

        TeamMatchRequest request = new TeamMatchRequest(
                Part.ANDROID,
                longMotivation,
                longPortfolioUrl
        );

        Set<ConstraintViolation<TeamMatchRequest>> violations = validator.validate(request);

        assertThat(violations).extracting("message").contains(
                "지원 동기는 최대 1000자까지 입력 가능합니다.",
                "포트폴리오 URL은 최대 500자까지 입력 가능합니다."
        );
    }

    @Test
    @DisplayName("URL 형식이 아닌 경우 검증 실패")
    void invalidUrlFormat_shouldFail() {
        TeamMatchRequest request = new TeamMatchRequest(
                Part.ANDROID,
                "팀의 안드로이드 개발을 담당하고 싶습니다.",
                "not-a-valid-url"
        );

        Set<ConstraintViolation<TeamMatchRequest>> violations = validator.validate(request);

        assertThat(violations).extracting("message")
                .contains("유효한 URL 형식이어야 합니다.");
    }
}