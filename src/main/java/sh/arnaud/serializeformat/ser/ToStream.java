package sh.arnaud.serializeformat.ser;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import sh.arnaud.serializeformat.next.stream.types.FieldTypeCode;
import sh.arnaud.serializeformat.next.stream.types.grammar.GrammarBlockdata;
import sh.arnaud.serializeformat.next.stream.types.grammar.GrammarContent;
import sh.arnaud.serializeformat.next.stream.types.grammar.GrammarStream;
import sh.arnaud.serializeformat.next.stream.types.grammar.*;
import sh.arnaud.serializeformat.next.stream.types.primitives.PrimitiveJson;

import java.nio.ByteBuffer;
import java.util.*;

import static java.io.ObjectStreamConstants.*;

@SuppressWarnings("UnstableApiUsage")
public class ToStream {
    private final ByteArrayDataOutput buffer = ByteStreams.newDataOutput();
    private final Map<GrammarObject, Integer> seen = new HashMap<>();

    // It may happen that multiple instance of GrammarNewString exists for the same String, we use another map to track
    // this special case.
    private final Map<String, Integer> seenStrings = new HashMap<>();
    private int currentHandle = baseWireHandle;

    public ByteBuffer serialize(GrammarStream stream) throws Exception {
        buffer.writeShort(STREAM_MAGIC);
        buffer.writeShort(STREAM_VERSION);

        for (var content : stream.contents) {
            writeContent(content);
        }

        return ByteBuffer.wrap(buffer.toByteArray());
    }

    private void writeContent(GrammarContent content) throws Exception {
        if (content == null) {
            buffer.writeByte(TC_NULL);
            return;
        }

        if (content instanceof GrammarNewObject newObject) {
            writeNewObject(newObject);
            return;
        }

        if (content instanceof GrammarNewEnum newEnum) {
            writeNewEnum(newEnum);
            return;
        }

        if (content instanceof GrammarNewString newString) {
            writeNewString(newString);
            return;
        }

        if (content instanceof GrammarNewClass newClass) {
            writeNewClass(newClass);
            return;
        }

        if (content instanceof GrammarBlockdata blockdata) {
            writeBlockdata(blockdata);
            return;
        }

        System.out.println(content);
        throw new UnsupportedOperationException("Serialization of content not implemented yet!");

        /*if (content instanceof TypeObject object) {
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
        }*/
    }

    private void writeNewClass(GrammarNewClass newClass) throws Exception {
        // We already saw this object, make a reference instead.
        if (seen.containsKey(newClass)) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(seen.get(newClass));
            return;
        }

        buffer.writeByte(TC_CLASS);
        writeClassDesc(newClass.classDesc);
        seen.put(newClass, currentHandle++);
    }

    private void writeNewEnum(GrammarNewEnum newEnum) throws Exception {
        // We already saw this object, make a reference instead.
        if (seen.containsKey(newEnum)) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(seen.get(newEnum));
            return;
        }

        buffer.writeByte(TC_ENUM);
        writeClassDesc(newEnum.classDesc);

        seen.put(newEnum, currentHandle++);

        writeNewString(newEnum.enumConstantName);
    }

    private void writeNewArray(GrammarNewArray array) throws Exception {
        /*if (object == null) {
            buffer.writeByte(TC_NULL);
            return;
        }*/

        // We already saw this object, make a reference instead.
        if (seen.containsKey(array)) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(seen.get(array));
            return;
        }

        buffer.writeByte(TC_ARRAY);
        writeClassDesc(array.classDesc);

        seen.put(array, currentHandle++);

        buffer.writeInt(array.values.size());


        // TODO: Check validity of asTypecodeClassDesc
        GrammarFieldDesc fakeField = new GrammarFieldDesc(
                FieldTypeCode.fromByte(array.classDesc.className.getBytes()[1]).orElseThrow(() -> new Exception("Array wrong item class name")),
                null,
                null
        );

        for (var item : array.values) {
            writeValue(item, fakeField);
        }
    }

    private void writeNewObject(GrammarNewObject object) throws Exception {
        if (object == null) {
            buffer.writeByte(TC_NULL);
            return;
        }

        // We already saw this object, make a reference instead.
        if (seen.containsKey(object)) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(seen.get(object));
            return;
        }

        buffer.writeByte(TC_OBJECT);
        writeClassDesc(object.classDesc);

        seen.put(object, currentHandle++);


        List<GrammarNewClassDesc> chain = new ArrayList<>();

        var current = object.classDesc;
        while (current != null) {
            chain.add(current);
            current = current.classDescInfo.superClassDesc();
        }

        Collections.reverse(chain);

        // TODO: Assert chain length is the same as the classdata length (in case wrong number of super class)

        int i = 0;
        for (var c : chain) {
            writeClassdata(c, object.classdata.get(i++));
        }
    }

    private void writeClassDesc(GrammarNewClassDesc classDesc) throws Exception {
        if (classDesc == null) {
            buffer.writeByte(TC_NULL);
            return;
        }

        if (seen.containsKey(classDesc)) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(seen.get(classDesc));
            return;
        }

        buffer.writeByte(TC_CLASSDESC);
        writeUtf(classDesc.className);
        buffer.writeLong(classDesc.serial);
        var handle = currentHandle++;
        seen.put(classDesc, handle);

        var infos = classDesc.classDescInfo;
        buffer.write(infos.classDescFlags());

        // TODO: Overflow if fields size is bigger than a short
        buffer.writeShort(infos.fields().size());
        for (var field : infos.fields()) {
            buffer.writeByte(field.typeCode.typecode);
            writeUtf(field.fieldName);
            if (!field.typeCode.isPrimitive) {
                writeNewString(field.className1);
            }
        }

        for (var annotation : infos.annotations()) {
            writeContent(annotation);
        }

        buffer.writeByte(TC_ENDBLOCKDATA);

        writeClassDesc(classDesc.classDescInfo.superClassDesc());
    }

    private void writeNewString(GrammarNewString string) {
        if (seenStrings.containsKey(string.string)) {
            buffer.writeByte(TC_REFERENCE);
            buffer.writeInt(seenStrings.get(string.string));
            return;
        }

        seenStrings.put(string.string, currentHandle++);

        // TODO: Check if it's okay with negative shorts
        if (string.string.length() > 0xffff) {
            buffer.writeByte(TC_LONGSTRING);
            buffer.writeLong(string.string.length());
        } else {
            buffer.write(TC_STRING);
            buffer.writeShort(string.string.length());
        }

        buffer.write(string.string.getBytes());
    }

    private void writeBlockdata(GrammarBlockdata blockdata) {
        if (blockdata.blockdata.length > 0xff) {
            buffer.writeByte(TC_BLOCKDATALONG);
            buffer.writeInt(blockdata.blockdata.length);
        } else {
            buffer.writeByte(TC_BLOCKDATA);
            buffer.writeByte(blockdata.blockdata.length);
        }

        buffer.write(blockdata.blockdata);
    }

    private void writeValue(GrammarObject value, GrammarFieldDesc field) throws Exception {
        switch (field.typeCode) {
            case Array -> writeNewArray((GrammarNewArray) value);
            case Object -> writeContent(value);
            default -> {
                // TODO: Maybe move this to another function ?

                // Edge-case, when parsing the JSON document we're not aware if a string is here to represent a char or not.
                // We fix this issue here when we have all the information we need.
                if (value instanceof GrammarNewString newString && field.typeCode == FieldTypeCode.Char) {
                    buffer.writeChar(newString.string.charAt(0));
                    return;
                }

                if (!(value instanceof PrimitiveJson primitive)) {
                    System.out.println(field.typeCode);
                    System.out.println(value);
                    throw new UnsupportedOperationException("Not implemented yet!");
                }

                switch (field.typeCode) {
                    case Byte -> buffer.writeByte(primitive.asByte());
                    case Char -> buffer.writeChar(primitive.asChar());
                    case Double -> buffer.writeDouble(primitive.asDouble());
                    case Float -> buffer.writeFloat(primitive.asFloat());
                    case Integer -> buffer.writeInt(primitive.asInt());
                    case Long -> buffer.writeLong(primitive.asLong());
                    case Short -> buffer.writeShort(primitive.asShort());
                    case Boolean -> buffer.writeBoolean(primitive.asBoolean());
                }
            }
        }
    }

//    private void writeNewClass(TypeClass clas) throws Exception {
//        /*if (object == null) {
//            buffer.writeByte(TC_NULL);
//            return;
//        }*/
//
//        // We already saw this object, make a reference instead.
//        if (handleMapping.containsKey(clas.handle)) {
//            buffer.writeByte(TC_REFERENCE);
//            buffer.writeInt(handleMapping.get(clas.handle));
//            return;
//        }
//
//        buffer.writeByte(TC_CLASS);
//        writeClassDesc(clas.classDesc);
//        nextHandle(clas.handle);
//    }
//
    private void writeClassdata(GrammarNewClassDesc classDesc, GrammarClassdata classData) throws Exception {
        if (classDesc.classDescInfo.isNowrclass() || classDesc.classDescInfo.isWrclass()) {
            for (int index = 0; index < classDesc.classDescInfo.fields().size(); index++) {
                var field = classDesc.classDescInfo.fields().get(index);
                var value = classData.values().get(index);

                writeValue(value, field);
            }
        } else {
            throw new UnsupportedOperationException("Not implemented yet!");
        }

        if (classDesc.classDescInfo.isWrclass()) {
            for (var annotation : classData.annotations()) {
                writeContent(annotation);
            }
            buffer.writeByte(TC_ENDBLOCKDATA);
        }
    }

    private void writeUtf(String string) {
        // TODO: Assert string is not too big
        buffer.writeShort(string.length());
        buffer.write(string.getBytes());
    }
}
