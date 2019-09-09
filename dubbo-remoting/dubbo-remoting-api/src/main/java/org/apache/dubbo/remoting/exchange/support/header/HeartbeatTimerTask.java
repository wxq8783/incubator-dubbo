/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.remoting.exchange.support.header;

import org.apache.dubbo.common.Version;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.exchange.Request;

/**
 * HeartbeatTimerTask
 */
public class HeartbeatTimerTask extends AbstractTimerTask {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatTimerTask.class);

    private final int heartbeat;

    HeartbeatTimerTask(ChannelProvider channelProvider, Long heartbeatTick, int heartbeat) {
        super(channelProvider, heartbeatTick);
        this.heartbeat = heartbeat;
    }

    /**
     * Dubbo 采取的是双向心跳设计，即服务端会向客户端发送心跳，客户端也会向服务端发送心跳，
     * 接收的一方更新 lastRead 字段，发送的一方更新 lastWrite 字段，超过心跳间隙的时间，便发送心跳请求给对端。
     * 这里的 lastRead/lastWrite 同样会被同一个通道上的普通调用更新，通过更新这两个字段，实现了只在连接空闲时才会真正发送空闲报文的机制，
     *
     * !!!!!不仅仅心跳请求会更新 lastRead 和 lastWrite，普通请求也会。这对应了我们预备知识中的空闲检测机制
     * @param channel
     */
    @Override
    protected void doTask(Channel channel) {
        try {
            Long lastRead = lastRead(channel);
            Long lastWrite = lastWrite(channel);
            if ((lastRead != null && now() - lastRead > heartbeat)
                    || (lastWrite != null && now() - lastWrite > heartbeat)) {
                Request req = new Request();
                req.setVersion(Version.getProtocolVersion());
                req.setTwoWay(true);
                req.setEvent(Request.HEARTBEAT_EVENT);
                channel.send(req);
                if (logger.isDebugEnabled()) {
                    logger.debug("Send heartbeat to remote channel " + channel.getRemoteAddress()
                            + ", cause: The channel has no data-transmission exceeds a heartbeat period: "
                            + heartbeat + "ms");
                }
            }
        } catch (Throwable t) {
            logger.warn("Exception when heartbeat to remote channel " + channel.getRemoteAddress(), t);
        }
    }
}
