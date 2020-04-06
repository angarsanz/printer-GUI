package com.hpscan;

import javax.swing.*;

public class main {


    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner();
                scanner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                scanner.setVisible(true);
            }
        });

    }
}
