package sh.arnaud.serializeformat;

import sh.arnaud.serializeformat.grammar.TypeContent;
import sh.arnaud.serializeformat.grammar.TypeGeneric;

import java.io.ObjectStreamConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HandleManager {
    public final Map<Integer, TypeContent> storage = new HashMap<>();
    private int currentHandle = ObjectStreamConstants.baseWireHandle;

    public int newHandle() {
        return currentHandle++;
    }

    public void registerResource(int handle, TypeContent resource) {
        storage.put(handle, resource);
    }

    public TypeContent fetchResource(int handle) {
        // TODO: Error if nothing ?
        return storage.get(handle);
    }

    public Optional<TypeContent> get(int handle) {
        if (hasResource(handle)) {
            return Optional.of(storage.get(handle));
        } else {
            return Optional.empty();
        }
    }

    public boolean hasResource(int handle) {
        return storage.containsKey(handle);
    }

    public boolean hackyHasString(String string) {
        for (var item : storage.values()) {
            if ((item instanceof TypeGeneric gen) && (gen.value instanceof String str) && (str == string)) {
                return true;
            }
        }

        return false;
    }

    /*public void reset() {
        currentHandle = ObjectStreamConstants.baseWireHandle;
        storage.clear();
    }*/
}
