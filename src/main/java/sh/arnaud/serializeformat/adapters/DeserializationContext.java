package sh.arnaud.serializeformat.adapters;

import com.google.gson.JsonParseException;
import sh.arnaud.serializeformat.types.grammar.GrammarObject;

import java.util.HashMap;
import java.util.Map;

public class DeserializationContext {
    private final Map<GrammarObject, Integer> storage = new HashMap<>();

    public void register(GrammarObject object, int handle) {
        if (storage.containsValue(handle)) {
            throw new JsonParseException("Two different resource have the same handle");
        }

        storage.put(object, handle);
    }

    /**
     * Try to find an object from the storage, throws an exception if not found.
     *
     * @param handle The resource's handle
     * @return Resource with the given handle
     */
    public GrammarObject find(int handle) {
        return storage.entrySet().stream()
                .filter(set -> set.getValue() == handle)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new JsonParseException("Reference to a not yet declared object"));
    }

    /**
     * Same as {@link #find(int)} but also checks the type of the resource.
     *
     * @param handle The resource's handle
     * @param type The object type
     * @return Resource with the given handle
     */
    public <T> T find(int handle, Class<T> type) {
        var object = find(handle);

        if (type.isInstance(object)) {
            return type.cast(object);
        }

        throw new JsonParseException("Reference to something with the wrong type");
    }
}
