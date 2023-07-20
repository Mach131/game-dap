package edu.mit.gamedap.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

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
}
