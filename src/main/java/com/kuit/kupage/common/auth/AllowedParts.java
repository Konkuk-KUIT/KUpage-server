package com.kuit.kupage.common.auth;

import com.kuit.kupage.domain.teamMatch.Part;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedParts {
    Part[] value(); // 허용할 파트 목록
}
