package sh.arnaud.serializeformat.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import sh.arnaud.serializeformat.types.grammar.GrammarBlockdata;
import sh.arnaud.serializeformat.types.grammar.GrammarContent;
import sh.arnaud.serializeformat.types.grammar.GrammarObject;

import java.lang.reflect.Type;

public class GrammarContentAdapter implements JsonDeserializer<GrammarContent> {
    @Override
    public GrammarContent deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (src.isJsonArray()) {
            return context.deserialize(src, GrammarBlockdata.class);
        } else {
            return context.deserialize(src, GrammarObject.class);
        }
    }
}
