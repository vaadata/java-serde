package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import sh.arnaud.serializeformat.grammar.classdesc.TypecodeClassDesc;

import java.util.List;

public class TypeStream {
    @Expose()
    public final List<TypecodeClassDesc> classes;

    @Expose()
    public final List<TypeContent> items;

    public TypeStream(List<TypecodeClassDesc> classes, List<TypeContent> items) {
        this.classes = classes;
        this.items = items;
    }
}
