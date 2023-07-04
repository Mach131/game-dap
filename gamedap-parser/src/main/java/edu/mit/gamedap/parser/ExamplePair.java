package edu.mit.gamedap.parser;

public class ExamplePair {
    private final String firstPart;
    private final String secondPart;

    public ExamplePair(String firstPart, String secondPart) {
        this.firstPart = firstPart;
        this.secondPart = secondPart;
    }
    
    public String getFirstPart() {
        return firstPart;
    }

    public String getSecondPart() {
        return secondPart;
    }

    @Override
    public String toString() {
        return String.format("[Pair] %s : %s", firstPart, secondPart);
    }
}
