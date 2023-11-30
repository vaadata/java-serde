package sh.arnaud.serializeformat;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import sh.arnaud.serializeformat.de.Deserialize;
import sh.arnaud.serializeformat.ser.Serialize;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FromSerializedTest {

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    private static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    private static byte[] readFile(File file) throws IOException {
        try (var input = new FileInputStream(file)) {
            return input.readAllBytes();
        }
    }

    private static DynamicTest generateTest(File file) {
        var jsonFile = Paths.get(file.getPath(), "stream.json").toFile();
        var binFile = Paths.get(file.getPath(), "stream.bin").toFile();
        var binReverseFile = Paths.get(file.getPath(), "stream-reverse.bin").toFile();

        return DynamicTest.dynamicTest(file.getName(), () -> {
            var json = readFile(jsonFile);
            var bin = readFile(binFile);
            var binReverse = binReverseFile.isFile() ? readFile(binReverseFile) : bin;

            var outputJson = Deserialize.deserialize(bin);

            assertEquals(new String(json), outputJson);

            var outputBin = Serialize.serialize(outputJson);

            assertEquals(bytesToHex(binReverse), bytesToHex(outputBin.array()));

            assertArrayEquals(binReverse, outputBin.array());
        });
    }

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
                .map(FromSerializedTest::generateTest);
    }
}