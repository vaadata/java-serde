package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;

import java.util.List;

public class TypeStream {
    @Expose()
    public final List<TypeNormalClassDesc> classes;

    @Expose()
    public final List<TypeContent> items;

    public TypeStream(List<TypeNormalClassDesc> classes, List<TypeContent> items) {
        this.classes = classes;
        this.items = items;
    }
}
