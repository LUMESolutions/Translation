package de.lumemedia.translation.unsorted;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lumemedia.translation.TextUtil;
import org.jetbrains.annotations.NotNull;

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
@AllArgsConstructor
public class UnsortedMessageRepository {
    protected String key;
    protected UnsortedContentPool unsortedContentPool;

    public boolean containsMessage(String s) {
        return this.unsortedContentPool.rawContent(s) != null;
    }

    public String message(@NotNull String messageKey, Object... objects) {
        String rawContent = this.unsortedContentPool.rawContent(messageKey);
        if (rawContent == null) {
            rawContent = "We are sorry. Unfortunately, this message could not be found in your language.";
        }
        return TextUtil.buildMessage(rawContent, objects);
    }
}
