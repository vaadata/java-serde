package sh.arnaud.javaserde.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import sh.arnaud.javaserde.types.grammar.*;
import sh.arnaud.javaserde.types.primitives.PrimitiveJson;

import java.lang.reflect.Type;

public class GrammarObjectAdapter implements JsonDeserializer<GrammarObject> {
    private final DeserializationContext deserializationContext;

    public GrammarObjectAdapter(DeserializationContext deserializationContext) {
        this.deserializationContext = deserializationContext;
    }

    @Override
    public GrammarObject deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (src.isJsonObject()) {
            var object = src.getAsJsonObject();

            if (object.has("@ref")) {
                var handle = object.get("@ref").getAsInt();
                return deserializationContext.find(handle);
            }

            if (object.has("@handle") && object.has("@class") && object.has("@data")) {
                return context.deserialize(src, GrammarNewObject.class);
            }

            if (object.has("@handle") && object.has("@class") && object.has("@variant")) {
                return context.deserialize(src, GrammarNewEnum.class);
            }

            if (object.has("@handle") && object.has("@class") && object.has("@items")) {
                return context.deserialize(src, GrammarNewArray.class);
            }

            if (object.has("@handle") && object.has("@class")) {
                return context.deserialize(src, GrammarNewClass.class);
            }
        }

        if (src.isJsonPrimitive()) {
            var primitive = src.getAsJsonPrimitive();

            if (primitive.isString()) {
                return context.deserialize(src, GrammarNewString.class);
            }

            // TODO: Should we do something like deserialize as PrimitiveJson and register a new TypeAdapter ?
            return new PrimitiveJson(primitive);
        }

        throw new JsonParseException("Not implemented yet!");
    }
}
