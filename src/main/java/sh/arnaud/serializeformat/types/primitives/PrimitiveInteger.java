package sh.arnaud.serializeformat.types.primitives;

import com.google.gson.JsonPrimitive;

public class PrimitiveInteger extends Primitive {
    public final int value;

    public PrimitiveInteger(int value) {
        this.value = value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public JsonPrimitive asJson() {
        return new JsonPrimitive(value);
    }
}
