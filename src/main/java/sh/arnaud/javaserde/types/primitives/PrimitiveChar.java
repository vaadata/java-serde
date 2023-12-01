package sh.arnaud.javaserde.types.primitives;

import com.google.gson.JsonPrimitive;

public class PrimitiveChar extends Primitive {
    public final char value;

    public PrimitiveChar(char value) {
        this.value = value;
    }

    @Override
    public char asChar() {
        return value;
    }

    @Override
    public JsonPrimitive asJson() {
        return new JsonPrimitive(value);
    }
}
