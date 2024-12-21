package com.doosan.msa.common.util;

import com.doosan.msa.common.entity.EmailDomainValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailDomainValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmailDomain {
    String message() default "허용되지 않은 이메일 도메인입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
