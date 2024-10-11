import dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan.Scanner;
import dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class LexerTest {
    private static String sampleInputFile;
    private static String sampleInputByteString;
    private static String sampleOutputExpectedFile;
    private static String sampleOutputExpected;
    private static String sampleOutputActual;

    @BeforeAll
    public static void prepareFiles() {
        sampleInputFile  =          "src/test/resources/sample-input.vpl";
        sampleOutputExpectedFile  = "src/test/resources/sample-tokens-output-expected.txt";
        sampleOutputActual =        "src/test/resources/sample-tokens-output-actual.txt";
        try {
            sampleInputByteString = new String(Files.readAllBytes(Paths.get(sampleInputFile)));
            sampleOutputExpected = new String(Files.readAllBytes(Paths.get(sampleOutputExpectedFile)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEquivalenceOfEachLine() {
        Scanner lexer = new Scanner(sampleInputByteString);
        List<Token> tokens = lexer.scanTokens();

        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder.append(token + "\n");
        }

        String sampleFileActual = builder.toString();
        List<String> sampleFileActualLines = Arrays.asList(sampleFileActual.split("\\R"));

        try {
            List<String> sampleFileExpectedLines = Files.readAllLines(Paths.get(sampleOutputExpectedFile));
            for (int i = 0; i < sampleFileExpectedLines.size(); i++) {
                String actualLine = sampleFileActualLines.get(i);
                String expectedLine = sampleFileExpectedLines.get(i);
                assertTrue(actualLine.equals(expectedLine), "line " + i + "of source and target mismatch. expected: " + expectedLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
