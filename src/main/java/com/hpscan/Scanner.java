package com.hpscan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Scanner extends JFrame{
    private JPanel root;
    private JButton scanButton;
    private JButton previwButton;
    private JComboBox comboBoxResolution;
    private JComboBox comboBoxColorSpace;
    private JComboBox comboBoxExtension;

    private JTextField windowsSizeTexBox;
    private JPanel configurationPanel;
    private JLabel tituloAjustesEscaneo;
    private JTextField texBoxFileName;
    private JTextField texBoxFolderPath;
    private JTextField prevewMousePossitionBox;
    private JPanel PanelDerecho;
    private JPanel panelHUD;
    private JPanel panelPreview;
    private JEditorPane editorPane1;

    //Imagenes
    ImageIcon imagen2;
    ImageIcon imagen1;
    ImageIcon imagen3;

    //Current Preview
    int viewerWith;
    int viewerHeight;
    JLayeredPane previewLayeredPanel;
    private JLabel imagePreviwLabel;
    private Image currentViewerImage;
    private Point initialSelectionPoint;
    private Point finalSelectionPoint;
    private  Boolean leftClickPosition = false;

    //Save scan
    private Path outputFolderScanPath;
    final String defaultFileName = "Imagen.png";
    private String fileName;




    public Scanner() {
        setTitle("Scanner");
        add(root);
        imagePreviwLabel = new JLabel();
        previewLayeredPanel = new JLayeredPane();
        //Crea el tamño de la ventana en funcion de el tamaño de la pantalla
        normalizeWindowsSize();
        setDefaultValues();


        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(panel1, "Hola: "+screenSize.getHeight()+" - "+screenSize.getWidth());
                //imagenPreviwLabel = new JLabel(new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\vlcsnap-2018-06-29-16h44m46s273.png"));
                updateViewerImage(1);

                runScan(Paths.get(texBoxFolderPath.getText()), texBoxFileName.getText(),Integer.parseInt(comboBoxResolution.getSelectedItem().toString()),comboBoxColorSpace.getSelectedItem().toString());


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


//        //Posicion del raton en canvas
//        imagenPreviwLabel.addMouseListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                super.mouseCli(e);
//                Boolean newState;
//                if(SwingUtilities.isLeftMouseButton(e)){
//                    newState=changeMousePosition();
//                    //Se ha liberado el boton
//                    if(!newState){
//                        finalSelectionPoint=e.getPoint();
//                        prevewMousePossitionBox.setText(initialSelectionPoint.toString() +"  - "+finalSelectionPoint);
//                    }else{ //Se ha capturado el boton
//                        initialSelectionPoint=e.getPoint();
//                    }
//                }
//                //prevewMousePossitionBox.setText(imagenPreviwLabel.getMousePosition().toString());
//            }
//        });

//        imagenPreviwLabel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                super.mousePressed(e);
//                finalSelectionPoint=imagenPreviwLabel.getMousePosition();
//                System.out.println("RELEASED: "+finalSelectionPoint.toString());
//
//
//            }
//        });
//        imagenPreviwLabel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased(e);
//
//                //initialSelectionPoint=imagenPreviwLabel.getMousePosition();
//                initialSelectionPoint=e.getPoint();
//                //prevewMousePossitionBox.setText(initialSelectionPoint.toString() +"  - "+finalSelectionPoint.toString());
//                System.out.println("PRESS: "+ initialSelectionPoint.toString());
//            }
//        });






        imagePreviwLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                System.out.println("D: "+e.getPoint().toString());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("U: "+e.getPoint().toString());

            }
        });
        previwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // paintRectangle();
            }
        });
    }


    private Boolean changeMousePosition(){

        synchronized(leftClickPosition){
            if(leftClickPosition){
                leftClickPosition=false;
                return false;
            }else{
                leftClickPosition=true;
                return true;
            }
        }
    }





    private void updateViewerImage(int imageNumber){
        switch(imageNumber){
            case 1:
                setViewerImage(imagen1.getImage());
                //currentViewerImage=imagen1.getImage();
                updateViewerSize();
                //imagenPreviwLabel.setIcon(imagen1);
                break;
            case 2:
                setViewerImage(imagen2.getImage());
                //currentViewerImage=imagen2.getImage();
                updateViewerSize();
                //imagenPreviwLabel.setIcon(imagen2);

                break;
            default :
                System.out.println("No se reconoce el valor");

        }
    }
    private void updateViewerImage(ImageIcon newImage){
        imagePreviwLabel.setIcon(newImage);

    }

    /////////////////INICIO PREVIEW//////////////
    private void initPreviewPanel(){



       // JLayeredPane previewLayeredPanel = new JLayeredPane();
        previewLayeredPanel.setBackground(Color.RED);
        previewLayeredPanel.setBorder(BorderFactory.createTitledBorder("Move the Mouse to Move Duke"));

        //imagePreviwLabel = new JLabel();


        Rectangles selector = new Rectangles();
        selector.setBackground(Color.orange);
        selector.setBounds(0, 0, 100, 100);
        //adding buttons on panel
        previewLayeredPanel.add(selector, 1);
        previewLayeredPanel.add(imagePreviwLabel, 2);

        //Carga el contenido en el panel
        panelPreview.add(previewLayeredPanel);
        panelPreview.validate();

    }
    private void UpdatePreviewPanelSize(){


    }

    /////////////////FINAL PREVIEW//////////////////


    public void paintRectangle(){
        System.out.println("Layered");

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBackground(Color.RED);
        layeredPane.setBounds(0,0,300,300);
        //Rectangles rects = new Rectangles();
        layeredPane.setBorder(BorderFactory.createTitledBorder("Move the Mouse to Move Duke"));

        int availableWith = root.getWidth() - configurationPanel.getWidth();
        //int availableHeight = root.getHeight() -
        //int availableWith = imagenPreviwLabel.getWidth();
        int availableHeight = root.getHeight()-windowsSizeTexBox.getHeight()-tituloAjustesEscaneo.getHeight()-10;


//        JButton top = new JButton();
//        top.setBackground(Color.white);
//        top.setBounds(20, 20, 50, 50);
//        JButton middle = new JButton();
//        middle.setBackground(Color.red);
//        middle.setBounds(40, 40, 50, 50);
//        JButton bottom = new JButton();
//        bottom.setBackground(Color.cyan);
//        bottom.setBounds(60, 60, 50, 50);
        Rectangles rects = new Rectangles();
        rects.setBackground(Color.orange);
        rects.setBounds(0, 0, 100, 200);
        //adding buttons on pane
//        layeredPane.add(bottom, new Integer(1));
//        layeredPane.add(middle, new Integer(2));
//        layeredPane.add(top, new Integer(3));
        layeredPane.add(rects, new Integer(0));

        panelPreview.add(layeredPane);
        panelPreview.validate();
        System.out.println(panelPreview.getComponents().length);
        //System.out.println(testJPanel.getComponents()[0].toString());

        //imagenPreviwLabel.getConten.add(rectangle);
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
        }else{
            //No se ha cargado la imagen por lo que se creara la vista

            initPreviewPanel();

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
        imagePreviwLabel = new JLabel();
        imagen2= new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\vlcsnap-2018-06-29-16h44m46s273.png");
        imagen1= new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\Sin título-1.jpg");
        imagen3= new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\hpscan001.png");


        currentViewerImage=imagen3.getImage();


       // updateViewerSize();
        //imagen3.setImage(getScaledImage(imagen3.getImage(),0.10f));

        //imagenPreviwLabel.setIcon(imagen3);

    }

    public void setViewerImage(Image newImage){
        synchronized (currentViewerImage){

            currentViewerImage=newImage;
        }

    }

    public void setDefaultValues(){

        outputFolderScanPath =  Path.of(System.getProperty("user.home"),"Escritorio");

        texBoxFolderPath.setText(outputFolderScanPath.toString());
        texBoxFileName.setText(defaultFileName);

        leftClickPosition=false;


    }

    class selectedArea extends JComponent {
        String s = "message";
        int x = 45;
        int y = 45;
        public void paint(Graphics g) {
            g.drawRect (10, 10, 200, 200);
            g.setColor(Color.red);
            g.drawString(s, x, y);
        }
    }

    public class Rectangles extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(new Color(212, 212, 212));
            g2d.drawRect(10, 15, 90, 60);


            g2d.setColor(new Color(31, 21, 1));
            g2d.fillRect(250, 195, 90, 60);

        }

    }

    ////////////////////////////LANZAR COMANDOS DE SISTEMA////////////////////////////
    public void runScan(Path filePath, String fileName, int dpiResolution, String colorSpace) {
        String destinationPath=Path.of(filePath+fileName).toString();


        String comandoDeEscaneo="hp-scan "+"--mode "+colorSpace+" --resolution "+dpiResolution+" --dest "+destinationPath;
        //ProcessBuilder builder = new ProcessBuilder();
        //builder.command(comandoDeEscaneo);
        System.out.println(comandoDeEscaneo);
    }


}
