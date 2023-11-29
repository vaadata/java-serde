package sh.arnaud.serializeformat.serde;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.next.stream.types.GrammarBlockdata;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewArray;
import sh.arnaud.serializeformat.next.stream.types.FieldTypeCode;
import sh.arnaud.serializeformat.next.stream.types.GrammarContent;
import sh.arnaud.serializeformat.next.stream.types.GrammarStream;
import sh.arnaud.serializeformat.next.stream.types.objects.*;
import sh.arnaud.serializeformat.next.stream.types.primitives.*;

import java.io.ObjectStreamConstants;
import java.lang.reflect.Type;
import java.util.*;

public class Json {

    private int currentHandle = ObjectStreamConstants.baseWireHandle;
    private Map<GrammarObject, Integer> seen = new HashMap<>();

    public final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()

            .registerTypeAdapter(GrammarStream.class, (JsonSerializer<GrammarStream>) (src, typeOfSrc, context) -> {
                return context.serialize(src.contents);
            })

            .registerTypeAdapter(GrammarStream.class, (JsonDeserializer<GrammarStream>) (json, typeOfT, context) -> new GrammarStream(context.deserialize(json, new TypeToken<List<GrammarContent>>() {}.getType())))

            .registerTypeAdapter(GrammarContent.class, (JsonDeserializer<GrammarContent>) (src, type, context) -> {
                if (src.isJsonArray()) {
                    return context.deserialize(src, GrammarBlockdata.class);
                } else {
                    return context.deserialize(src, GrammarObject.class);
                }
            })

            .registerTypeAdapter(GrammarObject.class, (JsonDeserializer<GrammarObject>) (src, type, context) -> {
                if (src.isJsonObject()) {
                    var object = src.getAsJsonObject();

                    if (object.has("@ref")) {
                        var handle = object.get("@ref").getAsInt();

                        return seen.entrySet().stream()
                                .filter(set -> set.getValue() == handle)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElseThrow(() -> {
                                    System.out.println(handle);
                                    return new JsonParseException("Reference to a not yet declared object");
                                });
                    }

                    if (object.has("@handle") && object.has("@class") && object.has("@data")) {
                        return context.deserialize(src, GrammarNewObject.class);
                    }

                    if (object.has("@handle") && object.has("@class") && object.has("@variant")) {
                        return context.deserialize(src, GrammarNewEnum.class);
                    }

                    if (object.has("@handle") && object.has("@class") && object.has("@items")) {
                        return context.deserialize(src, GrammarNewArray.class);
                    }

                    if (object.has("@handle") && object.has("@class")) {
                        return context.deserialize(src, GrammarNewClass.class);
                    }
                }

                if (src.isJsonPrimitive()) {
                    var primitive = src.getAsJsonPrimitive();

                    if (primitive.isString()) {
                        return context.deserialize(src, GrammarNewString.class);
                    }

                    // TODO: Should we do something like deserialize as PrimitiveJson and register a new TypeAdapter ?
                    return new PrimitiveJson(primitive);
                }

                System.out.println(src);
                throw new JsonParseException("Not implemented yet!");
            })

            .registerTypeAdapter(GrammarNewObject.class, (JsonDeserializer<GrammarNewObject>) (src, type, context) -> {
                var object = src.getAsJsonObject();
                GrammarNewClassDesc classDesc = context.deserialize(object.get("@class"), GrammarNewClassDesc.class);
                var handle = object.get("@handle").getAsInt();

                if (seen.containsValue(handle)) {
                    throw new JsonParseException("Two different resource have the same handle");
                }

                var newObject = new GrammarNewObject(classDesc);
                seen.put(newObject, handle);

                newObject.classdata = context.deserialize(object.get("@data"), new TypeToken<List<GrammarClassdata>>() {}.getType());

                return newObject;
            })

            .registerTypeAdapter(GrammarNewObject.class, (JsonSerializer<GrammarNewObject>) (src, typeOfSrc, context) -> {
                // TODO: Already seen ? Make it a reference

                if (seen.containsKey(src)) {
                    return makeRef(seen.get(src));
                }

                // We need to serialize the class before to ensure correct order in the handle generation.
                var classDesc = context.serialize(src.classDesc);

                // TODO: Clean this
                var handle = currentHandle++;
                seen.put(src, handle);

                var object = new JsonObject();
                object.addProperty("@handle", handle);
                object.add("@class", classDesc);
                object.add("@data", context.serialize(src.classdata));

                return object;
            })

            .registerTypeAdapter(GrammarNewEnum.class, (JsonDeserializer<GrammarNewEnum>) (src, typeOfSrc, context) -> {
                var object = src.getAsJsonObject();
                GrammarNewClassDesc classDesc = context.deserialize(object.get("@class"), GrammarNewClassDesc.class);
                var handle = object.get("@handle").getAsInt();

                if (seen.containsValue(handle)) {
                    throw new JsonParseException("Two different resource have the same handle");
                }

                var newEnum = new GrammarNewEnum(classDesc);
                seen.put(newEnum, handle);

                newEnum.enumConstantName = context.deserialize(object.get("@variant"), GrammarNewString.class);

                return newEnum;
            })

            .registerTypeAdapter(GrammarNewEnum.class, (JsonSerializer<GrammarNewEnum>) (src, typeOfSrc, context) -> {
                if (seen.containsKey(src)) {
                    return makeRef(seen.get(src));
                }

                // We need to serialize the class before to ensure correct order in the handle generation.
                var classDesc = context.serialize(src.classDesc);

                // TODO: Clean this
                var handle = currentHandle++;
                seen.put(src, handle);

                var object = new JsonObject();
                object.add("@class", classDesc);
                object.add("@variant", context.serialize(src.enumConstantName));
                object.addProperty("@handle", handle);

                return object;
            })

            .registerTypeAdapter(GrammarNewClass.class, (JsonSerializer<GrammarNewClass>) (src, typeOfSrc, context) -> {
                if (seen.containsKey(src)) {
                    return makeRef(seen.get(src));
                }

                // We need to serialize the class before to ensure correct order in the handle generation.
                var classDesc = context.serialize(src.classDesc);

                // TODO: Clean this
                var handle = currentHandle++;
                seen.put(src, handle);

                var object = new JsonObject();
                object.add("@class", classDesc);
                object.addProperty("@handle", handle);

                return object;
            })

            .registerTypeAdapter(GrammarNewClass.class, (JsonDeserializer<GrammarNewClass>) (src, typeOfSrc, context) -> {
                var object = src.getAsJsonObject();
                GrammarNewClassDesc classDesc = context.deserialize(object.get("@class"), GrammarNewClassDesc.class);
                var handle = object.get("@handle").getAsInt();

                if (seen.containsValue(handle)) {
                    throw new JsonParseException("Two different resource have the same handle");
                }

                var newClass = new GrammarNewClass(classDesc);
                seen.put(newClass, handle);

                return newClass;
            })

            .registerTypeAdapter(GrammarNewArray.class, (JsonDeserializer<GrammarNewArray>) (src, type, context) -> {
                var object = src.getAsJsonObject();
                GrammarNewClassDesc classDesc = context.deserialize(object.get("@class"), GrammarNewClassDesc.class);
                var handle = object.get("@handle").getAsInt();

                if (seen.containsValue(handle)) {
                    throw new JsonParseException("Two different resource have the same handle");
                }

                var newArray = new GrammarNewArray(classDesc);
                seen.put(newArray, handle);

                newArray.values = context.deserialize(object.get("@items"), new TypeToken<List<GrammarObject>>() {}.getType());

                return newArray;
            })

            .registerTypeAdapter(GrammarNewArray.class, (JsonSerializer<GrammarNewArray>) (src, typeOfSrc, context) -> {
                if (seen.containsKey(src)) {
                    return makeRef(seen.get(src));
                }

                // We need to serialize the class before to ensure correct order in the handle generation.
                var classDesc = context.serialize(src.classDesc);

                // TODO: Clean this
                var handle = currentHandle++;
                seen.put(src, handle);

                var object = new JsonObject();
                object.add("@class", classDesc);
                object.add("@items", context.serialize(src.values));
                object.addProperty("@handle", handle);

                return object;
            })

            .registerTypeAdapter(GrammarNewString.class, (JsonDeserializer<GrammarNewString>) (src, type, context) -> new GrammarNewString(src.getAsString()))

            .registerTypeAdapter(GrammarNewString.class, (JsonSerializer<GrammarNewString>) (src, type, context) -> {
                // TODO: Already seen ? Make it a reference

                if (!seen.containsKey(src)) {
                    seen.put(src, currentHandle++);
                }

                return new JsonPrimitive(src.string);
            })

            .registerTypeAdapter(GrammarNewClassDesc.class, (JsonDeserializer<GrammarNewClassDesc>) (src, type, context) -> {
                var object = src.getAsJsonObject();

                // TODO: DRY
                if (object.has("@ref")) {
                    var handle = object.get("@ref").getAsInt();

                    var x = seen.entrySet().stream()
                            .filter(set -> set.getValue() == handle)
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElseThrow(() -> {
                                System.out.println(handle);
                                return new JsonParseException("Reference to a not yet declared object");
                            });

                    if (!(x instanceof GrammarNewClassDesc newClassDesc)) {
                        throw new JsonParseException("Referenced something which is not a GrammarNewClassDesc");
                    }

                    return newClassDesc;
                }


                var className = object.get("@name").getAsString();
                var serialVersionUID = object.get("@serial").getAsLong();
                var handle = object.get("@handle").getAsInt();

                if (seen.containsValue(handle)) {
                    throw new JsonParseException("Two different resource have the same handle");
                }

                var newClassDesc = new GrammarNewClassDesc(className, serialVersionUID);
                seen.put(newClassDesc, handle);


                var classDescFlags = object.get("@flags").getAsByte();
                List<GrammarFieldDesc> fields = context.deserialize(object.get("@fields"), new TypeToken<List<GrammarFieldDesc>>() {}.getType());
                List<GrammarContent> annotations = context.deserialize(object.get("@annotations"), new TypeToken<List<GrammarContent>>() {}.getType());

                GrammarNewClassDesc superClassDesc = context.deserialize(object.get("@super"), GrammarNewClassDesc.class);

                newClassDesc.classDescInfo = new GrammarClassDescInfo(classDescFlags, fields, annotations, superClassDesc);

                return newClassDesc;
            })

            .registerTypeAdapter(GrammarNewClassDesc.class, (JsonSerializer<GrammarNewClassDesc>) (src, type, context) -> {
                if (seen.containsKey(src)) {
                    return makeRef(seen.get(src));
                }

                // TODO: Clean this
                var handle = currentHandle++;
                seen.put(src, handle);

                var object = new JsonObject();
                object.addProperty("@handle", handle);
                object.addProperty("@name", src.className);
                object.addProperty("@serial", src.serialVersionUID);
                object.addProperty("@flags", src.classDescInfo.classDescFlags);
                object.add("@fields", context.serialize(src.classDescInfo.fields));
                object.add("@annotations", context.serialize(src.classDescInfo.annotations));
                object.add("@super", context.serialize(src.classDescInfo.superClassDesc));

                return object;
            })

            .registerTypeAdapter(GrammarFieldDesc.class, (JsonDeserializer<GrammarFieldDesc>) (src, _type, context) -> {
                var object = src.getAsJsonObject();

                GrammarNewString type = context.deserialize(object.get("@type"), GrammarNewString.class);
                var name = object.get("@name").getAsString();

                var typecode = FieldTypeCode.fromByte(type.string.getBytes()[0])
                        .orElseThrow(() -> new JsonParseException("Cannot find typecode for the given field type"));

                return new GrammarFieldDesc(typecode, name, type);
            })

            .registerTypeAdapter(GrammarFieldDesc.class, (JsonSerializer<GrammarFieldDesc>) (src, type, context) -> {
                var object = new JsonObject();

                object.addProperty("@name", src.fieldName);

                // Ensure that the GrammarNewString is serialized separately to share the same instance between
                // Multiple strings.
                if (src.className1 != null) {
                    object.add("@type", context.serialize(src.className1));
                } else {
                    object.addProperty("@type", src.typeCode.typecodeString);
                }

                return object;
            })

            // TODO: Avoid this monstruosity somehow, why can't they use the abstract class if there's a type adapter on it ???
            .registerTypeAdapter(Primitive.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveBoolean.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveByte.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveChar.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveDouble.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveFloat.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveInteger.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveLong.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())
            .registerTypeAdapter(PrimitiveShort.class, (JsonSerializer<Primitive>) (primitive, _type, _context) -> primitive.asJson())



//            .registerTypeAdapter(TypeReferenceClassDesc.class, (JsonSerializer<TypeReferenceClassDesc>) (src, typeOfSrc, context) -> {
//                var object = new JsonObject();
//                object.addProperty("@ref", src.handle);
//                return object;
//            })
//            /*.registerTypeAdapter(ClassData.class, (JsonSerializer<ClassData>) (src, typeOfSrc, context) -> {
//                var object = context.serialize(src.values).getAsJsonObject();
//
//                if (src.annotations != null) {
//                    object.add("@annotations", context.serialize(src.annotations));
//                }
//
//                return object;
//            })*/
//            .registerTypeAdapter(TypecodeClassDesc.class, (JsonSerializer<TypecodeClassDesc>) (src, typeOfSrc, context) -> {
//                var object = new JsonObject();
//                object.addProperty("@handle", src.handle);
//                object.addProperty("@name", src.className);
//                object.addProperty("@serial", src.serialVersionUID);
//                object.addProperty("@flags", src.classDescInfo.classDescFlags);
//                object.add("@fields", context.serialize(src.classDescInfo.fields));
//                if (!src.classDescInfo.annotations.isEmpty()) {
//                    object.add("@annotations", context.serialize(src.classDescInfo.annotations));
//                }
//                object.add("@super", context.serialize(src.classDescInfo.superClassDesc));
//                return object;
//            })
//            .registerTypeAdapter(TypeObject.class, (JsonSerializer<TypeObject>) (src, typeOfSrc, context) -> {
//            /*Optional<JsonElement> classdata = src.classDesc.asTypecodeClassDesc().map((classDesc) -> {
//                // TODO: Check serial number
//                switch (classDesc.className) {
//                    case "java.lang.Double" -> {
//                        var value = src.classdata.get(1).values.get("value");
//                        return new JsonPrimitive((Double) value);
//                    }
//                    case "java.lang.Boolean" -> {
//                        var value = src.classdata.get(0).values.get("value");
//                        return new JsonPrimitive((Boolean) value);
//                    }
//                    case "java.lang.Byte" -> {
//                        var value = src.classdata.get(1).values.get("value");
//                        return new JsonPrimitive((Byte) value);
//                    }
//                    case "java.lang.Character" -> {
//                        var value = src.classdata.get(0).values.get("value");
//                        return new JsonPrimitive((Character) value);
//                    }
//                    case "java.lang.Short" -> {
//                        var value = src.classdata.get(1).values.get("value");
//                        return new JsonPrimitive((Short) value);
//                    }
//                    case "java.lang.Float" -> {
//                        var value = src.classdata.get(1).values.get("value");
//                        return new JsonPrimitive((Float) value);
//                    }
//                    case "java.lang.Integer" -> {
//                        var value = src.classdata.get(1).values.get("value");
//                        return new JsonPrimitive((Integer) value);
//                    }
//                    case "java.lang.Long" -> {
//                        var value = src.classdata.get(1).values.get("value");
//                        return new JsonPrimitive((Long) value);
//                    }
//                }
//
//                return null;
//            });*/
//
//                var object = new JsonObject();
//                object.addProperty("@handle", src.handle);
//                object.add("@class", context.serialize(src.classDesc));
//                //object.add("@data", classdata.orElseGet(() -> context.serialize(src.classdata)));
//                object.add("@data", context.serialize(src.classdata));
//
//                return object;
//            })
//            /*.registerTypeAdapter(TypeClass.class, (JsonSerializer<TypeClass>) (src, typeOfSrc, context) -> {
//                if (!(src.classDesc instanceof TypeNormalClassDesc)) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//
//                return context.serialize(((TypeNormalClassDesc)src.classDesc).className);
//            })
//            .registerTypeAdapter(TypeArray.class, (JsonSerializer<TypeArray>) (src, typeOfSrc, context) -> context.serialize(src.items))
//            *//*.registerTypeAdapter(TypeObject.class, (JsonSerializer<TypeObject>) (src, typeOfSrc, context) -> {
//            var object = context.serialize(src.classdataflatten).getAsJsonObject();
//
//            try {
//                var normalClass = src.classDesc.getAsNormalClassDesc(resources);
//
//                if (normalClass.className.equals("java.lang.StackTraceElement")) {
//                    if (object.get("methodName").isJsonObject()) {
//                        String formatted = "at %s(%s:%d)".formatted(
//                                object.get("declaringClass").getAsString(),
//                                object.get("fileName").getAsString(),
//                                object.get("lineNumber").getAsInt()
//                        );
//
//                        return new JsonPrimitive(formatted);
//                    }
//
//
//                    String formatted = "at %s.%s(%s:%d)".formatted(
//                            object.get("declaringClass").getAsString(),
//                            object.get("methodName").getAsString(),
//                            object.get("fileName").getAsString(),
//                            object.get("lineNumber").getAsInt()
//                    );
//
//                    return new JsonPrimitive(formatted);
//                }
//            } catch (Exception ignored) {
//                System.out.println("not a normal class desc");
//            }
//
//            object.addProperty(".class", src.classHandle);
//
//            return object;
//        })*/
//            .registerTypeAdapter(TypeEnum.class, (JsonDeserializer<TypeEnum>) (json1, typeOfT, context) -> {
//                var object = json1.getAsJsonObject();
//
//                var handle = object.get("@handle").getAsInt();
//                var variant = object.get("@variant").getAsString();
//                ClassDesc classDesc = context.deserialize(object.get("@class"), ClassDesc.class);
//
//                var typeEnum = new TypeEnum(resources, handle, classDesc, variant);
//
//                resources.registerResource(handle, typeEnum);
//
//                return typeEnum;
//            })
//            .registerTypeAdapter(TypeClass.class, (JsonDeserializer<TypeClass>) (json1, typeOfT, context) -> {
//                var object = json1.getAsJsonObject();
//
//                ClassDesc classDesc = context.deserialize(object.get("@class"), ClassDesc.class);
//                var handle = object.get("@handle").getAsInt();
//
//                var typeClass = new TypeClass(resources, handle, classDesc);
//
//                resources.registerResource(handle, typeClass);
//
//                return typeClass;
//            })
//            .registerTypeAdapter(TypeReferenceClassDesc.class, (JsonDeserializer<TypeReferenceClassDesc>) (json1, typeOfT, context) -> {
//                try {
//                    return new TypeReferenceClassDesc(resources, json1.getAsJsonObject().get("@ref").getAsInt());
//                } catch (Exception e) {
//                    throw new JsonParseException(e);
//                }
//            })
//            .registerTypeAdapter(TypeContent.class, (JsonDeserializer<TypeContent>) (json12, typeOfT, context) -> {
//                if (json12.isJsonObject()) {
//                    var object = json12.getAsJsonObject();
//
//                    if (object.has("@ref")) {
//                        var resource = resources.fetchResource(object.get("@ref").getAsInt());
//
//                        if (resource == null) {
//                            System.out.println(object.get("@ref").getAsInt());
//                            throw new JsonParseException("reference to nothing");
//                        }
//                        return resource;
//                    }
//
//                    if (object.has("@handle") && object.has("@class") && object.has("@data")) {
//                        return context.deserialize(json12, TypeObject.class);
//                    }
//
//                    if (object.has("@handle") && object.has("@class") && object.has("@variant")) {
//                        return context.deserialize(json12, TypeEnum.class);
//                    }
//
//                    if (object.has("@class") && object.has("@handle")) {
//                        return context.deserialize(json12, TypeClass.class);
//                    }
//
//                    if (object.has("@value")) {
//                        var value = object.get("@value");
//
//                        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
//                            var generic = new TypeGeneric(value.getAsString());
//
//                            if (!resources.hackyHasString(value.getAsString())) {
//                                resources.registerResource(resources.newHandle(), generic);
//                            }
//                            return generic;
//                        }
//
//                        return new TypeGeneric(value);
//                    }
//                }
//
//                SerializeFormat.log(json12.toString());
//                throw new JsonParseException("Couldn't parse TypeContent");
//            })
//            .registerTypeAdapter(TypecodeClassDesc.class, (JsonDeserializer<TypecodeClassDesc>) (json1, typeOfT, context) -> {
//                var object = json1.getAsJsonObject();
//
//                var className = object.get("@name").getAsString();
//                var serialVersionId = object.get("@serial").getAsLong();
//                var newHandle = object.get("@handle").getAsInt();
//                var classDesc = new TypecodeClassDesc(resources, newHandle, className, serialVersionId);
//
//                System.out.println("Registering class");
//                System.out.println(newHandle);
//                resources.registerResource(newHandle, classDesc);
//
//                var flags = object.get("@flags").getAsByte();
//                var fields = object.get("@fields").getAsJsonArray();
//                var annotations = object.has("@annotations")
//                        ? object.get("@annotations").getAsJsonArray()
//                        : new JsonArray();
//                ClassDesc zuper = context.deserialize(object.get("@super"), ClassDesc.class);
//
//                var fieldsList = new ArrayList<TypeFieldDesc>();
//
//                for (var prefield : fields) {
//                    var field = prefield.getAsJsonObject();
//                    var fieldName = field.get("@name").getAsString();
//                    var className1 = field.get("@type").getAsString();
//                    TypeFieldDesc finalField;
//                    try {
//                        finalField = new TypeFieldDesc(FieldTypeCode.fromByte(className1.getBytes()[0]), fieldName, className1);
//                    } catch (Exception e) {
//                        throw new JsonParseException(e);
//                    }
//
//                    fieldsList.add(finalField);
//                }
//
//                List<TypeContent> annotationsList = context.deserialize(annotations, new TypeToken<List<TypeContent>>() {}.getType());
//
//                classDesc.classDescInfo = new ClassDescInfo(flags, fieldsList, annotationsList, zuper);
//
//                return classDesc;
//            })
//
//            .registerTypeAdapter(ClassDesc.class, (JsonDeserializer<ClassDesc>) (json13, typeOfT, context) -> {
//                if (json13.isJsonNull()) {
//                    return null;
//                }
//
//                var object = json13.getAsJsonObject();
//
//                if (object.has("@ref")) {
//                    return context.deserialize(json13, TypeReferenceClassDesc.class);
//                }
//
//                return context.deserialize(json13, TypecodeClassDesc.class);
//            })
//            .registerTypeAdapter(TypeArray.class, (JsonDeserializer<TypeArray>) (json1, typeOfT, context) -> {
//                var object = json1.getAsJsonObject();
//                ClassDesc classDesc = context.deserialize(object.get("@class"), ClassDesc.class);
//
//                var newHandle = object.get("@handle").getAsInt();
//
//                var typeArray = new TypeArray(resources, newHandle, classDesc);
//
//                resources.registerResource(newHandle, typeArray);
//
//                typeArray.items = context.deserialize(object.get("@items").getAsJsonArray(), new TypeToken<List<TypeContent>>() {}.getType());
//
//                return typeArray;
//            })
//            .registerTypeAdapter(TypeObject.class, (JsonDeserializer<TypeObject>) (json1, typeOfT, context) -> {
//                var object = json1.getAsJsonObject();
//                ClassDesc classDesc = context.deserialize(object.get("@class"), ClassDesc.class);
//
//                var newHandle = object.get("@handle").getAsInt();
//
//                var typeObject = new TypeObject(resources, newHandle, classDesc);
//
//                resources.registerResource(newHandle, typeObject);
//
//                typeObject.classdata = context.deserialize(object.get("@data").getAsJsonArray(), new TypeToken<List<ClassData>>() {}.getType());
//
//                return typeObject;
//            })
//            .registerTypeAdapter(ClassData.class, (JsonDeserializer<ClassData>) (json1, typeOfT, context) -> {
//                var object = json1.getAsJsonObject();
//
//                    /*var annotations = object.has("@annotations")
//                            ? object.get("@annotations").getAsJsonArray()
//                            : new JsonArray();*/
//
//                var values = object.get("@values").getAsJsonArray();
//                List<TypeContent> annotations = context.deserialize(object.get("@annotations"), new TypeToken<List<TypeContent>>() {}.getType());
//
//                List<Object> fields = new ArrayList<>();
//
//                for (var value : values) {
//                    if (value.isJsonNull()) {
//                        fields.add(null);
//                    } else if (value.isJsonObject()) {
//                        var obj = value.getAsJsonObject();
//                        if (obj.has("@items")) {
//                            fields.add(context.deserialize(value, TypeArray.class));
//                        } else {
//                            fields.add(context.deserialize(value, TypeContent.class));
//                        }
//                    } else {
//                        var v = value.getAsJsonPrimitive();
//
//                        // TODO: This is an awful hack, look at this later
//                        fields.add(v);
//                    }
//                }
//
//                return new ClassData(fields, annotations);
//            })
            .create();

    private JsonElement makeRef(int handle) {
        var object = new JsonObject();
        object.addProperty("@ref", handle);
        return object;
    }
}
