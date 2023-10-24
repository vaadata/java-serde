package sh.arnaud.serializeformat.ser;

import java.nio.ByteBuffer;

public class Serialize {
    public static ByteBuffer serialize(String json) throws Exception {
        var fromJson = new FromJson();
        var toStream = new ToStream();

        var data = fromJson.writeStreamFromJson(json);

        return toStream.serialize(data);
    }
}
