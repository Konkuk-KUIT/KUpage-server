package com.kuit.kupage.unit.teamMatch.dto;

import com.kuit.kupage.domain.project.domain.AppType;
import com.kuit.kupage.domain.teamMatch.dto.IdeaRegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IdeaRegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void set_up() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private IdeaRegisterRequest create_valid_request() {
        return new IdeaRegisterRequest(
                "서비스 이름",
                AppType.Android,
                "토픽 요약",
                "https://image-url.com",
                "https://intro-file-url.com",
                "주요 기능",
                "이런 개발자분이 오시면 좋겠습니다"
        );
    }

    @DisplayName("serviceName이 비어있으면 검증에 실패한다")
    @Test
    void validate_should_fail_when_service_name_is_blank() {
        // given
        IdeaRegisterRequest request = new IdeaRegisterRequest(
                "   ",                 // invalid
                AppType.Android,
                "토픽 요약",
                "https://image-url.com",
                "https://intro-file-url.com",
                "주요 기능",
                "이런 개발자분이 오시면 좋겠습니다"
        );

        // when
        Set<ConstraintViolation<IdeaRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("serviceName"));
    }

    @DisplayName("serviceName이 100자를 초과하면 검증에 실패한다")
    @Test
    void validate_should_fail_when_service_name_exceeds_max_length() {
        // given
        String longName = "a".repeat(101);
        IdeaRegisterRequest request = new IdeaRegisterRequest(
                longName,
                AppType.Android,
                "토픽 요약",
                "https://image-url.com",
                "https://intro-file-url.com",
                "주요 기능",
                "이런 개발자분이 오시면 좋겠습니다"
        );

        // when
        Set<ConstraintViolation<IdeaRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("serviceName"));
    }

    @DisplayName("모든 값이 유효하면 검증을 통과한다")
    @Test
    void validate_should_pass_when_all_fields_are_valid() {
        // given
        IdeaRegisterRequest request = create_valid_request();

        // when
        Set<ConstraintViolation<IdeaRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }
}