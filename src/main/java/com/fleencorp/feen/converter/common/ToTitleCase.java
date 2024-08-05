package com.fleencorp.feen.converter.common;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fleencorp.feen.converter.impl.common.ToTitleCaseConverter;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(converter = ToTitleCaseConverter.class)
@JsonDeserialize(converter = ToTitleCaseConverter.class)
public @interface ToTitleCase {
}
