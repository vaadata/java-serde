package sh.arnaud.serializeformat;

import sh.arnaud.serializeformat.grammar.TypeContent;

import java.io.ObjectStreamConstants;
import java.util.HashMap;
import java.util.Map;

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

    public boolean hasResource(int handle) {
        return storage.containsKey(handle);
    }

    /*public void reset() {
        currentHandle = ObjectStreamConstants.baseWireHandle;
        storage.clear();
    }*/
}
