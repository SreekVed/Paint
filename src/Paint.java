import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Paint extends JPanel implements MouseMotionListener, ActionListener, MouseListener, ChangeListener, MenuListener {

    ArrayList<ArrayList<Point>> lines;
    ArrayList<Point> points;
    ArrayList<Shape> shapes;
    JColorChooser chooser;
    JColorChooser bgChooser;
    JFileChooser fileChooser;
    JFrame frame;
    JMenuBar bar;
    JMenu menu, file, bgColor;
    JButton line, rectangle, clear;
    JScrollBar penWidth;
    JToggleButton eraser;
    JMenuItem[] colorOptions;
    JMenuItem save, load;
    Color[] colors;
    Color currentColor;
    Color currentBG;
    Color lastColor;
    boolean lineMode = true, rectangleMode = false, first = true, bg = false;
    int x = 0, y = 0, w = 0, h = 0;
    Shape currentShape;
    Image loadedImage;

    public Paint() {
        lines = new ArrayList<>();
        points = new ArrayList<>();
        shapes = new ArrayList<>();
        frame = new JFrame("Sreekar's Amazing Paint Program");
        frame.add(this);
        frame.setBackground(Color.BLACK);
        bar = new JMenuBar();
        file = new JMenu("File");
        menu = new JMenu("Colors");
        bgColor = new JMenu("BG Color");
        line = new JButton("Line");
        clear = new JButton("CLEAR");
        eraser = new JToggleButton("Eraser");
        rectangle = new JButton("Rectangle");
        save = new JMenuItem("Save");
        load = new JMenuItem("Load");
        file.add(save);
        file.add(load);
        penWidth = new JScrollBar(JScrollBar.HORIZONTAL, 1, 0, 1, 30);
        colorOptions = new JMenuItem[7];
        colors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.WHITE};
        currentColor = colors[0];
        currentBG = Color.BLACK;
        menu.setLayout(new GridLayout(1, 7));
        for (int x = 0; x < 7; x++) {
            colorOptions[x] = new JMenuItem();
            colorOptions[x].setPreferredSize(new Dimension(60, 30));
            colorOptions[x].addActionListener(this);
            colorOptions[x].putClientProperty("colorIndex", x);
            colorOptions[x].setBackground(colors[x]);
            menu.add(colorOptions[x]);
        }
        currentShape = new Block(0, 0, 0, 0, 0, currentBG);
        chooser = new JColorChooser();
        chooser.getSelectionModel().addChangeListener(this);
        fileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        bgChooser = new JColorChooser();
        bgChooser.getSelectionModel().addChangeListener(this);
        menu.add(chooser);
        bgColor.add(bgChooser);
        bar.add(file);
        bar.add(menu);
        bar.add(bgColor);
        bar.add(clear);
        bar.add(line);
        bar.add(rectangle);
        bar.add(eraser);
        bar.add(penWidth);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        bgColor.addMenuListener(this);
        save.addActionListener(this);
        load.addActionListener(this);
        rectangle.addActionListener(this);
        line.addActionListener(this);
        clear.addActionListener(this);
        eraser.addActionListener(this);
        rectangle.setFocusPainted(false);
        line.setFocusPainted(false);
        clear.setFocusPainted(false);
        eraser.setFocusPainted(false);
        frame.add(bar, BorderLayout.NORTH);
        frame.setSize(1280, 720);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void main(String[] args) {
        new Paint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(currentBG);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        if (loadedImage != null) g.drawImage(loadedImage, 0, 0, null);
        for (ArrayList<Point> line : lines) {
            for (int x = 0; x < line.size() - 1; x++) {
                Point p1 = line.get(x);
                Point p2 = line.get(x + 1);
                g.setColor(p1.getColor());
                g2.setStroke(new BasicStroke(p1.getPenWidth()));
                g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
        }

        for (Shape s : shapes) {
            g.setColor(s.getColor());
            g2.setStroke(new BasicStroke(s.getPenWidth()));
            if (s instanceof Block) g2.draw(((Block) s).getRect());
        }
        if (lineMode) {
            for (int x = 0; x < points.size() - 1; x++) {
                Point p1 = points.get(x);
                Point p2 = points.get(x + 1);
                g.setColor(p1.getColor());
                g2.setStroke(new BasicStroke(p1.getPenWidth()));
                g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
        }

        if (rectangleMode) {
            g.setColor(currentColor);
            g2.setStroke(new BasicStroke(currentShape.getPenWidth()));
            g2.draw(((Block) currentShape).getRect());
        }

    }

    public BufferedImage createImage() {
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        this.paint(g2);
        g2.dispose();
        return image;
    }

    public void clear() {
        lines.clear();
        points.clear();
        shapes.clear();
        currentShape = new Block(0, 0, 0, 0, 0, currentBG);
        first = true;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (lineMode) {
            lines.add(points);
            points = new ArrayList<>();
        }
        if (rectangleMode) {
            first = true;
            shapes.add(currentShape);
        }
        currentShape = new Block(0, 0, 0, 0, 0, currentBG);
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == eraser) {
            if (eraser.isSelected()) {
                lineMode = true;
                rectangleMode = false;
                lastColor = currentColor;
                currentColor = currentBG;
            }
            if (!eraser.isSelected()) currentColor = lastColor;
        }
        if (!eraser.isSelected()) {
            for (JMenuItem colorOption : colorOptions)
                if (e.getSource() == colorOption) {
                    int index = Integer.parseInt("" + ((JMenuItem) e.getSource()).getClientProperty("colorIndex"));
                    currentColor = colors[index];
                }
            if (e.getSource() == line) {
                lineMode = true;
                rectangleMode = false;
            }
            if (e.getSource() == rectangle) {
                lineMode = false;
                rectangleMode = true;
            }
        }
        if (e.getSource() == clear) {
            clear();
            loadedImage = null;
            eraser.setSelected(false);
            if (lastColor != null) currentColor = lastColor;
        }
        if (e.getSource() == save) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("*.png", "png"));
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    ImageIO.write(createImage(), "png", new File(file.getAbsolutePath() + ".png"));
                } catch (IOException ignored) {
                }
            }
        }
        if (e.getSource() == load) {
            fileChooser.showOpenDialog(null);
            try {
                loadedImage = ImageIO.read(fileChooser.getSelectedFile());
            } catch (IOException ignored) {
            }
            clear();
        }
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lineMode) points.add(new Point(e.getX(), e.getY(), currentColor, penWidth.getValue()));
        if (rectangleMode) {
            if (first) {
                x = e.getX();
                y = e.getY();
                currentShape = new Block(x, y, 0, 0, penWidth.getValue(), currentColor);
                first = false;
            } else {
                w = Math.abs(e.getX() - x);
                h = Math.abs(e.getY() - y);
                currentShape.setWidth(w);
                currentShape.setHeight(h);
            }
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void stateChanged(ChangeEvent c) {

        if (bg) {
            currentBG = bgChooser.getColor();
            if (eraser.isSelected()) currentColor = currentBG;
        }
        if (!eraser.isSelected() && !bg) currentColor = chooser.getColor();
        frame.revalidate();
        frame.repaint();

    }

    @Override
    public void menuSelected(MenuEvent menuEvent) {
        bg = true;
    }

    @Override
    public void menuDeselected(MenuEvent menuEvent) {
        bg = false;
    }

    @Override
    public void menuCanceled(MenuEvent menuEvent) {
        bg = false;
    }

    public static class Point {
        private final int x;
        private final int y;
        private final int penWidth;
        Color color;

        public Point(int x, int y, Color color, int penWidth) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.penWidth = penWidth;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Color getColor() {
            return color;
        }

        public int getPenWidth() {
            return penWidth;
        }
    }

    public static class Shape {
        private final int x;
        private final int y;
        private final int penWidth;
        private final Color color;
        private int width;
        private int height;

        public Shape(int x, int y, int width, int height, int penWidth, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.penWidth = penWidth;
            this.color = color;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getPenWidth() {
            return penWidth;
        }

        public Color getColor() {
            return color;
        }

    }

    public static class Block extends Shape {

        public Block(int x, int y, int width, int height, int penWidth, Color color) {
            super(x, y, width, height, penWidth, color);
        }

        public Rectangle getRect() {
            return new Rectangle(getX(), getY(), getWidth(), getHeight());
        }

    }
}
