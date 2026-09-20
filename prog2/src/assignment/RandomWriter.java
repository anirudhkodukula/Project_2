package assignment;

import java.io.*;
import java.util.*;

/*
 * CS 314H Assignment 2 - Random Writing
 *
 * Your task is to implement this RandomWriter class
 */

public class RandomWriter implements TextProcessor {

    private int level;

    // Make one random-number generator and reuse it for every random choice.
    private Random random;

    // map to store the chars that follow the seed
    private Map<String, List<Character>> nextText;

    // entire source so we can choose random seed
    private String sourceText;

    public static void main(String[] args) {

        if (args.length != 4) {
            System.err.println(
                    "need to have 4 args");
            return;
        }

        String sourceName = args[0];
        String resultName = args[1];

        int k;
        int length;

        // the strings become ints, but if the int isn't valid, we need to check that
        try {
            k = Integer.parseInt(args[2]);
            length = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println(
                    "level and length need to be integers");
            return;
        }

        // seed can't be negative
        if (k < 0) {
            System.err.println("can't be negative");
            return;
        }

        // length can't be negative
        if (length < 0) {
            System.err.println("can't be negative");
            return;
        }

        // create so it can read the source and write our result
        TextProcessor writer = createProcessor(k);

        //need to check if source is even valid
        try {
            writer.readText(sourceName);
        } catch (IOException e) {
            System.err.println(
                    "can't read" + sourceName);
            return;
        } catch (IllegalArgumentException e) {
            System.err.println(
                    "need more chars than " + k);
            return;
        }

        // can result even be written
        try {
            writer.writeText(resultName, length);
        } catch (IOException e) {
            System.err.println( "can't write " + resultName);
        }
    }

    //Unless tou need extra logic here, you might not have to touch this method
    public static TextProcessor createProcessor(int level) {
        return new RandomWriter(level);
    }

    private RandomWriter(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("can't be negative");
        }
        this.level = level;
        this.random = new Random();
    }

    //check what chars come after seed
    public void readText(String inputFilename) throws IOException {
        // the data is cleared so each run is good
        nextText = null;
        sourceText = null;

        // Collect the characters here as we read them.
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(inputFilename))) {

            int c;

            // if the file ends, we return -1; we append char to the builder
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        }

        String text = builder.toString();

        //need to have space for the seed and then one char to add to it.
        if (text.length() <= level) {
            throw new IllegalArgumentException("need more chars than " + level);
        }

        nextText = new HashMap<>();
        //move throught text and grab seed and char 
        for (int i = 0; i < text.length() - level; i++) {
            String seed = text.substring(i, i + level);
            char next = text.charAt(i + level);

            // if seed is new, we have a new list
            if (!nextText.containsKey(seed)) {
                nextText.put(seed, new ArrayList<>());
            }
            //if char is alr added, if seen again, we still add it so it has higher chance
            nextText.get(seed).add(next);
        }

    
        sourceText = text;
    }

    public void writeText(String outputFilename, int length)
            throws IOException {

        // source has to be able to be read
        if (nextText == null || sourceText == null) {
            throw new IllegalStateException("text has to be able to be read before we write");
        }

        // Check this here too in case this method is called directly.
        if (length < 0) {
            throw new IllegalArgumentException(
                    "length must be non-negative");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
            String seed = randomSeed();

            for (int i = 0; i < length; i++) {
              // all chars that follow the seed
                List<Character> choices = nextText.get(seed);
                // we keep picking new seed
                while (choices == null || choices.isEmpty()) {
                    seed = randomSeed();
                    choices = nextText.get(seed);
                }
                // this is us picking a random char from list of chars that come after seed
                char next = choices.get(random.nextInt(choices.size()));
                writer.write(next);
                seed = advance(seed, next);
            }
        }
    }

    // we choose random grouping of chars that has length equal to that of the level from random position
    private String randomSeed() {
        // check if level 0, since it doesn't look back
        if (level == 0) {
            return "";
        }
        int start = random.nextInt(sourceText.length() - level + 1);
        return sourceText.substring(start, start + level);
    }

    //drop seed first char and add new char to end
    private String advance(String seed, char c) {
        // seed is empty at level 0
        if (level == 0) {
            return "";
        }
        return seed.substring(1) + c;
    }
    // helps for testing, white box
    List<Character> getFollowers(String seed) {
      return nextText.get(seed);
}

}