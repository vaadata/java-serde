package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TypeGeneric extends TypeContent {
    @Expose
    @SerializedName("@value")
    public final Object value;

    public TypeGeneric(Object value) {
        this.value = value;
    }
}
