import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;


public class MyFrame extends JFrame{
    private JPanel drawingArea;
    private JLabel statusLabel;
    private JLabel statusLabel2;
    private DrawingMode selectedShape;
    private String currentShape;
    private boolean isModified;
    private LinkedList<Figure> figures;
    private JFileChooser fileChooser;
    private String currentFileName;
    private File currentFile;
    private FileWriter fileWriter;
    private List<Point> points;
    private boolean isDrawing;
    private boolean doesContain;
    private Color penColor;

    private enum DrawingMode {
        CIRCLE,
        SQUARE,
        PEN
    }

    public MyFrame(){
        currentShape = "";
        statusLabel = new JLabel();
        statusLabel2 = new JLabel();
        statusLabel2.setText("New");
        setTitle("Simple Draw");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(420,420);

        JFrame frame = new JFrame();

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu drawMenu = new JMenu("Draw");

        JMenu figureMenu = new JMenu("Figure");
        JRadioButton circleButton = new JRadioButton("Circle");
        circleButton.setMnemonic(KeyEvent.VK_C);
        JRadioButton squareButton = new JRadioButton("Square");
        JRadioButton penButton = new JRadioButton("Pen");

        ButtonGroup shapeButtonGroup = new ButtonGroup();
        shapeButtonGroup.add(circleButton);
        shapeButtonGroup.add(squareButton);
        shapeButtonGroup.add(penButton);

        figureMenu.add(circleButton);
        figureMenu.add(squareButton);
        figureMenu.add(penButton);

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.addActionListener(e-> openFile());

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.addActionListener(e->saveFile());

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsMenuItem.addActionListener(e->saveAsFile());

        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        quitMenuItem.addActionListener(e->quit());

        JMenuItem colorMenuItem = new JMenuItem("Color");
        colorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        colorMenuItem.addActionListener(e->chooseColor());

        JMenuItem clearMenuItem = new JMenuItem("Clear");
        clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        clearMenuItem.addActionListener(e->clear());

        fileChooser = new JFileChooser();



        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(drawMenu);

        drawMenu.add(figureMenu);
        drawMenu.add(colorMenuItem);
        drawMenu.addSeparator();
        drawMenu.add(clearMenuItem);

        setJMenuBar(menuBar);

        figures = new LinkedList<>();

        drawingArea = new JPanel();
        drawingArea.setBackground(Color.blue);

        points = new ArrayList<>();

        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                Point point = e.getPoint();
                int x = point.x;
                int y = point.y;
                if (e.isAltDown()) {
                    if (squareButton.isSelected()) {
                        Color color = getRandomColor();
                        Square square = drawSquare(x, y, color);
                    } else if (circleButton.isSelected()) {
                        Color color = getRandomColor();
                        Circle circle = drawCircle(x, y, color);
                    }
                }
                if(penButton.isSelected()){
                    isDrawing = true;
                    points.clear();
                }
                if (e.isMetaDown()) {
                    doesContain  = false;
                    FigureIterator iterator = new FigureIterator(figures);
                    while (iterator.hasNext()) {
                        Figure figure = iterator.next();
                        if (figure.contains(x, y)) {
                            doesContain = true;
                            iterator.remove(); // Use iterator's remove() method to remove the element
                        }
                    }
                    if(doesContain) myRepaint(figures);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedShape == DrawingMode.PEN && isDrawing) {
                    isDrawing = false;
                    drawPen();
                }
            }
        });
        drawingArea.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape == DrawingMode.PEN && isDrawing) {
                    points.add(e.getPoint());
                    drawPen();
                }
            }
        });


        squareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedShape = DrawingMode.SQUARE;
                currentShape = "Square";
                statusLabel.setText(currentShape);

            }
        });
        circleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedShape = DrawingMode.CIRCLE;
                currentShape = "Circle";
                statusLabel.setText(currentShape);
            }
        });
        penButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedShape = DrawingMode.PEN;
                currentShape = "Pen";
                statusLabel.setText(currentShape);
            }
        });

        drawingArea.setFocusable(true);
        drawingArea.requestFocusInWindow();

        add(drawingArea, BorderLayout.CENTER);

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(statusLabel);
        panel1.setBackground(Color.white);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel2.add(statusLabel2);
        panel2.setBackground(Color.white);

        JPanel statusPanelContainer = new JPanel(new BorderLayout());
        statusPanelContainer.add(panel1, BorderLayout.WEST);
        statusPanelContainer.add(panel2, BorderLayout.EAST);

        getContentPane().add(statusPanelContainer, BorderLayout.SOUTH);

    }
    private void openFile(){
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("TXT Files", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION){
            isModified = false;
            File selectedFile = fileChooser.getSelectedFile();
            currentFile = selectedFile;
            currentFileName = currentFile.getName();
            setTitle("Simple Draw: " + currentFileName);
            try{
                figures.clear();
                Scanner scanner = new Scanner(selectedFile);

                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    String[] numbers = line.split(" ");

                    int shape = Integer.parseInt(numbers[0]);
                    int x = Integer.parseInt(numbers[1]);
                    int y = Integer.parseInt(numbers[2]);
                    int r = Integer.parseInt(numbers[3]);
                    int g = Integer.parseInt(numbers[4]);
                    int b = Integer.parseInt(numbers[5]);
                    int size = Integer.parseInt(numbers[6]);
                    Color color = new Color(r,g,b);
                    if(shape == 1){
                        Circle circle = drawCircle(x,y,color);
                    }
                    else if(shape == 2){
                        Square square = drawSquare(x,y,color);
                    }
                }
                scanner.close();
            }catch (IOException i){
                i.printStackTrace();
            }
        }
    }
    private void saveFile(){
        try{
            if(currentFile == null){
                saveAsFile();
            }
            else {
                fileWriter = new FileWriter(currentFile);
                fileWriter.write("");
                System.out.println("Wypisuje aktualne figury");
                for(Figure figure: figures){
                    System.out.println(figure);
                }
                for(Figure figure: figures){
                    String figureString = figure.toString();
                    fileWriter.write(figureString);
                }
                statusLabel2.setText("Saved");
                isModified = false;
                fileWriter.flush();
                fileWriter.close();
            }
        }catch (IOException i){
            System.out.println(i.getMessage());
        }
    }
    private void saveAsFile() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            currentFile = selectedFile;
            currentFileName = currentFile.getName();
            try {
                fileWriter = new FileWriter(selectedFile);
                fileWriter.write("");
                for (Figure figure : figures) {
                    String figureString = figure.toString();
                    fileWriter.write(figureString);
                }
                fileWriter.flush();
                statusLabel2.setText("Saved");
            } catch (IOException i) {
                System.out.println(i.getMessage());
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void quit(){
        if(isModified){
            int choice = JOptionPane.showConfirmDialog(this, "Do you want to save changes?");
            if(choice == JOptionPane.YES_OPTION){
                saveFile();
            }
        }
        if(currentFile != null) setTitle("Simple Draw");
        System.exit(0);
    }
    private void chooseColor(){
        JColorChooser colorChooser = new JColorChooser();
        penColor = JColorChooser.showDialog(null, "choose a pen color", Color.pink);
    }
    private void clear(){
        drawingArea.updateUI();
        figures.clear();
        if(currentFile != null){
            clearFile(currentFile);
        }
    }
    private Circle drawCircle(int x, int y, Color color){
        System.out.println("x:" + x + " y:" + y + "color: " +color.getRed() + " " + color.getGreen() + " " + color.getBlue());
        Graphics g = drawingArea.getGraphics();
        g.setColor(color);
        Circle circle = new Circle(x, y, color, 50);
        g.fillOval(x, y, 50, 50);
        figures.add(circle);
        statusLabel2.setText("Modified");
        isModified = true;
        return circle;
    }
    private Square drawSquare(int x, int y, Color color){
        Graphics g = drawingArea.getGraphics();
        g.setColor(color);
        Square square = new Square(x,y,color,50);
        g.fillRect(x, y, 50, 50);
        figures.add(square);
        statusLabel2.setText("Modified");
        isModified = true;
        return square;
    }

    private Color getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return new Color(r, g, b);
    }
    public static void clearFile(File file) {
        if (file.exists()) {
            try {
                if (file.delete()) {
                    file.createNewFile();
                } else {
                    System.out.println("Unable to clear the file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File does not exist.");
        }
    }
    public void myRepaint(List<Figure> f){
        for(Figure figure : f){
            System.out.println(figure);
        }
        List<Figure> figuresCopy = new ArrayList<>(f);

        Graphics g = drawingArea.getGraphics();
        g.setColor(drawingArea.getBackground());
        g.fillRect(0, 0, drawingArea.getWidth(), drawingArea.getHeight());

        figures.clear();
        for (Figure figure : figuresCopy) {
            Color color = new Color(figure.color.getRed(), figure.color.getGreen(), figure.color.getBlue());
            if (figure.getId() == 1) {
                Circle circle = drawCircle(figure.getX(), figure.getY(), color);
            } else if (figure.getId() == 2) {
                Square square = drawSquare(figure.getX(), figure.getY(), color);
            }
        }
        statusLabel2.setText("Modified");
        isModified = true;
        g.dispose();
    }
    private void drawPen() {
        Graphics g = drawingArea.getGraphics();
        g.setColor(penColor);
        for (Point point : points) {
            g.fillOval(point.x, point.y, 4, 4);
        }
        statusLabel2.setText("Modified");
        g.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MyFrame myFrame = new MyFrame();
                myFrame.setVisible(true);
            }
        });
    }
}
