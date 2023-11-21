package edu.mit.gamedap.generator;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStreams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import edu.mit.gamedap.generator.parsers.SampsonParser;
import edu.mit.gamedap.generator.parsers.SampsonParser.ParseResults;

/**
 * Hello world!
 *
 */
public class GrammarGeneratorExample 
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

    private static final String BASIC_FORMAT_START =
        "grammar GeneratedGrammar;\n" +
        "section\n" +
        "    : (title)? (pair)+ EOF ;\n" +
        "title: (NOT_TOKEN0 | NEW_LINE)+ ;\n" +
        "pair: ";
    private static final String BASIC_FORMAT_LEXER =
        "// default lexer\n" +
        "NEW_LINE : [\\n\\r\\f]+ ;";

    public static void main( String[] args ) {
        ClassLoader classLoader = new GrammarGeneratorExample().getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream("shortDialogueEx.txt");
        try {
            String inputText = IOUtils.toString(is, "UTF-8");
            System.out.println(inputText);
            System.out.println("---");
            SampsonParser sp = new SampsonParser(3, 300, SampsonParser.DEFAULT_LEARNING_RATE,
                250, SampsonParser.DEFAULT_CLUSTER_STDDEV_THRESH);
            ParseResults results = sp.parse(inputText);
            System.out.println(results.getRecordFormat());
            for (List<String> fieldSet : results.getRecordFields()) {
                System.out.println(fieldSet);
            }

            // temporary approach to printing out format: parse out .* and newlines, replace with lexer tokens
            List<String> recordDelimiters = Arrays.stream(results.getRecordFormat().split("\\.\\*"))
                .filter(d -> d.length() > 0)
                .toList();
            // Filter out empty tokens, generate the "title"
            String generatedLexer = "";
            String generatedParser = "";
            for (int i = 0; i < recordDelimiters.size(); i ++) {
                
                generatedLexer += String.format("TOKEN%s : '%s' ;\n", i, recordDelimiters.get(i));
                generatedLexer += String.format("NOT_TOKEN%s : %s ;\n", i, makeNegationToken(recordDelimiters.get(i)));

                int nextIdx = (i+1) % recordDelimiters.size();
                String chainSymbol = nextIdx == 0 ? "+" : "*";
                generatedParser += String.format("TOKEN%s? (NOT_TOKEN%s | NEW_LINE)%s ",
                    i, nextIdx, chainSymbol);
            }
            String temp_generated_grammar = BASIC_FORMAT_START + generatedParser + ";\n" +
                generatedLexer + BASIC_FORMAT_LEXER;



            System.out.println("-------------");
            System.out.println(temp_generated_grammar);

            

            String basePath = System.getProperty("user.dir");
            String targetPath = basePath + "/target/classes/antlr4/edu/mit/gamedap/parser";

            try {
                Files.createDirectories(Paths.get(targetPath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(targetPath + "/" + FILE_NAME));
                writer.write(temp_generated_grammar);
                writer.close();
            } catch (IOException e) {
                System.out.println(e);
            }

        }  catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Generates a parser rule to match anything that is not a given token
     * @param token The token to negate
     * @return The rule to match text that is not the token (not including a name for the rule)
     */
    private static String makeNegationToken(String token) {
        String result = "";
        for (int i = 0; i < token.length(); i ++) {
            if (i != 0) {
                // Add prefix of previous characters
                result += String.format(" | '%s' ", token.substring(0, i));
            }

            // Add negation of current characters
            result += String.format("~'%s'", token.charAt(i));
        }

        return result;
    }
}
