import java.awt.*;

public abstract class Figure {
    protected int x;
    protected int y;
    protected Color color;
    protected int size;
    protected int id;

    public Figure(int x, int y, Color color, int size){
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
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

    public int getSize(){
        return size;
    }

    public abstract boolean contains(int x, int y);

    public int getId() {
        return id;
    }
    public abstract String toString();
}
