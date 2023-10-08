package sh.arnaud.serializeformat.langs.java.grammar;

public class TypeGeneric<T> extends TypeContent {
    public final T value;

    public TypeGeneric(T value) {
        this.value = value;
    }
}
