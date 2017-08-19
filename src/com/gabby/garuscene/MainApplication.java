package com.gabby.garuscene;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MainApplication {
    private static JTextPane console;
    private static JTextField input;
    private static JScrollPane scrollPane;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gabby");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(960, 540);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        //Text console=================
        console = new JTextPane();
        console.setEditable(false);
        console.setMargin(new Insets(10, 10, 10, 10));
        console.setPreferredSize(new Dimension(960, 520));

        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(set, 0.12f);
        StyleConstants.setFontFamily(set, "Consolas");
        console.setParagraphAttributes(set, true);

        scrollPane = new JScrollPane(console);
        frame.add(scrollPane);

        //Input========================
        input = new JTextField();
        input.setMargin(new Insets(5, 5, 5, 5));
        input.setFont(new Font("Consolas", Font.PLAIN, 12));
        input.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDictinonary(input.getText());
            }
        });

        frame.add(input);

        //End==========================
        frame.pack();
        frame.setVisible(true);

        input.requestFocus();

        addConsole("Maligayang Pagdating sa Gabby!");
        addConsole("Maglagay ng salita upang isalin sa Filipino!");
    }

    private static void searchDictinonary(String word) {
        addConsole("***********************************************************");
        addConsole("Naghahanap ng mga kahulugan para sa \"" + word + "\"");
        addConsole("***********************************************************");
        //https://stackoverflow.com/questions/11497424/passing-data-to-an-html-post-form-using-java
        try {
            URL url = new URL("http://gabbydictionary.com/");
            URLConnection con = url.openConnection();
            con.setDoOutput(true);

            PrintWriter wr = new PrintWriter(con.getOutputStream(), true);

            StringBuilder parameters = new StringBuilder();
            parameters.append("q=");
            parameters.append(URLEncoder.encode(word, "UTF-8"));
            parameters.append("&x=");
            parameters.append(URLEncoder.encode("0", "UTF-8"));
            parameters.append("&y=");
            parameters.append(URLEncoder.encode("0", "UTF-8"));
            wr.println(parameters);
            wr.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while((line = br.readLine()) != null) {
                if (line.contains("Results:<br>")) {
                    formatResults(line);
                    break;
                }
                if (line.contains("Search returned zero results..")) {
                    addConsole("Walang nahanap.");
                    input.setText("");
                    break;
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addConsole(String t) {
        StyledDocument doc = console.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), t + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static void formatResults(String result) {
        result = result.trim();
        result = result.substring(12, result.length() - 12);

        ArrayList<Integer> lineMarkers = new ArrayList<>();

        //Split lines of nouns, verbs, adjectives
        final String NOUN = "n.  --";
        final String NOUN2 = "n. --";
        final String VERB = "v.  --";
        final String VERB2 = "v. --";
        final String ADJECTIVE = "adj.  --";
        final String ADJECTIVE2 = "adj. --";
        final String OTHER_WORDS = "/";

        //Mark all nouns
        markThese(result, lineMarkers, NOUN);
        markThese(result, lineMarkers, NOUN2);

        //Mark all verbs
        markThese(result, lineMarkers, VERB);
        markThese(result, lineMarkers, VERB2);

        //Mark all adj
        markThese(result, lineMarkers, ADJECTIVE);
        markThese(result, lineMarkers, ADJECTIVE2);

        //Mark all other words
        markThese(result, lineMarkers, OTHER_WORDS);

        //Mark related words
        Pattern p = Pattern.compile("\\b[A-Z]{4,}\\b");
        Matcher m = p.matcher(result);
        while (m.find()) {
            String word = m.group();

            int index = result.indexOf(word);
            while (lineMarkers.contains(index)) {
                index = result.indexOf(word, index + 1);
            }

            lineMarkers.add(index);
        }

        if (!lineMarkers.contains(0)) {
            lineMarkers.add(0);
        }

        //Sort in ascending order
        lineMarkers.sort(Comparator.comparing(Integer::valueOf));

        //Get all the strings into parts
        ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < lineMarkers.size(); i++) {

            //If it's the last marker, the upper bound should be the end of the string.
            int upBound = i + 1 > lineMarkers.size() - 1 ? result.length() : lineMarkers.get(i + 1) - 1;

            //Matches if the line should be indented
            String currentLine = result.substring(lineMarkers.get(i), upBound);
            if (Stream.of(NOUN, NOUN2, VERB, VERB2, ADJECTIVE, ADJECTIVE2).anyMatch(currentLine::startsWith)) {
                currentLine = "    " + currentLine.trim();
            }
            //No indention
            else if (currentLine.startsWith(OTHER_WORDS)) {
                currentLine = currentLine.substring(1, currentLine.length()).trim();
            }
            //Same.
            else {
                currentLine = currentLine.trim();
            }

            lines.add(currentLine);
        }

        printOutResults(lines);
    }

    private static void markThese(String base, ArrayList<Integer> list, String comparator) {
        for (int i = base.indexOf(comparator); i >= 0; i = base.indexOf(comparator, i + comparator.length())) {
            list.add(i);
        }
    }

    private static void printOutResults(ArrayList<String> lines) {
        lines.forEach(MainApplication::addConsole);
        input.setText("");
        scrollDown();
    }

    private static void scrollDown() {
        console.setCaretPosition(console.getDocument().getLength());
    }
}
