package objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Boundary extends Rectangle{
    private Color color = Color.gray;
    
    public Boundary(int x, int y, int w, int h){
        super(x,y,w,h);
    }
    
    public Color getColor(){
        return color;
    }
    
    public void draw(Graphics2D g){
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}