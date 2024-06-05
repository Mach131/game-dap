package edu.mit.gamedap.generator.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.mit.gamedap.generator.datatypes.EmptyContext;
import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;

public class SampsonParserTest {

  ////// buildCharacterSet

  private void validateCharacterSet(String text, Set<Character> result) {
    for (Character c : text.toCharArray()) {
      assertTrue("Character set did not contain character", result.contains(c));
    }
  }

  @Test
  public void buildCharacterSet_Empty()
  {
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(1);
    Set<Character> result = parser.buildCharacterSet("");
    assertEquals("Expected empty result", 0, result.size());
  }

  @Test
  public void buildCharacterSet_Singleton()
  {
    String input = "X";
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(1);
    Set<Character> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Unique()
  {
    String input = "World";
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(1);
    Set<Character> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Repeats()
  {
    String input = "Hello World";
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(1);
    Set<Character> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Longer()
  {
    String input = "This sentence has\nmany characters...";
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(1);
    Set<Character> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }
  

  ////// makeSubstringVectors

  private List<Vector<EmptyContext, Character>> makeSVList(List<String> strings) {
    return strings.stream()
      .map(s -> (Vector<EmptyContext, Character>) new StringVector(s))
      .toList();
  }

  @Test
  public void makeSubstringVectors_SizeOne()
  {
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(1);
    ParseLearningPrimer<EmptyContext, Character> primer = new StringParseLearningPrimer();
    String input = "input";
    List<Vector<EmptyContext, Character>> result = primer.makeSubstringVectors(input, 1, parser.buildCharacterSet(input));
    List<Vector<EmptyContext, Character>> expected = makeSVList(Arrays.asList("i", "n", "p", "u", "t"));
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }
  
  @Test
  public void makeSubstringVectors_SizeLonger()
  {
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(3);
    ParseLearningPrimer<EmptyContext, Character> primer = new StringParseLearningPrimer();
    String input = "input";
    List<Vector<EmptyContext, Character>> result = primer.makeSubstringVectors(input, 3, parser.buildCharacterSet(input));
    List<Vector<EmptyContext, Character>> expected = makeSVList(Arrays.asList("inp", "npu", "put"));
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }

  @Test
  public void makeSubstringVectors_SizeFull()
  {
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(5);
    ParseLearningPrimer<EmptyContext, Character> primer = new StringParseLearningPrimer();
    String input = "input";
    List<Vector<EmptyContext, Character>> result = primer.makeSubstringVectors(input, 5, parser.buildCharacterSet(input));
    List<Vector<EmptyContext, Character>> expected = makeSVList(Arrays.asList("input"));
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }
  
  @Test
  public void makeSubstringVectors_LongerInput()
  {
    SampsonParser<EmptyContext, Character> parser = new SampsonParser<>(4);
    ParseLearningPrimer<EmptyContext, Character> primer = new StringParseLearningPrimer();
    String input = "Hello World";
    List<Vector<EmptyContext, Character>> result = primer.makeSubstringVectors(input, 4, parser.buildCharacterSet(input));
    List<Vector<EmptyContext, Character>> expected = makeSVList(Arrays.asList(
      "Hell", "ello", "llo ", "lo W", "o Wo", " Wor", "Worl", "orld"));
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }

}
