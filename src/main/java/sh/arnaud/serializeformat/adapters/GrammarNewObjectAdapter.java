package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarClassdata;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewClassDesc;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewObject;

import java.lang.reflect.Type;
import java.util.List;

public class GrammarNewObjectAdapter implements JsonSerializer<GrammarNewObject>, JsonDeserializer<GrammarNewObject> {
    private final SerializationContext serializationContext;
    private final DeserializationContext deserializationContext;

    public GrammarNewObjectAdapter(SerializationContext serializationContext, DeserializationContext deserializationContext) {
        this.serializationContext = serializationContext;
        this.deserializationContext = deserializationContext;
    }

    @Override
    public GrammarNewObject deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        var object = src.getAsJsonObject();
        GrammarNewClassDesc classDesc = context.deserialize(object.get("@class"), GrammarNewClassDesc.class);
        var handle = object.get("@handle").getAsInt();

        if (deserializationContext.seen.containsValue(handle)) {
            throw new JsonParseException("Two different resource have the same handle");
        }

        var newObject = new GrammarNewObject(classDesc);
        deserializationContext.seen.put(newObject, handle);

        newObject.classdata = context.deserialize(object.get("@data"), new TypeToken<List<GrammarClassdata>>() {}.getType());

        return newObject;
    }

    @Override
    public JsonElement serialize(GrammarNewObject src, Type type, JsonSerializationContext context) {
        return serializationContext.referenceable(src, () -> {
            // We need to serialize the class before to ensure correct order in the handle generation.
            var classDesc = context.serialize(src.classDesc);

            var handle = serializationContext.register(src);

            var object = new JsonObject();
            object.addProperty("@handle", handle);
            object.add("@class", classDesc);
            object.add("@data", context.serialize(src.classdata));
            return object;
        });
    }
}
