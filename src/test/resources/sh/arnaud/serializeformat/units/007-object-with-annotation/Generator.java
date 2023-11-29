import java.io.*;
import java.util.Date;

public class ObjectWithAnnotation implements Serializable {
	private final Date one = new Date(1701269821);
	private final Date two = new Date(1101269821);
	private final Date three = new Date(1701269821);

	public static void main(String args[]) throws Exception {
		var stream = new ObjectOutputStream(System.out);
		stream.writeObject(new ObjectWithAnnotation());
		stream.flush();
	}
}
