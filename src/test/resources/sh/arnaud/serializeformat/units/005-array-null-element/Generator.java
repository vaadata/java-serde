import java.io.*;

public class ArrayNullElement implements Serializable {
	String[] strings = {
			"Hello, world",
			"Lorem ipsum",
			null,
			"I'm secret!",
			"Hello, world"
	};

	Integer[] integers = {
			42,
			null,
			101,
			42
	};

	Class[] classes = {
			String.class,
			Integer.class,
			null,
			String.class
	};

	public static void main(String args[]) throws Exception {
		var stream = new ObjectOutputStream(System.out);
		stream.writeObject(new ArrayNullElement());
		stream.flush();
	}
}
