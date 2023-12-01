import java.io.*;

public class ArrayReference implements Serializable {
	byte[] array;

	public static void main(String args[]) throws Exception {
		byte[] bytes = { 1, 2, 3, 4, 5 };

		var one = new ArrayReference();
		one.array = bytes;

		var two = new ArrayReference();
		two.array = bytes;

		var stream = new ObjectOutputStream(System.out);
		stream.writeObject(one);
		stream.writeObject(two);
		stream.flush();
	}
}
