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

package org.springframework.boot.actuate.web.trace.reactive;

import org.junit.Test;
import org.springframework.boot.actuate.web.trace.HttpExchangeTracer;
import org.springframework.boot.actuate.web.trace.HttpTraceRepository;
import org.springframework.boot.actuate.web.trace.InMemoryHttpTraceRepository;
import org.springframework.boot.actuate.web.trace.Include;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Mono;

import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link HttpTraceWebFilter}.
 *
 * @author Andy Wilkinson
 */
public class HttpTraceWebFilterIntegrationTests {

    @Test
    public void traceForNotFoundResponseHas404Status() {
        ReactiveWebApplicationContextRunner runner = new ReactiveWebApplicationContextRunner()
                .withUserConfiguration(Config.class);
        runner.run((context) -> {
            WebTestClient.bindToApplicationContext(context).build().get().uri("/")
                    .exchange().expectStatus().isNotFound();
            HttpTraceRepository repository = context.getBean(HttpTraceRepository.class);
            assertThat(repository.findAll()).hasSize(1);
            assertThat(repository.findAll().get(0).getResponse().getStatus())
                    .isEqualTo(404);
        });
    }

    @Test
    public void traceForMonoErrorWithRuntimeExceptionHas500Status() {
        ReactiveWebApplicationContextRunner runner = new ReactiveWebApplicationContextRunner()
                .withUserConfiguration(Config.class);
        runner.run((context) -> {
            WebTestClient.bindToApplicationContext(context).build().get()
                    .uri("/mono-error").exchange().expectStatus()
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            HttpTraceRepository repository = context.getBean(HttpTraceRepository.class);
            assertThat(repository.findAll()).hasSize(1);
            assertThat(repository.findAll().get(0).getResponse().getStatus())
                    .isEqualTo(500);
        });
    }

    @Test
    public void traceForThrownRuntimeExceptionHas500Status() {
        ReactiveWebApplicationContextRunner runner = new ReactiveWebApplicationContextRunner()
                .withUserConfiguration(Config.class);
        runner.run((context) -> {
            WebTestClient.bindToApplicationContext(context).build().get().uri("/thrown")
                    .exchange().expectStatus()
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            HttpTraceRepository repository = context.getBean(HttpTraceRepository.class);
            assertThat(repository.findAll()).hasSize(1);
            assertThat(repository.findAll().get(0).getResponse().getStatus())
                    .isEqualTo(500);
        });
    }

    @Configuration
    @EnableWebFlux
    static class Config {

        @Bean
        public HttpTraceWebFilter httpTraceWebFilter(HttpTraceRepository repository) {
            Set<Include> includes = EnumSet.allOf(Include.class);
            return new HttpTraceWebFilter(repository, new HttpExchangeTracer(includes),
                    includes);
        }

        @Bean
        public HttpTraceRepository httpTraceRepository() {
            return new InMemoryHttpTraceRepository();
        }

        @Bean
        public HttpHandler httpHandler(ApplicationContext applicationContext) {
            return WebHttpHandlerBuilder.applicationContext(applicationContext).build();
        }

        @Bean
        public RouterFunction<ServerResponse> router() {
            return RouterFunctions
                    .route(RequestPredicates.GET("/mono-error"),
                            (request) -> Mono.error(new RuntimeException()))
                    .andRoute(RequestPredicates.GET("/thrown"),
                            (HandlerFunction<ServerResponse>) (request) -> {
                                throw new RuntimeException();
                            });
        }

    }

}
