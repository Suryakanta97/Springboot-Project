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

package org.springframework.boot.devtools.tunnel.payload;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HttpTunnelPayloadForwarder}.
 *
 * @author Phillip Webb
 */
public class HttpTunnelPayloadForwarderTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void targetChannelMustNotBeNull() {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("TargetChannel must not be null");
        new HttpTunnelPayloadForwarder(null);
    }

    @Test
    public void forwardInSequence() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(out);
        HttpTunnelPayloadForwarder forwarder = new HttpTunnelPayloadForwarder(channel);
        forwarder.forward(payload(1, "he"));
        forwarder.forward(payload(2, "ll"));
        forwarder.forward(payload(3, "o"));
        assertThat(out.toByteArray()).isEqualTo("hello".getBytes());
    }

    @Test
    public void forwardOutOfSequence() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(out);
        HttpTunnelPayloadForwarder forwarder = new HttpTunnelPayloadForwarder(channel);
        forwarder.forward(payload(3, "o"));
        forwarder.forward(payload(2, "ll"));
        forwarder.forward(payload(1, "he"));
        assertThat(out.toByteArray()).isEqualTo("hello".getBytes());
    }

    @Test
    public void overflow() throws Exception {
        WritableByteChannel channel = Channels.newChannel(new ByteArrayOutputStream());
        HttpTunnelPayloadForwarder forwarder = new HttpTunnelPayloadForwarder(channel);
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage("Too many messages queued");
        for (int i = 2; i < 130; i++) {
            forwarder.forward(payload(i, "data" + i));
        }
    }

    private HttpTunnelPayload payload(long sequence, String data) {
        return new HttpTunnelPayload(sequence, ByteBuffer.wrap(data.getBytes()));
    }

}
