package sh.arnaud.serializeformat.de;

import sh.arnaud.serializeformat.types.grammar.GrammarStream;
import sh.arnaud.serializeformat.codec.Json;

public class ToJson {
    private final Json json = new Json();

    public String toJson(GrammarStream stream) {
        return json.gson.toJson(stream);
    }
}
