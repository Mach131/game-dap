package edu.mit.gamedap.parser;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Hello world!
 *
 */
public class ParserExample 
{
    public static void main( String[] args )
    {
        try {
            ClassLoader classLoader = new ParserExample().getClass().getClassLoader();
            System.out.println(classLoader.getResource("structuredParseEx.txt"));
            InputStream is = classLoader.getResourceAsStream("structuredParseEx.txt");
            GeneratedGrammarLexer lexer = new GeneratedGrammarLexer(CharStreams.fromStream(is));

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GeneratedGrammarParser parser = new GeneratedGrammarParser(tokens);
            ParseTree tree = parser.section();

            ParseTreeWalker walker = new ParseTreeWalker();
            ExamplePairBuilder listener = new ExamplePairBuilder();

            walker.walk(listener, tree);

            for (ExamplePair pair : listener.getPairs()) {
                System.out.println(pair);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
