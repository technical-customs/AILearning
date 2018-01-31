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
    private volatile boolean collided, fired;
    
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
    
    public synchronized void stepX(int dir, int steps){
        try{
            double xPos = this.x;
            if(dir != 0){
                if(dir > 0){
                    double xReach = xPos+steps;
                    while(this.getX() <= xReach){
                        if(collided){
                            collided = false;
                            return;
                        }
                        this.x += 1* speed;
                        Thread.sleep(17);
                    }
                    collided = true;
                    //System.out.println("COLLIDED");
                }else if(dir < 0){
                    double xReach = xPos-steps;
                    while(this.getX() >= xReach){
                        if(collided){
                            collided = false;
                            return;
                        }
                        
                        this.x -= 1* speed;
                        Thread.sleep(17);
                    }
                    collided = true;
                    //System.out.println("COLLIDED");
                }
            }
        }catch(Exception ex){
            System.out.println("StepX ex: " + ex);
        }
        
        
    }
    public synchronized void stepY(int dir, int steps){
        try{
            double yPos = this.getY();
            if(dir != 0){
                if(dir > 0){
                    double yReach = yPos+steps;
                    while(this.getY() <= yReach){
                        if(collided){
                            collided = false;
                            return;
                        }
                        this.y += 1* speed;
                        Thread.sleep(17);
                        
                    }
                    collided = true;
                    //System.out.println("COLLIDED");
                }else if(dir < 0){
                    double yReach = yPos-steps;
                    while(this.getY() >= yReach){
                        if(collided){
                            collided = false;
                            return;
                        }
                        this.y -= 1* speed;
                        Thread.sleep(17);
                        
                        
                    }
                    collided = true;
                    //System.out.println("COLLIDED");
                }
            }
            
            
        }catch(Exception ex){
            System.out.println("StepY ex: " + ex);
        }
    }
    public void setXDir(int dir){
        if(dir > 0){
            xDir = 1;
        }
        if(dir < 0){
            xDir = -1;
        }if(dir == 0){
            xDir = 0;
       
        }
    }
    public void setYDir(int dir){
        if(dir > 0){
            yDir = 1;
        }
        if(dir < 0){
            yDir = -1;
        }if(dir == 0){
            yDir = 0;
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
                collided = true;
            }
            if(this.x >= Screen.SSIZE.width + this.width){
                collided = true;
            }
            if(this.y <= 0 - this.height){
                collided = true;
            }
            if(this.x >= Screen.SSIZE.height + this.height){
                collided = true;
            }
        }
    }
    public void setCollided(boolean collided){
        this.collided = collided;
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
        //if(!collided){
            g.setColor(Color.red);
            g.fillRect(x, y, width, height);
        
        
    }
}