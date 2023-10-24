package sh.arnaud.serializeformat.serde;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sh.arnaud.serializeformat.HandleManager;
import sh.arnaud.serializeformat.grammar.*;
import sh.arnaud.serializeformat.grammar.classdesc.ClassDesc;
import sh.arnaud.serializeformat.grammar.classdesc.ClassDescInfo;
import sh.arnaud.serializeformat.grammar.classdesc.TypeReferenceClassDesc;
import sh.arnaud.serializeformat.grammar.classdesc.TypecodeClassDesc;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json {
    public final HandleManager resources = new HandleManager();

    public final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(TypeReferenceClassDesc.class, (JsonSerializer<TypeReferenceClassDesc>) (src, typeOfSrc, context) -> {
                var object = new JsonObject();
                object.addProperty("@ref", src.handle);
                return object;
            })
            /*.registerTypeAdapter(ClassData.class, (JsonSerializer<ClassData>) (src, typeOfSrc, context) -> {
                var object = context.serialize(src.values).getAsJsonObject();

                if (src.annotations != null) {
                    object.add("@annotations", context.serialize(src.annotations));
                }

                return object;
            })*/
            .registerTypeAdapter(TypecodeClassDesc.class, (JsonSerializer<TypecodeClassDesc>) (src, typeOfSrc, context) -> {
                var object = new JsonObject();
                object.addProperty("@handle", src.handle);
                object.addProperty("@name", src.className);
                object.addProperty("@serial", src.serialVersionUID);
                object.addProperty("@flags", src.classDescInfo.classDescFlags);
                object.add("@fields", context.serialize(src.classDescInfo.fields));
                if (!src.classDescInfo.annotations.isEmpty()) {
                    object.add("@annotations", context.serialize(src.classDescInfo.annotations));
                }
                object.add("@super", context.serialize(src.classDescInfo.superClassDesc));
                return object;
            })
            .registerTypeAdapter(TypeObject.class, (JsonSerializer<TypeObject>) (src, typeOfSrc, context) -> {
            /*Optional<JsonElement> classdata = src.classDesc.asTypecodeClassDesc().map((classDesc) -> {
                // TODO: Check serial number
                switch (classDesc.className) {
                    case "java.lang.Double" -> {
                        var value = src.classdata.get(1).values.get("value");
                        return new JsonPrimitive((Double) value);
                    }
                    case "java.lang.Boolean" -> {
                        var value = src.classdata.get(0).values.get("value");
                        return new JsonPrimitive((Boolean) value);
                    }
                    case "java.lang.Byte" -> {
                        var value = src.classdata.get(1).values.get("value");
                        return new JsonPrimitive((Byte) value);
                    }
                    case "java.lang.Character" -> {
                        var value = src.classdata.get(0).values.get("value");
                        return new JsonPrimitive((Character) value);
                    }
                    case "java.lang.Short" -> {
                        var value = src.classdata.get(1).values.get("value");
                        return new JsonPrimitive((Short) value);
                    }
                    case "java.lang.Float" -> {
                        var value = src.classdata.get(1).values.get("value");
                        return new JsonPrimitive((Float) value);
                    }
                    case "java.lang.Integer" -> {
                        var value = src.classdata.get(1).values.get("value");
                        return new JsonPrimitive((Integer) value);
                    }
                    case "java.lang.Long" -> {
                        var value = src.classdata.get(1).values.get("value");
                        return new JsonPrimitive((Long) value);
                    }
                }

                return null;
            });*/

                var object = new JsonObject();
                object.addProperty("@handle", src.handle);
                object.add("@class", context.serialize(src.classDesc));
                //object.add("@data", classdata.orElseGet(() -> context.serialize(src.classdata)));
                object.add("@data", context.serialize(src.classdata));

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
            .registerTypeAdapter(TypeReferenceClassDesc.class, (JsonDeserializer<TypeReferenceClassDesc>) (json1, typeOfT, context) -> {
                try {
                    return new TypeReferenceClassDesc(resources, json1.getAsJsonObject().get("@ref").getAsInt());
                } catch (Exception e) {
                    throw new JsonParseException(e);
                }
            })
            .registerTypeAdapter(TypeContent.class, (JsonDeserializer<TypeContent>) (json12, typeOfT, context) -> {
                if (json12.isJsonObject()) {
                    var object = json12.getAsJsonObject();

                    if (object.has("@handle") && object.has("@class") && object.has("@data")) {
                        return context.deserialize(json12, TypeObject.class);
                    }

                    if (object.has("@ref")) {
                        return resources.fetchResource(object.get("@ref").getAsInt());
                    }
                }
                throw new JsonParseException("Couldn't parse TypeContent");
            })
            .registerTypeAdapter(TypecodeClassDesc.class, (JsonDeserializer<TypecodeClassDesc>) (json1, typeOfT, context) -> {
                var object = json1.getAsJsonObject();

                var className = object.get("@name").getAsString();
                var serialVersionId = object.get("@serial").getAsLong();
                var newHandle = object.get("@handle").getAsInt();
                var classDesc = new TypecodeClassDesc(resources, newHandle, className, serialVersionId);

                resources.registerResource(newHandle, classDesc);

                var flags = object.get("@flags").getAsByte();
                var fields = object.get("@fields").getAsJsonArray();
                var annotations = object.has("@annotations")
                        ? object.get("@annotations").getAsJsonArray()
                        : new JsonArray();
                ClassDesc zuper = context.deserialize(object.get("@super"), ClassDesc.class);

                var fieldsList = new ArrayList<TypeFieldDesc>();

                for (var prefield : fields) {
                    var field = prefield.getAsJsonObject();
                    var fieldName = field.get("@name").getAsString();
                    var className1 = field.get("@type").getAsString();
                    TypeFieldDesc finalField;
                    try {
                        finalField = new TypeFieldDesc(FieldTypeCode.fromByte(className1.getBytes()[0]), fieldName, className1);
                    } catch (Exception e) {
                        throw new JsonParseException(e);
                    }

                    fieldsList.add(finalField);
                }

                List<TypeContent> annotationsList = context.deserialize(annotations, new TypeToken<List<TypeContent>>() {}.getType());

                classDesc.classDescInfo = new ClassDescInfo(flags, fieldsList, annotationsList, zuper);

                return classDesc;
            })

            .registerTypeAdapter(ClassDesc.class, (JsonDeserializer<ClassDesc>) (json13, typeOfT, context) -> {
                if (json13.isJsonNull()) {
                    return null;
                }

                var object = json13.getAsJsonObject();

                if (object.has("@ref")) {
                    return context.deserialize(json13, TypeReferenceClassDesc.class);
                }

                return context.deserialize(json13, TypecodeClassDesc.class);
            })
            .registerTypeAdapter(TypeObject.class, (JsonDeserializer<TypeObject>) (json1, typeOfT, context) -> {
                var object = json1.getAsJsonObject();
                ClassDesc classDesc = context.deserialize(object.get("@class"), ClassDesc.class);

                var newHandle = object.get("@handle").getAsInt();

                var typeObject = new TypeObject(resources, newHandle, classDesc);

                resources.registerResource(newHandle, typeObject);

                typeObject.classdata = context.deserialize(object.get("@data").getAsJsonArray(), new TypeToken<List<ClassData>>() {}.getType());

                return typeObject;
            })
            .registerTypeAdapter(ClassData.class, (JsonDeserializer<ClassData>) (json1, typeOfT, context) -> {
                var object = json1.getAsJsonObject();

                    /*var annotations = object.has("@annotations")
                            ? object.get("@annotations").getAsJsonArray()
                            : new JsonArray();*/

                var values = object.get("@values").getAsJsonArray();
                List<TypeContent> annotations = context.deserialize(object.get("@annotations"), new TypeToken<List<TypeContent>>() {}.getType());

                List<Object> fields = new ArrayList<>();

                for (var value : values) {
                    if (value.isJsonNull()) {
                        fields.add(null);
                    } else if (value.isJsonObject()) {
                        var obj = value.getAsJsonObject();
                        if (obj.has("@items")) {
                            fields.add(context.deserialize(value, TypeArray.class));
                        } else {
                            fields.add(context.deserialize(value, TypeContent.class));
                        }
                    } else {
                        var v = value.getAsJsonPrimitive();

                        // TODO: This is an awful hack, look at this later
                        fields.add(v);
                    }
                }

                return new ClassData(fields, annotations);
            })
            .create();
}
