import dk.sdu.imada.teaching.compiler.fs24.verbosepl.ast.Stmt;
import dk.sdu.imada.teaching.compiler.fs24.verbosepl.ast.visitors.ASTPrinter;
import dk.sdu.imada.teaching.compiler.fs24.verbosepl.parse.Parser;
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

public class ParserTest {

    private static String sampleInputFile;
    /** input file contents as a string of bytes (all in one line)    */
    private static String sampleInputString;
    /** path to the file which contains the expected output    */
    private static String sampleOutputExpectedFile;
    private static String sampleOutputExpected; // the string contained in the file

    @BeforeAll
    public static void prepareFiles() {
        sampleInputFile  =          "src/test/resources/sample-input.vpl";
        sampleOutputExpectedFile  = "src/test/resources/sample-ast-output-expected.txt";
        try {
            sampleInputString = new String(Files.readAllBytes(Paths.get(sampleInputFile)));
            sampleOutputExpected = new String(Files.readAllBytes(Paths.get(sampleOutputExpectedFile)));
            System.out.println("created input string of size: " + sampleOutputExpected.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * this is one proposed way to print the AST
     * implement an AST-Visitor which prints relevant AST nodes
     * you can find the relevant/expected AST nodes that should be printed in the
     * sample-ast-output-expected.txt file in the test resources
     */
    protected String getASTString(List<?> statements) {
        StringBuilder builder = new StringBuilder();
        ASTPrinter printer = new ASTPrinter();
        for (var stmt : statements) {
            builder.append(printer.print((Stmt)stmt));
        }
        return builder.toString();
    }

    @Test
    public void testEquivalenceOfEachLine() {
        Scanner scanner = new Scanner(sampleInputString);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<?> statements = parser.parse();

        String sampleFileActual = getASTString(statements);
        List<String> sampleFileActualLines = Arrays.asList(sampleFileActual.split("\\R"));

        try {
            List<String> sampleFileExpectedLines = Files.readAllLines(Paths.get(sampleOutputExpectedFile));
            for (int i = 0; i < sampleFileExpectedLines.size(); i++) {
                String actualLine = sampleFileActualLines.get(i);
                String expectedLine = sampleFileExpectedLines.get(i);
                assertTrue(actualLine.equals(expectedLine), "line " + i + " of source and target mismatch. Expected the content: " + expectedLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
