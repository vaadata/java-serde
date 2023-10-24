package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TypeFieldDesc {
    public final FieldTypeCode typecode;

    @SerializedName("@name")
    @Expose
    public final String fieldName;

    @SerializedName("@type")
    @Expose
    public final String className1;

    public TypeFieldDesc(FieldTypeCode typecode, String fieldName, String className1) {
        this.typecode = typecode;
        this.fieldName = fieldName;
        this.className1 = className1 != null ? className1 : String.format("%c", typecode.typecode);
    }
}
