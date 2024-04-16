package de.lumemedia.translation.unsorted;

import lombok.Getter;
import de.lumemedia.translation.Input;
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
public class UnsortedContentPool implements Serializable {
    protected final String poolMame;
    protected final List<Input> inputs;

    public UnsortedContentPool(@NotNull String poolMame) {
        this.poolMame = poolMame;
        this.inputs = new ArrayList<>();
    }

    public UnsortedContentPool(@NotNull String poolMame, @NotNull ArrayList<Input> inputs) {
        this.poolMame = poolMame;
        this.inputs = inputs;
    }

    public String rawContent(String s) {
        var input = this.inputs.stream().filter(languageContent -> languageContent.key().equalsIgnoreCase(
                s)).findFirst().orElse(null);
        return input == null ? null : input.raw();
    }
}
