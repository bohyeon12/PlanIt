package main;

import javax.swing.SwingUtilities;

import dao.DBConnector;
import view.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
