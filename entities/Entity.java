package entities;

import gui.Screen;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import objects.Boundary;

public abstract class Entity extends Rectangle{
    private volatile List<Entity> entities = new ArrayList<>();
    private volatile List<Entity> deadEntities = new ArrayList<>();
    
    public volatile Set<Entity> spottedents = new HashSet<>();
    public volatile Set<Boundary> spottedbounds = new HashSet<>();
   
    private volatile boolean dead = false, collided = false;
    private volatile Color color = Color.yellow;
    private volatile int xDir = 0, yDir = 0, speed = 1;
    protected boolean n,e,s,w;
    
    
    private int sMinD = 10, sMaxD = 50, sightD = new Random().nextInt(sMaxD) + sMinD;
    private int sMinP = 15, sMaxP = 75, sightP = new Random().nextInt(sMaxP) + sMinP;
    public int avoidanceDistance;
    final private SightLine sightline;
    protected volatile boolean spotted;
    
    
    
    
    private final Screen screen;
    
    
    public class SightLine extends Rectangle{
        final private int dist, periph;
        final private Entity entity;
        
        
        public SightLine(Entity entity){
            super();
            this.entity = entity;
            dist = sightD;
            periph = sightP;
            entity.avoidanceDistance = new Random().nextInt(dist)+2;
        }
        public SightLine(Entity entity, int dist, int periph){
            super();
            this.entity = entity;
            this.dist = dist;
            this.periph = periph;
            entity.avoidanceDistance = new Random().nextInt(dist)+2;
        }
        
        public void update(){
            Iterator<Entity> entityIter = Screen.entities.iterator();
        
            while(entityIter.hasNext()){
                Entity e = entityIter.next();
                
                if(entity.equals(e)){
                    continue;
                }
                
                if(this.intersects(e)){
                    //Spotted a character
                    entity.spotted = true;
                    spottedents.add(e);
                    //init ai move
                }
            }
        }
        public void draw(Graphics2D g){
            g.setColor(Color.white);
            
            if(entity.n){
                this.width = periph;
                this.height = dist;
                this.x = entity.x-(this.width - entity.width)/2;
                this.y = entity.y-this.height;
                
                
                g.drawRect(this.x,this.y,this.width,this.height);
            }
            if(entity.s){
                this.width = periph;
                this.height = dist;
                this.x = entity.x-(this.width - entity.width)/2;
                this.y = entity.y+entity.height;
                
                
                g.drawRect(this.x,this.y,this.width,this.height);
            }
            
            if(entity.e){
                this.width = dist;
                this.height = periph;
                this.x = entity.x + (entity.height);
                this.y = entity.y-(this.height - entity.height)/2;
                
                
                g.drawRect(this.x,this.y,this.width,this.height);
            }
            if(entity.w){
                this.width = dist;
                this.height = periph;
                this.x = entity.x-this.width;
                this.y = entity.y-(this.height - entity.height)/2;
                
                
                g.drawRect(this.x,this.y,this.width,this.height);
            }
            
        }
    }
    
    public Entity(Screen screen){
        super();
        this.screen = screen;
        n = true;
        sightline = new SightLine(this);
    }
    public Entity(Screen screen, int x, int y, int width, int height){
        super(x,y,width,height);
        this.screen = screen;
        n = true;
        sightline = new SightLine(this);
    }
    public Entity(Screen screen, int x, int y, int width, int height,
            Color color, int speed, int sightD, int sightP ){
        super(x,y,width,height);
        this.screen = screen;
        n = true;
        this.color = color;
        this.speed = speed;
        sightline = new SightLine(this,sightD,sightP);
    }
    
    
    //Entities
    public synchronized void setEntities(List<Entity> entities){
        this.entities.clear();
        this.entities.addAll(entities);
    }
    public synchronized List<Entity> getEntities(){
        return entities;
    }
    public synchronized void setDeadEntities(List<Entity> deadEntities){
        this.deadEntities = deadEntities;
    }
    public synchronized List<Entity> getDeadEntities(){
        return deadEntities;
    }
   
    
    //Color
    public void setRandColor(){
        color = new Color(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255));
    }
    public void setColor(Color color){
        this.color = color;
    }
    public Color getColor(){
        return color;
    }
    
    //Speed
    public void setRandSpeed(){
        this.speed = new Random().nextInt(9)+1;
    }
    public void setSpeed(int speed){
        this.speed = speed;
    }
    public int getSpeed(){
        return this.speed;
    }
    
    //Directions
    public void setXDir(int xDir){
        if(xDir != 0){
            this.xDir = xDir/Math.abs(xDir);
            
            if(this.xDir > 0){
                e = true;
                w = false;
            }else{
                e = false;
                w = true;
            }
        }else if(xDir == 0){
            this.xDir = 0;
        }
        
    }
    public int getXDir(){
        return xDir;
    }
    public void setYDir(int yDir){
        if(yDir != 0){
            this.yDir = yDir/Math.abs(yDir);
            if(this.yDir > 0){
                n = true;
                s = false;
            }else{
                n = false;
                s = true;
            }
        }else if(yDir == 0){
            this.yDir = 0;
        }
    }
    public int getYDir(){
        return yDir;
    }
    
    //Movement
    public void moveTo(int x, int y){
        //get amount of space needed to move
        stop();
        
        int xa = x-this.x;
        int axa = xa/Math.abs(xa);
        int ax = Math.abs(xa);
        
        int ya = y-this.y;
        int aya = ya/Math.abs(ya);
        int ay = Math.abs(ya);
        
        
        new Thread(new Runnable(){
            @Override
            public void run(){
                stepX(axa,ax);
                stepY(aya,ay);

                stepY(-1,1);
                stepY(1,1);
            }
        }).start();
        
        
    }
    public void stepX(int dir, int steps){
        try{
            double xPos = this.x;
            if(dir != 0){
                if(dir > 0){
                    n = false;
                    s = false;
                    e = true;
                    w = false;
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
                    n = false;
                    s = false;
                    e = false;
                    w = true;
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
                    n = false;
                    s = true;
                    e = false;
                    w = false;
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
                    
                    n = true;
                    s = false;
                    e = false;
                    w = false;
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
    public void pushRectX(){
        int xdir;
        do{
            xdir = new Random().nextInt(3)-1;
            setXDir(xdir);
        }while(xdir == 0);
    } //Randomizes xDir
    public void pushRectY(){
        int ydir;
        do{
            ydir = new Random().nextInt(3)-1;
            setYDir(ydir);
        }while(ydir == 0);
    } //Randomizes yDir
    
    public void move(){
        this.x += xDir * speed;
        this.y += yDir * speed;
    }
    public void stop(){
        collided = true;
        setXDir(0);
        setYDir(0);
        collided = false;
    }
    
    //Collisions
    public SightLine getSightLine(){
        return sightline;
    }
    private void boundCollision(){
        if(this.x <= 0){
            collided = true;
            this.x = 1;
            //this.setXDir(-xDir);
            //collided = false;
        }
        if( (this.x+this.width) >= screen.SSIZE.width){
            collided = true;
            this.x = (screen.SSIZE.width - this.width)-1;
            //this.setXDir(-xDir);
            //collided = false;
        }
        if(this.y <= 0){
            collided = true;
            this.y = 1;
            //this.setYDir(-yDir);
            //collided = false;
        }
        if( (this.y+this.height) >= screen.SSIZE.height){
            collided = true;
            this.y = (screen.SSIZE.height - this.height)-1;
            //this.setYDir(-yDir);
            //collided = false;
        }
                
    }
    protected abstract void entityCollision();
    public boolean getCollided(){
        return collided;
    }
    public void setCollided(boolean collided){
        this.collided = collided;
    }
    public boolean getDead(){
        return dead;
    }
    
    //mutate
    public boolean partner(Entity b){
        int mx = Math.abs( (b.x + x)/2 );
        int my = Math.abs( (b.y + y)/2 );
        
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable(){
            @Override
            public void run(){
                moveTo(mx-10,my);
                b.moveTo(mx+10,my);
                
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
                }
                latch.countDown();
            }
        }).start();
        
        try {
            latch.await();
            
        } catch (InterruptedException ex) {
            return false;
        }
        return false;
    }
    public Entity breedEntities(Entity b){
        //create different traits and make entity
        //width
        int highWidth = (this.width >= b.width)? this.width: b.width;
        int lowWidth = (this.width < b.width)? this.width: b.width;
        int newWidth;

        if(highWidth == lowWidth){
            newWidth = highWidth;
        }else{
            newWidth = new Random().nextInt(highWidth-lowWidth)+lowWidth;
        }

        //height
        int highHeight = (this.height >= b.height)? this.height: b.height;
        int lowHeight = (this.height < b.height)? this.height: b.height;
        int newHeight;
        if(highHeight == lowHeight){
            newHeight = highHeight;
        }else{
            newHeight = new Random().nextInt(highHeight-lowHeight)+lowHeight;
        }

        //color - get random color combo from parents
        int highRed = (this.getColor().getRed() >= b.getColor().getRed())? 
                this.getColor().getRed(): b.getColor().getRed();

        int lowRed = (this.getColor().getRed() < b.getColor().getRed())? 
                this.getColor().getRed(): b.getColor().getRed();
        int nr;
        if(highRed == lowRed){
            nr = highRed;
        }else{
            nr = new Random().nextInt(highRed-lowRed)+lowRed;
        }

        int highGreen = (this.getColor().getGreen() >= b.getColor().getGreen())? 
                this.getColor().getGreen(): b.getColor().getGreen();
        int lowGreen = (this.getColor().getGreen() < b.getColor().getGreen())? 
                this.getColor().getGreen(): b.getColor().getGreen();
        int ng;

        if(highGreen == lowGreen){
            ng = highGreen;
        }else{
            ng = new Random().nextInt(highGreen-lowGreen)+lowGreen;
        }

        int highBlue = (this.getColor().getBlue() >= b.getColor().getBlue())? 
                this.getColor().getBlue(): b.getColor().getBlue();
        int lowBlue = (this.getColor().getBlue() < b.getColor().getBlue())? 
                this.getColor().getBlue(): b.getColor().getBlue();
        int nb;
        if(highBlue == lowBlue){
            nb = highBlue;
        }else{
            nb = new Random().nextInt(highBlue-lowBlue)+lowBlue;
        }
        Color newColor = new Color(nr,ng,nb);

        //speed - get a random speed between the two parents speeds high and low
        int highSpeed = (this.speed >= b.speed)? this.speed: b.speed;
        int lowSpeed = (this.speed < b.speed)? this.speed: b.speed;
        int newSpeed;

        if(highSpeed == lowSpeed){
            newSpeed = highSpeed;
        }else{
            newSpeed = new Random().nextInt(highSpeed-lowSpeed)+lowSpeed;
        }

        SightLine tsl = this.getSightLine();
        SightLine esl = b.getSightLine();

        //sight height
        int highSightHeight = (tsl.height >= esl.height)? tsl.height: esl.height;
        int lowSightHeight = (tsl.height < esl.height)? tsl.height: esl.height;
        int newSightHeight;
        if(highSightHeight == lowSightHeight){
            newSightHeight  = highSightHeight ;
        }else{
            newSightHeight  = new Random().nextInt(highSightHeight -(lowSightHeight/2) )+lowSightHeight ;
        }

        //sight width
        int highSightWidth = (tsl.width >= esl.width)? tsl.width: esl.width;
        int lowSightWidth = (tsl.width < esl.width)? tsl.width: esl.width;
        int newSightWidth;
        if(highSightWidth == lowSightWidth){
            newSightWidth = highSightWidth;
        }else{
            newSightWidth = new Random().nextInt(highSightWidth-(lowSightWidth/2))+lowSightWidth;
        }
        //make new entity and kill off parents


        this.dead = true;
        b.dead = true;
        Screen.deadEntities.add(this);
        Screen.deadEntities.add(b);
        int xx = (this.x + b.x)/2;
        int yy = (this.y + b.y)/2;

        Entity newEnt = new Bot(screen,xx,yy,newWidth,newHeight,newColor,
                newSpeed,newSightHeight,newSightWidth);

        return screen.addBot((Bot) newEnt);

    }
    
    //Draw and Update
    public void update(){
        
        this.move();
        this.boundCollision();
        this.entityCollision();
        sightline.update();
        //System.out.println("ENTS: " + getEntities());
    }
    public void drawer(Graphics2D graphics){
        if(graphics == null){
            return;
        }
        
        graphics.setColor(color);
        if(n || s){
            graphics.fillRect(this.x, this.y, this.width, this.height);
        }else if(e || w){
            graphics.fillRect(this.x, this.y, this.height, this.width);
        }
        
        sightline.draw(graphics);
        //graphics.drawArc(this.x - (this.width/2), this.y - this.height, this.width, this.height * 2, 0, 180);
    }
    
    @Override
    public String toString(){
        return "Entity: " + this.getBounds();
    }
}