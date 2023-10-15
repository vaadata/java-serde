package sh.arnaud.serializeformat.langs.java;

import com.google.gson.*;
import sh.arnaud.serializeformat.langs.java.grammar.*;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDesc;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDescInfo;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.TypeReferenceClassDesc;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.TypecodeClassDesc;

import java.nio.ByteBuffer;
import java.util.*;

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

    public TypeReference readPrevObject(ByteBuffer data) throws Exception {
        expectByte(data, TC_REFERENCE);

        var handle = data.getInt();

        if (!resources.hasResource(handle)) {
            throw new Exception("No resource found for the given handle.");
        }

        return new TypeReference(handle);
    }

    public String readStreamToJson(ByteBuffer data) throws Exception {
        var stream = readStream(data);

        // Pre dump

        /*Gson gsonpre = new GsonBuilder().create();

        SerializeFormat.api.logging().logToOutput("readStreamToJson");
        try {
            SerializeFormat.api.logging().logToOutput(gsonpre.toJson(stream));
        } catch (Exception e) {
            SerializeFormat.api.logging().logToOutput(e.getMessage());
            SerializeFormat.api.logging().logToError(e);
            return "error";
        }*/

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(TypeGeneric.class, (JsonSerializer<TypeGeneric>) (src, typeOfSrc, context) -> context.serialize(src.value))
                .registerTypeAdapter(TypeReferenceClassDesc.class, (JsonSerializer<TypeReferenceClassDesc>) (src, typeOfSrc, context) -> {
                    var object = new JsonObject();
                    object.addProperty("@ref", src.handle);
                    return object;
                })
                /*.registerTypeAdapter(TypeClass.class, (JsonSerializer<TypeClass>) (src, typeOfSrc, context) -> {
                    if (!(src.classDesc instanceof TypeNormalClassDesc)) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    return context.serialize(((TypeNormalClassDesc)src.classDesc).className);
                })
                .registerTypeAdapter(TypeArray.class, (JsonSerializer<TypeArray>) (src, typeOfSrc, context) -> context.serialize(src.items))
                *//*.registerTypeAdapter(TypeObject.class, (JsonSerializer<TypeObject>) (src, typeOfSrc, context) -> {
                    var object = context.serialize(src.classdataflatten).getAsJsonObject();

                    try {
                        var normalClass = src.classDesc.getAsNormalClassDesc(resources);

                        if (normalClass.className.equals("java.lang.StackTraceElement")) {
                            if (object.get("methodName").isJsonObject()) {
                                String formatted = "at %s(%s:%d)".formatted(
                                        object.get("declaringClass").getAsString(),
                                        object.get("fileName").getAsString(),
                                        object.get("lineNumber").getAsInt()
                                );

                                return new JsonPrimitive(formatted);
                            }


                            String formatted = "at %s.%s(%s:%d)".formatted(
                                    object.get("declaringClass").getAsString(),
                                    object.get("methodName").getAsString(),
                                    object.get("fileName").getAsString(),
                                    object.get("lineNumber").getAsInt()
                            );

                            return new JsonPrimitive(formatted);
                        }
                    } catch (Exception ignored) {
                        System.out.println("not a normal class desc");
                    }

                    object.addProperty(".class", src.classHandle);

                    return object;
                })*/
                .create();

        return gson.toJson(stream);
    }

    public List<TypeContent> readStream(ByteBuffer data) throws Exception {
        expectShort(data, STREAM_MAGIC);
        expectShort(data, STREAM_VERSION);

        var contents = new ArrayList<TypeContent>();

        while (data.hasRemaining()) {
            contents.add(readContent(data));
        }

        /*var classes = resources.storage.values()
                .stream()
                .filter(typeContent -> typeContent instanceof TypecodeClassDesc)
                .map(typeContent -> (TypecodeClassDesc) typeContent)
                .collect(Collectors.toList());

        var stream = new TypeStream(classes, list);*/

        return contents;
    }

    private TypeGeneric readBlockData(ByteBuffer data) throws Exception {
        byte value = peekByte(data);

        return switch (value) {
            case TC_BLOCKDATA -> readBlockDataShort(data);
            case TC_BLOCKDATALONG -> readBlockDataLong(data);
            default -> throw new Exception("Unexpected block data variant");
        };
    }

    private TypeGeneric readBlockDataLong(ByteBuffer data) throws Exception {
        expectByte(data, TC_BLOCKDATALONG);

        int size = data.getInt();
        byte[] buffer = new byte[size];
        data.get(buffer);
        return new TypeGeneric(buffer);
    }

    private TypeGeneric readBlockDataShort(ByteBuffer data) throws Exception {
        expectByte(data, TC_BLOCKDATA);

        int size = data.get() & 0xff;
        byte[] buffer = new byte[size];
        data.get(buffer);
        return new TypeGeneric(buffer);
    }

    private TypeContent readContent(ByteBuffer data) throws Exception {
        byte value = peekByte(data);

        return switch (value) {
            case TC_BLOCKDATA, TC_BLOCKDATALONG -> readBlockData(data);
            case TC_OBJECT, TC_CLASS, TC_ARRAY, TC_STRING, TC_LONGSTRING, TC_ENUM, TC_CLASSDESC, TC_REFERENCE, TC_NULL -> readObject(data);
            default -> throw new Exception("Invalid content magic byte:" + value);
        };
    }

    // NOTE: Maybe do not use TypeContent and instead use another type to avoid getting blockdata.
    private TypeContent readObject(ByteBuffer data) throws Exception {
        byte value = peekByte(data);

        return switch (value) {
            case TC_OBJECT -> readNewObject(data);
            case TC_CLASS -> readNewClass(data);
            case TC_ARRAY -> readNewArray(data);
            case TC_STRING, TC_LONGSTRING -> new TypeGeneric(readNewString(data));
            case TC_ENUM -> readNewEnum(data);
            case TC_CLASSDESC -> readNewClassDesc(data);
            case TC_REFERENCE -> readPrevObject(data);
            case TC_NULL -> {
                readNullReference(data);
                yield null;
            }
            //case TC_EXCEPTION -> readException(data);
            case TC_RESET -> {
                // Consume the byte from the buffer.

                throw new Exception("Reset are not handled yet!");
                //data.get();

                //resources.reset();

                //yield null;
            }
            default -> throw new Exception("Unexpected object variant: " + value);
        };
    }

    private void readException(ByteBuffer data) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    private TypeEnum readNewEnum(ByteBuffer data) throws Exception {
        expectByte(data, TC_ENUM);

        var classDesc = readClassDesc(data);
        var handle = resources.newHandle();
        var enumConstantName = readNewString(data);

        var e = new TypeEnum(resources, handle, classDesc, enumConstantName);
        resources.registerResource(e.handle, e);
        return e;
    }

    private TypeArray readNewArray(ByteBuffer data) throws Exception {
        expectByte(data, TC_ARRAY);

        var classDesc = readClassDesc(data);
        var newHandle = resources.newHandle();

        var classDescNormal = classDesc.getAsNormalClassDesc(resources);

        // TODO: assert classDesc no super class
        // TODO: assert className starts with [

        var typeArray = new TypeArray(newHandle, classDesc, resources);

        resources.registerResource(newHandle, typeArray);

        var size = data.getInt();

        List<Object> items = new ArrayList<>();

        TypeFieldDesc fakeField = new TypeFieldDesc(
                FieldTypeCode.fromByte((byte) classDescNormal.className.charAt(1)),
                null,
                null
        );

        for (var i = 0; i < size; i++) {
            items.add(readValue(data, fakeField));
        }

        typeArray.items = items;

        return typeArray;
    }

    private TypeObject readNewObject(ByteBuffer data) throws Exception {
        if (data.get() != TC_OBJECT) {
            throw new Exception("Unexpected TC_OBJECT");
        }
        
        var classDesc = readClassDesc(data);

        var typeobject = new TypeObject(resources, classDesc);

        var cursor = classDesc;

        List<Map<String, Object>> list = new ArrayList<>();
        List<TypecodeClassDesc> chain = new ArrayList<>();

        while (cursor != null) {
            if (cursor instanceof TypecodeClassDesc) {
                chain.add((TypecodeClassDesc) cursor);
            }

            if (cursor instanceof TypeReferenceClassDesc c) {
                chain.add(c.getAsNormalClassDesc(resources));
            }

            cursor = cursor.superClassDesc();
        }

        Collections.reverse(chain);;

        for (var c : chain) {
            list.add(readClassdata(data, c));
        }

        typeobject.classdata = list;

        if (!list.isEmpty()) {
            typeobject.classdataflatten = new HashMap<>(list.get(list.size() - 1));
            var last = typeobject.classdataflatten;

            int consecutiveEmpty = 0;

            while (consecutiveEmpty < list.size() && list.get(consecutiveEmpty).isEmpty()) {
                consecutiveEmpty++;
            }

            for (int i = 1; i < list.size() - consecutiveEmpty; i++) {
                var next = new HashMap<>(list.get(list.size() - 1 - i));
                last.put(".super", next);
                last = next;
            }
        }

        return typeobject;
    }

    private Map<String, Object> readClassdata(ByteBuffer data, TypecodeClassDesc currentClass) throws Exception {
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

    private void readObjectAnnotation(ByteBuffer data) throws Exception {
        while (peekByte(data) != TC_ENDBLOCKDATA) {
            readContent(data);
        }

        // Remove the TC_ENDBLOCKDATA from the buffer.
        data.get();
    }

    private Map<String, Object> readValues(ByteBuffer data, ClassDesc currentClass) throws Exception {
        // Handling only the values for a TC_CLASSDESC, not handling TC_PROXYCLASSDESC yet.
        if (!(currentClass instanceof TypecodeClassDesc)) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        // TODO: Optimization, allocate the right number of items
        Map<String, Object> list = new HashMap<>();

        for (var field : ((TypecodeClassDesc)currentClass).classDescInfo._fields) {
            var value = readValue(data, field);

            if (value instanceof TypeGeneric generic) {
                list.put(field.fieldName, generic.value);
            } else {
                list.put(field.fieldName, value);
            }
        }

        return list;
    }

    private TypeContent readValue(ByteBuffer data, TypeFieldDesc field) throws Exception {
        return switch (field.typecode) {
            case Byte -> new TypeGeneric(data.get());
            case Char -> new TypeGeneric(data.getChar());
            case Double -> new TypeGeneric(data.getDouble());
            case Float -> new TypeGeneric(data.getFloat());
            case Integer -> new TypeGeneric(data.getInt());
            case Long -> new TypeGeneric(data.getLong());
            case Short -> new TypeGeneric(data.getShort());
            case Boolean -> new TypeGeneric(data.get() != 0);
            case Object -> readObject(data);
            case Array -> readNewArray(data);
        };
    }

    private ClassDesc readClassDesc(ByteBuffer data) throws Exception {
        byte value = data.get(data.position());

        return switch (value) {
            case TC_CLASSDESC -> readNewClassDesc(data);
            case TC_REFERENCE -> new TypeReferenceClassDesc(resources, readPrevObject(data).reference);
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

        return new TypeClass(resources, classDesc);
    }

    private ClassDesc readNewClassDesc(ByteBuffer data) throws Exception {
        var value = peekByte(data);

        return switch (value) {
            case TC_CLASSDESC -> {
                // Consume the byte from the buffer
                data.get();

                var className = readUtf(data);
                var serialVersionUID = data.getLong();

                var classDesc = new TypecodeClassDesc(resources, className, serialVersionUID);

                classDesc.classDescInfo = readClassDescInfo(data);

                yield classDesc;
            }

            case TC_PROXYCLASSDESC -> throw new UnsupportedOperationException("Not yet implemented!");

            default -> throw new Exception("Invalid newClassDesc magic byte");
        };
    }

    private ClassDescInfo readClassDescInfo(ByteBuffer data) throws Exception {
        var classDescFlags = data.get();
        List<TypeFieldDesc> fields = readFields(data);
        readClassAnnotation(data);
        var superClassDesc = readClassDesc(data);

        return new ClassDescInfo(classDescFlags, fields, superClassDesc, resources);
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
                resources.registerResource(resources.newHandle(), new TypeGeneric(string));
                return string;
            }
            case TC_LONGSTRING -> {
                data.get();
                var string = readLongUtf(data);
                resources.registerResource(resources.newHandle(), new TypeGeneric(string));
                return string;
            }
            case TC_REFERENCE -> {
                var resource = resources.fetchResource(readPrevObject(data).reference);

                if (resource instanceof TypeGeneric wrap) {
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
