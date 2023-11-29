package sh.arnaud.serializeformat.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import sh.arnaud.serializeformat.next.stream.types.GrammarBlockdata;
import sh.arnaud.serializeformat.next.stream.types.GrammarContent;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarObject;

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
