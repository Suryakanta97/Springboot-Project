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

package org.springframework.boot.actuate.autoconfigure.endpoint.web;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.ExposeExcludePropertyEndpointFilter;
import org.springframework.boot.actuate.endpoint.EndpointFilter;
import org.springframework.boot.actuate.endpoint.EndpointsSupplier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.http.ActuatorMediaType;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvokerAdvisor;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for web {@link Endpoint} support.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @since 2.0.0
 */
@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(EndpointAutoConfiguration.class)
@EnableConfigurationProperties(WebEndpointProperties.class)
public class WebEndpointAutoConfiguration {

    private static final List<String> MEDIA_TYPES = Arrays
            .asList(ActuatorMediaType.V2_JSON, "application/json");

    private final ApplicationContext applicationContext;

    private final WebEndpointProperties properties;

    public WebEndpointAutoConfiguration(ApplicationContext applicationContext,
                                        WebEndpointProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public PathMapper webEndpointPathMapper() {
        return new MappingWebEndpointPathMapper(this.properties.getPathMapping());
    }

    @Bean
    @ConditionalOnMissingBean
    public EndpointMediaTypes endpointMediaTypes() {
        return new EndpointMediaTypes(MEDIA_TYPES, MEDIA_TYPES);
    }

    @Bean
    @ConditionalOnMissingBean(WebEndpointsSupplier.class)
    public WebEndpointDiscoverer webEndpointDiscoverer(
            ParameterValueMapper parameterValueMapper,
            EndpointMediaTypes endpointMediaTypes, PathMapper webEndpointPathMapper,
            ObjectProvider<Collection<OperationInvokerAdvisor>> invokerAdvisors,
            ObjectProvider<Collection<EndpointFilter<ExposableWebEndpoint>>> filters) {
        return new WebEndpointDiscoverer(this.applicationContext, parameterValueMapper,
                endpointMediaTypes, webEndpointPathMapper,
                invokerAdvisors.getIfAvailable(Collections::emptyList),
                filters.getIfAvailable(Collections::emptyList));
    }

    @Bean
    @ConditionalOnMissingBean(ControllerEndpointsSupplier.class)
    public ControllerEndpointDiscoverer controllerEndpointDiscoverer(
            PathMapper webEndpointPathMapper,
            ObjectProvider<Collection<EndpointFilter<ExposableControllerEndpoint>>> filters) {
        return new ControllerEndpointDiscoverer(this.applicationContext,
                webEndpointPathMapper, filters.getIfAvailable(Collections::emptyList));
    }

    @Bean
    @ConditionalOnMissingBean
    public PathMappedEndpoints pathMappedEndpoints(
            Collection<EndpointsSupplier<?>> endpointSuppliers,
            WebEndpointProperties webEndpointProperties) {
        return new PathMappedEndpoints(webEndpointProperties.getBasePath(),
                endpointSuppliers);
    }

    @Bean
    public ExposeExcludePropertyEndpointFilter<ExposableWebEndpoint> webExposeExcludePropertyEndpointFilter() {
        Set<String> expose = this.properties.getExpose();
        Set<String> exclude = this.properties.getExclude();
        return new ExposeExcludePropertyEndpointFilter<>(ExposableWebEndpoint.class,
                expose, exclude, "info", "health");
    }

    @Bean
    public ExposeExcludePropertyEndpointFilter<ExposableControllerEndpoint> controllerExposeExcludePropertyEndpointFilter() {
        Set<String> expose = this.properties.getExpose();
        Set<String> exclude = this.properties.getExclude();
        return new ExposeExcludePropertyEndpointFilter<>(
                ExposableControllerEndpoint.class, expose, exclude);
    }

    @Configuration
    @ConditionalOnWebApplication(type = Type.SERVLET)
    static class WebEndpointServletAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(ServletEndpointsSupplier.class)
        public ServletEndpointDiscoverer servletEndpointDiscoverer(
                ApplicationContext applicationContext, PathMapper webEndpointPathMapper,
                ObjectProvider<Collection<EndpointFilter<ExposableServletEndpoint>>> filters) {
            return new ServletEndpointDiscoverer(applicationContext,
                    webEndpointPathMapper,
                    filters.getIfAvailable(Collections::emptyList));
        }

    }

}
