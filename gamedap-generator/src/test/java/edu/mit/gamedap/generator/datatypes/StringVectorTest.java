package edu.mit.gamedap.generator.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class StringVectorTest {
  
  ////// get, set, size

  private void checkStringVector(StringVector vector, String string) {
    assertEquals("Unexpected vector size", vector.size(), string.length());
    for (int i = 0; i < string.length(); i++) {
      assertEquals("Unexpected value in vector", string.charAt(i), (char) vector.get(i));
    }
  }

  @Test
  public void testStringVector_Empty() {
    StringVector vector = new StringVector("");
    checkStringVector(vector, "");
  }

  @Test
  public void testStringVector_Singleton() {
    StringVector vector = new StringVector("R");
    checkStringVector(vector, "R");

    char previous = vector.set(0, 'S');
    assertEquals("Unexpected return value from set", 'R', previous);
    checkStringVector(vector, "S");
  }

  @Test
  public void testStringVector_Normal() {
    StringVector vector = new StringVector("hello");
    checkStringVector(vector, "hello");

    char previous = vector.set(3, 'b');
    assertEquals("Unexpected return value from set", 'l', previous);
    checkStringVector(vector, "helbo");
  }


  ////// randomElement

  @Test
  public void testRandomElement_SingletonSet() {
    Set<Character> characterSet = new HashSet<>(Arrays.asList('a'));
    StringVector vector = new StringVector("aaa", characterSet);
    char random = vector.randomElement();
    assertEquals("Unexpected random element returned", 'a', random);
  }

  @Test
  public void testRandomElement_LargerSet() {
    Set<Character> characterSet = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f'));
    StringVector vector = new StringVector("aaa", characterSet);

    for (int i = 0; i < 5; i ++) {
      char random = vector.randomElement();
      assertTrue("Unexpected random element returned", characterSet.contains(random));
    }
  }

  ////// randomize

  @Test
  public void testRandomize() {
    Set<Character> characterSet = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
    StringVector vector = new StringVector("aaaaa", characterSet);
    vector.randomize();

    assertEquals("Unexpected size after randomize", 5, vector.size());
    for (int i = 0; i < 5; i ++) {
      assertTrue("Unexpected randomized element", characterSet.contains(vector.get(i)));
    }
  }

  ////// distance

  @Test
  public void testDistance_Identical() {
    String string = "hello";
    StringVector v1 = new StringVector(string);
    StringVector v2 = new StringVector(string);
    assertEquals("Unexpected distance", 0, v1.distance(v2), 0.01);
    assertEquals("Distances not symmetric", v2.distance(v1), v1.distance(v2), 0.01);
  }

  @Test
  public void testDistance_Single() {
    StringVector v1 = new StringVector("shoe");
    StringVector v2 = new StringVector("show");
    assertEquals("Unexpected distance", 1, v1.distance(v2), 0.01);
    assertEquals("Distances not symmetric", v2.distance(v1), v1.distance(v2), 0.01);
  }

  @Test
  public void testDistance_Multiple() {
    StringVector v1 = new StringVector("hello world");
    StringVector v2 = new StringVector("fellow cold");
    assertEquals("Unexpected distance", 5, v1.distance(v2), 0.01);
    assertEquals("Distances not symmetric", v2.distance(v1), v1.distance(v2), 0.01);
  }

  @Test
  public void testDistance_Disjoint() {
    StringVector v1 = new StringVector("aeiou");
    StringVector v2 = new StringVector("qwert");
    assertEquals("Unexpected distance", 5, v1.distance(v2), 0.01);
    assertEquals("Distances not symmetric", v2.distance(v1), v1.distance(v2), 0.01);
  }
}
