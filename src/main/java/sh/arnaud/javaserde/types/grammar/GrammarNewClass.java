package sh.arnaud.javaserde.types.grammar;

import java.util.List;

public class GrammarNewClass extends GrammarObject {
    public final GrammarNewClassDesc classDesc;
    public List<GrammarClassdata> classdata = null;

    public GrammarNewClass(GrammarNewClassDesc classDesc) {
        this.classDesc = classDesc;
    }
}
