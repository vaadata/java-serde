package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.next.stream.types.GrammarContent;
import sh.arnaud.serializeformat.next.stream.types.objects.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class GrammarNewClassDescAdapter implements JsonSerializer<GrammarNewClassDesc>, JsonDeserializer<GrammarNewClassDesc> {
    private final SerializationContext serializationContext;
    private final DeserializationContext deserializationContext;

    public GrammarNewClassDescAdapter(SerializationContext serializationContext, DeserializationContext deserializationContext) {
        this.serializationContext = serializationContext;
        this.deserializationContext = deserializationContext;
    }
    @Override
    public GrammarNewClassDesc deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        var object = src.getAsJsonObject();

        // TODO: DRY
        if (object.has("@ref")) {
            var handle = object.get("@ref").getAsInt();

            var x = deserializationContext.seen.entrySet().stream()
                    .filter(set -> set.getValue() == handle)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow(() -> {
                        System.out.println(handle);
                        return new JsonParseException("Reference to a not yet declared object");
                    });

            if (!(x instanceof GrammarNewClassDesc newClassDesc)) {
                throw new JsonParseException("Referenced something which is not a GrammarNewClassDesc");
            }

            return newClassDesc;
        }


        var className = object.get("@name").getAsString();
        var serialVersionUID = object.get("@serial").getAsLong();
        var handle = object.get("@handle").getAsInt();

        if (deserializationContext.seen.containsValue(handle)) {
            throw new JsonParseException("Two different resource have the same handle");
        }

        var newClassDesc = new GrammarNewClassDesc(className, serialVersionUID);
        deserializationContext.seen.put(newClassDesc, handle);


        var classDescFlags = object.get("@flags").getAsByte();
        List<GrammarFieldDesc> fields = context.deserialize(object.get("@fields"), new TypeToken<List<GrammarFieldDesc>>() {}.getType());
        List<GrammarContent> annotations = context.deserialize(object.get("@annotations"), new TypeToken<List<GrammarContent>>() {}.getType());

        GrammarNewClassDesc superClassDesc = context.deserialize(object.get("@super"), GrammarNewClassDesc.class);

        newClassDesc.classDescInfo = new GrammarClassDescInfo(classDescFlags, fields, annotations, superClassDesc);

        return newClassDesc;
    }

    @Override
    public JsonElement serialize(GrammarNewClassDesc src, Type type, JsonSerializationContext context) {
        return serializationContext.referenceable(src, () -> {
            var handle = serializationContext.register(src);

            var object = new JsonObject();
            object.addProperty("@handle", handle);
            object.addProperty("@name", src.className);
            object.addProperty("@serial", src.serialVersionUID);
            object.addProperty("@flags", src.classDescInfo.classDescFlags);
            object.add("@fields", context.serialize(src.classDescInfo.fields));
            object.add("@annotations", context.serialize(src.classDescInfo.annotations));
            object.add("@super", context.serialize(src.classDescInfo.superClassDesc));

            return object;
        });
    }
}
