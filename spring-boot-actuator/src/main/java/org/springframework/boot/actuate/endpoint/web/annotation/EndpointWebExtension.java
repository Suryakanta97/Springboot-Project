/*
 * Copyright 2012-2017 the original author or authors.
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

package org.springframework.boot.actuate.endpoint.web.annotation;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.EndpointExtension;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Identifies a type as being a Web-specific extension of an {@link Endpoint}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @see Endpoint
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EndpointExtension(filter = WebEndpointFilter.class)
public @interface EndpointWebExtension {

    /**
     * The {@link Endpoint endpoint} class to which this Web extension relates.
     *
     * @return the endpoint class
     */
    @AliasFor(annotation = EndpointExtension.class)
    Class<?> endpoint();

}
