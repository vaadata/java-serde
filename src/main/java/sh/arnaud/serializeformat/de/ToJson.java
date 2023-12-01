package sh.arnaud.serializeformat.de;

import sh.arnaud.serializeformat.next.stream.types.grammar.GrammarStream;
import sh.arnaud.serializeformat.serde.Json;

public class ToJson {
    private final Json json = new Json();

    public String toJson(GrammarStream stream) {
        return json.gson.toJson(stream);
    }
}
