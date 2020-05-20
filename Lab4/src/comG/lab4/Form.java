package comG.lab4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Form {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        final DrawArea drawArea = new DrawArea();
        content.add(drawArea, BorderLayout.CENTER);

        JPanel controls = new JPanel();

        JButton chooseRegionButton = new JButton("Choose region");
        JButton newPointsSetButton = new JButton("New set of points");

        ActionListener actionListener = e -> {
            if(e.getSource() == chooseRegionButton) {
                drawArea.setChoosingRegionMode();
            } else if(e.getSource() == newPointsSetButton) {
                drawArea.setDrawingPointsMode();
            }
        };

        chooseRegionButton.addActionListener(actionListener);
        newPointsSetButton.addActionListener(actionListener);

        controls.add(newPointsSetButton);
        controls.add(chooseRegionButton);

        content.add(controls, BorderLayout.NORTH);

        // JTextArea infoArea = new JTextArea("Results...\n\n");
        // content.add(infoArea, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

