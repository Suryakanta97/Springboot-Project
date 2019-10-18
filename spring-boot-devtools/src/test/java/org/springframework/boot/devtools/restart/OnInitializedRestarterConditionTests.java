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

package org.springframework.boot.devtools.restart;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link OnInitializedRestarterCondition}.
 *
 * @author Phillip Webb
 */
public class OnInitializedRestarterConditionTests {

    private static Object wait = new Object();

    @Before
    @After
    public void cleanup() {
        Restarter.clearInstance();
    }

    @Test
    public void noInstance() {
        Restarter.clearInstance();
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(
                Config.class);
        assertThat(context.containsBean("bean")).isFalse();
        context.close();
    }

    @Test
    public void noInitialization() {
        Restarter.initialize(new String[0], false, RestartInitializer.NONE);
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(
                Config.class);
        assertThat(context.containsBean("bean")).isFalse();
        context.close();
    }

    @Test
    public void initialized() throws Exception {
        Thread thread = new Thread(TestInitialized::main);
        thread.start();
        synchronized (wait) {
            wait.wait();
        }
    }

    public static class TestInitialized {

        public static void main(String... args) {
            RestartInitializer initializer = mock(RestartInitializer.class);
            given(initializer.getInitialUrls(any(Thread.class))).willReturn(new URL[0]);
            Restarter.initialize(new String[0], false, initializer);
            ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(
                    Config.class);
            assertThat(context.containsBean("bean")).isTrue();
            context.close();
            synchronized (wait) {
                wait.notify();
            }
        }

    }

    @Configuration
    public static class Config {

        @Bean
        @ConditionalOnInitializedRestarter
        public String bean() {
            return "bean";
        }

    }

}