package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@JsonPropertyOrder({
//        "id",
        // outros campos comuns...
        "createdBy", "lastModifiedBy", "createdAt", "updatedAt"
})
public @interface OrderedResponse {
    String[] value() default {};
}