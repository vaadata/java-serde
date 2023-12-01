package sh.arnaud.javaserde.adapters;

import com.google.gson.*;
import sh.arnaud.javaserde.types.grammar.GrammarNewString;

import java.lang.reflect.Type;

public class GrammarNewStringAdapter implements JsonSerializer<GrammarNewString>, JsonDeserializer<GrammarNewString> {
    private final SerializationContext serializationContext;

    public GrammarNewStringAdapter(SerializationContext serializationContext) {
        this.serializationContext = serializationContext;
    }
    @Override
    public GrammarNewString deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return new GrammarNewString(src.getAsString());
    }

    @Override
    public JsonElement serialize(GrammarNewString src, Type type, JsonSerializationContext context) {
        serializationContext.register(src);

        return new JsonPrimitive(src.string);
    }
}
