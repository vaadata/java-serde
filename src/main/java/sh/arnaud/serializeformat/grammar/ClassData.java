package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ClassData {
    @Expose
    @SerializedName("@values")
    public final List<Object> values;

    @Expose
    @SerializedName("@annotations")
    public final List<TypeContent> annotations;

    public ClassData(List<Object> values, List<TypeContent> annotations) {
        this.values = values;
        this.annotations = annotations;
    }
}
