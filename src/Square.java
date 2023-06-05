import java.awt.*;


public class Square extends Figure{
    private int left;
    private int right;
    private int top;
    private int bottom;

    public Square(int x, int y, Color color, int size){
        super(x,y,color,size);
        id = 2;
        calculateBounds();
    }
    public String toString(){
        return "2 "+ getX() + " " + getY() + " " + color.getRed()
                + " " + color.getGreen()+ " " + color.getBlue() + " " + getSize() + "\n";
    }
    private void calculateBounds() {
        left = getX();
        right = getX() + getSize();
        top = getY();
        bottom = getY() + getSize();
    }

    public boolean contains(int x, int y) {
        return x >= left && x <= right && y >= top && y <= bottom;
    }


}
