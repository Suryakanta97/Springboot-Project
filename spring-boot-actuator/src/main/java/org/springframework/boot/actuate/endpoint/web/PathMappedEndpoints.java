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

package org.springframework.boot.actuate.endpoint.web;

import org.springframework.boot.actuate.endpoint.EndpointsSupplier;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of {@link PathMappedEndpoint path mapped endpoints}.
 *
 * @author Phillip Webb
 */
public class PathMappedEndpoints implements Iterable<PathMappedEndpoint> {

    private final String basePath;

    private final Map<String, PathMappedEndpoint> endpoints;

    /**
     * Create a new {@link PathMappedEndpoints} instance for the given supplier.
     *
     * @param basePath the base path of the endpoints
     * @param supplier the endpoint supplier
     */
    public PathMappedEndpoints(String basePath, EndpointsSupplier<?> supplier) {
        Assert.notNull(supplier, "Supplier must not be null");
        this.basePath = (basePath == null ? "" : basePath);
        this.endpoints = getEndpoints(Collections.singleton(supplier));
    }

    /**
     * Create a new {@link PathMappedEndpoints} instance for the given suppliers.
     *
     * @param basePath  the base path of the endpoints
     * @param suppliers the endpoint suppliers
     */
    public PathMappedEndpoints(String basePath,
                               Collection<EndpointsSupplier<?>> suppliers) {
        Assert.notNull(suppliers, "Suppliers must not be null");
        this.basePath = (basePath == null ? "" : basePath);
        this.endpoints = getEndpoints(suppliers);
    }

    private Map<String, PathMappedEndpoint> getEndpoints(
            Collection<EndpointsSupplier<?>> suppliers) {
        Map<String, PathMappedEndpoint> endpoints = new LinkedHashMap<>();
        suppliers.forEach((supplier) -> {
            supplier.getEndpoints().forEach((endpoint) -> {
                if (endpoint instanceof PathMappedEndpoint) {
                    endpoints.put(endpoint.getId(), (PathMappedEndpoint) endpoint);
                }
            });
        });
        return Collections.unmodifiableMap(endpoints);
    }

    /**
     * Return the root path for the endpoint with the given ID or {@code null} if the
     * endpoint cannot be found.
     *
     * @param endpointId the endpoint ID
     * @return the root path or {@code null}
     */
    public String getRootPath(String endpointId) {
        PathMappedEndpoint endpoint = getEndpoint(endpointId);
        return (endpoint == null ? null : endpoint.getRootPath());
    }

    /**
     * Return the full path for the endpoint with the given ID or {@code null} if the
     * endpoint cannot be found.
     *
     * @param endpointId the endpoint ID
     * @return the full path or {@code null}
     */
    public String getPath(String endpointId) {
        return getPath(getEndpoint(endpointId));
    }

    /**
     * Return the root paths for each mapped endpoint.
     *
     * @return all root paths
     */
    public Collection<String> getAllRootPaths() {
        return asList(stream().map(PathMappedEndpoint::getRootPath));
    }

    /**
     * Return the full paths for each mapped endpoint.
     *
     * @return all root paths
     */
    public Collection<String> getAllPaths() {
        return asList(stream().map(this::getPath));
    }

    /**
     * Return the {@link PathMappedEndpoint} with the given ID or {@code null} if the
     * endpoint cannot be found.
     *
     * @param endpointId the endpoint ID
     * @return the path mapped endpoint or {@code null}
     */
    public PathMappedEndpoint getEndpoint(String endpointId) {
        return this.endpoints.get(endpointId);
    }

    /**
     * Stream all {@link PathMappedEndpoint path mapped endpoints}.
     *
     * @return a stream of endpoints
     */
    public Stream<PathMappedEndpoint> stream() {
        return this.endpoints.values().stream();
    }

    @Override
    public Iterator<PathMappedEndpoint> iterator() {
        return this.endpoints.values().iterator();
    }

    private String getPath(PathMappedEndpoint endpoint) {
        return (endpoint == null ? null : this.basePath + "/" + endpoint.getRootPath());
    }

    private <T> List<T> asList(Stream<T> stream) {
        return stream.collect(Collectors.collectingAndThen(Collectors.toList(),
                Collections::unmodifiableList));
    }

}
