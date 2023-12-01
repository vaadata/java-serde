package sh.arnaud.javaserde.de;

import sh.arnaud.javaserde.codec.Decoder;

import java.nio.ByteBuffer;

public class Deserialize {
    public static String deserialize(ByteBuffer buffer) throws Exception {
        var fromStream = new Decoder();
        var toJson = new ToJson();

        var data = fromStream.readStream(buffer);

        return toJson.toJson(data);
    }

    public static String deserialize(byte[] buffer) throws Exception {
        return deserialize(ByteBuffer.wrap(buffer));
    }
}
