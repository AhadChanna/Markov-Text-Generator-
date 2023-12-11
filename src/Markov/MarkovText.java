package Markov;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface MarkovTextGenerator {
    void train(String sourceText);

    String suggestNextWords(String inputWord);

    void retrain(String sourceText);
}

class ListNode {
    String word;
    List<String> nextWords;

    public ListNode(String word) {
        this.word = word;
        this.nextWords = new ArrayList<>();
    }

    public void addNextWord(String nextWord) {
        nextWords.add(nextWord);
    }
}

public class MarkovText implements MarkovTextGenerator {

    private List<ListNode> wordList;
    private String starter;
    private List<String> selectedWords;

    public MarkovText() {
        this.wordList = new ArrayList<>();
        this.starter = "";
        this.selectedWords = new ArrayList<>();
    }

    @Override
    public void train(String sourceText) {
        String[] cleanedWords = sourceText.replaceAll("[^a-zA-Z0-9]+", " ").toLowerCase().split("\\s+");

        if (cleanedWords.length == 0) {
            return;
        }

        starter = cleanedWords[0];
        String prevWord = starter;

        for (String currentWord : cleanedWords) {
            ListNode prevNode = findNode(prevWord);
            if (prevNode == null) {
                prevNode = new ListNode(prevWord);
                wordList.add(prevNode);
            }
            prevNode.addNextWord(currentWord);

            prevWord = currentWord;
        }

        ListNode lastNode = findNode(cleanedWords[cleanedWords.length - 1]);
        if (lastNode == null) {
            lastNode = new ListNode(cleanedWords[cleanedWords.length - 1]);
            wordList.add(lastNode);
        }
        lastNode.addNextWord(starter);
    }

    @Override
    public String suggestNextWords(String inputWord) {
        ListNode node = findNode(inputWord);
        return (node != null) ? String.join(", ", node.nextWords) : "No suggestions available for the given word.";
    }

    @Override
    public void retrain(String sourceText) {
        wordList.clear();
        starter = "";
        train(sourceText);
    }

    private ListNode findNode(String word) {
        return wordList.stream().filter(node -> node.word.equalsIgnoreCase(word)).findFirst().orElse(null);
    }

    public void trainFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder text = new StringBuilder();
            reader.lines().forEach(line -> text.append(line).append(" "));
            train(text.toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MarkovText generator = new MarkovText();
        generator.trainFromFile("C:\\Users\\GAMERS GATE\\eclipse-workspace\\Markov\\src\\Markov\\file.txt");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter a word (or 'exit' to quit): ");
            String inputWord = scanner.nextLine();

            if (inputWord.equalsIgnoreCase("exit")) {
                System.out.println("Selected words: " + String.join(" ", generator.selectedWords));
                break;
            }

            String suggestions = generator.suggestNextWords(inputWord);
            System.out.println("Suggestions: " + suggestions);

            generator.selectedWords.add(inputWord);
        }

        scanner.close();
    }
}
