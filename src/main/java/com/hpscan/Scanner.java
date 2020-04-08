package com.hpscan;

import javax.swing.*;
import javax.swing.border.Border;
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
    ImageIcon previewImageIcon;

    //Current Preview
    int viewerWith;
    int viewerHeight;
    JLayeredPane previewLayeredPanel;
    private JLabel imagePreviwLabel;
    private Image currentViewerImage;
    private Point initialSelectionPoint;
    private Point finalSelectionPoint;
    private  Boolean leftClickPosition = false;
    JLabel selector;

    //Save scan
    private Path outputFolderScanPath;
    final String defaultFileName = "Imagen.png";
    private String fileName;




    public Scanner() {
        setTitle("Scanner");
        add(root);
        imagePreviwLabel = new JLabel();
        previewLayeredPanel = new JLayeredPane();
        panelPreview.setLayout(new BoxLayout(panelPreview,BoxLayout.Y_AXIS));
        //Crea el tamño de la ventana en funcion de el tamaño de la pantalla
        normalizeWindowsSize();
        setDefaultValues();


        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(panel1, "Hola: "+screenSize.getHeight()+" - "+screenSize.getWidth());
                //imagenPreviwLabel = new JLabel(new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\vlcsnap-2018-06-29-16h44m46s273.png"));
                //updateViewerImage(1);

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



        imagePreviwLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                initialSelectionPoint=e.getPoint();
                System.out.println("D: "+e.getPoint().toString());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                finalSelectionPoint=e.getPoint();
                System.out.println("U: "+e.getPoint().toString());
                updateSelectedArea();

            }
        });
        previwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // paintRectangle();
                //get File path

                String tmpFilename="scanner-"+ThreadLocalRandom.current().nextInt(1, 100)+".jpg";
                Path previewImagePath = Path.of(System.getProperty("java.io.tmpdir"));
                //Lock graphical interface

                //Scan
                runScan(previewImagePath,tmpFilename,75,"color");

                //Wait untilFinish

                //Remplace image
                System.out.println(previewImagePath.toString()+"/"+tmpFilename);
                previewImageIcon = new ImageIcon(Path.of(previewImagePath.toString(),tmpFilename).toString());
                System.out.println(">>>>>>"+previewImageIcon.getIconHeight());
                setViewerImage(previewImageIcon.getImage());
                //updatePreviewPanelSize();
                updateViewerSize();

                //Remove prevew Image
                File temporalFile = new File(Path.of(previewImagePath.toString(),tmpFilename).toString());
                temporalFile.delete();
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
        System.out.println("Resulucionde la imagen: "+newImage.getIconWidth()+"X - "+newImage.getIconHeight()+"Y" );

    }

    /////////////////INICIO PREVIEW//////////////
    private void initPreviewPanel(){



       // JLayeredPane previewLayeredPanel = new JLayeredPane();
        previewLayeredPanel.setBackground(Color.RED);
        //previewLayeredPanel.setBorder(BorderFactory.createTitledBorder("TEST-ELIMINAR AL TERMINAR"));
        previewLayeredPanel.setBackground(new Color(132,137,141));
        panelPreview.setBackground(new Color(132,137,141));
        //imagePreviwLabel = new JLabel();


        selector = new JLabel();
        selector.setBackground(Color.red);
        selector.setVisible(true);
        //selector.setText("dfsdsfdsfdsfdsfds");
        selector.setBounds(0, 0, 100, 100);
        selector.setSize(400,500);
        Border selectorBorder = BorderFactory.createLineBorder(Color.BLUE, 2);
        selector.setBorder(selectorBorder);

        imagePreviwLabel.setBounds(100,200,100,200);
        //adding buttons on panel
        previewLayeredPanel.add(selector, 0);
        previewLayeredPanel.add(imagePreviwLabel, 1);

        //Carga el contenido en el panel
        panelPreview.add(previewLayeredPanel);
        panelPreview.validate();
        //previewLayeredPanel.setBounds(0,0,previewLayeredPanel.getWidth(),previewLayeredPanel.getHeight());
    }
    private void updatePreviewPanelSize(){
        //selector.setBounds(0, 0, panelPreview.getWidth(), 100);
        imagePreviwLabel.setBounds(0,0,panelPreview.getWidth(),panelPreview.getHeight());
        selector.setBounds(0,0,panelPreview.getWidth(),panelPreview.getHeight());
        panelPreview.validate();
        //previewLayeredPanel.add(imagePreviwLabel, 0);
        //System.out.println(imagePreviwLabel.getIcon().getIconHeight());

    }

    /////////////////FINAL PREVIEW//////////////////

    private void updateSelectedArea(){

        //private Point initialSelectionPoint;
        //private Point finalSelectionPoint;
        System.out.println(">>>>>>>"+(int) initialSelectionPoint.getX());
        selector.setBounds((int) initialSelectionPoint.getX(), (int) initialSelectionPoint.getY(), (int) (finalSelectionPoint.getX()-initialSelectionPoint.getX()), (int) (finalSelectionPoint.getY()-initialSelectionPoint.getY()));

        panelPreview.validate();
    }

    private void updateViewerSize(){

        if(currentViewerImage!=null) {
            updatePreviewPanelSize();
            //valores de la ventana
            int currentWindowWith = root.getWidth();
            int currentWindowHeight = root.getHeight();
            //valor actual de la imagen
            currentViewerImage.getWidth(null);
            currentViewerImage.getHeight(null);

            //int availableWith = root.getWidth() - configurationPanel.getWidth();
            //int availableWith = previewLayeredPanel.getWidth();
            int availableWith = panelPreview.getWidth();
            //int availableHeight = root.getHeight() -
            //int availableWith = imagenPreviwLabel.getWidth();
            //int availableHeight = panelPreview.getHeight();
            //int availableHeight = previewLayeredPanel.getHeight();
            int availableHeight = root.getHeight()-windowsSizeTexBox.getHeight()-panelHUD.getHeight()-10;


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
            System.out.println("Se incia el panel de administracion");
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

        //Cargar imagenes de test
        //imagen2= new ImageIcon("C:\\Users\\angar\\IdeaProjects\\printer-GUI\\src\\main\\resources\\vlcsnap-2018-06-29-16h44m46s273.png");
        imagen1= new ImageIcon("/home/anto/IdeaProjects/printer-GUI/src/main/resources/Sin título-1.jpg");
        imagen3= new ImageIcon("/home/anto/IdeaProjects/printer-GUI/src/main/resources/hpscan001.png");

        System.out.println("LoadImage>>>>>>>>>>>>>>>>>>>");
        currentViewerImage=imagen3.getImage();

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

        String destinationPath=Path.of(filePath.toString(),fileName).toString();
        String scanBashCommand="hp-scan "+"--mode="+colorSpace+" --resolution="+dpiResolution+" -f "+destinationPath;
        String result=null;

        try {
            System.out.println("Se va a lanzar el comando: "+scanBashCommand);
            Process process = Runtime.getRuntime().exec(scanBashCommand);

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                result += inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ProcessBuilder builder = new ProcessBuilder();
        //builder.command(comandoDeEscaneo);
        //System.out.println(comandoDeEscaneo);
    }


}
