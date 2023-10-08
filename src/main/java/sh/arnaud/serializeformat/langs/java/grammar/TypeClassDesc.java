package sh.arnaud.serializeformat.langs.java.grammar;

import sh.arnaud.serializeformat.langs.java.HandleManager;

public abstract class TypeClassDesc extends TypeContent {
    public abstract TypeClassDesc superClassDesc(HandleManager manager);
    public abstract int getHandle();
}
