package de.lumemedia.translation.folder;

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
public class MultiFolderContentPool implements Serializable {
    protected final String poolMame;
    protected final List<Folder> folders;

    public MultiFolderContentPool(@NotNull String poolMame) {
        this.poolMame = poolMame;
        this.folders = new ArrayList<>();
    }

    public MultiFolderContentPool(@NotNull String poolMame, @NotNull ArrayList<Folder> folders) {
        this.poolMame = poolMame;
        this.folders = folders;
    }

    public List<Input> content(@NotNull String s) {
        Folder content = this.folders.stream().filter(languageContent ->
                languageContent.key().equalsIgnoreCase(s)).findFirst().orElse(null);
        return content == null ? null : content.rows();
    }

    public String rawContent(@NotNull String s, @NotNull String messageKey) {
        Folder content = this.folders.stream().filter(languageContent ->
                languageContent.key().equalsIgnoreCase(s)).findFirst().orElse(null);
        return content == null ? null : content.rows().stream().filter(languageContent ->
                        languageContent.key().equalsIgnoreCase(messageKey))
                .map(Input::raw).findFirst().orElse(null);
    }
}
