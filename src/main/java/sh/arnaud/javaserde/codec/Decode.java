package sh.arnaud.javaserde.codec;

import java.nio.ByteBuffer;

public class Decode {
    public static String decode(ByteBuffer buffer) throws Exception {
        var fromStream = new Decoder();
        var gson = new Json();
        var data = fromStream.readStream(buffer);

        return gson.gson.toJson(data);
    }

    public static String decode(byte[] buffer) throws Exception {
        return decode(ByteBuffer.wrap(buffer));
    }
}
