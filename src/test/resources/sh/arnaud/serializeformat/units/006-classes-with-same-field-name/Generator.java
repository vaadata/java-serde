import java.io.*;

public class ArrayNullElement implements Serializable {
	public static class A implements Serializable {
		public final String one = "My string";
	}

	public static class B implements Serializable {
		public final String one = "My string";
		public final String two = "My string";
	}

	public static void main(String args[]) throws Exception {
		var stream = new ObjectOutputStream(System.out);
		stream.writeObject(new ArrayNullElement.A());
		stream.writeObject(new ArrayNullElement.B());
		stream.flush();
	}
}
