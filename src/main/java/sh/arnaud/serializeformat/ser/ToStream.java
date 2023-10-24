package sh.arnaud.serializeformat.ser;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonPrimitive;
import sh.arnaud.serializeformat.grammar.*;
import sh.arnaud.serializeformat.grammar.classdesc.ClassDesc;
import sh.arnaud.serializeformat.grammar.classdesc.TypeReferenceClassDesc;
import sh.arnaud.serializeformat.grammar.classdesc.TypecodeClassDesc;

import java.nio.ByteBuffer;
import java.util.*;

import static java.io.ObjectStreamConstants.*;

@SuppressWarnings("UnstableApiUsage")
public class ToStream {
    private final ByteArrayDataOutput buffer = ByteStreams.newDataOutput();
    private final Map<Integer, Integer> handleMapping = new HashMap<>();
    private final Map<String, Integer> encounteredString = new HashMap<>();
    private int currentHandle = baseWireHandle;

    private int nextHandle(int premappingHandle) {
        // TODO: Check if duplicate and throw an error
        handleMapping.put(premappingHandle, currentHandle);
        return currentHandle++;
    }

    private int getMapped(int handle) {
        return handleMapping.get(handle);
    }

    public ByteBuffer serialize(List<TypeContent> contents) throws Exception {
        buffer.writeShort(STREAM_MAGIC);
        buffer.writeShort(STREAM_VERSION);

        for (var content : contents) {
            writeContent(content);
        }

        return ByteBuffer.wrap(buffer.toByteArray());
    }

    private void writeContent(TypeContent content) throws Exception {
        if (content instanceof TypeObject object) {
            writeNewObject(object);
        } else if (content instanceof TypeEnum newEnum) {

            if (handleMapping.containsKey(newEnum.handle)) {
                buffer.writeByte(TC_REFERENCE);
                buffer.writeInt(handleMapping.get(newEnum.handle));
                return;
            }

            buffer.writeByte(TC_ENUM);
            writeClassDesc(newEnum.classDesc);
            nextHandle(newEnum.handle);
            writeNewString(newEnum.enumConstantName);
        } else {
            System.out.println(content);
            throw new UnsupportedOperationException("Not yet implemented!");
        }
    }

    private void writeNewObject(TypeObject object) throws Exception {
        if (object == null) {
            buffer.writeByte(TC_NULL);
            return;
        }

        // We already saw this object, make a reference instead.
        if (handleMapping.containsKey(object.handle)) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(handleMapping.get(object.handle));
            return;
        }

        buffer.writeByte(TC_OBJECT);
        writeClassDesc(object.classDesc);
        nextHandle(object.handle);


        List<TypecodeClassDesc> chain = new ArrayList<>();

        var current = object.classDesc;
        while (current != null) {
            current.asTypecodeClassDesc().ifPresent(chain::add);
            current = current.superClassDesc();
        }

        Collections.reverse(chain);

        // TODO: Assert chain length is the same as the classdata

        int i = 0;
        for (var c : chain) {
            writeClassdata(c, object.classdata.get(i++));
        }
    }

    private void writeClassdata(TypecodeClassDesc classDesc, ClassData classData) throws Exception {
        if (classDesc.classDescInfo.isNowrclass() || classDesc.classDescInfo.isWrclass()) {
            for (int index = 0; index < classDesc.classDescInfo.fields.size(); index++) {
                var field = classDesc.classDescInfo.fields.get(index);
                var value = classData.values.get(index);

                switch (field.typecode) {
                    case Byte -> buffer.writeByte(((JsonPrimitive)value).getAsByte());
                    case Char -> buffer.writeChar(((JsonPrimitive)value).getAsString().charAt(0));
                    case Double -> buffer.writeDouble(((JsonPrimitive)value).getAsDouble());
                    case Float -> buffer.writeFloat(((JsonPrimitive)value).getAsFloat());
                    case Integer -> buffer.writeInt(((JsonPrimitive)value).getAsInt());
                    case Long -> buffer.writeLong(((JsonPrimitive)value).getAsLong());
                    case Short -> buffer.writeShort(((JsonPrimitive)value).getAsShort());
                    case Boolean -> buffer.writeBoolean(((JsonPrimitive)value).getAsBoolean());
                    case Array -> writeNewArray((TypeArray)value);
                    case Object -> writeNewObject((TypeObject)value);
                }
            }
        } else {
            throw new UnsupportedOperationException("Not implemented yet!");
        }

        if (classDesc.classDescInfo.isWrclass()) {
            throw new UnsupportedOperationException("Not yet implemented!");
        }
    }

    private void writeNewArray(TypeArray value) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    private void writeClassDesc(ClassDesc classDesc) throws Exception {
        if (classDesc == null) {
            buffer.writeByte(TC_NULL);
        } else if (classDesc instanceof TypeReferenceClassDesc rcd) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(getMapped(rcd.handle));
        } else {
            writeNewClassDesc(classDesc);
        }
    }

    private void writeNewClassDesc(ClassDesc classDesc) throws Exception {
        if (classDesc instanceof TypecodeClassDesc tcd) {
            buffer.writeByte(TC_CLASSDESC);
            writeUtf(tcd.className);
            buffer.writeLong(tcd.serialVersionUID);
            nextHandle(tcd.handle);

            // classdescinfo
            var infos = tcd.classDescInfo;
            buffer.writeByte(infos.classDescFlags);

            // TODO: Overflow if fields size is bigger than a short
            buffer.writeShort(infos.fields.size());
            for (var field : infos.fields) {
                buffer.writeByte(field.className1.charAt(0));
                writeUtf(field.fieldName);
                if (field.className1.length() > 1) {
                    writeNewString(field.className1);
                }
            }

            for (var annotation : infos.annotations) {
                writeContent(annotation);
            }

            buffer.writeByte(TC_ENDBLOCKDATA);

            writeClassDesc(infos.superClassDesc);
        } else {
            throw new UnsupportedOperationException("Proxy class desc not handled yet");
        }
    }

    private void writeNewString(String string) {
        // We suppose that the algorithm tries to use a string reference every time the same string appears multiple
        // times.
        // TODO: We need to confirm if that's the case (or not for small string for example)
        if (encounteredString.containsKey(string)) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(encounteredString.get(string));
            return;
        }

        // TODO: Check if it's okay with negative shorts
        if (string.length() > 0xffff) {
            buffer.writeByte(TC_LONGSTRING);
            buffer.writeLong(string.length());
        } else {
            buffer.write(TC_STRING);
            buffer.writeShort(string.length());
        }
        buffer.write(string.getBytes());

        encounteredString.put(string, currentHandle++);
    }

    private void writeUtf(String string) {
        // TODO: Assert string is not too big
        buffer.writeShort(string.length());
        buffer.write(string.getBytes());
    }
}
