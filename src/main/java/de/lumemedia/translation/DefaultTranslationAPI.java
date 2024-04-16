package de.lumemedia.translation;

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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.golgolex.quala.json.document.JsonDocument;
import dev.golgolex.quala.netty5.client.NettyClient;
import de.lumemedia.translation.folder.Folder;
import de.lumemedia.translation.folder.FolderMessageRepository;
import de.lumemedia.translation.folder.MultiFolderContentPool;
import de.lumemedia.translation.network.TranslationNetworkManager;
import de.lumemedia.translation.network.protocol.PacketOutLanguageCreation;
import de.lumemedia.translation.network.protocol.PacketOutLanguagesReload;
import de.lumemedia.translation.serialize.LanguageSerializer;
import de.lumemedia.translation.serialize.MultiFolderContentPoolSerializer;
import de.lumemedia.translation.serialize.UnsortedContentPoolSerializer;
import de.lumemedia.translation.unsorted.UnsortedContentPool;
import de.lumemedia.translation.unsorted.UnsortedMessageRepository;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class DefaultTranslationAPI implements TranslationAPI {
    protected final MongoCollection<Document> mongoCollection;
    protected final List<Language> cachedLanguages = new ArrayList<>();
    protected final List<FolderMessageRepository> folderRepositories = new ArrayList<>();
    protected final List<UnsortedMessageRepository> unsortedRepositories = new ArrayList<>();
    protected final UnsortedContentPoolSerializer unsortedContentPoolSerializer = new UnsortedContentPoolSerializer();
    protected final MultiFolderContentPoolSerializer multiFolderContentPoolSerializer = new MultiFolderContentPoolSerializer();
    protected final LanguageSerializer languageSerializer = new LanguageSerializer(unsortedContentPoolSerializer, multiFolderContentPoolSerializer);
    protected TranslationNetworkManager translationNetworkManager;

    public DefaultTranslationAPI(MongoCollection<Document> mongoCollection) {
        this.mongoCollection = mongoCollection;
        this.initLanguages();
    }

    @Override
    public void enableNetworkHandling(@NotNull NettyClient nettyClient) {
        this.translationNetworkManager = new TranslationNetworkManager(nettyClient);
    }

    @Override
    public List<Language> cachedLanguages() {
        return cachedLanguages;
    }

    @Override
    public List<UnsortedMessageRepository> unsortedRepositories() {
        return unsortedRepositories;
    }

    @Override
    public List<FolderMessageRepository> folderRepositories() {
        return folderRepositories;
    }

    @Override
    public FolderMessageRepository repositoryOf(@NotNull Language language, @NotNull String pool, @NotNull String multiKey) {
        // Filter the existing folderMessageRepositories to find a match based on Language, pool name, and multi-key
        FolderMessageRepository receiver = this.folderRepositories.stream().filter(messageReceiver ->
                        messageReceiver.key().equalsIgnoreCase(language.name()) &&
                                messageReceiver.multiFolderContentPool().poolMame().equalsIgnoreCase(pool) &&
                                messageReceiver.multiKey().equalsIgnoreCase(multiKey))
                .findFirst()
                .orElse(null);

        // If no existing repository is found, register a new one
        if (receiver == null) {
            return this.registerMessageRepo(language, pool, multiKey);
        }

        // Return the found or newly registered repository
        return receiver;
    }

    @Override
    public UnsortedMessageRepository repositoryOf(@NotNull Language language, @NotNull String pool) {
        // Filter the existing singleMessageRepositories to find a match based on Language and pool name
        UnsortedMessageRepository receiver = this.unsortedRepositories.stream().filter(messageReceiver ->
                        messageReceiver.key().equalsIgnoreCase(language.name()) &&
                                messageReceiver.unsortedContentPool().poolMame().equalsIgnoreCase(pool))
                .findFirst()
                .orElse(null);

        // If no existing repository is found, register a new one
        if (receiver == null) {
            return this.registerMessageRepo(language, pool);
        }

        // Return the found or newly registered repository
        return receiver;
    }

    @Override
    public FolderMessageRepository registerMessageRepo(@NotNull Language language, @NotNull String pool, @NotNull String multiKey) {
        // Filter the existing folderMessageRepositories to find a match based on Language, pool name, and multi-key
        FolderMessageRepository receiver = this.folderRepositories.stream().filter(messageReceiver ->
                        messageReceiver.key().equalsIgnoreCase(language.name()) &&
                                messageReceiver.multiFolderContentPool().poolMame().equalsIgnoreCase(pool) &&
                                messageReceiver.multiKey().equalsIgnoreCase(multiKey))
                .findFirst()
                .orElse(null);

        // If no existing repository is found, create a new one and add it to the list
        if (receiver == null) {
            receiver = new FolderMessageRepository(language.name(), multiKey, language.folder(pool));
            this.folderRepositories.add(receiver);
        }

        // Return the found or newly created repository
        return receiver;
    }

    @Override
    public UnsortedMessageRepository registerMessageRepo(@NotNull Language language, @NotNull String pool) {
        // Filter the existing singleMessageRepositories to find a match based on Language and pool name
        UnsortedMessageRepository receiver = this.unsortedRepositories.stream().filter(messageReceiver ->
                        messageReceiver.key().equalsIgnoreCase(language.name()) &&
                                messageReceiver.unsortedContentPool().poolMame().equalsIgnoreCase(pool))
                .findFirst()
                .orElse(null);

        // If no existing repository is found, create a new one and add it to the list
        if (receiver == null) {
            receiver = new UnsortedMessageRepository(language.name(), language.unsorted(pool));
            this.unsortedRepositories.add(receiver);
        }

        // Return the found or newly created repository
        return receiver;
    }

    @Override
    public List<Language> defaultLanguages() {
        // Filter the loaded languages to find those marked as default
        return this.cachedLanguages.stream().filter(Language::isDefault).toList();
    }

    @Override
    public Language getLanguage(String s) {
        // Find the language in the loaded languages based on its name
        return this.cachedLanguages.stream().filter(language -> language.name().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    @Override
    public Language getLanguage(@NotNull Predicate<Language> predicate) {
        // Find the language in the loaded languages based on the provided predicate
        return this.cachedLanguages.stream().filter(predicate).findFirst().orElse(null);
    }

    @Override
    public void injectContent(@NotNull String poolName, Input... entries) {
        // Delegate to the version of injectContent with a list of entries
        this.injectContent(poolName, Arrays.asList(entries));
    }

    @Override
    public void injectContent(@NotNull String poolName, @NotNull String folderName, Input... input) {
        // Check if the content array is empty
        if (input.length < 1) {
            return;
        }

        // Create a new folder and add the content to its rows
        var multiContent = new Folder(folderName);
        multiContent.rows().addAll(Arrays.asList(input));

        // Delegate to the version of injectContent with a Folder parameter
        this.injectContent(poolName, folderName, multiContent);
    }

    @Override
    public void injectContent(@NotNull String poolName, @NotNull String folderName, @NotNull ArrayList<Input> input) {
        // Check if the content list is empty
        if (input.isEmpty()) {
            return;
        }

        // Create a new folder and add the content to its rows
        var multiContent = new Folder(folderName);
        multiContent.rows().addAll(input);

        // Delegate to the version of injectContent with a Folder parameter
        this.injectContent(poolName, folderName, multiContent);
    }

    @Override
    public void injectContent(@NotNull String poolName, @NotNull String folderName, Folder... folders) {
        // Check if the folders array is empty
        if (folders.length < 1) return;

        // Delegate to the version of injectContent with a List of Folder parameter
        this.injectContent(poolName, folderName, Arrays.asList(folders));
    }

    @Override
    public void injectContent(@NotNull String poolName, Folder... entries) {
        // Check if the multipleEntries array is empty
        if (entries.length < 1) return;

        // Delegate to the version of injectContent with a List of Folder parameter
        this.injectContent(poolName, entries[0].key(), Arrays.asList(entries));
    }

    @Override
    public void injectContent(@NotNull String poolName, @NotNull String folderName, @NotNull List<Folder> folders) {
        // Flag to track if an update is needed
        var needUpdate = false;

        // Iterate through loaded languages
        for (var language : this.cachedLanguages) {
            var newPool = false;
            var newMessage = false;

            // Get or create the multiFolderContentPool for the specified poolName
            var multiFolderContentPool = language.folder(poolName);
            if (multiFolderContentPool == null) {
                newPool = true;
                multiFolderContentPool = new MultiFolderContentPool(poolName);
            }

            // Get or create the folder for the specified folderName
            var folder = multiFolderContentPool
                    .folders()
                    .stream()
                    .filter(contents -> contents.key().equalsIgnoreCase(folderName))
                    .findFirst()
                    .orElse(new Folder(folderName));

            // Iterate through the provided folders
            for (var content : folders) {
                // Skip empty or invalid content
                if (content.key().isEmpty() || content.rows().isEmpty()) {
                    continue;
                }

                // Iterate through the rows of the content and add new messages to the folder
                for (var row : content.rows()) {
                    if (folder.rows()
                            .stream()
                            .noneMatch(languageContent -> languageContent.key().equalsIgnoreCase(row.key()))) {
                        newMessage = true;
                        folder.rows().add(row);
                    }
                }
            }

            // Update the multiFolderContentPool if new messages are added
            if (newMessage) {
                if (folder.rows().isEmpty()) continue;
                multiFolderContentPool.folders().add(folder);
                needUpdate = true;
            }

            // Update the language with the new multiFolderContentPool if created
            if (newPool) {
                language.multiFolderContentPools().add(multiFolderContentPool);
                needUpdate = true;
            }
        }

        // Trigger an asynchronous database update if needed
        if (needUpdate) {
            this.updateDatabase();
            this.sendUpdate();
        }
    }

    @Override
    public void injectContent(@NotNull String poolName, @NotNull List<Input> entryList) {
        // Flag to track if any change is made
        var anyChange = false;

        // Iterate through loaded languages
        for (Language language : this.cachedLanguages) {
            // Get or create the unsortedContentPool for the specified poolName
            UnsortedContentPool unsortedContentPool = language
                    .unsortedContentPools()
                    .stream()
                    .filter(languagePool -> languagePool.poolMame().equalsIgnoreCase(poolName))
                    .findFirst()
                    .orElseGet(() -> {
                        var newPool = new UnsortedContentPool(poolName);
                        language.unsortedContentPools().add(newPool);
                        return newPool;
                    });

            // Iterate through the provided list of ContentTemplate
            for (Input input : entryList) {
                // Skip existing content or invalid content
                if (unsortedContentPool.inputs()
                        .stream()
                        .noneMatch(languageContent1 -> languageContent1.key()
                                .equalsIgnoreCase(input.key()))
                        && !(input.key().isEmpty() && input.raw().isEmpty())) {
                    anyChange = true;
                    unsortedContentPool.inputs().add(input);
                }
            }
        }

        // Trigger an asynchronous database update if any change is made
        if (anyChange) {
            this.updateDatabase();
            this.sendUpdate();
        }
    }

    @Override
    public void initLanguages() {
        // Clear the list of loaded languages to prepare for new data
        this.cachedLanguages.clear();

        // Retrieve all documents from the MongoDB collection
        List<Document> documents = new ArrayList<>();
        for (var document : this.mongoCollection.find()) {
            documents.add(document);
        }

        // If there are no documents retrieved from the collection, create a backup English language
        if (documents.isEmpty()) {
            // Define a backup English language object
            var backupLanguage = new Language(
                    "english",
                    new JsonDocument(),
                    new String[]{"en_us"},
                    new ArrayList<>(),
                    new ArrayList<>(),
                    true
            );

            // Create the backup language in the system
            this.createLanguage(backupLanguage);
        }

        // Iterate over the retrieved documents to deserialize and load languages
        for (var document : documents) {
            try {
                // Deserialize each document into a Language object using the languageSerializer
                var language = this.languageSerializer.deserialize(document);

                // Check if a language with the same name already exists in the loaded languages
                if (this.cachedLanguages.stream().anyMatch(l -> l.name().equalsIgnoreCase(language.name()))) {
                    // Skip adding the language if it already exists
                    continue;
                }

                // Add the deserialized language to the list of loaded languages
                this.cachedLanguages.add(language);
            } catch (Exception exception) {
                // Print stack trace if deserialization fails for any document
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void initLanguage(@NotNull String name) {
        // Retrieve the document from the MongoDB collection that matches the specified language name
        var document = this.mongoCollection.find(Filters.eq("name", name)).first();

        // Check if the retrieved document is not null
        if (document != null) {
            try {
                // Deserialize the document into a Language object using the languageSerializer
                var language = this.languageSerializer.deserialize(document);

                // Check if any loaded language has the same name as the deserialized language
                // If a language with the same name is already loaded, skip adding it again
                if (this.cachedLanguages.stream().anyMatch(l -> l.name().equalsIgnoreCase(language.name()))) {
                    return; // Skip adding the language if it's already loaded
                }

                // Add the deserialized language to the list of loaded languages
                this.cachedLanguages.add(language);
            } catch (Exception exception) {
                // Handle any exceptions that occur during deserialization
                exception.printStackTrace(); // Print the stack trace for debugging purposes
            }
        }

    }

    @Override
    public void createLanguage(@NotNull Language language) {
        // Add the language to the loaded languages list
        this.cachedLanguages.add(language);

        // Insert the language into the MongoDB repository
        this.mongoCollection.insertOne(this.languageSerializer.serialize(language));

        if (this.translationNetworkManager != null) {
            this.translationNetworkManager.nettyClient().thisNetworkChannel().sendPacket(new PacketOutLanguageCreation(language));
        }

        // Trigger an asynchronous database update
        this.updateDatabase();
        this.sendUpdate();
    }

    @Override
    public void updateDatabase() {
        // Iterate through each loaded language in the list of loaded languages
        for (var loadedLanguage : this.cachedLanguages) {
            // Update the corresponding document in the MongoDB collection for the current language
            // Use Filters.eq() to create a filter to match documents by the language name field
            // Use the languageSerializer to serialize the loadedLanguage object into a BSON Document
            // Use the $set operator to update the fields of the matched document with the serialized language object
            this.mongoCollection.updateOne(
                    Filters.eq("name", loadedLanguage.name()), // Filter to match documents by language name
                    new Document("$set", this.languageSerializer.serialize(loadedLanguage)) // Update operation
            );
            if (this.translationNetworkManager != null) {
                this.translationNetworkManager.nettyClient().thisNetworkChannel().sendPacket(new PacketOutLanguagesReload(new Language().withName("null")));
            }
        }
    }

    @Override
    public void reload() {
        // Clear the existing list of loaded languages and reinitialize
        this.cachedLanguages.clear();
        this.initLanguages();
    }

    @Override
    public void reload(@NotNull String name) {
        // Remove the language with the specified name from the list and reinitialize
        this.cachedLanguages.removeIf(language -> language.name().equalsIgnoreCase(name));
        this.initLanguage(name);
    }

    @Override
    public void sendUpdate() {
        // Check if the networkClient is not null
        if (this.translationNetworkManager != null) {
            // If networkClient is not null, send a ReloadLanguagesPacket through its networkChannel
            // The ReloadLanguagesPacket is used to trigger a reload of languages on the network
            this.translationNetworkManager.nettyClient().thisNetworkChannel().sendPacket(new PacketOutLanguagesReload(
                    new Language().withName("null")
            ));
        }
    }
}
