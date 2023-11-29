package sh.arnaud.serializeformat.next.stream.types;

import java.util.List;

public class GrammarStream {
    public final List<GrammarContent> contents;

    public GrammarStream(List<GrammarContent> contents) {
        this.contents = contents;
    }
}
