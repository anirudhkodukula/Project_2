package assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class RandomWriterTest {

    @Test
    void testExample() {
        int a = 1;
        int b = 2;
        int c = 3;

        assertEquals(a + b, c);
    }

    // black box
    @Test
    void testCreateProcessor() {
        TextProcessor processor = RandomWriter.createProcessor(2);
        assertNotNull(processor);
        assertTrue(processor instanceof RandomWriter);
        try {
            RandomWriter.createProcessor(-1);
            fail("level can't be negative");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    void testNegativeLength() throws IOException {
        TextProcessor processor = RandomWriter.createProcessor(2);
        processor.readText("test_books/CatInTheHat.txt");
        try {
        processor.writeText("output.txt", -1);
        fail("length can't be negative");
    } catch (IllegalArgumentException e) {
    }
}

    // black box
    @Test
    void testReadText() throws IOException {
        TextProcessor processor = RandomWriter.createProcessor(2);
        processor.readText("test_books/CatInTheHat.txt");
        processor.readText("test_books/MuchAdo.txt");
        processor.readText("test_books/OneFishTwoFish.txt");
        File emptySource = File.createTempFile("emptySource", ".txt");
        try {
            processor.readText(emptySource.getPath());
            fail("source can't be empty");
        } catch (IllegalArgumentException e) {
        }
    }

    // black box
    @Test
    void testWriteText() throws IOException {
        File source = File.createTempFile("testSource", ".txt");
        File output = File.createTempFile("testOutput", ".txt");
        try (FileWriter writer = new FileWriter(source)) {
            writer.write("abc");
        }
        TextProcessor processor = RandomWriter.createProcessor(2);
        processor.readText(source.getPath());
        processor.writeText(output.getPath(), 10);

        StringBuilder result = new StringBuilder();

        try (FileReader reader = new FileReader(output)) {
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char) c);
            }
        }
        assertEquals("cccccccccc", result.toString());
        processor.writeText(output.getPath(), 0);
        try (FileReader reader = new FileReader(output)) {
            assertEquals(-1, reader.read());
        }
    }

    // white box
    @Test
    void testFollowerCounts() throws IOException {
        File source = File.createTempFile("testFollowers", ".txt");
        try (FileWriter writer = new FileWriter(source)) {
            writer.write("aaaabaa");
        }
        RandomWriter processor = (RandomWriter) RandomWriter.createProcessor(2);
        processor.readText(source.getPath());
        assertEquals(Arrays.asList('a', 'a', 'b'), processor.getFollowers("aa"));
        assertEquals(Arrays.asList('a'), processor.getFollowers("ab"));
        assertEquals(Arrays.asList('a'), processor.getFollowers("ba"));
        assertNull(processor.getFollowers("zz"));
    }

    // white box
    @Test
    void testLevelZeroFollowers() throws IOException {
        File source = File.createTempFile("testLevelZero", ".txt");
        try (FileWriter writer = new FileWriter(source)) {
            writer.write("aa b!\n");
        }
        RandomWriter processor = (RandomWriter) RandomWriter.createProcessor(0);
        processor.readText(source.getPath());
        assertEquals(Arrays.asList('a', 'a', ' ', 'b', '!', '\n'), processor.getFollowers(""));
    }

    @Test
    void testLevelZeroCreation() {
        TextProcessor processor = RandomWriter.createProcessor(0);
        assertNotNull(processor);
}

    @Test
    void testWriteBeforeRead() throws IOException {
        TextProcessor processor = RandomWriter.createProcessor(2);
        try {
            processor.writeText("output.txt", 10);
            fail("source has to be read before writing");
    } catch (IllegalStateException e) {
    }
}

  
}