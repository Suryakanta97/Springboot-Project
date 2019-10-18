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

package org.springframework.boot.context.properties.bind.convert;

import org.junit.AssumptionViolatedException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Base class for {@link InetAddress} tests.
 *
 * @author Phillip Webb
 */
public abstract class AbstractInetAddressTests {

    public void assumeResolves(String host) {
        try {
            InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            throw new AssumptionViolatedException("Host " + host + " not resolvable", ex);
        }
    }

}