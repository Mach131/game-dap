package edu.mit.gamedap.generator;

import java.io.*;
import java.nio.file.*;

/**
 * Hello world!
 *
 */
public class GrammarGenerator 
{
    private static final String TEST_GRAMMAR =
        "grammar GeneratedGrammar;\n" +
        "section\n" +
        "    : (title NEW_LINE)? (pair NEW_LINE)+ EOF ;\n" +
        "title: TEXT ;\n" +
        "pair: TEXT PAIR_SEP TEXT ;\n" +
        "// lexer\n" +
        "TEXT : [a-zA-Z_0-9 ]+ ;\n" +
        "PAIR_SEP : ':' | [\\t] ;\n" +
        "NEW_LINE : [\\n\\r\\f]+ ;";

    private static final String FILE_NAME = "GeneratedGrammar.g4";

    public static void main( String[] args ) {
        String basePath = System.getProperty("user.dir");
        String targetPath = basePath + "/target/classes/antlr4/edu/mit/gamedap/parser";

        try {
            Files.createDirectories(Paths.get(targetPath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(targetPath + "/" + FILE_NAME));
            writer.write(TEST_GRAMMAR);
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
