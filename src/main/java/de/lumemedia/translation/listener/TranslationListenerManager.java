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
import de.lumemedia.translation.Input;
import de.lumemedia.translation.TranslationAPI;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TranslationListenerManager {

    private static final List<RegisteredTranslationListener> registeredListeners = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger("TranslationListenerManager");

    /**
     * Unregisters a TranslationListener.
     *
     * @param listener The class of the TranslationListener to unregister.
     */
    public static void unregisterListener(@NotNull Class<? extends TranslationListener> listener) {
        // Create a list to store the listeners to remove
        List<RegisteredTranslationListener> toRemove = new ArrayList<>();

        // Iterate through the registered listeners
        for (var registeredListener : registeredListeners) {
            // Check if the listener's package name and class name match the provided listener
            if (registeredListener.listener().getPackageName().equalsIgnoreCase(listener.getPackageName()) &&
                    registeredListener.listener().getName().equalsIgnoreCase(listener.getName())) {
                toRemove.add(registeredListener); // Add the listener to the removal list
            }
        }

        // Iterate through the listeners to remove and remove them from the registered listeners list
        for (var registeredTranslationListener : toRemove) {
            registeredListeners.remove(registeredTranslationListener);
            LOGGER.log(Level.INFO, "Unregistered Listener [" + registeredTranslationListener.listener().getSimpleName() + "]");
        }
    }

    /**
     * Registers a translation listener and its associated content with the provided language manager.
     *
     * @param listener         The translation listener class to register.
     * @param translationAPI  The language manager to register the listener with.
     */
    public static void registerListener(@NotNull Class<? extends TranslationListener> listener, @NotNull TranslationAPI translationAPI) {
        // Get all methods declared in the listener class
        var methods = listener.getDeclaredMethods();
        // Create a new RegisteredTranslationListener instance
        var registeredTranslationListener = new RegisteredTranslationListener(listener);

        // Iterate through each method
        for (var method : methods) {
            if (method.isAnnotationPresent(Translation.class)) {
                if (List.class.isAssignableFrom(method.getReturnType())) {
                    try {
                        var methodOwner = method.getDeclaringClass().newInstance();
                        method.setAccessible(true);
                        var result = (List<Input>) method.invoke(methodOwner);
                        var methodName = method.getName();
                        var translation = method.getAnnotation(Translation.class);

                        registeredTranslationListener.content().put(
                                new Pair<>(methodName, translation),
                                result
                        );
                        registeredTranslationListener.registerContentAll(translationAPI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // Add the registered listener to the list of registered listeners
        registeredListeners.add(registeredTranslationListener);
        LOGGER.log(Level.INFO, "Registered Listener [" + listener.getSimpleName() + "] with contents [" + registeredTranslationListener.content().size() + "]");
    }

}