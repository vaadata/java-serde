package sh.arnaud.serializeformat.next.stream.types.primitives;

import com.google.gson.JsonPrimitive;
import sh.arnaud.serializeformat.next.stream.types.grammar.GrammarObject;

public abstract class Primitive extends GrammarObject {
    public byte asByte() {
        throw new UnsupportedOperationException("Primitive is not a byte");
    }

    public char asChar() {
        throw new UnsupportedOperationException("Primitive is not a char");
    }

    public double asDouble() {
        throw new UnsupportedOperationException("Primitive is not a double");
    }

    public float asFloat() {
        throw new UnsupportedOperationException("Primitive is not a float");
    }

    public int asInt() {
        throw new UnsupportedOperationException("Primitive is not an int");
    }

    public long asLong() {
        throw new UnsupportedOperationException("Primitive is not a long");
    }

    public short asShort() {
        throw new UnsupportedOperationException("Primitive is not a short");
    }

    public boolean asBoolean() {
        throw new UnsupportedOperationException("Primitive is not a boolean");
    }

    public abstract JsonPrimitive asJson();
}
