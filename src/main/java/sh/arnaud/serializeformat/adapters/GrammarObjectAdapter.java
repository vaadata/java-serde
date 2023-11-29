package sh.arnaud.serializeformat.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import sh.arnaud.serializeformat.next.stream.types.objects.*;
import sh.arnaud.serializeformat.next.stream.types.primitives.PrimitiveJson;

import java.lang.reflect.Type;
import java.util.Map;

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

                return deserializationContext.seen.entrySet().stream()
                        .filter(set -> set.getValue() == handle)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElseThrow(() -> {
                            System.out.println(handle);
                            return new JsonParseException("Reference to a not yet declared object");
                        });
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

        System.out.println(src);
        throw new JsonParseException("Not implemented yet!");
    }
}
