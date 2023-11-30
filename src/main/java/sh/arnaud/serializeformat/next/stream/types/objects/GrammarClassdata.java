package sh.arnaud.serializeformat.next.stream.types.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.next.stream.types.GrammarContent;

import java.util.List;

public record GrammarClassdata(
        @Expose @SerializedName("@values") List<GrammarObject> values,
        @Expose @SerializedName("@annotations") List<GrammarContent> annotations
) { }
