package edu.mit.gamedap.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Hello world!
 *
 */
public class ParserExample 
{
    private final static String HTML_OUTLINE = "<!DOCTYPE html>\n" + //
            "<html>\n" + //
            "<head>\n" + //
            "<title>GameDAP Visualized Output</title>\n" + //
            "</head>\n" + //
            "<body><pre>\n" + //
            "%s\n" + //
            "</pre></body>\n" + //
            "</html>";

    public static void main( String[] args )
    {
        try {
            ClassLoader classLoader = new ParserExample().getClass().getClassLoader();
            System.out.println(classLoader.getResource("shortDialogueEx.txt"));
            InputStream is = classLoader.getResourceAsStream("shortDialogueEx.txt");
            GeneratedGrammarLexer lexer = new GeneratedGrammarLexer(CharStreams.fromStream(is));

            Map<String, Integer> vocabMap = new HashMap<>();
            for (int i = 0; i <= lexer.getVocabulary().getMaxTokenType(); i ++) {
                vocabMap.put(lexer.getVocabulary().getSymbolicName(i), i);
            }

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GeneratedGrammarParser parser = new GeneratedGrammarParser(tokens);
            ParseTree tree = parser.section();

            ParseTreeWalker walker = new ParseTreeWalker();
            DialogueLineBuilder listener = new DialogueLineBuilder(vocabMap);

            walker.walk(listener, tree);
            for (DialogueLine pair : listener.getLines()) {
                System.out.println(pair);
            }

            List<String> colorHexes = generateHighlightColors(listener.getDelimiterCount() + 1);
            // for (String hex : colorHexes) {
            //     System.out.println(hex);
            // }


            String bodyOutput = escapeHtml4(listener.getTitle());
            for (DialogueLine line : listener.getLines()) {
                for (int i = 0; i < line.numParts(); i ++) {
                    if (i > 0) {
                        bodyOutput += escapeHtml4(line.getDelim(i - 1));
                    }
                    bodyOutput += String.format("<span style=\"background-color:#%s\">%s</span>",
                    colorHexes.get(i), escapeHtml4(line.getPart(i)));
                }
            }

            String htmlOutput = String.format(HTML_OUTLINE, bodyOutput);

            System.out.println(htmlOutput);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static List<String> generateHighlightColors(int numColors) {
        return generateHighlightColors(numColors, 0, 0.1, 0.9);
    }

    public static List<String> generateHighlightColors(int numColors, double baseHue, double saturation, double value) {
        List<Double> hues = new ArrayList<>();
        for (int i = 0; i < numColors; i ++) {
            hues.add((baseHue + (360.0 * i / numColors)) % 360);
        }

        // hsv to rgb; taken from https://www.rapidtables.com/convert/color/hsv-to-rgb.html
        List<String> results = new ArrayList<>();
        for (double hue : hues) {
            double c = value * saturation;
            double x = c * (1 - Math.abs(((hue / 60) % 2) - 1));
            double m = value - c;

            double rp = 0, gp = 0, bp = 0;
            switch ((int) Math.floor(hue / 60)) {
                case 0:
                    rp = c;
                    gp = x;
                    break;
                case 1:
                    rp = x;
                    gp = c;
                    break;
                case 2:
                    gp = c;
                    bp = x;
                    break;
                case 3:
                    gp = x;
                    bp = c;
                    break;
                case 4:
                    rp = x;
                    bp = c;
                    break;
                case 5:
                    rp = c;
                    bp = x;
                    break;
            }

            long R = Math.round((rp + m) * 255);
            long G = Math.round((gp + m) * 255);
            long B = Math.round((bp + m) * 255);
            String colorString = Long.toHexString(R) + Long.toHexString(G) + Long.toHexString(B);
            results.add(colorString);
        }

        return results;
    }
}
