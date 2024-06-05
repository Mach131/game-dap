package edu.mit.gamedap.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.gamedap.parser.GeneratedGrammarParser.PairContext;
import edu.mit.gamedap.parser.GeneratedGrammarParser.TitleContext;

public class DialogueLineBuilder extends GeneratedGrammarBaseListener {
    private List<DialogueLine> lines = new ArrayList<>();
    private Map<String, Integer> vocabMap = new HashMap<>();
    private int delimiterCount = 0;
    private String title = "";

    public DialogueLineBuilder(Map<String, Integer> vocabMap) {
        this.vocabMap.putAll(vocabMap);

        while (vocabMap.containsKey("TOKEN" + this.delimiterCount)) {
            this.delimiterCount++;
        }
    }

    public List<DialogueLine> getLines() {
        return lines;
    }

    public int getDelimiterCount() {
        return delimiterCount;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void enterTitle(TitleContext ctx) {
        this.title = ctx.getText();
    }

    @Override
    public void enterPair(PairContext ctx) {
        System.out.println(ctx.getText());

        // Locate delimiters in the context
        List<Integer> delimTokenIndex = new ArrayList<>();
        for (int i = 0; i < this.delimiterCount; i ++) {
            int vocabIndex = this.vocabMap.get("TOKEN" + i);
            delimTokenIndex.add(ctx.children.indexOf(ctx.getToken(vocabIndex, 0)));
        }

        int currentPart = 0;
        List<String> dialogueParts = new ArrayList<>();
        List<String> presentDelims = new ArrayList<>();
        dialogueParts.add("");
        for (int i = 0; i < ctx.children.size(); i ++) {
            while (currentPart < delimTokenIndex.size() && delimTokenIndex.get(currentPart) == -1) {
                presentDelims.add("");
                currentPart ++;
                dialogueParts.add("");
            }

            if (currentPart < delimTokenIndex.size() && delimTokenIndex.get(currentPart) == i) {
                do {
                    presentDelims.add(ctx.children.get(i).getText());
                    currentPart ++;
                    dialogueParts.add("");
                } while (currentPart < delimTokenIndex.size() && delimTokenIndex.get(currentPart) != -1);
            } else {
                dialogueParts.set(currentPart, dialogueParts.get(currentPart) + ctx.children.get(i).getText());
            }
        }

        DialogueLine newLine = new DialogueLine(dialogueParts, presentDelims);
        this.lines.add(newLine);
    }
}
