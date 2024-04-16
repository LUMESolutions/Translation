package de.lumemedia.translation;

import dev.golgolex.quala.json.JsonObjectSerializer;
import dev.golgolex.quala.json.document.JsonDocument;
import dev.golgolex.quala.netty5.protocol.codec.CodecBuffer;
import lombok.Getter;
import de.lumemedia.translation.folder.Folder;
import de.lumemedia.translation.folder.MultiFolderContentPool;
import de.lumemedia.translation.unsorted.UnsortedContentPool;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright 2023-2024 LumeMedia-Translation contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Getter
public class Language implements Serializable {

    protected String name;
    protected JsonDocument metaData;
    protected String[] tags;
    protected List<UnsortedContentPool> unsortedContentPools;
    protected List<MultiFolderContentPool> multiFolderContentPools;
    protected boolean defaultLanguage;

    public Language withName(String name) {
        this.name = name;
        return this;
    }

    public boolean isDefault() {
        return this.defaultLanguage;
    }

    public Language() {
    }

    public Language(@NotNull String name,
                    @NotNull JsonDocument metaData,
                    @NotNull String[] tags,
                    @NotNull List<UnsortedContentPool> unsortedContentPools,
                    @NotNull List<MultiFolderContentPool> multiFolderContentPools,
                    boolean defaultLanguage) {
        this.name = name;
        this.metaData = metaData;
        this.tags = tags;
        this.unsortedContentPools = unsortedContentPools;
        this.multiFolderContentPools = multiFolderContentPools;
        this.defaultLanguage = defaultLanguage;
    }

    public void readBuf(@NotNull CodecBuffer codecBuffer) {
        this.name = codecBuffer.readString();
        this.metaData = new JsonDocument(codecBuffer.readJsonDocument().jsonObject());
        this.defaultLanguage = codecBuffer.readBoolean();

        this.unsortedContentPools = codecBuffer.readList(new ArrayList<>(), () -> {
            var poolName = codecBuffer.readString();
            var contents = codecBuffer.readList(new ArrayList<>(), () -> new Input(
                    codecBuffer.readString(),
                    codecBuffer.readString()
            ));
            return new UnsortedContentPool(poolName, new ArrayList<>(contents));
        });

        this.multiFolderContentPools = codecBuffer.readList(new ArrayList<>(), () -> {
            var poolName = codecBuffer.readString();
            var folders = codecBuffer.readList(new ArrayList<>(), () -> {
                var key = codecBuffer.readString();
                var contents = codecBuffer.readList(new ArrayList<>(), () -> new Input(
                        codecBuffer.readString(),
                        codecBuffer.readString()
                ));
                var folder = new Folder(key);
                folder.rows().addAll(contents);
                return folder;
            });
            return new MultiFolderContentPool(poolName, new ArrayList<>(folders));
        });
    }

    public void writeBuf(@NotNull CodecBuffer codecBuffer) {
        codecBuffer.writeString(this.name)
                .writeJsonDocument(new JsonObjectSerializer(this.metaData.jsonObject()))
                .writeBoolean(this.defaultLanguage);

        codecBuffer.writeList(this.unsortedContentPools, (listBuf, unsortedContentPool) -> listBuf.writeString(unsortedContentPool.poolMame())
                .writeList(unsortedContentPool.inputs(), (valBuf, contentTemplate) -> valBuf.writeString(contentTemplate.key())
                        .writeString(contentTemplate.raw())));
        codecBuffer.writeList(this.multiFolderContentPools, (listBuf, multiFolderContentPool) -> listBuf.writeString(multiFolderContentPool.poolMame())
                .writeList(multiFolderContentPool.folders(), (folderBuf, folder) -> folderBuf.writeString(folder.key())
                        .writeList(folder.rows(), (valBuf, contentTemplate) -> valBuf.writeString(contentTemplate.key())
                                .writeString(contentTemplate.raw()))));
    }

    public MultiFolderContentPool folder(@NotNull String s) {
        return this.multiFolderContentPools.stream().filter(messagePoolImpl -> messagePoolImpl.poolMame().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    public UnsortedContentPool unsorted(@NotNull String s) {
        return this.unsortedContentPools.stream().filter(messagePoolImpl -> messagePoolImpl.poolMame().equalsIgnoreCase(s)).findFirst().orElse(null);
    }
}
