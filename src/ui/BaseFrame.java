package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract base class for all application frames.
 * Demonstrates OOP Concepts: Abstraction & Inheritance.
 *
 * Provides a shared clean light-themed design system with high-contrast,
 * easily readable colors.
 */
public abstract class BaseFrame extends JFrame {

    // ── Design System — Color Palette (Clean Light Theme) ───────────
    protected static final Color BG_PRIMARY      = new Color(245, 247, 250);   // soft off-white
    protected static final Color BG_SECONDARY    = Color.WHITE;                // pure white cards
    protected static final Color BG_CARD         = new Color(237, 241, 247);   // light blue-gray
    protected static final Color ACCENT_PRIMARY  = new Color(47, 100, 190);    // strong blue
    protected static final Color ACCENT_HOVER    = new Color(35, 80, 160);     // darker blue
    protected static final Color ACCENT_SUCCESS  = new Color(30, 142, 62);     // strong green
    protected static final Color ACCENT_DANGER   = new Color(210, 43, 43);     // clear red
    protected static final Color ACCENT_WARNING  = new Color(200, 130, 0);     // warm orange
    protected static final Color TEXT_PRIMARY     = new Color(30, 30, 30);      // near-black
    protected static final Color TEXT_SECONDARY   = new Color(90, 90, 90);      // dark gray
    protected static final Color TEXT_MUTED       = new Color(140, 140, 140);   // medium gray
    protected static final Color BORDER_COLOR     = new Color(210, 215, 222);   // soft border

    // ── Design System — Fonts ───────────────────────────────────────
    protected static final Font FONT_TITLE     = new Font("SansSerif", Font.BOLD, 26);
    protected static final Font FONT_SUBTITLE  = new Font("SansSerif", Font.BOLD, 18);
    protected static final Font FONT_BODY      = new Font("SansSerif", Font.PLAIN, 15);
    protected static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 15);
    protected static final Font FONT_SMALL     = new Font("SansSerif", Font.PLAIN, 12);
    protected static final Font FONT_BUTTON    = new Font("SansSerif", Font.BOLD, 14);

    // ── Constructor ─────────────────────────────────────────────────
    public BaseFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_PRIMARY);
        setLayout(null);
    }

    // ── Abstract lifecycle hooks ────────────────────────────────────
    protected abstract void initializeUI();
    protected abstract void setupListeners();

    // ── Helper: Create a styled JButton ─────────────────────────────
    protected JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color originalBg = bgColor;
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg);
            }
        });

        return button;
    }

    // ── Helper: Create a styled JLabel ──────────────────────────────
    protected JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    // ── Helper: Create a card panel with border ─────────────────────
    protected JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_SECONDARY);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        panel.setLayout(null);
        return panel;
    }

    // ── Helper: Create a styled text field ──────────────────────────
    protected JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    // ── Helper: Create a styled password field ──────────────────────
    protected JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
}
