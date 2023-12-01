package sh.arnaud.serializeformat.types.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public record GrammarClassdata(
        @Expose @SerializedName("@values") List<GrammarObject> values,
        @Expose @SerializedName("@annotations") List<GrammarContent> annotations
) { }
