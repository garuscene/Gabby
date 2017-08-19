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

public class MainApplication {
    private static JTextPane console;

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

        frame.add(console);

        //Input========================
        JTextField input = new JTextField();
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
        addConsole("...");
        addConsole("Naghahanap ng mga kahulugan para sa \"" + word + "\"");
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
        final String VERB = "v.  --";
        final String ADJECTIVE = "adj.  --";
        final String OTHER_WORDS = "/";

        //Mark all nouns, put them in first indention
        for (int i = result.indexOf(NOUN); i >= 0; i = result.indexOf(NOUN, i + NOUN.length())) {
            lineMarkers.add(i);
        }

        //Mark all verbs, put them in first indention
        for (int i = result.indexOf(VERB); i >= 0; i = result.indexOf(VERB, i + VERB.length())) {
            lineMarkers.add(i);
        }

        //Mark all adj, put them in first indention
        for (int i = result.indexOf(ADJECTIVE); i >= 0; i = result.indexOf(ADJECTIVE, i + ADJECTIVE.length())) {
            lineMarkers.add(i);
        }

        //Mark all other words, put them in first indention
        for (int i = result.indexOf(OTHER_WORDS); i >= 0; i = result.indexOf(OTHER_WORDS, i + OTHER_WORDS.length())) {
            lineMarkers.add(i);
        }
    }
}
