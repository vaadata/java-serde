package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TypeReference extends TypeContent {
    @Expose
    @SerializedName("@ref")
    public final int reference;

    public TypeReference(int reference) {
        this.reference = reference;
    }
}
