import java.awt.*;

public class Circle extends Figure{

    private int centerX;
    private int centerY;
    private int radius;
    public Circle(int x, int y, Color color, int size){
        super(x,y,color,size);
        id = 1;
        calculateBounds();
    }

    public String toString(){
        return "1 "+ getX() + " " + getY() + " " + color.getRed() + " " + color.getGreen() +
                " " + color.getBlue() + " " + getSize() + "\n";
    }
    private void calculateBounds() {
        centerX = getX() + getSize() / 2;
        centerY = getY() + getSize() / 2;
        radius = getSize() / 2;
    }
    public boolean contains(int x, int y) {
        int distanceSquared = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY);
        return distanceSquared <= radius * radius;
    }

}
