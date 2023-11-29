package sh.arnaud.serializeformat.adapters;

import sh.arnaud.serializeformat.next.stream.types.objects.GrammarObject;

import java.util.HashMap;
import java.util.Map;

public class DeserializationContext {
    public final Map<GrammarObject, Integer> seen = new HashMap<>();
}
