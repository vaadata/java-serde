package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewArray;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewClassDesc;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewString;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarObject;

import java.lang.reflect.Type;
import java.util.List;

import static sh.arnaud.serializeformat.serde.Json.makeRef;

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
        if (!serializationContext.seen.containsKey(src)) {
            serializationContext.seen.put(src, serializationContext.currentHandle++);
        }

        return new JsonPrimitive(src.string);
    }
}
