package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewClassDesc;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewEnum;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewString;

import java.lang.reflect.Type;

public class GrammarNewEnumAdapter implements JsonSerializer<GrammarNewEnum>, JsonDeserializer<GrammarNewEnum> {
    private final SerializationContext serializationContext;
    private final DeserializationContext deserializationContext;

    public GrammarNewEnumAdapter(SerializationContext serializationContext, DeserializationContext deserializationContext) {
        this.serializationContext = serializationContext;
        this.deserializationContext = deserializationContext;
    }
    @Override
    public GrammarNewEnum deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        var object = src.getAsJsonObject();
        GrammarNewClassDesc classDesc = context.deserialize(object.get("@class"), GrammarNewClassDesc.class);
        var handle = object.get("@handle").getAsInt();

        if (deserializationContext.seen.containsValue(handle)) {
            throw new JsonParseException("Two different resource have the same handle");
        }

        var newEnum = new GrammarNewEnum(classDesc);
        deserializationContext.seen.put(newEnum, handle);

        newEnum.enumConstantName = context.deserialize(object.get("@variant"), GrammarNewString.class);

        return newEnum;
    }

    @Override
    public JsonElement serialize(GrammarNewEnum src, Type type, JsonSerializationContext context) {
        return serializationContext.referenceable(src, () -> {
            // We need to serialize the class before to ensure correct order in the handle generation.
            var classDesc = context.serialize(src.classDesc);

            var handle = serializationContext.register(src);

            var object = new JsonObject();
            object.add("@class", classDesc);
            object.add("@variant", context.serialize(src.enumConstantName));
            object.addProperty("@handle", handle);
            return object;
        });
    }
}
