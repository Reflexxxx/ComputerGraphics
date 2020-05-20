package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        var frame = new JFrame();
        var content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        final var drawArea = new DrawArea();
        content.add(drawArea, BorderLayout.CENTER);
        var controls = new JPanel();
        var newPointsButton = new JButton("Очистити");
        var okButton = new JButton("Побудувати опуклу оболонку методом швидкоподілу");

        ActionListener actionListener = e -> {
            if (e.getSource() == newPointsButton) {
                okButton.setEnabled(true);
                drawArea.lockDrawArea = false;
                drawArea.clearData();
            } else if (e.getSource() == okButton) {
                //okButton.setEnabled(false);
                //drawArea.lockDrawArea = true;
                drawArea.createConvexHull();
            }
        };

        newPointsButton.addActionListener(actionListener);
        okButton.addActionListener(actionListener);
        controls.add(newPointsButton);
        controls.add(okButton);
        content.add(controls, BorderLayout.NORTH);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}