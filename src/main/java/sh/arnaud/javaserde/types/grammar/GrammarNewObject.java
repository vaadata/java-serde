package sh.arnaud.javaserde.types.grammar;

import java.util.List;

public class GrammarNewObject extends GrammarObject {
    public final GrammarNewClassDesc classDesc;
    public List<GrammarClassdata> classdata = null;

    public GrammarNewObject(GrammarNewClassDesc classDesc) {
        this.classDesc = classDesc;
    }
}
