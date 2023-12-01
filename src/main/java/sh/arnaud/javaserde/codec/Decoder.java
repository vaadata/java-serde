package sh.arnaud.javaserde.codec;

import sh.arnaud.javaserde.types.FieldTypeCode;
import sh.arnaud.javaserde.types.grammar.*;
import sh.arnaud.javaserde.types.primitives.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.io.ObjectStreamConstants.*;

public class Decoder {
    private final HandleStorage handles = new HandleStorage();

    private void expectByte(ByteBuffer data, byte expected) throws Exception {
        byte value = data.get();

        if (value != expected) {
            throw new Exception("Unexpected byte received, expecting %d received %d".formatted(expected, value));
        }
    }

    private void expectShort(ByteBuffer data, short expected) throws Exception {
        short value = data.getShort();

        if (value != expected) {
            throw new Exception("Unexpected short received, expecting %d received %d".formatted(expected, value));
        }
    }

    private byte peekByte(ByteBuffer data) {
        return data.get(data.position());
    }

    public GrammarStream readStream(ByteBuffer data) throws Exception {
        expectShort(data, STREAM_MAGIC);
        expectShort(data, STREAM_VERSION);

        var contents = new ArrayList<GrammarContent>();

        while (data.hasRemaining()) {
            contents.add(readContent(data));
        }

        return new GrammarStream(contents);
    }

    public int readPrevObject(ByteBuffer data) throws Exception {
        expectByte(data, TC_REFERENCE);

        var handle = data.getInt();

        if (!handles.hasHandle(handle)) {
            throw new Exception("No resource found for the given handle.");
        }

        return handle;
    }

    private GrammarBlockdata readBlockdata(ByteBuffer data) throws Exception {
        // Consume the magic byte.
        byte value = data.get();

        if (value != TC_BLOCKDATA && value != TC_BLOCKDATALONG) {
            throw new Exception("Wrong magic byte for block data");
        }

        int size = value == TC_BLOCKDATA ? data.get() & 0xff : data.getInt();
        byte[] buffer = new byte[size];
        data.get(buffer);
        return new GrammarBlockdata(buffer);
    }

    private GrammarContent readContent(ByteBuffer data) throws Exception {
        byte value = peekByte(data);

        return switch (value) {
            case TC_BLOCKDATA, TC_BLOCKDATALONG -> readBlockdata(data);
            case TC_OBJECT, TC_CLASS, TC_ARRAY, TC_STRING, TC_LONGSTRING, TC_ENUM, TC_CLASSDESC, TC_REFERENCE, TC_NULL -> readObject(data);
            default -> throw new Exception("Invalid content magic byte:" + value);
        };
    }

    private GrammarObject readObject(ByteBuffer data) throws Exception {
        byte value = peekByte(data);

        return switch (value) {
            case TC_OBJECT -> readNewObject(data);
            case TC_CLASS -> readNewClass(data);
            case TC_ARRAY -> readNewArray(data);
            case TC_STRING, TC_LONGSTRING -> readNewString(data);
            case TC_ENUM -> readNewEnum(data);
            case TC_CLASSDESC -> readNewClassDesc(data);
            case TC_REFERENCE -> handles.retrieve(readPrevObject(data));
            case TC_NULL -> {
                data.get();
                yield null;
            }
            // `TC_EXCEPTION` and `TC_RESET` are handled here, may implement them in the future.
            default -> throw new Exception("Unexpected object variant: " + value);
        };
    }

    private GrammarNewEnum readNewEnum(ByteBuffer data) throws Exception {
        expectByte(data, TC_ENUM);

        var classDesc = readClassDesc(data);
        var newEnum = new GrammarNewEnum(classDesc);
        handles.register(newEnum);

        newEnum.enumConstantName = readNewString(data);

        return newEnum;
    }

    private GrammarNewArray readNewArray(ByteBuffer data) throws Exception {
        if (peekByte(data) == TC_REFERENCE) {
            data.get();

            return handles.retrieve(data.getInt(), GrammarNewArray.class);
        }

        expectByte(data, TC_ARRAY);

        var classDesc = readClassDesc(data);
        var newArray = new GrammarNewArray(classDesc);
        handles.register(newArray);

        // TODO: Check if the field is valid, maybe do this in the classDesc directly ?
        var fakeField = new GrammarFieldDesc(
            FieldTypeCode.fromByte((byte) classDesc.className.charAt(1)).get(),
            null,
            null
        );

        var size = data.getInt();
        List<GrammarObject> items = new ArrayList<>();

        for (var i = 0; i < size; i++) {
            items.add(readValue(data, fakeField));
        }

        newArray.values = items;

        return newArray;
    }

    private GrammarNewObject readNewObject(ByteBuffer data) throws Exception {
        expectByte(data, TC_OBJECT);

        var classDesc = readClassDesc(data);
        var typeobject = new GrammarNewObject(classDesc);
        handles.register(typeobject);

        List<GrammarNewClassDesc> chain = new ArrayList<>();

        var cursor = classDesc;
        while (cursor != null) {
            chain.add(cursor);
            cursor = cursor.classDescInfo.superClassDesc();
        }

        Collections.reverse(chain);

        List<GrammarClassdata> list = new ArrayList<>();
        for (var c : chain) {
            list.add(readClassdata(data, c));
        }

        typeobject.classdata = list;

        return typeobject;
    }

    private GrammarClassdata readClassdata(ByteBuffer data, GrammarNewClassDesc currentClass) throws Exception {
        byte flag = currentClass.classDescInfo.classDescFlags();

        if ((SC_SERIALIZABLE & flag) == SC_SERIALIZABLE) {
            if ((SC_WRITE_METHOD & flag) == SC_WRITE_METHOD) {
                // wrclass objectAnnotation
                var values = readValues(data, currentClass);
                var annotations = readObjectAnnotation(data);
                return new GrammarClassdata(values, annotations);
            } else {
                // nowrclass
                var values = readValues(data, currentClass);
                return new GrammarClassdata(values, new ArrayList<>());
            }
        }

        // `SC_EXTERNALIZABLE` class are not implemented yet.

        throw new Exception("Invalid class flags");
    }

    private List<GrammarContent> readObjectAnnotation(ByteBuffer data) throws Exception {
        var contents = new ArrayList<GrammarContent>();

        while (peekByte(data) != TC_ENDBLOCKDATA) {
            contents.add(readContent(data));
        }

        // Remove `TC_ENDBLOCKDATA`.
        data.get();

        return contents;
    }

    private List<GrammarObject> readValues(ByteBuffer data, GrammarNewClassDesc currentClass) throws Exception {
        var fields = currentClass.classDescInfo.fields();

        List<GrammarObject> list = new ArrayList<>(fields.size());

        for (var field : fields) {
            list.add(readValue(data, field));
        }

        return list;
    }

    private GrammarObject readValue(ByteBuffer data, GrammarFieldDesc field) throws Exception {
        return switch (field.typeCode) {
            case Byte -> new PrimitiveByte(data.get());
            case Char -> new PrimitiveChar(data.getChar());
            case Double -> new PrimitiveDouble(data.getDouble());
            case Float -> new PrimitiveFloat(data.getFloat());
            case Integer -> new PrimitiveInteger(data.getInt());
            case Long -> new PrimitiveLong(data.getLong());
            case Short -> new PrimitiveShort(data.getShort());
            case Boolean -> new PrimitiveBoolean(data.get() != 0);
            case Object -> readObject(data);
            case Array -> readNewArray(data);
        };
    }

    private GrammarNewClassDesc readClassDesc(ByteBuffer data) throws Exception {
        byte value = data.get(data.position());

        return switch (value) {
            case TC_CLASSDESC -> readNewClassDesc(data);
            case TC_REFERENCE -> handles.retrieve(readPrevObject(data), GrammarNewClassDesc.class);
            case TC_NULL -> {
                data.get();
                yield null;
            }
            default -> throw new Exception("Invalid class desc magic byte");
        };
    }

    private GrammarNewClass readNewClass(ByteBuffer data) throws Exception {
        expectByte(data, TC_CLASS);

        var classDesc = readClassDesc(data);
        var newClass = new GrammarNewClass(classDesc);
        handles.register(newClass);

        return newClass;
    }

    private GrammarNewClassDesc readNewClassDesc(ByteBuffer data) throws Exception {
        if (peekByte(data) == TC_PROXYCLASSDESC) {
            throw new UnsupportedOperationException("Proxy class description are not implemented yet!");
        }

        expectByte(data, TC_CLASSDESC);

        var className = readUtf(data);
        var serialVersionUID = data.getLong();
        var classDesc = new GrammarNewClassDesc(className, serialVersionUID);

        handles.register(classDesc);

        classDesc.classDescInfo = readClassDescInfo(data);

        return classDesc;
    }

    private GrammarClassDescInfo readClassDescInfo(ByteBuffer data) throws Exception {
        var classDescFlags = data.get();
        List<GrammarFieldDesc> fields = readFields(data);
        var annotations = readClassAnnotation(data);
        var superClassDesc = readClassDesc(data);

        return new GrammarClassDescInfo(classDescFlags, fields, annotations, superClassDesc);
    }

    private List<GrammarContent> readClassAnnotation(ByteBuffer data) throws Exception {
        List<GrammarContent> annotations = new ArrayList<>();

        while (peekByte(data) != TC_ENDBLOCKDATA) {
            annotations.add(readContent(data));
        }

        // Remove magic byte from stream.
        data.get();

        return annotations;
    }

    private List<GrammarFieldDesc> readFields(ByteBuffer data) throws Exception {
        short count = data.getShort();
        var fields = new ArrayList<GrammarFieldDesc>(count);

        for (int i = 0; i < count; i++) {
            fields.add(readFieldDesc(data));
        }

        return fields;
    }

    private GrammarFieldDesc readFieldDesc(ByteBuffer data) throws Exception {
        var typecode = FieldTypeCode.fromByte(data.get())
                .orElseThrow(() -> new Exception("Unexpected typecode"));

        var fieldName = readUtf(data);
        var className1 = typecode.isPrimitive ? null : readNewString(data);

        return new GrammarFieldDesc(typecode, fieldName, className1);
    }

    private GrammarNewString readNewString(ByteBuffer data) throws Exception {
        var value = peekByte(data);

        switch (value) {
            case TC_STRING -> {
                data.get();
                var string = new GrammarNewString(readUtf(data));
                handles.register(string);
                return string;
            }
            case TC_LONGSTRING -> {
                data.get();
                var string = new GrammarNewString(readLongUtf(data));
                handles.register(string);
                return string;
            }
            case TC_REFERENCE -> {
                return handles.retrieve(readPrevObject(data), GrammarNewString.class);
            }
            default -> throw new Exception("Unexpected newString variant");
        }
    }

    private String readUtf(ByteBuffer data) {
        byte[] buffer = new byte[data.getShort() & 0xffff];

        data.get(buffer);
        return new String(buffer);
    }

    private String readLongUtf(ByteBuffer data) {
        // TODO: Never seen a string with a value bigger than the maximum integer, fixing this when it happens.
        byte[] buffer = new byte[(int) data.getLong()];

        data.get(buffer);
        return new String(buffer);
    }
}
