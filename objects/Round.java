package objects;

import entities.Entity;
import gui.Screen;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Round extends Rectangle{
    private int damage;
    private int speed = 1;
    private int xDir, yDir;
    private boolean collided, fired;
    
    public Round(int x, int y, int width, int height){
        super(x,y,width,height);
    }
    
    public void setDamage(int damage){
        this.damage = damage;
    }
    public int getDamage(){
        return damage;
    }
    public void setSpeed(int speed){
        this.speed = speed;
    }
    
    public void stepX(int dir, int steps){
        try{
            double xPos = this.x;
            if(dir != 0){
                if(dir > 0){
                    while(this.x < xPos+steps){
                        if(collided){
                            collided = false;
                            return;
                        }
                        this.x += 1* speed;
                        Thread.sleep(30);
                        
                        
                        
                    }
                    //this.xDir = 1;
                }
                if(dir < 0){
                    while(this.x > xPos-steps){
                        if(collided){
                            collided = false;
                            return;
                        }
                        
                        this.x -= 1* speed;
                        Thread.sleep(30);
                        
                        
                    }
                }
            }
        }catch(InterruptedException ex){
            System.out.println("StepX ex: " + ex);
        }
        
        
    }
    public void stepY(int dir, int steps){
        try{
            double yPos = this.y;
            if(dir != 0){
                if(dir > 0){
                    while(this.y < yPos+steps){
                        if(collided){
                            collided = false;
                            return;
                        }
                        this.y += 1* speed;
                        Thread.sleep(30);
                        
                    }
                }
                if(dir < 0){
                    while(this.y > yPos-steps){
                        if(collided){
                            collided = false;
                            return;
                        }
                        this.y -= 1* speed;
                        Thread.sleep(30);
                        
                        
                    }
                }
            }
            
            
        }catch(InterruptedException ex){
            System.out.println("StepY ex: " + ex);
        }
    }
    
    
    public void collision(){
        if(fired){
            for(Entity entity: Screen.entities){
                if(this.intersects(entity)){
                    entity.health-=damage;
                    collided = true;
                }
            }
            for(Boundary bound: Screen.boundaries){
                if(this.intersects(bound)){
                    collided = true;
                }
            }
            
            if(this.x <= 0 - this.width){
                //collided = true;
            }
            if(this.x >= Screen.SSIZE.width + this.width){
                //collided = true;
            }
            if(this.y <= 0 - this.height){
                //collided = true;
            }
            if(this.x >= Screen.SSIZE.height + this.height){
                //collided = true;
            }
        }
    }
    public boolean getCollided(){
        return collided;
    }
    public boolean getFired(){
        return fired;
    }
    public void setFired(boolean fired){
        this.fired = fired;
    }
    public void draw(Graphics2D g){
        g.setColor(Color.red);
        g.fillRect(x, y, width, height);
    }
}