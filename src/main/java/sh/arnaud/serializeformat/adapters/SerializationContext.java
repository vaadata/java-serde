package sh.arnaud.serializeformat.adapters;

import com.google.gson.JsonElement;
import sh.arnaud.serializeformat.types.grammar.GrammarObject;

import java.io.ObjectStreamConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static sh.arnaud.serializeformat.codec.Json.makeRef;

public class SerializationContext {
    private final Map<GrammarObject, Integer> storage = new HashMap<>();
    private int currentHandle = ObjectStreamConstants.baseWireHandle;

    public JsonElement referenceable(GrammarObject object, Supplier<JsonElement> ifAbsent) {
        return storage.containsKey(object) ? makeRef(storage.get(object)) : ifAbsent.get();
    }

    /**
     * This function can be called multiple times on the same object without generating a new handle.
     *
     * @param object The GrammarObject to register and generate a handle for.
     * @return The handle of the object.
     */
    public int register(GrammarObject object) {
        return storage.computeIfAbsent(object, _object -> currentHandle++);
    }
}
