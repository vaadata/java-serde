package sh.arnaud.serializeformat.next.stream.types;

public class GrammarBlockdata extends GrammarContent {
    public final byte[] blockdata;

    public GrammarBlockdata(byte[] blockdata) {
        this.blockdata = blockdata;
    }

    @Override
    public GrammarBlockdata asBlockData() {
        return this;
    }
}
