package de.lumemedia.translation.listener;

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

import dev.golgolex.quala.utils.data.Pair;
import lombok.Getter;
import de.lumemedia.translation.Input;
import de.lumemedia.translation.TranslationAPI;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class RegisteredTranslationListener {

    private final Class<? extends TranslationListener> listener;
    private final Map<Pair<String, Translation>, List<Input>> content = new HashMap<>();

    public void registerContent(@NotNull String methodName, @NotNull TranslationAPI translationAPI) {
        Translation translation = translation(methodName);

        if (translation.system().isEmpty()) {
            throw new NullPointerException("No system name could be found.");
        }

        switch (translation.type()) {
            case UNKNOWN -> throw new NullPointerException("No translation type could be found.");
            case FOLDER -> {
                if (translation.folder().isEmpty()) {
                    throw new NullPointerException("No folder name could be found.");
                }

                for (Map.Entry<Pair<String, Translation>, List<Input>> entry : this.content.entrySet()) {
                    if (entry.getKey().second().system().equalsIgnoreCase(translation.system()) &&
                            entry.getKey().second().folder().equalsIgnoreCase(translation.folder())) {
                        translationAPI.injectContent(translation.system(), translation.folder(), new ArrayList<>(entry.getValue()));
                    }
                }
            }
            case UNSORTED -> {
                for (Map.Entry<Pair<String, Translation>, List<Input>> entry : this.content.entrySet()) {
                    if (entry.getKey().second().system().equalsIgnoreCase(translation.system())) {
                        translationAPI.injectContent(translation.system(), new ArrayList<>(entry.getValue()));
                    }
                }
            }
        }
    }

    @NotNull
    private Translation translation(@NotNull String methodName) {
        Translation translation = null;

        for (Map.Entry<Pair<String, Translation>, List<Input>> returnListEntry : content.entrySet()) {
            if (returnListEntry.getKey().first().equalsIgnoreCase(methodName)) {
                translation = returnListEntry.getKey().second();
                break;
            }
        }

        if (translation == null) {
            throw new NullPointerException("No translation annotation could be found.");
        }
        return translation;
    }

    public void registerContentAll(TranslationAPI languageManager) {
        content.forEach((stringTranslationReturn, contentTemplates) -> this.registerContent(stringTranslationReturn.first(), languageManager));
    }

}
