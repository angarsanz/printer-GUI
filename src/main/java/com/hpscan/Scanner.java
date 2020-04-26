package com.hpscan;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import com.hpscan.normalizedPoint;

public class Scanner extends JFrame {
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
    private JTextField selectedAreaBox;
    private JPanel PanelDerecho;
    private JPanel panelHUD;
    private JPanel panelPreview;
    private JComboBox comboBox1;
    private JTextArea consoleLog;
    private JProgressBar progressBarScan;
    protected JButton scanAreaButton;
    protected JButton cleanSelectionButton;
    private JEditorPane editorPane1;

    //Imagenes
//    ImageIcon imagen2;
//    ImageIcon imagen1;
    ImageIcon defaultImage;
    ImageIcon previewImageIcon;

    //Current Preview
    int viewerWith;
    int viewerHeight;
    JLayeredPane previewLayeredPanel;
    private JLabel imagePreviwLabel;
    private Image currentViewerImage;
    private Point initialSelectionPoint;
    private Point finalSelectionPoint;
    private normalizedPoint initialSelectionPointNormalized;
    private normalizedPoint finalSelectionPointNormalized;
    private Boolean leftClickPosition = false;
    JLabel selector;
    private final float scanPlateWith = 215.9f; //tamaño de la plancha completa
    private final float scanPlateHeight = 297.0f; //tamaño de la plancha completa

    //Save scan
    private Path outputFolderScanPath;
    final String defaultFileName = "Imagen.png";
    private String fileName;


    public Scanner() {
        System.out.println("VR");
        //root = new JPanel();
        setTitle("Scanner");
        if (root == null) {
            System.out.println("Falta algo en root");
        }
        this.add(root);
        imagePreviwLabel = new JLabel();
        previewLayeredPanel = new JLayeredPane();
        panelPreview.setLayout(new BoxLayout(panelPreview, BoxLayout.Y_AXIS));
        //Crea el tamño de la ventana en funcion de el tamaño de la pantalla
        normalizeWindowsSize();
        setDefaultValues();
        progressBarScan.setStringPainted(true);


        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new Thread(new Runnable() {
                    public void run() {
                        runScan(Paths.get(texBoxFolderPath.getText()), texBoxFileName.getText(), Integer.parseInt(comboBoxResolution.getSelectedItem().toString()), comboBoxColorSpace.getSelectedItem().toString());

                    }
                }).start();


            }
        });


        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);

                windowsSizeTexBox.setText(root.getWidth() + " X,   " + root.getHeight() + " Y");
                updateViewerSize();

            }
        });


        imagePreviwLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                initialSelectionPoint = e.getPoint();
                System.out.println("D: " + e.getPoint().toString());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);


                finalSelectionPoint = e.getPoint();
                System.out.println("U: " + e.getPoint().toString());
                updateSelectedArea();

            }
        });
        previwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // paintRectangle();
                //get File path

                String tmpFilename = "scanner-" + ThreadLocalRandom.current().nextInt(1, 100) + ".jpg";
                Path previewImagePath = Path.of(System.getProperty("java.io.tmpdir"));
                //Lock graphical interface

                //Scan
                new Thread(new Runnable() {
                    public void run() {
                        runScan(previewImagePath, tmpFilename, 75, "color");
                        // runScan(previewImagePath, tmpFilename, 75, "color");

                        //Wait untilFinish

                        //Remplace image
                        System.out.println(previewImagePath.toString() + "/" + tmpFilename);
                        previewImageIcon = new ImageIcon(Path.of(previewImagePath.toString(), tmpFilename).toString());
                        System.out.println(">>>>>>" + previewImageIcon.getIconHeight());
                        setViewerImage(previewImageIcon.getImage());
                        //updatePreviewPanelSize();
                        updateViewerSize();

                        //Remove prevew Image
                        File temporalFile = new File(Path.of(previewImagePath.toString(), tmpFilename).toString());
                        temporalFile.delete();

                    }

                }).start();
            }
        });
        scanAreaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                float selectedAreaTlX=((float)initialSelectionPointNormalized.getX()/100.0f)*scanPlateWith;
                float selectedAreaTlY=((float)initialSelectionPointNormalized.getY()/100.0f)*scanPlateHeight;
                float selectedAreaBrX=((float)finalSelectionPointNormalized.getX()/100.0f)*scanPlateWith;
                float selectedAreaBrY=((float)finalSelectionPointNormalized.getY()/100.0f)*scanPlateHeight;

                System.out.println("initialSelectionPointNormalized.getX(): "+initialSelectionPointNormalized.getX());

                new Thread(new Runnable() {
                    public void run() {
                        runScan(Paths.get(texBoxFolderPath.getText()),
                                texBoxFileName.getText(),
                                Integer.parseInt(comboBoxResolution.getSelectedItem().toString()),
                                comboBoxColorSpace.getSelectedItem().toString(),
                                (int) selectedAreaTlX,
                                (int) selectedAreaTlY,
                                (int) selectedAreaBrX,
                                (int) selectedAreaBrY);

                    }
                }).start();
            }
        });

        cleanSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Eliminacion de area seleccionada almacenada
                initialSelectionPoint.setLocation(0,0);
                finalSelectionPoint.setLocation(0,0);

                //Refreco del interfaz (para que el usuario perciba los cambios)
                updateSelectedArea();

            }
        });
    }




    private void updateViewerImage(ImageIcon newImage) {
        imagePreviwLabel.setIcon(newImage);
        System.out.println("Resulucionde la imagen: " + newImage.getIconWidth() + "X - " + newImage.getIconHeight() + "Y");

    }


    /////////////////INICIO PREVIEW//////////////
    private void initPreviewPanel() {

        Border selectorBorder;
        // JLayeredPane previewLayeredPanel = new JLayeredPane();
        previewLayeredPanel.setBackground(Color.RED);
        //previewLayeredPanel.setBorder(BorderFactory.createTitledBorder("TEST-ELIMINAR AL TERMINAR"));
        //previewLayeredPanel.setBackground(new Color(132,137,141));
        //previewLayeredPanel.setBackground(new Color(0,0,0));
        panelPreview.setBackground(new Color(132, 137, 141));
        //imagePreviwLabel = new JLabel();


        selector = new JLabel();
        selector.setBackground(Color.red);
        selector.setVisible(true);
        //selector.setText("dfsdsfdsfdsfdsfds");
        selector.setBounds(0, 0, 100, 100);
        selector.setSize(0, 0);
        selectorBorder = BorderFactory.createDashedBorder(Color.BLACK,2,2);
        //selectorBorder = BorderFactory.createLineBorder(Color.BLUE, 2);
        selector.setBorder(selectorBorder);

        imagePreviwLabel.setBounds(100, 200, 100, 200);
        //imagePreviwLabel.setBackground(Color.WHITE);
        //imagePreviwLabel.setSize(100,200);
        //adding buttons on panel
        previewLayeredPanel.add(selector, 0);
        previewLayeredPanel.add(imagePreviwLabel, 1);

        //Carga el contenido en el panel
        panelPreview.add(previewLayeredPanel);
        panelPreview.validate();
        //previewLayeredPanel.setBounds(0,0,previewLayeredPanel.getWidth(),previewLayeredPanel.getHeight());
    }

    private void updatePreviewPanelSize() {

        imagePreviwLabel.setBounds(0, 0, panelPreview.getWidth(), panelPreview.getHeight());
        selector.setBounds(0, 0, panelPreview.getWidth(), panelPreview.getHeight());
        panelPreview.validate();


    }

    /////////////////FINAL PREVIEW//////////////////

    private void updateSelectedArea() {

        //Correcion de valores (permite hacer seleccione ne cualquier direccion)
        System.out.println(">>>>>>> " + initialSelectionPoint + " >>>> "+finalSelectionPoint);
        if(initialSelectionPoint.getX() < finalSelectionPoint.getX()){
            //Se deja igual
        }if(initialSelectionPoint.getX() > finalSelectionPoint.getX()){
            //Se intercambian
            double auxInitial = initialSelectionPoint.getX();
            initialSelectionPoint.setLocation(finalSelectionPoint.getX(),initialSelectionPoint.getY());
            finalSelectionPoint.setLocation(auxInitial,finalSelectionPoint.getY());
        }

        if(initialSelectionPoint.getY() < finalSelectionPoint.getY()){
//Se deja igual
        }if(initialSelectionPoint.getY() > finalSelectionPoint.getY()){
//Se intercambian
            double auxInitial = initialSelectionPoint.getY();
            initialSelectionPoint.setLocation(initialSelectionPoint.getX(),finalSelectionPoint.getY());
            finalSelectionPoint.setLocation(finalSelectionPoint.getX(),auxInitial);
        }
        System.out.println("++++++++ " + initialSelectionPoint + " >>>> "+finalSelectionPoint);
        //private Point initialSelectionPoint;
        //private Point finalSelectionPoint;
        //System.out.println(">>>>>>>" + (int) initialSelectionPoint.getX());
        selector.setBounds((int) initialSelectionPoint.getX(),
                (int) initialSelectionPoint.getY(),
                (int) (finalSelectionPoint.getX() - initialSelectionPoint.getX()),
                (int) (finalSelectionPoint.getY() - initialSelectionPoint.getY()));
        //Mostrar panel de informacion
        //selectedAreaBox.setText(initialSelectionPoint.toString() + " " + finalSelectionPoint.toString());
        panelPreview.validate();
        float imagePreviewWith=(float)imagePreviwLabel.getIcon().getIconWidth();
        float imagePreviewHeight=(float)imagePreviwLabel.getIcon().getIconHeight();

        //Calculo offset de la imagen respecto al cuadro de previsualizacion previewLayeredPanel
        int offsetHeight = (int) (((float)previewLayeredPanel.getHeight() - imagePreviewHeight) / 2.0f);
        int offsetWith = (int) (((float)previewLayeredPanel.getWidth() - imagePreviewWith) / 2.0f);

        float sectorProprotionInitialX=((float)initialSelectionPoint.getX()/imagePreviewWith)*100.0f;
        float sectorProprotionInitialY=(((float)initialSelectionPoint.getY()-offsetHeight)/imagePreviewHeight)*100.0f;
        float sectorProprotionFinalX=((float)finalSelectionPoint.getX()/imagePreviewWith)*100.0f;
        float sectorProprotionFinalY=(((float)finalSelectionPoint.getY()-offsetHeight)/imagePreviewHeight)*100.0f;
        //selectedAreaBox.setText(sectorProprotionInitialX+"-"+sectorProprotionInitialY+" /-/ "+sectorProprotionFinalX+"-"+sectorProprotionFinalY);




        //System.out.println("PD:"+PanelDerecho.getHeight()+"  /  IPL"+imagePreviwLabel.getHeight()+"  ////   OffSet: "+offsetHeight);
        //Se almacena el valor normalizado en un puntos (si fuese inpreciso se recomienda crear una clase que extienda puento y permmita guardar floats en vez de enteros)
        initialSelectionPointNormalized = new normalizedPoint((float)sectorProprotionInitialX,(float)sectorProprotionInitialY);
        finalSelectionPointNormalized = new normalizedPoint((float)sectorProprotionFinalX,(float)sectorProprotionFinalY);

        selectedAreaBox.setText(initialSelectionPointNormalized.toString() + " " + finalSelectionPointNormalized.toString());
    }


    private void updateViewerSize() {

        if (currentViewerImage != null) {
            updatePreviewPanelSize();
            //valores de la ventana
            int currentWindowWith = root.getWidth();
            int currentWindowHeight = root.getHeight();
            //valor actual de la imagen
            currentViewerImage.getWidth(null);
            currentViewerImage.getHeight(null);


            int availableWith = panelPreview.getWidth();
            int availableHeight = root.getHeight() - windowsSizeTexBox.getHeight() - panelHUD.getHeight() - 10;


            float proporcionW = (float) ((float) availableWith / (float) currentViewerImage.getWidth(null));
            float proporcionH = (float) ((float) availableHeight / (float) currentViewerImage.getHeight(null));
            System.out.println("La nueva proporcion es: " + proporcionW + "X - " + proporcionH + "Y" + "****" + availableHeight);
            //Actualzia el valor de la imagen
            ImageIcon scaledViewerImage = new ImageIcon(currentViewerImage);
            if (proporcionW < proporcionH) {

                updateViewerImage(new ImageIcon(getScaledImage(scaledViewerImage.getImage(), proporcionW)));
            } else {
                updateViewerImage(new ImageIcon(getScaledImage(scaledViewerImage.getImage(), proporcionH)));
            }


            //Update selector Size
            if (initialSelectionPoint != null && finalSelectionPoint != null) {



                float imagePreviewWith=(float)imagePreviwLabel.getIcon().getIconWidth();
                float imagePreviewHeight=(float)imagePreviwLabel.getIcon().getIconHeight();

                //Calculo offset de la imagen respecto al cuadro de previsualizacion
                int offsetHeight = (int) (((float)previewLayeredPanel.getHeight() - imagePreviewHeight) / 2.0f);
                int offsetWith = (int) (((float)previewLayeredPanel.getWidth() - imagePreviewWith) / 2.0f);

                initialSelectionPoint.setLocation(imagePreviewWith*((float)initialSelectionPointNormalized.getX())/100.0f,
                        (imagePreviewHeight*((float)initialSelectionPointNormalized.getY())/100.0f)+offsetHeight);
                finalSelectionPoint.setLocation(imagePreviewWith*((float)finalSelectionPointNormalized.getX())/100.0f,
                        (imagePreviewHeight*((float)finalSelectionPointNormalized.getY())/100.0f)+offsetHeight);


                updateSelectedArea();
            }


        } else {
            //No se ha cargado la imagen por lo que se creara la vista
            System.out.println("Se incia el panel de administracion");
            initPreviewPanel();

        }
    }

    private void normalizeWindowsSize() {

        float proporcionHorizontal = 0.5f;
        float proporcionVertical = 0.5f;
        int windowWith;
        int windowHeight;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        windowWith = (int) ((float) screenSize.getWidth() * proporcionHorizontal);
        windowHeight = (int) ((float) screenSize.getHeight() * proporcionVertical);

        setSize(windowWith, windowHeight);
    }


    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    private Image getScaledImage(Image srcImg, float scalingFactor) {

        int w = srcImg.getWidth(null);
        int h = srcImg.getHeight(null);
        h *= scalingFactor;
        w *= scalingFactor;

        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }


    public void setViewerImage(Image newImage) {
        synchronized (currentViewerImage) {

            currentViewerImage = newImage;
        }

    }

    public void setDefaultValues() {

        outputFolderScanPath = Path.of(System.getProperty("user.home"), "Escritorio");

        texBoxFolderPath.setText(outputFolderScanPath.toString());
        texBoxFileName.setText(defaultFileName);

        leftClickPosition = false;

        //Cargar imagenes de test
        //imagen2= new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\vlcsnap-2018-06-29-16h44m46s273.png");
//        imagen1= new ImageIcon("/home/anto/IdeaProjects/printer-GUI/src/main/resources/Sin título-1.jpg");
        defaultImage = new ImageIcon("/home/anto/IdeaProjects/printer-GUI/src/main/resources/hpscan001.png");
//
//        System.out.println("LoadImage>>>>>>>>>>>>>>>>>>>");
        currentViewerImage = defaultImage.getImage();

        //Inicia panel de previsualizacion
        initPreviewPanel();


        // updateViewerSize();
        //imagen3.setImage(getScaledImage(imagen3.getImage(),0.10f));

        //imagenPreviwLabel.setIcon(imagen3);


    }

    class selectedArea extends JComponent {
        String s = "message";
        int x = 45;
        int y = 45;

        public void paint(Graphics g) {
            g.drawRect(10, 10, 200, 200);
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

    public void runScan(Path filePath, String fileName, int dpiResolution, String colorSpace, int tlx, int tly, int brx, int bry) {


        //Command construction
        String destinationPath = Path.of(filePath.toString(), fileName).toString();
        String scanBashCommand="";
        if(tlx<=-1){//Escaneo de plancha completa
            scanBashCommand = "hp-scan " + "--mode=" + colorSpace + " --resolution=" + dpiResolution + " -f " + destinationPath;
        }else{ //Escaneo de seccion
            scanBashCommand = "hp-scan " + "--mode=" + colorSpace + " --resolution=" + dpiResolution + " -f " + destinationPath+" --tlx="+tlx+" --tly="+tly+" --brx="+brx+" --bry="+bry;
        }


        System.out.println(Thread.currentThread().getName());


        String result = null;

        try {
            progressBarScan.setValue(0);
            System.out.println("Se va a lanzar el comando: " + scanBashCommand);
            Process process = Runtime.getRuntime().exec(scanBashCommand);

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputLine;


            char progressCharArray[] = new char[5];
            //int starCount = 0;
            int theCharNum = in.read();
            while (theCharNum != -1) {
                char theChar = (char) theCharNum;
                //System.out.print(theChar);
                //System.out.print(theChar);
                if (Character.compare(theChar, (char) ']') == 0) {
                    in.read(progressCharArray, 0, 5);
                    //progressBarScan.setValue(50);
                    String progresString = new String(progressCharArray);
                    progresString = progresString.replace(" ", "");
                    progresString = progresString.replace("%", "");
                    //System.out.print(progresString+"-");
                    progressBarScan.setValue(Integer.parseInt(progresString));
                }
                theCharNum = in.read();

            }
            //System.out.println("Nuemero de estrellas contadas: " + starCount);


        } catch (IOException e) {
            e.printStackTrace();
        }


        //procesoBashScan.run();
        System.out.println("Se ha terminado de escanear");
    }

    public void runScan(Path filePath, String fileName, int dpiResolution, String colorSpace) {

        runScan(filePath,fileName,dpiResolution,colorSpace,-1,-1,-1,-1);

    }



}


