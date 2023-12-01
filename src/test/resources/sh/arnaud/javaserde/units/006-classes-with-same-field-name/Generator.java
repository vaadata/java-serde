import java.io.*;

public class ClassesWithSameFieldName implements Serializable {
	public static class A implements Serializable {
		public final String one = "My string";
	}

	public static class B implements Serializable {
		public final String one = "My string";
		public final String two = "My string";
	}

	public static void main(String args[]) throws Exception {
		var stream = new ObjectOutputStream(System.out);
		stream.writeObject(new ClassesWithSameFieldName.A());
		stream.writeObject(new ClassesWithSameFieldName.B());
		stream.flush();
	}
}
