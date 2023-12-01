
package sh.arnaud.serializeformat.next.stream.types.grammar;

public class GrammarNewEnum extends GrammarObject {
    public final GrammarNewClassDesc classDesc;
    public GrammarNewString enumConstantName;

    public GrammarNewEnum(GrammarNewClassDesc classDesc) {
        this.classDesc = classDesc;
    }
}
