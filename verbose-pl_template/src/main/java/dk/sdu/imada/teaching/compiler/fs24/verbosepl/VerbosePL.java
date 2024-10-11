package dk.sdu.imada.teaching.compiler.fs24.verbosepl;

import dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan.Scanner;
import dk.sdu.imada.teaching.compiler.fs24.verbosepl.scan.Token;
import dk.sdu.imada.teaching.compiler.fs24.verbosepl.parse.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class VerbosePL {

    // Expects files that comprise an VPL program as arguments
    public static void main(String[] args) throws IOException {
        for (String s: args)
           new VerbosePL().interpretFile(s);
    }

    private void interpretFile(String path) throws IOException {
        System.out.println(" ------------ Processing file " + path + " ------------ \n");

        run(new String(Files.readAllBytes(Paths.get(path))));

    }

    private void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    public void saveToFile(StringBuilder stringContent, String target) {
        try {
            Path targetPath = Path.of(target);
            Files.deleteIfExists(targetPath);
            if (Files.notExists(targetPath.getParent()))
                Files.createDirectory(targetPath.getParent());
            Files.createFile(targetPath);
            Files.writeString(targetPath, stringContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
