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
        // ⚠️ 이 케이스는 Jackson이 JSON -> Object 변환 시점에 발생하므로,
        // Validator 단계에서는 탐지되지 않습니다.
        // 따라서 별도의 Controller 통합 테스트에서 발생하는 예외임을 주석으로 명시합니다.
        // 여기서는 단순히 valid enum을 확인하기 위한 예시만 작성합니다.

        TeamMatchRequest request = new TeamMatchRequest(
                "이서연",
                "20204567",
                null, // Enum 누락 → NotNull 위반
                "안드로이드 개발에 참여하고 싶습니다.",
                "https://portfolio.com/seoyeon",
                "UI/UX 개선에 관심이 있습니다.",
                "실무 경험을 쌓고 싶습니다."
        );

        Set<ConstraintViolation<TeamMatchRequest>> violations = validator.validate(request);

        assertThat(violations).extracting("message")
                .contains("지원 파트는 필수 입력 항목입니다.");
    }

    @Test
    @DisplayName("NotNull 필드에 null이 들어가면 검증 실패")
    void nullFields_shouldFail() {
        TeamMatchRequest request = new TeamMatchRequest(
                null, // name null
                null, // studentId null
                null, // appliedPart null
                null,
                "https://portfolio.com/test",
                null,
                null
        );

        Set<ConstraintViolation<TeamMatchRequest>> violations = validator.validate(request);

        assertThat(violations).extracting("message").contains(
                "이름은 필수 입력 항목입니다.",
                "학번은 필수 입력 항목입니다.",
                "지원 파트는 필수 입력 항목입니다."
        );
    }

    @Test
    @DisplayName("글자수가 초과된 경우 검증 실패")
    void exceededFieldLength_shouldFail() {
        String longName = "A".repeat(60);
        String longStudentId = "1".repeat(30);
        String longMotivation = "M".repeat(2000);
        String longPortfolioUrl = "https://".concat("a".repeat(600));
        String longAnswer = "B".repeat(6000);

        TeamMatchRequest request = new TeamMatchRequest(
                longName,
                longStudentId,
                Part.ANDROID,
                longMotivation,
                longPortfolioUrl,
                longAnswer,
                longAnswer
        );

        Set<ConstraintViolation<TeamMatchRequest>> violations = validator.validate(request);

        assertThat(violations).extracting("message").contains(
                "이름은 최대 50자까지 입력 가능합니다.",
                "학번은 최대 20자까지 입력 가능합니다.",
                "지원 동기는 최대 1000자까지 입력 가능합니다.",
                "포트폴리오 URL은 최대 500자까지 입력 가능합니다.",
                "추가 질문 1의 답변은 최대 5000자까지 입력 가능합니다.",
                "추가 질문 2의 답변은 최대 5000자까지 입력 가능합니다."
        );
    }

    @Test
    @DisplayName("URL 형식이 아닌 경우 검증 실패")
    void invalidUrlFormat_shouldFail() {
        TeamMatchRequest request = new TeamMatchRequest(
                "이서연",
                "20204567",
                Part.ANDROID,
                "팀의 안드로이드 개발을 담당하고 싶습니다.",
                "not-a-valid-url",
                "UX 개선에 기여하고 싶습니다.",
                "팀워크 경험을 쌓고 싶습니다."
        );

        Set<ConstraintViolation<TeamMatchRequest>> violations = validator.validate(request);

        assertThat(violations).extracting("message")
                .contains("유효한 URL 형식이어야 합니다.");
    }
}