package de.lumemedia.translation.serialize;

import de.lumemedia.translation.Input;
import de.lumemedia.translation.unsorted.UnsortedContentPool;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

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
public class UnsortedContentPoolSerializer {

    /**
     * Serializes an UnsortedContentPool object into a Document.
     *
     * @param languagePool The UnsortedContentPool object to serialize.
     * @return The Document representation of the serialized UnsortedContentPool.
     */
    public Document serialize(UnsortedContentPool languagePool) {
        // Initialize a HashMap to store the contents of the UnsortedContentPool
        HashMap<String, String> contents = new HashMap<>();
        // Iterate through each content template in the UnsortedContentPool
        for (Input input : languagePool.inputs()) {
            // Add the content template's key and raw value to the contents map
            contents.put(input.key(), input.raw());
        }
        // Create and return a Document containing the pool name and the contents map
        return new Document("pool", languagePool.poolMame()).append("content", contents);
    }

    /**
     * Deserializes a Document into an UnsortedContentPool object.
     *
     * @param document The Document to deserialize.
     * @return The UnsortedContentPool object represented by the deserialized Document.
     */
    public UnsortedContentPool deserialize(Document document) {
        // Initialize an ArrayList to store the deserialized content templates
        ArrayList<Input> toInput = new ArrayList<>();
        // Iterate through each entry in the content section of the Document
        document.get("content", Document.class).forEach((s, o) -> {
            // Convert the value to a String
            String value = (String) o;
            // Create a new ContentTemplate object and add it to the ArrayList
            toInput.add(new Input(s, value));
        });
        // Create and return a new UnsortedContentPool using the deserialized data
        return new UnsortedContentPool(document.getString("pool"), toInput);
    }

}
