package sh.arnaud.serializeformat.de;

import sh.arnaud.serializeformat.next.stream.FromStream;

import java.nio.ByteBuffer;

public class Deserialize {
    public static String deserialize(ByteBuffer buffer) throws Exception {
        var fromStream = new FromStream();
        var toJson = new ToJson();

        var data = fromStream.readStream(buffer);

        return toJson.toJson(data);
    }

    public static String deserialize(byte[] buffer) throws Exception {
        return deserialize(ByteBuffer.wrap(buffer));
    }
}
