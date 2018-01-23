package entities;

import gui.Screen;
import java.util.Iterator;

public class Player extends Entity{
    
    public Player(Screen screen, int x, int y, int width, int height) {
        super(screen, x, y, width, height);
    }

    @Override
    protected void entityCollision() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}