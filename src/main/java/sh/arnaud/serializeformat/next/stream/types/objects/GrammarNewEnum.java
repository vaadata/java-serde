
package sh.arnaud.serializeformat.next.stream.types.objects;

public class GrammarNewEnum extends GrammarObject {
    public final GrammarNewClassDesc classDesc;
    public GrammarNewString enumConstantName;

    public GrammarNewEnum(GrammarNewClassDesc classDesc) {
        this.classDesc = classDesc;
    }
}
