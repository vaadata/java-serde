package sh.arnaud.javaserde.codec;

import com.google.gson.*;
import sh.arnaud.javaserde.adapters.*;
import sh.arnaud.javaserde.types.grammar.*;
import sh.arnaud.javaserde.types.primitives.*;

public class Json {

    private final DeserializationContext deserializationContext = new DeserializationContext();
    private final SerializationContext serializationContext = new SerializationContext();

    public final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()

            .registerTypeAdapter(GrammarStream.class, new GrammarStreamAdapter())
            .registerTypeAdapter(GrammarContent.class, new GrammarContentAdapter())
            .registerTypeAdapter(GrammarObject.class, new GrammarObjectAdapter(deserializationContext))
            .registerTypeAdapter(GrammarNewObject.class, new GrammarNewObjectAdapter(serializationContext, deserializationContext))
            .registerTypeAdapter(GrammarNewEnum.class, new GrammarNewEnumAdapter(serializationContext, deserializationContext))
            .registerTypeAdapter(GrammarNewClass.class, new GrammarNewClassAdapter(serializationContext, deserializationContext))
            .registerTypeAdapter(GrammarNewArray.class, new GrammarNewArrayAdapter(serializationContext, deserializationContext))
            .registerTypeAdapter(GrammarNewString.class, new GrammarNewStringAdapter(serializationContext))
            .registerTypeAdapter(GrammarNewClassDesc.class, new GrammarNewClassDescAdapter(serializationContext, deserializationContext))
            .registerTypeAdapter(GrammarFieldDesc.class, new GrammarFieldDescAdapter())
            .registerTypeAdapter(GrammarBlockdata.class, new GrammarBlockdataAdapter())

            // TODO: Avoid this monstruosity somehow, why can't they use the abstract class if there's a type adapter on it ???
            .registerTypeAdapter(Primitive.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveBoolean.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveByte.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveChar.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveDouble.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveFloat.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveInteger.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveLong.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveShort.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())

            .create();

    public static JsonElement makeRef(int handle) {
        var object = new JsonObject();
        object.addProperty("@ref", handle);
        return object;
    }
}
