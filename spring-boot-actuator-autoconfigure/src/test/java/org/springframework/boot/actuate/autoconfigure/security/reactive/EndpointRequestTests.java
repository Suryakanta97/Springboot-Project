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

package org.springframework.boot.actuate.autoconfigure.security.reactive;

import org.assertj.core.api.AssertDelegateTarget;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.Operation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoint;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link EndpointRequest}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
public class EndpointRequestTests {

    @Test
    public void toAnyEndpointShouldMatchEndpointPath() {
        ServerWebExchangeMatcher matcher = EndpointRequest.toAnyEndpoint();
        assertMatcher(matcher).matches("/actuator/foo");
        assertMatcher(matcher).matches("/actuator/bar");
    }

    @Test
    public void toAnyEndpointShouldNotMatchOtherPath() {
        ServerWebExchangeMatcher matcher = EndpointRequest.toAnyEndpoint();
        assertMatcher(matcher).doesNotMatch("/actuator/baz");
    }

    @Test
    public void toEndpointClassShouldMatchEndpointPath() {
        ServerWebExchangeMatcher matcher = EndpointRequest.to(FooEndpoint.class);
        assertMatcher(matcher).matches("/actuator/foo");
    }

    @Test
    public void toEndpointClassShouldNotMatchOtherPath() {
        ServerWebExchangeMatcher matcher = EndpointRequest.to(FooEndpoint.class);
        assertMatcher(matcher).doesNotMatch("/actuator/bar");
    }

    @Test
    public void toEndpointIdShouldMatchEndpointPath() {
        ServerWebExchangeMatcher matcher = EndpointRequest.to("foo");
        assertMatcher(matcher).matches("/actuator/foo");
    }

    @Test
    public void toEndpointIdShouldNotMatchOtherPath() {
        ServerWebExchangeMatcher matcher = EndpointRequest.to("foo");
        assertMatcher(matcher).doesNotMatch("/actuator/bar");
    }

    @Test
    public void excludeByClassShouldNotMatchExcluded() {
        ServerWebExchangeMatcher matcher = EndpointRequest.toAnyEndpoint()
                .excluding(FooEndpoint.class);
        assertMatcher(matcher).doesNotMatch("/actuator/foo");
        assertMatcher(matcher).matches("/actuator/bar");
    }

    @Test
    public void excludeByIdShouldNotMatchExcluded() {
        ServerWebExchangeMatcher matcher = EndpointRequest.toAnyEndpoint()
                .excluding("foo");
        assertMatcher(matcher).doesNotMatch("/actuator/foo");
        assertMatcher(matcher).matches("/actuator/bar");
    }

    private RequestMatcherAssert assertMatcher(ServerWebExchangeMatcher matcher) {
        return assertMatcher(matcher, mockPathMappedEndpoints());
    }

    private PathMappedEndpoints mockPathMappedEndpoints() {
        List<ExposableEndpoint<?>> endpoints = new ArrayList<>();
        endpoints.add(mockEndpoint("foo", "foo"));
        endpoints.add(mockEndpoint("bar", "bar"));
        return new PathMappedEndpoints("/actuator", () -> endpoints);
    }

    private TestEndpoint mockEndpoint(String id, String rootPath) {
        TestEndpoint endpoint = mock(TestEndpoint.class);
        given(endpoint.getId()).willReturn(id);
        given(endpoint.getRootPath()).willReturn(rootPath);
        return endpoint;
    }

    private RequestMatcherAssert assertMatcher(ServerWebExchangeMatcher matcher,
                                               PathMappedEndpoints pathMappedEndpoints) {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerBean(PathMappedEndpoints.class, () -> pathMappedEndpoints);
        return assertThat(new RequestMatcherAssert(context, matcher));
    }

    interface TestEndpoint extends ExposableEndpoint<Operation>, PathMappedEndpoint {

    }

    private static class RequestMatcherAssert implements AssertDelegateTarget {

        private final StaticApplicationContext context;

        private final ServerWebExchangeMatcher matcher;

        RequestMatcherAssert(StaticApplicationContext context,
                             ServerWebExchangeMatcher matcher) {
            this.context = context;
            this.matcher = matcher;
        }

        void matches(String path) {
            ServerWebExchange exchange = webHandler().createExchange(
                    MockServerHttpRequest.get(path).build(),
                    new MockServerHttpResponse());
            matches(exchange);
        }

        private void matches(ServerWebExchange exchange) {
            assertThat(this.matcher.matches(exchange).block().isMatch())
                    .as("Matches " + getRequestPath(exchange)).isTrue();
        }

        void doesNotMatch(String path) {
            ServerWebExchange exchange = webHandler().createExchange(
                    MockServerHttpRequest.get(path).build(),
                    new MockServerHttpResponse());
            doesNotMatch(exchange);
        }

        private void doesNotMatch(ServerWebExchange exchange) {
            assertThat(this.matcher.matches(exchange).block().isMatch())
                    .as("Does not match " + getRequestPath(exchange)).isFalse();
        }

        private TestHttpWebHandlerAdapter webHandler() {
            TestHttpWebHandlerAdapter adapter = new TestHttpWebHandlerAdapter(
                    mock(WebHandler.class));
            adapter.setApplicationContext(this.context);
            return adapter;
        }

        private String getRequestPath(ServerWebExchange exchange) {
            return exchange.getRequest().getPath().toString();
        }

    }

    private static class TestHttpWebHandlerAdapter extends HttpWebHandlerAdapter {

        TestHttpWebHandlerAdapter(WebHandler delegate) {
            super(delegate);
        }

        @Override
        protected ServerWebExchange createExchange(ServerHttpRequest request,
                                                   ServerHttpResponse response) {
            return super.createExchange(request, response);
        }

    }

    @Endpoint(id = "foo")
    private static class FooEndpoint {

    }

}
