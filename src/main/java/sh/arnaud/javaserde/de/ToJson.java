package sh.arnaud.javaserde.de;

import sh.arnaud.javaserde.types.grammar.GrammarStream;
import sh.arnaud.javaserde.codec.Json;

public class ToJson {
    private final Json json = new Json();

    public String toJson(GrammarStream stream) {
        return json.gson.toJson(stream);
    }
}
