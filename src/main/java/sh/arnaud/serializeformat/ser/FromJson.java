package sh.arnaud.serializeformat.ser;

import sh.arnaud.serializeformat.next.stream.types.GrammarStream;
import sh.arnaud.serializeformat.serde.Json;

import java.io.ObjectStreamConstants;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class FromJson {
    // The key is the one found in the document, the value is the written one.
    private final Map<Integer, Integer> referenceMapping = new HashMap<>();
    private int currentHandle = ObjectStreamConstants.baseWireHandle;



    public GrammarStream writeStreamFromJson(String s) {
        /*writeStream(JsonParser.parseString(json));

        return ByteBuffer.wrap(buffer.toByteArray());*/

        Json json = new Json();

        var yo = json.gson.fromJson(s, GrammarStream.class);

        System.out.println(yo);

        return yo;
    }
/*
    public void writeStream(JsonElement document) throws Exception {
        buffer.writeShort(STREAM_MAGIC);
        buffer.writeShort(STREAM_VERSION);

        if (!document.isJsonArray()) {
            throw new Exception("Expected a JSON array");
        }

        for (JsonElement jsonElement : document.getAsJsonArray()) {
            writeContent(jsonElement);
        }
    }

    public void writeContent(JsonElement document) throws Exception {
        if (!document.isJsonObject()) {
            throw new Exception("Expected a JSON object");
        }

        var object = document.getAsJsonObject();

        if (object.has("@handle") && object.has("@class") && object.has("@data")) {
            writeNewObject(object);
        }
    }

    private void writeNewObject(JsonObject object) throws Exception {
        writeClassDesc(object.get("@class"));


    }

    private int registerHandle(int handle) {
        var h = currentHandle++;
        referenceMapping.put(handle, h);
        return h;
    }

    private void writeClassDesc(JsonElement element) throws Exception {
        if (element.isJsonNull()) {
            buffer.writeByte(TC_NULL);
            return;
        }

        var object = element.getAsJsonObject();

        if (object.has("@ref")) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(referenceMapping.get(object.get("@ref").getAsInt()));
            return;
        }

        buffer.writeByte(TC_CLASSDESC);

        var name = object.get("@name").getAsString();
        writeString(name);

        var serial = object.get("@serial").getAsLong();
        buffer.writeLong(serial);

        var handle = object.get("@handle").getAsInt();
        var newHandle = registerHandle(handle);

        var flags = object.get("@flags").getAsByte();
        buffer.writeByte(flags);

        var fields = object.get("@fields").getAsJsonObject();
        writeFields(fields);
        
        if (object.has("@annotations")) {
            var annotations = object.get("@annotations").getAsJsonArray();
            writeAnnotations(annotations);
        }

        var superClassDesc = object.get("@super");
        writeClassDesc(superClassDesc);
    }

    private void writeAnnotations(JsonArray annotations) throws Exception {
        for (JsonElement annotation : annotations) {
            writeContent(annotation);
        }

        buffer.writeByte(TC_ENDBLOCKDATA);
    }

    private void writeFields(JsonObject fields) {
        buffer.writeShort(fields.size());

        for (var entry : fields.entrySet()) {
            var className1 = entry.getValue().getAsString();

            buffer.writeByte(className1.charAt(0));
            writeString(entry.getKey());

            if (className1.length() > 1) {
                writeString(className1);
            }
        }
    }

    private void writeString(String string) {
        // TODO: Need to check if a negative short is used as unsigned value or not.
        if (string.length() > 0xffff) {
            buffer.writeByte(TC_LONGSTRING);
            buffer.writeLong(string.length());
        } else {
            buffer.writeByte(TC_STRING);
            buffer.writeShort(string.length());
        }

        buffer.write(string.getBytes());
    }*/
}
