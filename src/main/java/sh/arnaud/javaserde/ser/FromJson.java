package sh.arnaud.javaserde.ser;

import sh.arnaud.javaserde.types.grammar.GrammarStream;
import sh.arnaud.javaserde.codec.Json;

public class FromJson {
    public GrammarStream writeStreamFromJson(String s) {

        Json json = new Json();

        var yo = json.gson.fromJson(s, GrammarStream.class);

        System.out.println(yo);

        return yo;
    }
}
