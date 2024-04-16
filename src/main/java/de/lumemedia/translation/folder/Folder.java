package de.lumemedia.translation.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import de.lumemedia.translation.Input;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class Folder implements Serializable {
    private final List<Input> rows = new ArrayList<>();
    private String key;

    public static Folder withEntries(String key, Input... emptyEntries) {
        Folder folder = new Folder(key);
        folder.rows.addAll(Arrays.asList(emptyEntries));
        return folder;
    }
}
