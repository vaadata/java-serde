package sh.arnaud.serializeformat.next.stream.types.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.next.stream.types.GrammarContent;

import java.util.List;

public class GrammarClassdata {
    @Expose
    @SerializedName("@values")
    public final List<GrammarObject> values;

    @Expose
    @SerializedName("@annotations")
    public final List<GrammarContent> annotations;

    public GrammarClassdata(List<GrammarObject> values, List<GrammarContent> annotations) {
        this.values = values;
        this.annotations = annotations;
    }
}
