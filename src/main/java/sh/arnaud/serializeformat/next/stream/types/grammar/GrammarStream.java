package sh.arnaud.serializeformat.next.stream.types.grammar;

import java.util.List;

public class GrammarStream {
    public final List<GrammarContent> contents;

    public GrammarStream(List<GrammarContent> contents) {
        this.contents = contents;
    }
}
