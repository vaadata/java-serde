package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class TypeClassDescInfo {
    @SerializedName("flags")
    @Expose
    public final byte classDescFlags;

    @Expose
    public final List<TypeFieldDesc> fields;

    // TODO: classAnnotation
    @SerializedName("super")
    @Expose
    public final Integer superClassDesc;

    public TypeClassDescInfo(byte classDescFlags, List<TypeFieldDesc> fields, TypeClassDesc superClassDesc) {
        this.classDescFlags = classDescFlags;
        this.fields = fields;
        this.superClassDesc = superClassDesc != null ? superClassDesc.getHandle() : null;
    }
}
