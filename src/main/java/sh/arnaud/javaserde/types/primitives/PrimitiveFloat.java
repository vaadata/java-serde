package sh.arnaud.javaserde.types.primitives;

import com.google.gson.JsonPrimitive;

public class PrimitiveFloat extends Primitive {
    public final float value;

    public PrimitiveFloat(float value) {
        this.value = value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public JsonPrimitive asJson() {
        return new JsonPrimitive(value);
    }
}
