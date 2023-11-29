package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewClass;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewClassDesc;

import java.lang.reflect.Type;

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

        var newClass = new GrammarNewClass(
            context.deserialize(object.get("@class"), GrammarNewClassDesc.class)
        );

        var handle = object.get("@handle").getAsInt();
        deserializationContext.register(newClass, handle);

        return newClass;
    }

    @Override
    public JsonElement serialize(GrammarNewClass src, Type type, JsonSerializationContext context) {
        return serializationContext.referenceable(src, () -> {
            // We need to serialize the class before to ensure correct order in the handle generation.
            var classDesc = context.serialize(src.classDesc);

            var handle = serializationContext.register(src);

            var object = new JsonObject();
            object.add("@class", classDesc);
            object.addProperty("@handle", handle);
            return object;
        });
    }
}
