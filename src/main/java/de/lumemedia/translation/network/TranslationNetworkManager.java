package de.lumemedia.translation.network;

/*
 * MIT License
 *
 * Copyright 2023-2024 LumeMedia-Translation contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import dev.golgolex.quala.netty5.client.NettyClient;
import lombok.Getter;
import de.lumemedia.translation.Language;
import de.lumemedia.translation.network.protocol.PacketOutLanguageCreation;
import de.lumemedia.translation.network.protocol.PacketOutLanguageUpdate;
import de.lumemedia.translation.network.protocol.PacketOutLanguagesReload;

@Getter
public class TranslationNetworkManager {

    private final NettyClient nettyClient;

    public TranslationNetworkManager(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
        this.nettyClient.packetRegistry().register(
                PacketOutLanguageCreation.class,
                -8574,
                integer -> new PacketOutLanguageCreation(new Language())
        );
        this.nettyClient.packetRegistry().register(
                PacketOutLanguageUpdate.class,
                -8575,
                integer -> new PacketOutLanguageUpdate(new Language())
        );
        this.nettyClient.packetRegistry().register(
                PacketOutLanguagesReload.class,
                -8576,
                integer -> new PacketOutLanguagesReload(new Language())
        );
    }

}
