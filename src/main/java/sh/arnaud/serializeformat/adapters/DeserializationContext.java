package sh.arnaud.serializeformat.adapters;

import com.google.gson.JsonParseException;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarObject;

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

    public GrammarObject find(int handle) {
        return storage.entrySet().stream()
                .filter(set -> set.getValue() == handle)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> {
                    System.out.println(handle);
                    return new JsonParseException("Reference to a not yet declared object");
                });
    }

    public <T> T find(int handle, Class<T> type) {
        var object = find(handle);

        if (type.isInstance(object)) {
            return type.cast(object);
        }

        throw new JsonParseException("Reference to something with the wrong type");
    }
}
