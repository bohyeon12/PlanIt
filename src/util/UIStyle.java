// util/UIStyle.java
package util;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.border.LineBorder;
import javax.swing.DefaultListCellRenderer;
import java.awt.*;


public class UIStyle {

    private static boolean darkMode = false;

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void toggleDarkMode() {
        darkMode = !darkMode;
    }

    public static Color getBackground() {
        return darkMode
                ? new Color(25, 28, 35)
                : Color.WHITE;
    }

    public static Color getCardBackground() {
        return darkMode
                ? new Color(36, 40, 48)
                : Color.WHITE;
    }

    public static Color getCardBorder() {
        return darkMode
                ? new Color(70, 75, 85)
                : new Color(220, 220, 220);
    }

    public static Color getTextPrimary() {
        return darkMode
                ? Color.WHITE
                : Color.DARK_GRAY;
    }

    public static Color getTextSecondary() {
        return darkMode
                ? new Color(170, 170, 180)
                : Color.GRAY;
    }

    public static Color getAccent() {
        return darkMode
                ? new Color(130, 170, 255)
                : new Color(90, 132, 255);
    }

    public static Color getSundayColor() {
        return darkMode
                ? new Color(255, 160, 160)
                : new Color(220, 80, 80);
    }

    public static Color getSaturdayColor() {
        return darkMode
                ? new Color(160, 185, 255)
                : new Color(80, 120, 220);
    }
    
    public static Color getFieldBackground() {
        return darkMode ? new Color(40, 44, 52) : Color.WHITE;
    }

    public static Color getFieldBorderColor() {
        return darkMode ? new Color(90, 90, 110) : new Color(200, 200, 200);
    }


    public static void styleTextField(JTextField field) {
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        field.setForeground(getTextPrimary());
        field.setBackground(UIStyle.isDarkMode()
                ? new Color(70, 74, 84)  
                : new Color(220, 220, 230) );
        field.setCaretColor(getTextPrimary());
        field.setBorder(new LineBorder(getFieldBorderColor()));
    }

    public static void styleComboBox(JComboBox<?> box) {
        Color primary = getTextPrimary();

        box.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        box.setBackground(UIStyle.isDarkMode()
                ? new Color(70, 74, 84)  
                : new Color(220, 220, 230) );
        box.setOpaque(true);   
        box.setBorder(new LineBorder(getFieldBorderColor()));

        
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus
            ) {
                JLabel l = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                l.setOpaque(true);

                if (isSelected) {
                    l.setBackground(
                            UIStyle.isDarkMode()
                                    ? new Color(70, 74, 84)  
                                    : new Color(220, 220, 230) 
                    );
                } else {
                    l.setBackground(UIStyle.getFieldBackground());
                }
                l.setForeground(primary);
                return l;
            }
        });

        box.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▼");
                btn.setMargin(new Insets(0, 4, 0, 4));
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setFocusable(false);
                btn.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
                btn.setForeground(UIStyle.getTextSecondary());
                btn.setBackground(UIStyle.getFieldBackground());
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
        
        if (box.isEditable() && box.getEditor() != null) {
            Component editorComp = box.getEditor().getEditorComponent();
            if (editorComp instanceof JTextField tf) {
                styleTextField(tf);  
            }
        }

    }
    
    public static void styleTextArea(JTextArea area) {
        area.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        area.setForeground(getTextPrimary());
        area.setBackground(getFieldBackground());
        area.setCaretColor(getTextPrimary());
        area.setBorder(new LineBorder(getFieldBorderColor()));
    }

    public static void styleTextScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(new LineBorder(getFieldBorderColor()));
        scrollPane.getViewport().setBackground(getFieldBackground());
        scrollPane.setOpaque(false);
    }
}
