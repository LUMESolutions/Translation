package de.lumemedia.translation;

import lombok.Setter;
import de.lumemedia.translation.folder.FolderMessageRepository;
import de.lumemedia.translation.unsorted.UnsortedMessageRepository;

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
@Setter
public class MessageRepository {

    @Setter
    private static TranslationAPI translationAPI;

    /**
     * Retrieves a FolderMessageRepository for the specified language, pool, and folder name.
     *
     * @param language   The language for which to retrieve the repository.
     * @param pool       The message pool name.
     * @param folderName The folder name within the pool.
     * @return The FolderMessageRepository for the specified parameters.
     */
    public static FolderMessageRepository folder(Language language, String pool, String folderName) {
        return translationAPI.repositoryOf(language, pool, folderName);
    }

    /**
     * Retrieves an UnsortedMessageRepository for the specified language and pool.
     *
     * @param language The language for which to retrieve the repository.
     * @param pool     The message pool name.
     * @return The UnsortedMessageRepository for the specified parameters.
     */
    public static UnsortedMessageRepository unsorted(Language language, String pool) {
        return translationAPI.repositoryOf(language, pool);
    }


}
