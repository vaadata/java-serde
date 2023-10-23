import java.io.*;

public class Circular implements Serializable {
	Circular circular = null;
	Circular other = null;

	public static void main(String args[]) throws Exception {
		var obj = new Circular();
		obj.circular = obj;
		obj.other = new Circular();

		var stream = new ObjectOutputStream(System.out);
		stream.writeObject(obj);
		stream.flush();
	}
}
