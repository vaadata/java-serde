package sh.arnaud.serializeformat.next.stream.types.primitives;

import com.google.gson.JsonPrimitive;

public class PrimitiveDouble extends Primitive {
    public final double value;

    public PrimitiveDouble(double value) {
        this.value = value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public JsonPrimitive asJson() {
        return new JsonPrimitive(value);
    }
}
