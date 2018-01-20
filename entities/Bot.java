package entities;

import gui.Screen;
import java.awt.Color;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends Entity{
    public boolean move = true;
    public Bot(Screen screen){
        super(screen);
    }

    public Bot(Screen screen, int x, int y, int width, int height) {
        super(screen, x, y, width, height);
    }
    public Bot(Screen screen, int x, int y, int width, int height,
            Color color, int speed, int sightD, int sightP ){
        super(screen,x,y,width,height,color,speed,sightD,sightP);
    }
    
    
    public void spotDecision(){
        if(this.spotted){
            this.spotted = false;
            
            Iterator<Entity> entityIter = spottedents.iterator();
        
            while(entityIter.hasNext()){
                Entity entity = entityIter.next();
                
                
                if(this.equals(entity)){
                    continue;
                }
                
                if(this.getSightLine().intersects(entity)){
                    //approach and mate
                }
            }
        }
    }
    public void smartMove(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                int maxsteps = 100, minsteps = 1;
                while(move){
                    int randsteps = new Random().nextInt(maxsteps-minsteps)+minsteps;

                    if(new Random().nextBoolean()){
                        //x
                        if(new Random().nextBoolean()){
                            //+
                            stepX(1, randsteps);
                        }else{
                            //-
                            stepX(-1, randsteps);
                        }
                    }else{
                        //y
                        if(new Random().nextBoolean()){
                            //+
                            stepY(1, randsteps);
                        }else{
                            //-\
                            stepY(-1, randsteps);
                        }
                    }
                }
            }
        }).start();
        
    }
    
    @Override
    protected void entityCollision(){
        //spotDecision();
        
        Iterator<Entity> entityIter = Screen.entities.iterator();
        
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(this.equals(entity)){
                continue;
            }
            if(Screen.deadEntities.contains(entity)){
                continue;
            }
            
            if(this.intersects(entity)){
                setCollided(true);
                
                //smartMove();
                if(n){
                    //n = false;
                    //s = true;
                    //System.out.println("Upda");
                }
                if(s){
                    //s = false;
                    //n = true;
                    //System.out.println("Upda");
                }
                if(e){
                    //e = false;
                    //w = true;
                    //System.out.println("Upda");
                }
                if(w){
                    //w = false;
                    //e = true;
                    //System.out.println("Upda");
                }
                //setCollided(false);
                //this.breedEntities(entity);
            }
            
        }
    }
}