package sh.arnaud.serializeformat.langs.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class FromSerializedTest {
    private static class A implements Serializable {
        public String string = "Hello, world!";
        public String other = "You, too!";


        public byte b = '@';
        public char c = 'A';
        public double d = 1337.42;
        public float f = 12.34F;
        public int i = 69;
        public long j =  123456789;
        public short s = 1337;
        public boolean z = true;

        public A example = null;
        public Class ref_class = null;

    }

    private static class B extends A {
        public String childField = "<I'm B and I'm extending A>";
    }
    
    private enum E {
        One("One", 1, true),
        Two("Two", 2, false);

        E(String two, int i, boolean b) {
        }
    }

    @Test
    void readStreamSimple() throws Exception {

        var example = new A();
        var example2 = new A();

        var bytestream = new ByteArrayOutputStream();
        var objectstream = new ObjectOutputStream(bytestream);
        objectstream.writeObject(example);
        objectstream.writeObject(example);

        example2.example = example;
        example2.ref_class = A.class;
        objectstream.writeObject(example2);

        var fromSerialized = new FromSerialized();
        var stream = fromSerialized.readStream(ByteBuffer.wrap(bytestream.toByteArray()));

        System.out.println(stream);

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
        System.out.println(gson.toJson(stream));
    }

    @Test
    void readStreamSuper() throws Exception {
        var example = new B();

        var bytestream = new ByteArrayOutputStream();
        var objectstream = new ObjectOutputStream(bytestream);
        objectstream.writeObject(example);

        var fromSerialized = new FromSerialized();
        var stream = fromSerialized.readStream(ByteBuffer.wrap(bytestream.toByteArray()));

        System.out.println(stream);

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
        System.out.println(gson.toJson(stream));
    }

    @Test
    void readStreamEnum() throws Exception {
        var one = E.One;
        var two = E.Two;

        var bytestream = new ByteArrayOutputStream();
        var objectstream = new ObjectOutputStream(bytestream);
        objectstream.writeObject(one);
        objectstream.writeObject(two);

        var fromSerialized = new FromSerialized();
        var stream = fromSerialized.readStream(ByteBuffer.wrap(bytestream.toByteArray()));

        System.out.println(stream);

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
        System.out.println(gson.toJson(stream));
    }
}