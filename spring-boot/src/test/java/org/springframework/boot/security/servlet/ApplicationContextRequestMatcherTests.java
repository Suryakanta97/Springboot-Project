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

package org.springframework.boot.security.servlet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ApplicationContextRequestMatcher}.
 *
 * @author Phillip Webb
 */
public class ApplicationContextRequestMatcherTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createWhenContextClassIsNullShouldThrowException() {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Context class must not be null");
        new TestApplicationContextRequestMatcher<>(null);
    }

    @Test
    public void matchesWhenContextClassIsApplicationContextShouldProvideContext() {
        StaticWebApplicationContext context = createWebApplicationContext();
        assertThat(new TestApplicationContextRequestMatcher<>(ApplicationContext.class)
                .callMatchesAndReturnProvidedContext(context)).isEqualTo(context);
    }

    @Test
    public void matchesWhenContextClassIsExistingBeanShouldProvideBean() {
        StaticWebApplicationContext context = createWebApplicationContext();
        context.registerSingleton("existingBean", ExistingBean.class);
        assertThat(new TestApplicationContextRequestMatcher<>(ExistingBean.class)
                .callMatchesAndReturnProvidedContext(context))
                .isEqualTo(context.getBean(ExistingBean.class));
    }

    @Test
    public void matchesWhenContextClassIsNewBeanShouldProvideBean() {
        StaticWebApplicationContext context = createWebApplicationContext();
        context.registerSingleton("existingBean", ExistingBean.class);
        assertThat(new TestApplicationContextRequestMatcher<>(NewBean.class)
                .callMatchesAndReturnProvidedContext(context).getBean())
                .isEqualTo(context.getBean(ExistingBean.class));
    }

    private StaticWebApplicationContext createWebApplicationContext() {
        StaticWebApplicationContext context = new StaticWebApplicationContext();
        MockServletContext servletContext = new MockServletContext();
        context.setServletContext(servletContext);
        servletContext.setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        return context;
    }

    static class ExistingBean {

    }

    static class NewBean {

        private final ExistingBean bean;

        NewBean(ExistingBean bean) {
            this.bean = bean;
        }

        public ExistingBean getBean() {
            return this.bean;
        }

    }

    static class TestApplicationContextRequestMatcher<C>
            extends ApplicationContextRequestMatcher<C> {

        private C providedContext;

        TestApplicationContextRequestMatcher(Class<? extends C> context) {
            super(context);
        }

        public C callMatchesAndReturnProvidedContext(WebApplicationContext context) {
            return callMatchesAndReturnProvidedContext(
                    new MockHttpServletRequest(context.getServletContext()));
        }

        public C callMatchesAndReturnProvidedContext(HttpServletRequest request) {
            matches(request);
            return getProvidedContext();
        }

        @Override
        protected boolean matches(HttpServletRequest request, C context) {
            this.providedContext = context;
            return false;
        }

        public C getProvidedContext() {
            return this.providedContext;
        }

    }

}
