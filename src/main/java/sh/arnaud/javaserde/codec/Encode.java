package sh.arnaud.javaserde.codec;

import sh.arnaud.javaserde.ser.FromJson;
import sh.arnaud.javaserde.types.grammar.GrammarStream;

import java.nio.ByteBuffer;

public class Encode {
    public static ByteBuffer serialize(String json) throws Exception {
        var toStream = new Encoder();
        var gson = new Json();
        var data = gson.gson.fromJson(json, GrammarStream.class);

        return toStream.serialize(data);
    }
}
