package de.lumemedia.translation.serialize;

import de.lumemedia.translation.Input;
import de.lumemedia.translation.folder.Folder;
import de.lumemedia.translation.folder.MultiFolderContentPool;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
public class MultiFolderContentPoolSerializer {
    /**
     * Serializes a MultiFolderContentPool object into a Document.
     *
     * @param multiFolderContentPool The MultiFolderContentPool object to serialize.
     * @return The Document representation of the serialized MultiFolderContentPool.
     */
    public Document serialize(MultiFolderContentPool multiFolderContentPool) {
        // Initialize a HashMap to store the contents of the MultiFolderContentPool
        HashMap<String, HashMap<String, String>> contents = new HashMap<>();
        // Iterate through each folder in the MultiFolderContentPool
        for (Folder content : multiFolderContentPool.folders()) {
            // If the folder key is not already present in the contents map, add it
            if (!contents.containsKey(content.key())) contents.put(content.key(), new HashMap<>());
            // Iterate through each row in the folder and add it to the contents map
            for (Input row : content.rows()) contents.get(content.key()).put(row.key(), row.raw());
        }
        // Create and return a Document containing the pool name and the contents map
        return new Document("pool", multiFolderContentPool.poolMame())
                .append("content", contents);
    }

    /**
     * Deserializes a Document into a MultiFolderContentPool object.
     *
     * @param document The Document to deserialize.
     * @return The MultiFolderContentPool object represented by the deserialized Document.
     */
    public MultiFolderContentPool deserialize(Document document) {
        // Initialize a HashMap to store the deserialized contents
        HashMap<String, HashMap<String, String>> x = new HashMap<>();
        // Iterate through each entry in the content section of the Document
        document.get("content", Document.class).forEach((s, o) -> {
            // If the key is not already present in the map, add it
            if (!x.containsKey(s)) x.put(s, new HashMap<>());
            // Iterate through each row in the folder and add it to the map
            ((Document) o).forEach((s1, o1) -> x.get(s).put(s1, (String) o1));
        });

        // Create and return a new MultiFolderContentPool using the deserialized data
        return new MultiFolderContentPool(
                // Get the pool name from the Document
                document.getString("pool"),
                // Map the deserialized data to create a list of Folder objects
                new ArrayList<>(x.entrySet().stream().map(stringHashMapEntry -> {
                    // Create a new Folder with the folder key
                    Folder multiContent = new Folder(stringHashMapEntry.getKey());
                    // Iterate through each entry in the folder's contents and add them as ContentTemplate objects
                    for (Map.Entry<String, String> stringStringEntry : stringHashMapEntry.getValue().entrySet())
                        multiContent.rows().add(new Input(
                                // Get the key of the row
                                stringStringEntry.getKey(),
                                // Get the raw value of the row
                                stringStringEntry.getValue()
                        ));
                    return multiContent;
                }).toList())
        );
    }
}
