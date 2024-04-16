package de.lumemedia.translation;

import dev.golgolex.quala.netty5.client.NettyClient;
import de.lumemedia.translation.folder.Folder;
import de.lumemedia.translation.folder.FolderMessageRepository;
import de.lumemedia.translation.unsorted.UnsortedMessageRepository;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
public interface TranslationAPI {

    /**
     * Enables network handling for the specified NetworkClient.
     * <p>
     * This method configures the provided NetworkClient instance to enable network handling,
     * allowing it to send and receive network packets. Once enabled, the NetworkClient is
     * ready to establish connections, send requests, and receive responses over the network.
     *
     * @param nettyClient The NettyClient instance to enable network handling for.
     *                      This client will be configured to handle network operations.
     */
    void enableNetworkHandling(@NotNull NettyClient nettyClient);

    /**
     * Retrieves a list of loaded languages.
     *
     * @return A list of loaded languages.
     */
    List<Language> cachedLanguages();

    /**
     * Retrieves a list of single message repositories.
     *
     * @return A list of single message repositories.
     */
    List<UnsortedMessageRepository> unsortedRepositories();

    /**
     * Retrieves a list of folder message repositories.
     *
     * @return A list of folder message repositories.
     */
    List<FolderMessageRepository> folderRepositories();

    /**
     * Retrieves a folder message repository for the specified language, pool, and multiKey.
     *
     * @param language The language associated with the repository.
     * @param pool     The message pool name.
     * @param multiKey The multiKey (folder name) within the pool.
     * @return The folder message repository for the specified parameters.
     */
    FolderMessageRepository repositoryOf(@NotNull Language language, @NotNull String pool, @NotNull String multiKey);

    /**
     * Retrieves an unsorted message repository for the specified language and pool.
     *
     * @param language The language associated with the repository.
     * @param pool     The message pool name.
     * @return The unsorted message repository for the specified parameters.
     */
    UnsortedMessageRepository repositoryOf(@NotNull Language language, @NotNull String pool);

    /**
     * Registers a folder message repository for the specified language, pool, and multiKey.
     *
     * @param language The language associated with the repository.
     * @param pool     The message pool name.
     * @param multiKey The multiKey (folder name) within the pool.
     * @return The registered folder message repository.
     */
    FolderMessageRepository registerMessageRepo(@NotNull Language language, @NotNull String pool, @NotNull String multiKey);

    /**
     * Registers an unsorted message repository for the specified language and pool.
     *
     * @param language The language associated with the repository.
     * @param pool     The message pool name.
     * @return The registered unsorted message repository.
     */
    UnsortedMessageRepository registerMessageRepo(@NotNull Language language, @NotNull String pool);

    /**
     * Retrieves a list of default languages.
     *
     * @return A list of default languages.
     */
    List<Language> defaultLanguages();

    /**
     * Retrieves a language by name.
     *
     * @param s The name of the language.
     * @return The language with the specified name.
     */
    Language getLanguage(String s);

    /**
     * Retrieves a language based on a predicate.
     *
     * @param predicate The predicate used to filter languages.
     * @return The language that matches the predicate.
     */
    Language getLanguage(@NotNull Predicate<Language> predicate);

    /**
     * Injects content into the specified message pool.
     *
     * @param poolName The name of the message pool.
     * @param entries  The content entries to inject.
     */
    void injectContent(@NotNull String poolName, @NotNull Input... entries);

    /**
     * Injects content into the specified folder within a message pool.
     *
     * @param poolName   The name of the message pool.
     * @param folderName The name of the folder within the pool.
     * @param input    The content to inject.
     */
    void injectContent(@NotNull String poolName, @NotNull String folderName, @NotNull Input... input);

    /**
     * Injects content into the specified folder within a message pool.
     *
     * @param poolName   The name of the message pool.
     * @param folderName The name of the folder within the pool.
     * @param input    The content to inject.
     */
    void injectContent(@NotNull String poolName, @NotNull String folderName, @NotNull ArrayList<Input> input);

    /**
     * Injects folders into the specified message pool.
     *
     * @param poolName The name of the message pool.
     * @param folders  The folders to inject.
     */
    void injectContent(@NotNull String poolName, @NotNull String folderName, @NotNull Folder... folders);

    /**
     * Injects content into the specified message pool.
     *
     *
     * @param poolName The name of the message pool.
     * @param entries  The content entries to inject.
     */
    void injectContent(@NotNull String poolName, @NotNull Folder... entries);

    /**
     * Injects folders into the specified folder within a message pool.
     *
     * @param poolName   The name of the message pool.
     * @param folderName The name of the folder within the pool.
     * @param folders    The folders to inject.
     */
    void injectContent(@NotNull String poolName, @NotNull String folderName, @NotNull List<Folder> folders);

    /**
     * Injects content into the specified message pool.
     *
     * @param poolName The name of the message pool.
     * @param entryList The content entries to inject.
     */
    void injectContent(@NotNull String poolName, @NotNull List<Input> entryList);

    /**
     * Initializes the languages.
     */
    void initLanguages();

    /**
     * Initializes a language with the specified name.
     *
     * @param name The name of the language to initialize.
     */
    void initLanguage(@NotNull String name);

    /**
     * Creates a new language.
     *
     * @param language The language to create.
     */
    void createLanguage(@NotNull Language language);

    /**
     * Updates the database.
     */
    void updateDatabase();

    /**
     * Reloads all languages.
     */
    void reload();

    /**
     * Reloads the languages with the specified name.
     *
     * @param name The name of the languages to reload.
     */
    void reload(@NotNull String name);

    /**
     * Sends an update packet over the network.
     */
    void sendUpdate();

}
