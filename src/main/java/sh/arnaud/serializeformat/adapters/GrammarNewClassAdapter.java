package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewClass;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewClassDesc;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewEnum;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewString;

import java.lang.reflect.Type;

import static sh.arnaud.serializeformat.serde.Json.makeRef;

public class GrammarNewClassAdapter implements JsonSerializer<GrammarNewClass>, JsonDeserializer<GrammarNewClass> {
    private final SerializationContext serializationContext;
    private final DeserializationContext deserializationContext;

    public GrammarNewClassAdapter(SerializationContext serializationContext, DeserializationContext deserializationContext) {
        this.serializationContext = serializationContext;
        this.deserializationContext = deserializationContext;
    }
    @Override
    public GrammarNewClass deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        var object = src.getAsJsonObject();
        GrammarNewClassDesc classDesc = context.deserialize(object.get("@class"), GrammarNewClassDesc.class);
        var handle = object.get("@handle").getAsInt();

        if (deserializationContext.seen.containsValue(handle)) {
            throw new JsonParseException("Two different resource have the same handle");
        }

        var newClass = new GrammarNewClass(classDesc);
        deserializationContext.seen.put(newClass, handle);

        return newClass;
    }

    @Override
    public JsonElement serialize(GrammarNewClass src, Type type, JsonSerializationContext context) {
        if (serializationContext.seen.containsKey(src)) {
            return makeRef(serializationContext.seen.get(src));
        }

        // We need to serialize the class before to ensure correct order in the handle generation.
        var classDesc = context.serialize(src.classDesc);

        // TODO: Clean this
        var handle = serializationContext.currentHandle++;
        serializationContext.seen.put(src, handle);

        var object = new JsonObject();
        object.add("@class", classDesc);
        object.addProperty("@handle", handle);

        return object;
    }
}
