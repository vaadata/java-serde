package sh.arnaud.serializeformat.langs.java;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FromSerializedTest {

    @TestFactory
    Stream<DynamicTest> dynamicTests() {
        var units = getClass().getResource("units");

        assertNotNull(units);

        var unitsDirectory = new File(units.getPath());

        assertNotNull(units);
        assertTrue(unitsDirectory.isDirectory());

        var unitsFiles = unitsDirectory.listFiles();

        assertNotNull(unitsFiles);

        return Stream.of(unitsFiles)
                .filter(File::isDirectory)
                .map((file) -> {
                    var streamText = Paths.get(file.getPath(), "stream.bin").toFile();
                    var streamJson = Paths.get(file.getPath(), "stream.json").toFile();

                    return DynamicTest.dynamicTest(file.getName(), () -> {
                        var fromSerialized = new FromSerialized();

                        ByteBuffer text;
                        String json;

                        try (var textInput = new FileInputStream(streamText); var streamInput = new FileInputStream(streamJson)) {
                            text = ByteBuffer.wrap(textInput.readAllBytes());
                            json = new String(streamInput.readAllBytes());
                        }

                        var output = fromSerialized.readStreamToJson(text);

                        assertEquals(json, output);
                    });
                });
    }

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
        var stream = fromSerialized.readStreamToJson(ByteBuffer.wrap(bytestream.toByteArray()));

        System.out.println(stream);
    }

    @Test
    void readStreamSuper() throws Exception {
        var example = new B();

        var bytestream = new ByteArrayOutputStream();
        var objectstream = new ObjectOutputStream(bytestream);
        objectstream.writeObject(example);

        var fromSerialized = new FromSerialized();
        var stream = fromSerialized.readStreamToJson(ByteBuffer.wrap(bytestream.toByteArray()));

        System.out.println(stream);
    }

    @Test
    void readStreamEnum() throws Exception {
        var one = E.One;
        var two = E.Two;

        var bytestream = new ByteArrayOutputStream();
        var objectstream = new ObjectOutputStream(bytestream);
        objectstream.writeObject(one);
        objectstream.writeObject(two);
        objectstream.writeObject(two);

        var fromSerialized = new FromSerialized();
        var stream = fromSerialized.readStreamToJson(ByteBuffer.wrap(bytestream.toByteArray()));

        System.out.println(stream);
    }

    @Test
    void readStreamArray() throws Exception {
        int[] one = { 0xde, 0xad, 0xbe, 0xef };
        int[][] two = { { 0x1, 0x2 }, { 0x3, 0x4 } };

        var bytestream = new ByteArrayOutputStream();
        var objectstream = new ObjectOutputStream(bytestream);
        objectstream.writeObject(one);
        objectstream.writeObject(two);

        var fromSerialized = new FromSerialized();
        var stream = fromSerialized.readStreamToJson(ByteBuffer.wrap(bytestream.toByteArray()));

        System.out.println(stream);
    }

    @Test
    void readStreamWeird() throws Exception {
        int[] one = { 0xde, 0xad, 0xbe, 0xef };
        int[][] two = { { 0x1, 0x2 }, { 0x3, 0x4 } };

        var file = new FileInputStream("/tmp/tmp.Y1BWIlJJOl/test2.txt");
        var bytes = ByteBuffer.wrap(file.readAllBytes());

        var fromSerialized = new FromSerialized();
        var stream = fromSerialized.readStreamToJson(bytes);

        System.out.println(stream);
    }
}