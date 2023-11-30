package sh.arnaud.serializeformat.adapters;

import com.google.gson.*;
import sh.arnaud.serializeformat.next.stream.types.GrammarBlockdata;

import java.lang.reflect.Type;

public class GrammarBlockdataAdapter implements JsonSerializer<GrammarBlockdata>, JsonDeserializer<GrammarBlockdata> {
    @Override
    public GrammarBlockdata deserialize(JsonElement src, Type _type, JsonDeserializationContext context) throws JsonParseException {
        byte[] blockdata = context.deserialize(src, byte[].class);
        return new GrammarBlockdata(blockdata);
    }

    @Override
    public JsonElement serialize(GrammarBlockdata src, Type type, JsonSerializationContext context) {
        return context.serialize(src.blockdata);
    }
}
