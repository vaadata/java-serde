package sh.arnaud.serializeformat.langs.java;

import sh.arnaud.serializeformat.langs.java.grammar.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static java.io.ObjectStreamConstants.*;

public class FromSerialized {
    private final HandleManager resources = new HandleManager();

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

    public void readNullReference(ByteBuffer data) throws Exception {
        expectByte(data, TC_NULL);
    }

    public TypeContent readPrevObject(ByteBuffer data) throws Exception {
        expectByte(data, TC_REFERENCE);

        var handle = data.getInt();
        var resource = resources.fetchResource(handle);

        System.out.println("readPrevObject with handle " + handle);

        if (resource == null) {
            throw new Exception("No resource found for the given handle.");
        }

        return resource;
    }

    public TypeStream readStream(ByteBuffer data) throws Exception {
        expectShort(data, STREAM_MAGIC);
        expectShort(data, STREAM_VERSION);

        var list = new ArrayList<TypeContent>();

        while (data.hasRemaining()) {
            var content = readContent(data);
            list.add(content);
            System.out.println(content);
            /*if (content instanceof TypeObject) {
                var handle = ((TypeObject) content).classHandle;
                var classDesc = resources.fetchResource(handle);

                if (classDesc instanceof TypeNormalClassDesc) {
                    classes.add((TypeNormalClassDesc) classDesc);
                }
            }*/
        }

        var classes = resources.storage.values()
                .stream()
                .filter(typeContent -> typeContent instanceof TypeNormalClassDesc)
                .map(typeContent -> (TypeNormalClassDesc) typeContent)
                .collect(Collectors.toList());

        var stream = new TypeStream(classes, list);

        return stream;
    }

    private void readBlockData(ByteBuffer data) throws Exception {
        byte value = peekByte(data);

        switch (value) {
            case TC_BLOCKDATA -> readBlockDataShort(data);
            case TC_BLOCKDATALONG -> readBlockDataLong(data);
            default -> throw new Exception("Unexpected block data variant");
        }
    }

    private void readBlockDataLong(ByteBuffer data) throws Exception {
        if (data.get() != TC_BLOCKDATALONG) {
            throw new Exception("Invalid TC_BLOCKDATALONG");
        }

        int size = data.getInt();
        byte[] buffer = new byte[size];
        data.get(buffer);
    }

    private void readBlockDataShort(ByteBuffer data) throws Exception {
        if (data.get() != TC_BLOCKDATA) {
            throw new Exception("Invalid TC_BLOCKDATA");
        }

        int size = data.get() & 0xff;
        byte[] buffer = new byte[size];
        data.get(buffer);
    }

    private TypeContent readContent(ByteBuffer data) throws Exception {
        byte value = peekByte(data);

        return switch (value) {
            //case TC_BLOCKDATA, TC_BLOCKDATALONG -> readBlockData(data);
            case TC_OBJECT, TC_CLASS, TC_STRING, TC_LONGSTRING, TC_CLASSDESC, TC_REFERENCE, TC_NULL -> readObject(data);
            default -> throw new Exception("Invalid content magic byte:" + value);
        };
    }

    // NOTE: Maybe do not use TypeContent and instead use another type to avoid getting blockdata.
    private TypeContent readObject(ByteBuffer data) throws Exception {
        byte value = peekByte(data);

        return switch (value) {
            case TC_OBJECT -> readNewObject(data);
            //case TC_CLASS -> readNewClass(data);
            //case TC_ARRAY -> readNewArray(data);
            case TC_STRING, TC_LONGSTRING -> new TypeGeneric<>(readNewString(data));
            //case TC_ENUM -> readNewEnum(data);
            case TC_CLASSDESC -> readNewClassDesc(data);
            case TC_REFERENCE -> {
                System.out.println("reference");
                yield readPrevObject(data);
            }
            case TC_NULL -> {
                readNullReference(data);
                yield null;
            }
            //case TC_EXCEPTION -> readException(data);
            case TC_RESET -> {
                // Consume the byte from the buffer.
                data.get();

                resources.reset();

                yield null;
            }
            default -> throw new Exception("Unexpected object variant: " + value);
        };
    }

    private void readException(ByteBuffer data) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    private void readNewEnum(ByteBuffer data) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    private void readNewArray(ByteBuffer data) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    private TypeObject readNewObject(ByteBuffer data) throws Exception {
        if (data.get() != TC_OBJECT) {
            throw new Exception("Unexpected TC_OBJECT");
        }
        
        var classDesc = readClassDesc(data);
        var newHandle = resources.newHandle();

        var typeobject = new TypeObject(newHandle, classDesc);

        resources.registerResource(newHandle, typeobject);

        var cursor = classDesc;

        List<Map<String, Object>> list = new ArrayList<>();
        List<TypeNormalClassDesc> chain = new ArrayList<>();

        while (cursor != null) {
            if (cursor instanceof TypeNormalClassDesc) {
                chain.add((TypeNormalClassDesc) cursor);
            }

            cursor = cursor.superClassDesc(resources);
        }

        Collections.reverse(chain);;

        for (var c : chain) {
            list.add(readClassdata(data, c));
        }

        typeobject.classdata = list;

        return typeobject;
    }

    private Map<String, Object> readClassdata(ByteBuffer data, TypeNormalClassDesc currentClass) throws Exception {
        byte flag = currentClass.classDescInfo.classDescFlags;

        if ((SC_SERIALIZABLE & flag) == SC_SERIALIZABLE) {
            if ((SC_WRITE_METHOD & flag) == SC_WRITE_METHOD) {
                // wrclass objectAnnotation
                var values = readValues(data, currentClass);
                readObjectAnnotation(data);
                return values;
            } else {
                // nowrclass
                var values = readValues(data, currentClass);
                return values;
            }
        } else if ((SC_EXTERNALIZABLE & flag) == SC_EXTERNALIZABLE) {
            if ((SC_BLOCK_DATA & flag) == SC_BLOCK_DATA) {
                // objectAnnotation
                readObjectAnnotation(data);
            } else {
                // externalContents
                readExternalContents(data);
            }
        }
        return null;
    }

    private void readExternalContents(ByteBuffer data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void readObjectAnnotation(ByteBuffer data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Map<String, Object> readValues(ByteBuffer data, TypeClassDesc currentClass) throws Exception {
        // Handling only the values for a TC_CLASSDESC, not handling TC_PROXYCLASSDESC yet.
        if (!(currentClass instanceof TypeNormalClassDesc)) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        // TODO: Optimization, allocate the right number of items
        Map<String, Object> list = new HashMap<>();

        for (var field : ((TypeNormalClassDesc)currentClass).classDescInfo.fields) {
            var value = readValue(data, field);

            if (value != null && TypeGeneric.class.isAssignableFrom(value.getClass())) {
                list.put(field.fieldName, ((TypeGeneric<?>) value).value);
            } else {
                list.put(field.fieldName, value);
            }
        }
        return list;
    }

    private TypeContent readValue(ByteBuffer data, TypeFieldDesc field) throws Exception {
        return switch (field.typecode) {
            case Byte -> new TypeGeneric<>(data.get());
            case Char -> new TypeGeneric<>(data.getChar());
            case Double -> new TypeGeneric<>(data.getDouble());
            case Float -> new TypeGeneric<>(data.getFloat());
            case Integer -> new TypeGeneric<>(data.getInt());
            case Long -> new TypeGeneric<>(data.getLong());
            case Short -> new TypeGeneric<>(data.getShort());
            case Boolean -> new TypeGeneric<>(data.get() != 0);
            case Object -> readObject(data);
            default -> throw new UnsupportedOperationException("Not supported yet: " + field.typecode);
        };
    }

    private TypeClassDesc readClassDesc(ByteBuffer data) throws Exception {
        byte value = data.get(data.position());

        return switch (value) {
            case TC_CLASSDESC -> readNewClassDesc(data);
            case TC_REFERENCE -> {
                var resource = readPrevObject(data);

                if (!(resource instanceof TypeClassDesc)) {
                    throw new Exception("Handle referenced wrong type of element");
                }

                yield (TypeClassDesc) resource;
            }
            case TC_NULL -> {
                readNullReference(data);
                yield null;
            }
            default -> throw new Exception("Invalid class desc magic byte");
        };
    }

    private TypeClass readNewClass(ByteBuffer data) throws Exception {
        expectByte(data, TC_CLASS);

        var classDesc = readClassDesc(data);
        var handle = resources.newHandle();

        var typeClass = new TypeClass(handle, classDesc);

        resources.registerResource(typeClass.handle, typeClass);

        return typeClass;
    }

    private TypeClassDesc readNewClassDesc(ByteBuffer data) throws Exception {
        var value = peekByte(data);

        return switch (value) {
            case TC_CLASSDESC -> {
                // Consume the byte from the buffer
                data.get();

                var className = readUtf(data);
                var serialVersionUID = data.getLong();
                var newHandle = resources.newHandle();

                var classDesc = new TypeNormalClassDesc(newHandle, className, serialVersionUID);
                resources.registerResource(classDesc.handle, classDesc);

                classDesc.classDescInfo = readClassDescInfo(data);

                yield classDesc;
            }

            case TC_PROXYCLASSDESC -> throw new UnsupportedOperationException("Not yet implemented!");

            default -> throw new Exception("Invalid newClassDesc magic byte");
        };
    }

    private TypeClassDescInfo readClassDescInfo(ByteBuffer data) throws Exception {
        var classDescFlags = data.get();
        List<TypeFieldDesc> fields = readFields(data);
        readClassAnnotation(data);
        var superClassDesc = readClassDesc(data);

        return new TypeClassDescInfo(classDescFlags, fields, superClassDesc);
    }

    private void readClassAnnotation(ByteBuffer data) throws Exception {
        while (data.get(data.position()) != TC_ENDBLOCKDATA) {
            readContent(data);
        }

        // Remove the TC_ENDBLOCKDATA from stream.
        data.get();
    }

    private List<TypeFieldDesc> readFields(ByteBuffer data) throws Exception {
        short count = data.getShort();
        var fields = new ArrayList<TypeFieldDesc>(count);

        for (int i = 0; i < count; i++) {
            fields.add(readFieldDesc(data));
        }

        return fields;
    }

    private TypeFieldDesc readFieldDesc(ByteBuffer data) throws Exception {
        var typecode = data.get();
        if ("BCDFIJSZ[L".indexOf(typecode) < 0) {
            throw new Exception("Unexpected typecode");
        }

        var fieldName = readUtf(data);

        if (typecode == '[' || typecode == 'L') {
            return new TypeFieldDesc(FieldTypeCode.fromByte(typecode), fieldName, readNewString(data));
        } else {
            return new TypeFieldDesc(FieldTypeCode.fromByte(typecode), fieldName, null);
        }
    }

    private String readNewString(ByteBuffer data) throws Exception {
        var value = peekByte(data);

        switch (value) {
            case TC_STRING -> {
                data.get();
                var string = readUtf(data);
                resources.registerResource(resources.newHandle(), new TypeGeneric<>(string));
                return string;
            }
            case TC_LONGSTRING -> {
                data.get();
                var string = readLongUtf(data);
                resources.registerResource(resources.newHandle(), new TypeGeneric<>(string));
                return string;
            }
            case TC_REFERENCE -> {
                var resource = readPrevObject(data);

                if (TypeGeneric.class.isAssignableFrom(resource.getClass())) {
                    var wrap = (TypeGeneric<?>) resource;

                    if (wrap.value instanceof String) {
                        return (String) wrap.value;
                    }
                }

                throw new Exception("Resource exists for handle but wasn't a String");
            }
            default -> throw new Exception("Unexpected newString variant");
        }
    }

    private String readUtf(ByteBuffer data) {
        byte[] buffer = new byte[data.getShort()];

        data.get(buffer);
        return new String(buffer);
    }

    private String readLongUtf(ByteBuffer data) {
        // TODO: Never seen a string with a value bigger than the maximum integer, fixing this when it happens.
        byte[] buffer = new byte[(int)data.getLong()];

        data.get(buffer);
        return new String(buffer);
    }
}
