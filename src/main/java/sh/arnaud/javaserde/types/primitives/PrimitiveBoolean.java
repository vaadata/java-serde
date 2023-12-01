package sh.arnaud.javaserde.types.primitives;

import com.google.gson.JsonPrimitive;

public class PrimitiveBoolean extends Primitive {
    public final boolean value;

    public PrimitiveBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public JsonPrimitive asJson() {
        return new JsonPrimitive(value);
    }
}
