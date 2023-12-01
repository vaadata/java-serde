package sh.arnaud.serializeformat.ser;

import sh.arnaud.serializeformat.types.grammar.GrammarStream;
import sh.arnaud.serializeformat.codec.Json;

public class FromJson {
    public GrammarStream writeStreamFromJson(String s) {

        Json json = new Json();

        var yo = json.gson.fromJson(s, GrammarStream.class);

        System.out.println(yo);

        return yo;
    }
}
