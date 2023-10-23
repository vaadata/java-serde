package sh.arnaud.serializeformat.langs.java.grammar.classdesc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.grammar.TypeFieldDesc;

import java.util.List;

public class ClassDescInfo {
    public final byte classDescFlags;

    public final List<TypeFieldDesc> fields;

    // TODO: classAnnotation
    public final ClassDesc superClassDesc;

    public ClassDescInfo(byte classDescFlags, List<TypeFieldDesc> fields, ClassDesc superClassDesc) throws Exception {
        this.classDescFlags = classDescFlags;
        this.fields = fields;
        this.superClassDesc = superClassDesc;
    }
}
