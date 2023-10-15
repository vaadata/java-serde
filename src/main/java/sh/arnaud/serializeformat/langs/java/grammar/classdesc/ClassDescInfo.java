package sh.arnaud.serializeformat.langs.java.grammar.classdesc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.TypeFieldDesc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDescInfo {
    @SerializedName("flags")
    @Expose
    public final byte classDescFlags;

    public final List<TypeFieldDesc> _fields;

    @SerializedName("fields")
    @Expose
    public final Map<String, String> fields_fmt;

    // TODO: classAnnotation
    @SerializedName("super")
    @Expose
    public final ClassDesc superClassDesc;

    public ClassDescInfo(byte classDescFlags, List<TypeFieldDesc> fields, ClassDesc superClassDesc, HandleManager manager) throws Exception {
        this.classDescFlags = classDescFlags;
        this._fields = fields;
        this.superClassDesc = superClassDesc;

        fields_fmt = new HashMap<>(fields.size());

        for (var field : fields) {
            fields_fmt.put(field.fieldName, field.className1);
        }
    }
}
