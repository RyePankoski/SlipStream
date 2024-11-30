package com.Rye.DarknessGame;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class DebugUtility {
    private static JFrame frame;
    private static JTextArea textArea;
    private static ConcurrentHashMap<String, String> debugVariables = new ConcurrentHashMap<>();

    // Private constructor to prevent instantiation
    private DebugUtility() {}

    // Initialize the debug window (call this once, e.g., at game startup)
    public static void initialize() {
        if (frame != null) return; // Prevent multiple initializations

        // Set up the frame
        frame = new JFrame("Debug Window");
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);

        // Start a thread to update the debug window
        new Thread(DebugUtility::updateDebugWindow).start();
    }

    // Add or update a debug variable
    public static void updateVariable(String key, String value) {
        debugVariables.put(key, value);
    }

    // Internal method to refresh the debug window content
    private static void updateDebugWindow() {
        while (true) {
            StringBuilder content = new StringBuilder();
            debugVariables.forEach((key, value) -> content.append(key).append(": ").append(value).append("\n"));
            SwingUtilities.invokeLater(() -> textArea.setText(content.toString()));
            try {
                Thread.sleep(100); // Update every 100ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
