package objects;

import entities.Entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Blaster {
    private int strength = 5;
    public int range = 200;
    
    private int numRounds = 500;
    private List<Round> rounds = new ArrayList<>();
    private int xx, yy, length, width;
    private Entity entity;

    public Blaster(Entity entity){
        this.entity = entity;
        loadRounds();
    }
    
    private void loadRounds(){
        for(int x = 0; x < numRounds; x++){
            Round r = new Round(entity.x,entity.y,this.width/4,this.width/4);
            r.setSpeed(strength);
            r.setDamage(strength);
            rounds.add(r);
            
        }
    }
    public void shoot(){
        Iterator<Round> rIter = rounds.iterator();
        
        while(rIter.hasNext()){
            Round round = rIter.next();
            updateRoundPosition();

            round.setFired(true);
            if(entity.getN()){
                round.stepY(-1, range);
            }
            if(entity.getS()){
                round.stepY(1, range);
            }
            if(entity.getE()){
                round.stepX(1, range);
            }
            if(entity.getW()){
                round.stepX(-1, range);
            }
            //rIter.remove();
            return;
        }
        //empty
    }
    
    private void updateRoundPosition(){
        Iterator<Round> rIter = rounds.iterator();
        
        while(rIter.hasNext()){
            Round round = rIter.next();
            if(round.getFired()){
                continue;
            }
            if(entity.getN()){
                round.x = xx;
                round.y = yy;
                round.height = width;
                round.width = width;
            }
            if(entity.getS()){
                round.height = width;
                round.width = width;
                round.x = xx;
                round.y = yy+length-round.height;
            }
            if(entity.getE()){
                round.height = length;
                round.width = length;
                round.x = xx+width-round.width;
                round.y = yy;
            }
            if(entity.getW()){
                round.height = length;
                round.width = length;
                round.x = xx;
                round.y = yy;
            }
            
        }
    }
    public void update(){
        Iterator<Round> rIter = rounds.iterator();
        
        while(rIter.hasNext()){
            Round round = rIter.next();
            if(round.getCollided()){
                rIter.remove();
                continue;
            }
            round.collision();
        }
        updateRoundPosition();
    }
    public void draw(Graphics2D g){
        Iterator<Round> rIter = rounds.iterator();
        
        while(rIter.hasNext()){
            Round r = rIter.next();
            
            if(r.getCollided()){
                rIter.remove();
                continue;
            }
            r.draw(g);
        }
        
        g.setColor(Color.gray);
        if(entity.getN()){
            
            length = entity.height;
            width = entity.width/4;
            xx = (entity.x+(entity.width/2)) - width/2;
            yy = entity.y - length;
            
            g.fillRect(xx, yy, width, length);
        }
        if(entity.getS()){
            length = entity.height;
            width = entity.width/4;
            
            xx = (entity.x+(entity.width/2)) - (width/2);
            yy = entity.y +entity.height;
            
            g.fillRect(xx, yy, width, length);
        }
        if(entity.getE()){
            length = entity.height/4;
            width  = entity.width;
            
            xx = entity.x + entity.width;
            yy = (entity.y + entity.height/2) - (length/2);
            
            g.fillRect(xx,yy, width, length);
        }
        if(entity.getW()){
            length = entity.height/4;
            width = entity.width;
            
            xx = entity.x - width;
            yy = (entity.y + entity.height/2) - (length/2);
            
            g.fillRect(xx,yy, width, length);
        }
        
        
        
    }
}