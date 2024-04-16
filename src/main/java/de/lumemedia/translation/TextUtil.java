package de.lumemedia.translation;

import lombok.experimental.UtilityClass;
import org.bson.Document;
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
@UtilityClass
public class TextUtil {

    /**
     * Finds a message string from the given document based on the provided key.
     *
     * @param document The document containing messages.
     * @param key      The key to find the message in the document.
     * @return The message string if found, otherwise null.
     */
    public String findMessageFromDocument(Document document, String key) {
        // Check if the document contains the specified key
        if (!document.containsKey(key))
            return null;

        // Return the message string corresponding to the key
        return document.getString(key);
    }

    /**
     * Builds a converted message by replacing placeholders in the message string with provided objects.
     *
     * @param document The document containing messages.
     * @param key      The key to find the message in the document.
     * @param objects  Objects to replace placeholders in the message string.
     * @return The built message string.
     */
    public String buildConvertedMessage(@NotNull Document document, @NotNull String key, @NotNull Object... objects) {
        // Find the message string from the document based on the key
        var message = findMessageFromDocument(document, key);

        // If message not found, return an error message
        if (message == null) {
            return "Error";
        }

        // Replace placeholders in the message string with provided objects
        message = message.replace("{NEXT_LINE}", "\n");
        if (message.contains("{")) {
            for (var i = 0; i < objects.length; i++) {
                message = message.replace("{" + i + "}", String.valueOf(objects[i]));
            }
        }

        // Return the built message string
        return message;
    }

    /**
     * Builds a message string by replacing placeholders in the message with provided objects.
     *
     * @param message The message string containing placeholders.
     * @param objects Objects to replace placeholders in the message string.
     * @return The built message string.
     */
    public String buildMessage(String message, Object... objects) {
        // Check for null input
        if (message == null) {
            return null;
        }

        // Replace placeholders in the message string with provided objects
        message = message.replace("{NEXT_LINE}", "\n");
        if (message.contains("{")) {
            for (int i = 0; i < objects.length; i++) {
                message = message.replace("{" + i + "}", String.valueOf(objects[i]));
            }
        }

        // Return the built message string
        return message;
    }


}
