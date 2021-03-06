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

package org.springframework.boot.actuate.metrics.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CacheMetricsRegistrar}.
 *
 * @author Stephane Nicoll
 */
public class CacheMetricsRegistrarTests {

    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Test
    public void bindToSupportedCache() {
        CacheMetricsRegistrar registrar = new CacheMetricsRegistrar(this.meterRegistry,
                "root", Collections.singleton(new CaffeineCacheMeterBinderProvider()));
        assertThat(registrar.bindCacheToRegistry(
                new CaffeineCache("test", Caffeine.newBuilder().build()))).isTrue();
        assertThat(this.meterRegistry.get("root.requests").tags("name", "test").meter())
                .isNotNull();
    }

    @Test
    public void bindToUnsupportedCache() {
        CacheMetricsRegistrar registrar = new CacheMetricsRegistrar(this.meterRegistry,
                "root", Collections.emptyList());
        assertThat(registrar.bindCacheToRegistry(
                new CaffeineCache("test", Caffeine.newBuilder().build()))).isFalse();
        assertThat(this.meterRegistry.find("root.requests").tags("name", "test").meter())
                .isNull();
    }

}
