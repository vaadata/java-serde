package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import sh.arnaud.serializeformat.next.stream.types.FieldTypeCode;
import sh.arnaud.serializeformat.next.stream.types.objects.*;

import java.lang.reflect.Type;

public class GrammarFieldDescAdapter implements JsonSerializer<GrammarFieldDesc>, JsonDeserializer<GrammarFieldDesc> {
    @Override
    public GrammarFieldDesc deserialize(JsonElement src, Type _type, JsonDeserializationContext context) throws JsonParseException {
        var object = src.getAsJsonObject();

        GrammarNewString type = context.deserialize(object.get("@type"), GrammarNewString.class);
        var name = object.get("@name").getAsString();

        var typecode = FieldTypeCode.fromByte(type.string.getBytes()[0])
                .orElseThrow(() -> new JsonParseException("Cannot find typecode for the given field type"));

        return new GrammarFieldDesc(typecode, name, type);
    }

    @Override
    public JsonElement serialize(GrammarFieldDesc src, Type type, JsonSerializationContext context) {
        var object = new JsonObject();

        object.addProperty("@name", src.fieldName);

        // Ensure that the GrammarNewString is serialized separately to share the same instance between
        // Multiple strings.
        if (src.className1 != null) {
            object.add("@type", context.serialize(src.className1));
        } else {
            object.addProperty("@type", src.typeCode.typecodeString);
        }

        return object;
    }
}
