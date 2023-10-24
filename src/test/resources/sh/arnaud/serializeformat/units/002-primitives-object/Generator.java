import java.io.*;

public class PrimitivesObject implements Serializable {
	Byte b = 42;
	Character c = '!';
	Double d = 42.1337;
	Float f = 420.69f;
	Integer i = 2147483647;
	Long j = 0x0123456789abcdefL;
	Short s = 0x1337;
	Boolean one = true;
	Boolean zero = false;

	public static void main(String args[]) throws Exception {
         var stream = new ObjectOutputStream(System.out);
         stream.writeObject(new PrimitivesObject());
         stream.flush();
    }
}
