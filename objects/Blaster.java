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
    public boolean readyfire;
    private int numRounds = 500;
    public volatile List<Round> rounds = new ArrayList<>();
    private int xx, yy, length, width;
    private Entity entity;

    public Blaster(Entity entity){
        this.entity = entity;
        loadRounds();
    }
    
    private void loadRounds(){
        for(int x = 0; x < numRounds; x++){
            Round r = new Round(entity.x,entity.y,width/4,width/4);
            r.setSpeed(strength);
            r.setDamage(strength);
            rounds.add(r);
        }
        readyfire = true;
    }
    
    int countfires = 0;
    public synchronized void shoot(int times){
        if(!readyfire || times > rounds.size() || rounds.isEmpty()){
            return;
        }
        
        
        System.out.println("Times: " + times);
        new Thread(new Runnable(){
            @Override
            public void run(){
                Iterator<Round> rIter = rounds.iterator();
        
                for(int x = 0; x < times;x++){
                    if(rIter.hasNext()){
                        try{
                            if(rounds.isEmpty()){
                                return;
                            }

                            Round round = rIter.next();
                            countfires++;

                            System.out.println("FIRED");
                            round.setFired(true);


                            if(entity.getN()){
                                round.stepY(-1, range);
                                System.out.println("FIRED N");
                            }
                            if(entity.getS()){
                                round.stepY(1, range);
                                System.out.println("FIRED S");
                            }
                            if(entity.getE()){
                                round.stepX(1, range);
                                System.out.println("FIRED E");
                            }
                            if(entity.getW()){
                                round.stepX(-1, range);
                                System.out.println("FIRED W");
                            }
                            while(!round.getCollided()){readyfire = false;}
                            readyfire = true;
                        }catch(Exception ex){
                            System.out.println("Exception: " + ex);
                            return;
                        }
                    }
                }
                return;
            }
        }).start();
    }
    
    private void updateRoundPosition(Round round){
        //Iterator<Round> rIter = rounds.iterator();
        
        //while(rIter.hasNext()){
            //Round round = rIter.next();
            if(round.getFired()){
                return;
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
            
        //}
    }
    public synchronized void update(){
        Iterator<Round> rIter = rounds.iterator();
        
        while(rIter.hasNext()){
            Round round = rIter.next();
            if(round.getCollided()){
                //rIter.remove();
                continue;
            }
            round.collision();
            updateRoundPosition(round);
        }
        
    }
    public void draw(Graphics2D g){
        Iterator<Round> rIter = rounds.iterator();
        
        while(rIter.hasNext()){
            Round r = rIter.next();
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