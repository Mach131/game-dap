package edu.mit.gamedap.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SampsonParserTest {

  // makeSubstringVectors

  @Test
  public void makeSubstringVectors_SizeOne()
  {
    SampsonParser parser = new SampsonParser(1);
    List<String> result = parser.makeSubstringVectors("input");
    List<String> expected = Arrays.asList("i", "n", "p", "u", "t");
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }
  
  @Test
  public void makeSubstringVectors_SizeLonger()
  {
    SampsonParser parser = new SampsonParser(3);
    List<String> result = parser.makeSubstringVectors("input");
    List<String> expected = Arrays.asList("inp", "npu", "put");
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }

  @Test
  public void makeSubstringVectors_SizeFull()
  {
    SampsonParser parser = new SampsonParser(5);
    List<String> result = parser.makeSubstringVectors("input");
    List<String> expected = Arrays.asList("input");
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }
  
  @Test
  public void makeSubstringVectors_LongerInput()
  {
    SampsonParser parser = new SampsonParser(4);
    List<String> result = parser.makeSubstringVectors("Hello World");
    List<String> expected = Arrays.asList(
      "Hell", "ello", "llo ", "lo W", "o Wo", " Wor", "Worl", "orld");
    assertEquals("Unexpected makeSubstringVectors result", expected, result);
  }

  // buildCharacterSet

  private void validateCharacterSet(String text, Map<Character, Integer> result) {
    Map<Character, Integer> foundCharacters = new HashMap<>();
    for (Character c : text.toCharArray()) {
      assertTrue("Character set did not contain character", result.containsKey(c));
      if (!foundCharacters.containsKey(c)) {
        int idx = result.get(c);
        assertFalse("Character set contains duplicate indices", foundCharacters.containsValue(idx));
        assertTrue("Index in character set is too large", idx < result.size());
        foundCharacters.put(c, idx);
      }
    }
  }

  @Test
  public void buildCharacterSet_Empty()
  {
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> result = parser.buildCharacterSet("");
    assertEquals("Expected empty result", 0, result.size());
  }

  @Test
  public void buildCharacterSet_Singleton()
  {
    String input = "X";
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Unique()
  {
    String input = "World";
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Repeats()
  {
    String input = "Hello World";
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }

  @Test
  public void buildCharacterSet_Longer()
  {
    String input = "This sentence has\nmany characters...";
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> result = parser.buildCharacterSet(input);
    validateCharacterSet(input, result);
  }
}
