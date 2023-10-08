package sh.arnaud.serializeformat.langs.java.grammar;

public class TypeClass extends TypeContent {
    public final TypeClassDesc classDesc;

    public final int handle;

    public TypeClass(int handle, TypeClassDesc classDesc) {
        this.classDesc = classDesc;
        this.handle = handle;
    }
}
