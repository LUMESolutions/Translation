package de.lumemedia.translation.serialize;

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

import com.google.gson.JsonObject;
import dev.golgolex.quala.json.JsonUtils;
import dev.golgolex.quala.json.document.JsonDocument;
import dev.golgolex.quala.mongo.ObjectToBson;
import lombok.AllArgsConstructor;
import de.lumemedia.translation.Language;
import org.bson.Document;

import java.util.Arrays;
import java.util.stream.Collectors;

@AllArgsConstructor
public class LanguageSerializer implements ObjectToBson<Language> {

    private final UnsortedContentPoolSerializer unsortedContentPoolSerializer;
    private final MultiFolderContentPoolSerializer multiFolderContentPoolSerializer;

    @Override
    public Document serialize(Language o) {
        return new Document("name", o.name())
                .append("metaData", JsonUtils.toJson(o.metaData().jsonObject()))
                .append("tags", Arrays.stream(o.tags()).toList())
                .append("unsortedContentPools", o.unsortedContentPools()
                        .stream()
                        .map(this.unsortedContentPoolSerializer::serialize)
                        .toList())
                .append("multiFolderContentPools", o.multiFolderContentPools()
                        .stream()
                        .map(this.multiFolderContentPoolSerializer::serialize)
                        .toList())
                .append("defaultLanguage", o.defaultLanguage());
    }

    @Override
    public Language deserialize(Document document) {
        var name = document.getString("name");
        var metaData = new JsonDocument(JsonUtils.fromJson(document.getString("metaData"), JsonObject.class));
        var tags = document.getList("tags", String.class).toArray(new String[0]);
        var unsortedContentPools = document.getList("unsortedContentPools", Document.class)
                .stream()
                .map(unsortedContentPoolSerializer::deserialize)
                .collect(Collectors.toList());
        var multiFolderContentPools = document.getList("multiFolderContentPools", Document.class)
                .stream()
                .map(multiFolderContentPoolSerializer::deserialize)
                .collect(Collectors.toList());
        var defaultLanguage = document.getBoolean("defaultLanguage");
        return new Language(name, metaData, tags, unsortedContentPools, multiFolderContentPools, defaultLanguage);
    }

}
