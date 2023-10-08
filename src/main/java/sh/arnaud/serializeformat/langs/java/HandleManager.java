package sh.arnaud.serializeformat.langs.java;

import sh.arnaud.serializeformat.langs.java.grammar.TypeContent;

import java.io.ObjectStreamConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class HandleManager {
    public final Map<Integer, TypeContent> storage = new HashMap<>();
    private int currentHandle = ObjectStreamConstants.baseWireHandle;

    public int newHandle() {
        System.out.println("Giving resource with handle: " + currentHandle);
        return currentHandle++;
    }

    public void registerResource(int handle, TypeContent resource) {
        storage.put(handle, resource);
    }

    public TypeContent fetchResource(int handle) {
        return storage.get(handle);
    }

    public void reset() {
        currentHandle = ObjectStreamConstants.baseWireHandle;
        storage.clear();
    }
}
