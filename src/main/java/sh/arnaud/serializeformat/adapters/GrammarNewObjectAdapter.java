package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.types.grammar.GrammarClassdata;
import sh.arnaud.serializeformat.types.grammar.GrammarNewClassDesc;
import sh.arnaud.serializeformat.types.grammar.GrammarNewObject;
import sh.arnaud.serializeformat.types.grammar.GrammarObject;
import sh.arnaud.serializeformat.types.primitives.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

        var newObject = new GrammarNewObject(
            context.deserialize(object.get("@class"), GrammarNewClassDesc.class)
        );

        var handle = object.get("@handle").getAsInt();
        deserializationContext.register(newObject, handle);

        // TODO: Make it toggleable. It is possible that the representation of these types is different in some
        //  system.
        // TODO: Find a way to avoid it being so ugly.
        boolean inserted = deserializePrimitive("java.lang.Boolean", 0, newObject, object)
                || deserializePrimitive("java.lang.Byte", 1, newObject, object)
                || deserializePrimitive("java.lang.Character", 0, newObject, object)
                || deserializePrimitive("java.lang.Double", 1, newObject, object)
                || deserializePrimitive("java.lang.Float", 1, newObject, object)
                || deserializePrimitive("java.lang.Integer", 1, newObject, object)
                || deserializePrimitive("java.lang.Long", 1, newObject, object)
                || deserializePrimitive("java.lang.Short", 1, newObject, object);

        // Use the default deserialization method.
        if (!inserted) {
            newObject.classdata = context.deserialize(
                    object.get("@data"),
                    new TypeToken<List<GrammarClassdata>>() {}.getType()
            );
        }

        return newObject;
    }

    private static boolean deserializePrimitive(String className, int offset, GrammarNewObject newObject, JsonObject object) {
        if (!newObject.classDesc.className.equals(className)) {
            return false;
        }

        var values = new ArrayList<GrammarObject>();
        values.add(new PrimitiveJson(object.get("@data").getAsJsonPrimitive()));
        newObject.classdata = new ArrayList<>();
        for (var i = 0; i < offset; i++) {
            newObject.classdata.add(new GrammarClassdata(new ArrayList<>(), new ArrayList<>()));
        }
        newObject.classdata.add(new GrammarClassdata(values, new ArrayList<>()));
        return true;
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

            // TODO: Make it toggleable. It is possible that the representation of these types is different in some
            //  system.
            // TODO: Find a way to avoid it being so ugly.
            boolean inserted = serializePrimitive("java.lang.Boolean", PrimitiveBoolean.class, 0, src, context, object)
                || serializePrimitive("java.lang.Byte", PrimitiveByte.class, 1, src, context, object)
                || serializePrimitive("java.lang.Character", PrimitiveChar.class, 0, src, context, object)
                || serializePrimitive("java.lang.Double", PrimitiveDouble.class, 1, src, context, object)
                || serializePrimitive("java.lang.Float", PrimitiveFloat.class, 1, src, context, object)
                || serializePrimitive("java.lang.Integer", PrimitiveInteger.class, 1, src, context, object)
                || serializePrimitive("java.lang.Long", PrimitiveLong.class, 1, src, context, object)
                || serializePrimitive("java.lang.Short", PrimitiveShort.class, 1, src, context, object);

            // Use the default serialization method.
            if (!inserted) {
                object.add("@data", context.serialize(src.classdata));
            }

            return object;
        });
    }

    private boolean serializePrimitive(String className, Class<?> classType, int offset, GrammarNewObject src, JsonSerializationContext context, JsonObject object) {
        if (!src.classDesc.className.equals(className)) {
            return false;
        }

        object.add("@data", context.serialize(src.classdata.get(offset).values().get(0), classType));
        return true;
    }
}
