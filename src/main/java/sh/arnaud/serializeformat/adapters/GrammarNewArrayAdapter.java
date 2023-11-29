package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.next.stream.types.objects.*;

import java.lang.reflect.Type;
import java.util.List;

public class GrammarNewArrayAdapter implements JsonSerializer<GrammarNewArray>, JsonDeserializer<GrammarNewArray> {
    private final SerializationContext serializationContext;
    private final DeserializationContext deserializationContext;

    public GrammarNewArrayAdapter(SerializationContext serializationContext, DeserializationContext deserializationContext) {
        this.serializationContext = serializationContext;
        this.deserializationContext = deserializationContext;
    }
    @Override
    public GrammarNewArray deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        var object = src.getAsJsonObject();

        var newArray = new GrammarNewArray(
                context.deserialize(object.get("@class"), GrammarNewClassDesc.class)
        );

        var handle = object.get("@handle").getAsInt();
        deserializationContext.register(newArray, handle);

        newArray.values = context.deserialize(object.get("@items"), new TypeToken<List<GrammarObject>>() {}.getType());

        return newArray;
    }

    @Override
    public JsonElement serialize(GrammarNewArray src, Type type, JsonSerializationContext context) {
        return serializationContext.referenceable(src, () -> {
            // We need to serialize the class before to ensure correct order in the handle generation.
            var classDesc = context.serialize(src.classDesc);

            var handle = serializationContext.register(src);

            var object = new JsonObject();
            object.add("@class", classDesc);
            object.add("@items", context.serialize(src.values));
            object.addProperty("@handle", handle);
            return object;
        });
    }
}
