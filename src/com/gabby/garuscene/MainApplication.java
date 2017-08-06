package com.gabby.garuscene;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class MainApplication {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gabby");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(960, 540);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        //Text console
        JTextPane console = new JTextPane();
        console.setBounds(0, 0, frame.getWidth(), frame.getHeight() - 100);
        //console.setEditable(false);
        console.setMargin(new Insets(10, 10, 10, 10));

        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(set, 0.12f);
        StyleConstants.setFontFamily(set, "Consolas");
        console.setParagraphAttributes(set, true);

        frame.getContentPane().add(console);

        frame.setLayout(null);
        frame.setVisible(true);
    }
}
