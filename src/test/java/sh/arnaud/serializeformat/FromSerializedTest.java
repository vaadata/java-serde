package sh.arnaud.serializeformat;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import sh.arnaud.serializeformat.de.Deserialize;
import sh.arnaud.serializeformat.ser.FromJson;
import sh.arnaud.serializeformat.ser.Serialize;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.Arrays;
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
                        ByteBuffer text;
                        String json;

                        try (var textInput = new FileInputStream(streamText); var streamInput = new FileInputStream(streamJson)) {
                            text = ByteBuffer.wrap(textInput.readAllBytes());
                            json = new String(streamInput.readAllBytes());
                        }

                        var output = Deserialize.deserialize(text.duplicate());

                        assertEquals(json, output);

                        var back = Serialize.serialize(output);

                        System.out.println(Arrays.toString(text.array()));
                        System.out.println(Arrays.toString(back.array()));
                        assertArrayEquals(text.array(), back.array());
                    });
                });
    }
}