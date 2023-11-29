package sh.arnaud.serializeformat.serde;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.adapters.*;
import sh.arnaud.serializeformat.next.stream.types.GrammarBlockdata;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewArray;
import sh.arnaud.serializeformat.next.stream.types.FieldTypeCode;
import sh.arnaud.serializeformat.next.stream.types.GrammarContent;
import sh.arnaud.serializeformat.next.stream.types.GrammarStream;
import sh.arnaud.serializeformat.next.stream.types.objects.*;
import sh.arnaud.serializeformat.next.stream.types.primitives.*;

import java.io.ObjectStreamConstants;
import java.lang.reflect.Type;
import java.util.*;

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

            .registerTypeAdapter(GrammarFieldDesc.class, new GrammarFieldDescAdapter(serializationContext, deserializationContext))

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
