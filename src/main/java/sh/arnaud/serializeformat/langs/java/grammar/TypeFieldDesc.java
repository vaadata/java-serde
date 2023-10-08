package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TypeFieldDesc {
    @Expose
    public final FieldTypeCode typecode;
    @SerializedName("name")
    @Expose
    public final String fieldName;
    @SerializedName("class")
    @Expose
    public final String className1;

    public TypeFieldDesc(FieldTypeCode typecode, String fieldName, String className1) {
        this.typecode = typecode;
        this.fieldName = fieldName;
        this.className1 = className1;
    }
}
