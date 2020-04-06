package com.hpscan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class Scanner extends JFrame{
    private JPanel root;
    private JButton scanButton;
    private JButton previwButton;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JLabel imagenPreviwLabel;
    private JTextField windowsSizeTexBox;
    private JPanel configurationPanel;
    private JLabel tituloAjustesEscaneo;
    private JTextField textField1;
    private JTextField textField2;


    //Imagenes
    ImageIcon imagen2;
    ImageIcon imagen1;
    ImageIcon imagen3;

    //Current Preview
    int viewerWith;
    int viewerHeight;
    Image currentViewerImage;


    public Scanner() {
        setTitle("Scanner");
        add(root);
        //Crea el tamño de la ventana en funcion de el tamaño de la pantalla
        normalizeWindowsSize();



        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(panel1, "Hola: "+screenSize.getHeight()+" - "+screenSize.getWidth());
                //imagenPreviwLabel = new JLabel(new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\vlcsnap-2018-06-29-16h44m46s273.png"));
                updateViewerImage(1);
            }
        });


        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                windowsSizeTexBox.setText(root.getWidth()+" X,   "+ root.getHeight()+" Y");
                updateViewerSize();

            }
        });

    }

    private void updateViewerImage(int imageNumber){
        switch(imageNumber){
            case 1:
                imagenPreviwLabel.setIcon(imagen1);
                break;
            case 2:
                imagenPreviwLabel.setIcon(imagen2);
                break;
            default :
                System.out.println("No se reconoce el valor");

        }
    }
    private void updateViewerImage(ImageIcon newImage){
        imagenPreviwLabel.setIcon(newImage);
    }

    private void updateViewerSize(){
        if(currentViewerImage!=null) {
            //valores de la ventana
            int currentWindowWith = root.getWidth();
            int currentWindowHeight = root.getHeight();
            //valor actual de la imagen
            currentViewerImage.getWidth(null);
            currentViewerImage.getHeight(null);

            int availableWith = root.getWidth() - configurationPanel.getWidth();
            //int availableHeight = root.getHeight() -
            //int availableWith = imagenPreviwLabel.getWidth();
            int availableHeight = root.getHeight()-windowsSizeTexBox.getHeight()-tituloAjustesEscaneo.getHeight()-10;


            float proporcionW = (float) ((float) availableWith / (float) currentViewerImage.getWidth(null));
            float proporcionH = (float) ((float) availableHeight / (float) currentViewerImage.getHeight(null));
            System.out.println("La nueva proporcion es: "+proporcionW+"X - "+proporcionH+"Y"+ "****" +availableHeight);
            //Actualzia el valor de la imagen
            ImageIcon scaledViewerImage=new ImageIcon(currentViewerImage);
            if(proporcionW<proporcionH){

                updateViewerImage(new ImageIcon(getScaledImage(scaledViewerImage.getImage(), proporcionW)));
            }else{
                updateViewerImage(new ImageIcon(getScaledImage(scaledViewerImage.getImage(), proporcionH)));
            }
            //updateViewerImage(new ImageIcon(getScaledImage(scaledViewerImage.getImage(), proporcionW)));
            //imagen3.setImage(getScaledImage(scaledViewerImage.getImage(), proporcion));
            //imagenPreviwLabel.setIcon(imagen3);
        }
    }
    private void normalizeWindowsSize(){

        float proporcionHorizontal = 0.5f;
        float proporcionVertical = 0.5f;
        int windowWith;
        int windowHeight;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        windowWith= (int )((float) screenSize.getWidth()*proporcionHorizontal);
        windowHeight= (int )((float) screenSize.getHeight()*proporcionVertical);

        setSize(windowWith,windowHeight);
    }


    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
    private Image getScaledImage(Image srcImg, float scalingFactor){

        int w = srcImg.getWidth(null);
        int h = srcImg.getHeight(null);
        h*=scalingFactor;
        w*=scalingFactor;

        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        //imagenPreviwLabel = new JLabel(new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\Sin título-1.jpg"));
        imagenPreviwLabel= new JLabel();
        imagen2= new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\vlcsnap-2018-06-29-16h44m46s273.png");
        imagen1= new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\Sin título-1.jpg");
        imagen3= new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\hpscan001.png");


        currentViewerImage=imagen3.getImage();
       // updateViewerSize();
        //imagen3.setImage(getScaledImage(imagen3.getImage(),0.10f));

        //imagenPreviwLabel.setIcon(imagen3);



    }
}
