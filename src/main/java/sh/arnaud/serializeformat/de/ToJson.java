package sh.arnaud.serializeformat.de;

import com.google.gson.*;
import sh.arnaud.serializeformat.grammar.ClassData;
import sh.arnaud.serializeformat.grammar.TypeContent;
import sh.arnaud.serializeformat.grammar.TypeObject;
import sh.arnaud.serializeformat.grammar.classdesc.TypeReferenceClassDesc;
import sh.arnaud.serializeformat.grammar.classdesc.TypecodeClassDesc;
import sh.arnaud.serializeformat.serde.Json;

import java.util.List;
import java.util.Optional;

public class ToJson {
    private final Json json = new Json();

    public String toJson(List<TypeContent> stream) {
        return json.gson.toJson(stream);
    }
}
