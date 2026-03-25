package com.anime.shop.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ValidatorUtil {
    // 非静态变量，由 Spring 管理
    private final Validator validator;

    // 构造方法初始化校验器（替代 @PostConstruct）
    public ValidatorUtil() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * 按分组校验对象
     * @param obj 待校验对象
     * @param groups 校验分组
     * @param <T> 泛型
     * @throws RuntimeException 校验失败抛出异常
     */
    public <T> void validate(T obj, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj, groups);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getMessage()).append(";");
            }
            throw new RuntimeException(sb.toString());
        }
    }
}
