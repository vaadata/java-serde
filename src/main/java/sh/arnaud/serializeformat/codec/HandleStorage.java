package sh.arnaud.serializeformat.codec;

import sh.arnaud.serializeformat.types.grammar.GrammarObject;

import java.io.ObjectStreamConstants;
import java.util.HashMap;
import java.util.Map;

public class HandleStorage {
    private int currentHandle = ObjectStreamConstants.baseWireHandle;
    public final Map<Integer, GrammarObject> storage = new HashMap<>();

    public void register(GrammarObject object) {
        storage.put(currentHandle++, object);
    }

    public boolean hasHandle(int handle) {
        return storage.containsKey(handle);
    }

    public <T> T retrieve(int handle, Class<T> expectedClass) throws Exception {
        if (hasHandle(handle)) {
            var object = storage.get(handle);

            if (expectedClass.isInstance(object)) {
                return expectedClass.cast(object);
            }
        }

        throw new Exception("Class not found for given handle");
    }

    public GrammarObject retrieve(int handle) throws Exception {
        if (hasHandle(handle)) {
            return storage.get(handle);
        }

        throw new Exception("Class not found for given handle");
    }
}
