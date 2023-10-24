import java.io.*;

public class Primitives implements Serializable {
	byte b = 42;
	char c = '!';
	double d = 42.1337;
	float f = 420.69f;
	int i = 2147483647;
	long j = 0x0123456789abcdefL;
	short s = 0x1337;
	boolean one = true;
	boolean zero = false;

	public static void main(String args[]) throws Exception {
         var stream = new ObjectOutputStream(System.out);
         stream.writeObject(new Primitives());
         stream.flush();
    }
}
