import java.io.*;
import java.util.Date;

public class ShortStrings implements Serializable {
	private final String string;

	ShortStrings(String string) {
		this.string = string;
	}

	public static void main(String args[]) throws Exception {
		var stream = new ObjectOutputStream(System.out);

		stream.writeObject(new ShortStrings(new String("foo")));
		stream.writeObject(new ShortStrings(new String("foo")));
		stream.writeObject(new ShortStrings(new String("foo")));
		stream.flush();
	}
}
