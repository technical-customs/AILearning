package objects;

import entities.Entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Blaster {
    private int strength = 5;
    public int range = 200;
    public volatile boolean readyfire;
    private int numRounds = 3;
    public volatile List<Round> rounds = new ArrayList<>();
    private int xx, yy, length, width;
    private Entity entity;

    public Blaster(Entity entity){
        this.entity = entity;
        loadRounds();
    }
    private Round loadRound(){
        //for(int x = 0; x < numRounds; x++){
        Round r = new Round(entity.x,entity.y,width/4,width/4);
        updateRoundPosition(r);
        r.setSpeed(strength);
        r.setDamage(strength);
        rounds.add(0,r);
        //}
        readyfire = true;
        return r;
    }
    private void loadRounds(){
        for(int x = 0; x < numRounds; x++){
            loadRound();
        }
        readyfire = true;
    }
    
    int countfires = 0;
    public synchronized void shoot(int times){
        
        if(countfires >= rounds.size()){
            //countfires = 0;
        }
        //System.out.println("Times: " + times);
        new Thread(new Runnable(){
            @Override
            public void run(){
                Iterator<Round> rIter = rounds.iterator();
        
                for(int x = 0; x < times;x++){
                    
                    //if(rIter.hasNext()){
                        try{
                            if(countfires >= rounds.size()){
                                //loadRounds();
                                //countfires = 0;
                                //continue;
                            }
                            
                            //Round round = rIter.next();
                            //Round round = rounds.get(countfires);
                            Round round = loadRound();
                            //updateRoundPosition(round);

                            round.setFired(true);
                            countfires++;
                            if(entity.getN()){
                                //System.out.println("FIRED N");
                                round.stepY(-1, range);
                                
                            }
                            if(entity.getS()){
                                //System.out.println("FIRED S");
                                round.stepY(1, range);
                                
                            }
                            if(entity.getE()){
                                //System.out.println("FIRED E");
                                round.stepX(1, range);
                                
                            }
                            if(entity.getW()){
                                //System.out.println("FIRED W");
                                round.stepX(-1, range);
                                
                            }
                            //while(!round.getCollided()){}
                            
                            Thread.sleep(2000);
                            //readyfire = true;
                            //rIter.remove();
                            
                        }catch(Exception ex){
                            System.out.println("Exception: " + ex);
                            return;
                        }
                    //}
                }
                
            }
        }).start();
    }
    
    private void updateRoundPosition(Round round){
        //Iterator<Round> rIter = rounds.iterator();
        
        //while(rIter.hasNext()){
            //Round round = rIter.next();
        if(!round.getFired()){
            
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
    public synchronized void update(){
        Iterator<Round> rIter = rounds.iterator();
        
        while(rIter.hasNext()){
            Round round = rIter.next();
            if(!round.getCollided()){
                round.collision();
                updateRoundPosition(round);
            }
            
        }
        
    }
    public void draw(Graphics2D g){
        Iterator<Round> rIter = rounds.iterator();
        
        while(rIter.hasNext()){
            Round r = rIter.next();
            
            if(r.getCollided()){
                //rIter.remove();
                //continue;
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