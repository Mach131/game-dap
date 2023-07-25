package edu.mit.gamedap.generator.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import edu.mit.gamedap.generator.datatypes.StringVector;
import edu.mit.gamedap.generator.datatypes.Vector;
import edu.mit.gamedap.generator.parsers.SampsonParser;

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
    SampsonParser parser = new SampsonParser(1);
    Set<Character> result = parser.buildCharacterSet("");
    assertEquals("Expected empty result", 0, result.size());
  }

  @Test
  public void buildCharacterSet_Singleton()
  {
    String input = "X";
    SampsonParser parser = new SampsonParser(1);
    Set<Character> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Unique()
  {
    String input = "World";
    SampsonParser parser = new SampsonParser(1);
    Set<Character> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Repeats()
  {
    String input = "Hello World";
    SampsonParser parser = new SampsonParser(1);
    Set<Character> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Longer()
  {
    String input = "This sentence has\nmany characters...";
    SampsonParser parser = new SampsonParser(1);
    Set<Character> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }
  

  ////// makeSubstringVectors

  private List<Vector<String>> makeSVList(List<String> strings) {
    return strings.stream()
      .map(s -> (Vector<String>) new StringVector(s))
      .toList();
  }

  @Test
  public void makeSubstringVectors_SizeOne()
  {
    SampsonParser parser = new SampsonParser(1);
    List<Vector<String>> result = parser.makeSubstringVectors("input");
    List<Vector<String>> expected = makeSVList(Arrays.asList("i", "n", "p", "u", "t"));
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }
  
  @Test
  public void makeSubstringVectors_SizeLonger()
  {
    SampsonParser parser = new SampsonParser(3);
    List<Vector<String>> result = parser.makeSubstringVectors("input");
    List<Vector<String>> expected = makeSVList(Arrays.asList("inp", "npu", "put"));
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }

  @Test
  public void makeSubstringVectors_SizeFull()
  {
    SampsonParser parser = new SampsonParser(5);
    List<Vector<String>> result = parser.makeSubstringVectors("input");
    List<Vector<String>> expected = makeSVList(Arrays.asList("input"));
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }
  
  @Test
  public void makeSubstringVectors_LongerInput()
  {
    SampsonParser parser = new SampsonParser(4);
    List<Vector<String>> result = parser.makeSubstringVectors("Hello World");
    List<Vector<String>> expected = makeSVList(Arrays.asList(
      "Hell", "ello", "llo ", "lo W", "o Wo", " Wor", "Worl", "orld"));
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }

}
