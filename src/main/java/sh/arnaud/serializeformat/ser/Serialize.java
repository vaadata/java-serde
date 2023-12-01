package sh.arnaud.serializeformat.ser;

import sh.arnaud.serializeformat.codec.Encoder;

import java.nio.ByteBuffer;

public class Serialize {
    public static ByteBuffer serialize(String json) throws Exception {
        var fromJson = new FromJson();
        var toStream = new Encoder();

        var data = fromJson.writeStreamFromJson(json);

        return toStream.serialize(data);
    }
}
