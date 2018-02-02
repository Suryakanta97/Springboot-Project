/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.context.properties;

import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Validator that supports configuration classes annotated with
 * {@link Validated @Validated}.
 *
 * @author Phillip Webb
 */
class Jsr303ConfigurationPropertiesValidator implements Validator {

    private final Delegate delegate;

    Jsr303ConfigurationPropertiesValidator(ApplicationContext applicationContext) {
        this.delegate = new Delegate(applicationContext);
    }

    @Override
    public boolean supports(Class<?> type) {
        return AnnotatedElementUtils.hasAnnotation(type, Validated.class)
                && this.delegate.supports(type);
    }

    @Override
    public void validate(Object target, Errors errors) {
        this.delegate.validate(target, errors);
    }

    private static class Delegate extends LocalValidatorFactoryBean {

        Delegate(ApplicationContext applicationContext) {
            setApplicationContext(applicationContext);
            setMessageInterpolator(new MessageInterpolatorFactory().getObject());
            afterPropertiesSet();
        }

    }

}
