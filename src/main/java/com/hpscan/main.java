package com.hpscan;

import javax.swing.*;

public class main {


    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        System.out.println("taxi!!");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        System.out.println("V1");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //scanner.main();
                Scanner scanner = new Scanner();
                System.out.println("V2");
                //scanner.main();
                scanner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                scanner.pack();
                scanner.setVisible(true);
            }
        });

    }
}
