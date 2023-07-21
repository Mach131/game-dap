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

  ////// makeSubstringVectors

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

  ////// buildCharacterSet

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

  ////// oneHotEncoding

  private Map<Character, Integer> makeCharacterMap(String chars) {
    Map<Character, Integer> characterMap = new HashMap<>();
    for (int i = 0; i < chars.length(); i++) {
      characterMap.put(chars.charAt(i), i);
    }
    return characterMap;
  }

  @Test
  public void oneHotEncoding_singleSet()
  {
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> characterMap = makeCharacterMap("b");
    Vector<Integer> result = parser.oneHotEncoding('b', characterMap);
    Vector<Integer> expected = new Vector<>(Arrays.asList(1));
    assertEquals("Unexpected oneHotEncoding result", expected, result);
  }

  @Test
  public void oneHotEncoding_middleOfSet()
  {
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> characterMap = makeCharacterMap("aeiou");
    Vector<Integer> result = parser.oneHotEncoding('i', characterMap);
    Vector<Integer> expected = new Vector<>(Arrays.asList(0, 0, 1, 0, 0));
    assertEquals("Unexpected oneHotEncoding result", expected, result);
  }

  @Test
  public void oneHotEncoding_endOfSet()
  {
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> characterMap = makeCharacterMap("qwop");
    Vector<Integer> result = parser.oneHotEncoding('p', characterMap);
    Vector<Integer> expected = new Vector<>(Arrays.asList(0, 0, 0, 1));
    assertEquals("Unexpected oneHotEncoding result", expected, result);
  }

  ////// convertSubstringVectors

  @Test
  public void convertSubstringVectors_OneCharOneSet() {
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> characterMap = makeCharacterMap("Q");
    List<String> input = Arrays.asList("Q");
    List<Vector<Integer>> result = parser.convertSubstringVectors(input, characterMap);
    List<Vector<Integer>> expected = Arrays.asList(new Vector<>(Arrays.asList(1)));
    assertEquals("Unexpected convertSubstringVectors result", expected, result);
  }

  @Test
  public void convertSubstringVectors_MultiCharOneSet() {
    SampsonParser parser = new SampsonParser(3);
    Map<Character, Integer> characterMap = makeCharacterMap("a");
    List<String> input = Arrays.asList("aaa");
    List<Vector<Integer>> result = parser.convertSubstringVectors(input, characterMap);
    List<Vector<Integer>> expected = Arrays.asList(new Vector<>(Arrays.asList(1, 1, 1)));
    assertEquals("Unexpected convertSubstringVectors result", expected, result);
  }

  @Test
  public void convertSubstringVectors_OneCharMultiSet() {
    SampsonParser parser = new SampsonParser(1);
    Map<Character, Integer> characterMap = makeCharacterMap("xyz");
    List<String> input = Arrays.asList("y");
    List<Vector<Integer>> result = parser.convertSubstringVectors(input, characterMap);
    List<Vector<Integer>> expected = Arrays.asList(new Vector<>(Arrays.asList(0, 1, 0)));
    assertEquals("Unexpected convertSubstringVectors result", expected, result);
  }

  @Test
  public void convertSubstringVectors_MultiCharMultiSet() {
    SampsonParser parser = new SampsonParser(3);
    Map<Character, Integer> characterMap = makeCharacterMap("abc");
    List<String> input = Arrays.asList("cba");
    List<Vector<Integer>> result = parser.convertSubstringVectors(input, characterMap);
    List<Vector<Integer>> expected = Arrays.asList(new Vector<>(
      Arrays.asList(0, 0, 1, /**/ 0, 1, 0, /**/ 1, 0, 0)));
    assertEquals("Unexpected convertSubstringVectors result", expected, result);
  }
  
  @Test
  public void convertSubstringVectors_MultipleElements() {
    SampsonParser parser = new SampsonParser(2);
    Map<Character, Integer> characterMap = makeCharacterMap("12345");
    List<String> input = Arrays.asList("31", "41", "52");
    List<Vector<Integer>> result = parser.convertSubstringVectors(input, characterMap);
    List<Vector<Integer>> expected = Arrays.asList(
      new Vector<>(Arrays.asList(0, 0, 1, 0, 0, /**/ 1, 0, 0, 0, 0)),
      new Vector<>(Arrays.asList(0, 0, 0, 1, 0, /**/ 1, 0, 0, 0, 0)),
      new Vector<>(Arrays.asList(0, 0, 0, 0, 1, /**/ 0, 1, 0, 0, 0)));
    assertEquals("Unexpected convertSubstringVectors result", expected, result);
  }
}
