package sh.arnaud.javaserde.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.javaserde.types.grammar.GrammarContent;
import sh.arnaud.javaserde.types.grammar.GrammarStream;

import java.lang.reflect.Type;
import java.util.List;

public class GrammarStreamAdapter implements JsonSerializer<GrammarStream>, JsonDeserializer<GrammarStream> {
    @Override
    public GrammarStream deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return new GrammarStream(context.deserialize(src, new TypeToken<List<GrammarContent>>() {}.getType()));
    }

    @Override
    public JsonElement serialize(GrammarStream src, Type type, JsonSerializationContext context) {
        return context.serialize(src.contents);
    }
}
