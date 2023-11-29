package sh.arnaud.serializeformat.adapters;

import sh.arnaud.serializeformat.next.stream.types.objects.GrammarObject;

import java.io.ObjectStreamConstants;
import java.util.HashMap;
import java.util.Map;

public class SerializationContext {
    public Map<GrammarObject, Integer> seen = new HashMap<>();
    public int currentHandle = ObjectStreamConstants.baseWireHandle;
}
