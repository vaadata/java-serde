import java.io.*;
import java.util.Date;

public class Nullable implements Serializable {
	enum Enum {
		ONE,
		TWO
	}

	private final String null_string = null;
	private final int[] null_ints = null;
	private final Enum null_enum = null;

	public static void main(String args[]) throws Exception {
		var stream = new ObjectOutputStream(System.out);

		stream.writeObject(new Nullable());
		stream.flush();
	}
}
