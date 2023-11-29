package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.next.stream.types.GrammarContent;
import sh.arnaud.serializeformat.next.stream.types.objects.*;

import java.lang.reflect.Type;
import java.util.List;

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

        if (object.has("@ref")) {
            var handle = object.get("@ref").getAsInt();
            return deserializationContext.find(handle, GrammarNewClassDesc.class);
        }

        var className = object.get("@name").getAsString();
        var serialVersionUID = object.get("@serial").getAsLong();
        var handle = object.get("@handle").getAsInt();

        var newClassDesc = new GrammarNewClassDesc(className, serialVersionUID);
        deserializationContext.register(newClassDesc, handle);


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
