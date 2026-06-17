package main;

import ui.LoginFrame;
import javax.swing.*;

/**
 * Entry point of the Student Quiz Application.
 * Launches the LoginFrame on the Swing Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        // Set system look-and-feel for native window decorations
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default if system L&F is unavailable
        }

        // Launch UI on the Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
