import java.io.*;

public enum Enum implements Serializable {
	First('a', 1),
	Second('b', 2),
	Third('c', 3),
	Fourth('d', 4);

	private final char letter;
	private final int number;

	Enum(char letter, int number) {
		this.letter = letter;
		this.number = number;
	}

	public static void main(String args[]) throws Exception {
		var stream = new ObjectOutputStream(System.out);
		stream.writeObject(First);
		stream.writeObject(Second);
		stream.writeObject(Second);
		stream.writeObject(Third);
		stream.writeObject(Fourth);
		stream.writeObject(Third);
        stream.flush();
    }
}
