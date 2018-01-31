package gui;

import entities.Bot;
import entities.Entity;
import entities.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import objects.Boundary;

public class Screen extends JPanel implements ActionListener, Serializable{
    private static final long serialVersionUID = 1L;
    
    //Entites
    private volatile boolean moveBots;
    public static volatile List<Entity> entities;
    public static List<Entity> deadEntities;
    public static List<Boundary> boundaries;
    public int generation = 0;
    //SIZE
    public static final int SWIDTH = 200;
    public static final int SHEIGHT = SWIDTH / 16 * 9;
    public static int SSCALE = 3;
    public static Dimension SSIZE = new Dimension(SWIDTH * SSCALE, SHEIGHT * SSCALE);
    
    //GRAPHICS
    protected BufferStrategy bufferStrategy;
    
    //THREAD
    private volatile SwingWorker sw;
    private volatile Timer st;
    private volatile  boolean mainRunning;
    
    //FPS
    public static final int TARGETFPS = 30;
    
    public Screen(){
        entities = new ArrayList<>();
        deadEntities = new ArrayList<>();
        boundaries = new ArrayList<>();
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                setupGUI();
            }
        });
    }
    public Screen(Dimension size){
        entities = new ArrayList<>();
        deadEntities = new ArrayList<>();
        boundaries = new ArrayList<>();
        SSIZE = size;
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                setupGUI();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2= (Graphics2D) g;
        
        g2.setColor(Color.black);
        g2.fillRect(0,0,SSIZE.width,SSIZE.height);
        
        //game draws
        render(g2);
        g2.dispose();
        repaint();
        
        //bufferStrategy.show();
        
        g2.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                if(mainRunning){
                    try{
                        tick();
                    }catch(Exception ex){
                        
                    }
                }
                
            }
        });
        
    }
   
    private synchronized void setupGUI(){
        JFrame frame = new JFrame();
        
        setPreferredSize(SSIZE);
        setLayout(null);

        frame.getContentPane().add(this);
        frame.pack();
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we){
                try{
                    stop();
                }finally{
                    System.exit(0);
                }
            }
        });
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }
    
    public synchronized void start(){
        if(mainRunning){
            return;
        }
        
        mainRunning = true;
        
        st = new Timer(17,this);
        st.setRepeats(true);
        st.start();
        
        //log("Started Thread");
    }
    public synchronized void stop(){
        if(!mainRunning){
            return;
        }
        if(!st.isRunning()){
            return;
        }
        stopBots();
        
        mainRunning = false;
        st.stop();
        //log("Stopped Thread");
    }
    
    //Speed
    public synchronized void tick(){
        if(mainRunning){
            //game updates
            Iterator<Entity> entityIter = entities.iterator();
            while(entityIter.hasNext()){
                Entity entity = entityIter.next();
                if(entity.getDead()){
                    deadEntities.add(entity);
                    continue;
                }
                if(deadEntities.contains(entity)){
                    continue;
                }
                entity.update();
            }
        }
    }
    public synchronized void render(Graphics2D g){
        //paint screen
        g.setColor(Color.black);
        g.fillRect(0, 0, SSIZE.width,SSIZE.height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(Color.red);
        g.drawString("Generation: " + generation, 5, 15);
        //game draws
        Iterator<Entity> entityIter = entities.iterator();
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(deadEntities.contains(entity)){
                entityIter.remove();
                continue;
            }
            if(entity.getDead()){
                continue;
            }
            entity.drawer(g);
        }
        Iterator<Boundary> boundIter = boundaries.iterator();
        while(boundIter.hasNext()){
            Boundary bound = boundIter.next();
            bound.draw(g);
        }
    }
    
    //Entity
    public synchronized List<Entity> getEntities(){
        return entities;
    }
    public synchronized List<Entity> getDeadEntities(){
        return deadEntities;
    }
    public synchronized Entity addPlayer(int x, int y, int width, int height){
        Entity entity = new Player(this,x,y,width,height);
        entities.add(entity);
        return entity;
    }
    public synchronized Entity addBot(int x, int y, int width, int height){
        Entity entity = new Bot(this,x,y,width,height);
        entities.add(entity);
        return entity;
    }
    public synchronized Entity addBot(Bot bot){
        entities.add(bot);
        return bot;
    }
    public synchronized Boundary addBoundary(int x, int y, int width, int height){
        Boundary bound = new Boundary(x,y,width,height);
        boundaries.add(bound);
        return bound;
    }
    public synchronized Boundary addBoundary(Boundary bound){
        boundaries.add(bound);
        return bound;
    }
    
   
    
    private void startBots(){
        if(!mainRunning){
            return;
        }
        Iterator<Entity> entityIter = entities.iterator();
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(deadEntities.contains(entity)){continue;}
            entity.pushRectX();
            entity.pushRectY();
        }
    }
    private void stopBots(){
        Iterator<Entity> entityIter = entities.iterator();
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(deadEntities.contains(entity)){continue;}
            entity.stop();
        }
    }
    public synchronized void killEntity(Entity entity){
        deadEntities.add(entity);
        entities.remove(entity);
    }
    
    //GENERAL PURPOSE LOGGER
    public static void log(String string){
        System.out.println(string);
    }
    
    public static void main(String[] args) throws InterruptedException, InvocationTargetException{
        Screen screen = new Screen();
        SwingUtilities.invokeAndWait(new Runnable(){
            @Override
            public void run(){
                screen.start();
            }
        });
        
        int numOfGens= 0;
        int numOfBots = 0;
        
        
        do{
            try{
                numOfGens= Integer.parseInt(JOptionPane.showInputDialog
        (null, "Input Number of Generations:"));
            }catch(HeadlessException | NumberFormatException ex){System.exit(0);}
            
        }while(numOfGens == 0);
        do{
            try{
                numOfBots= Integer.parseInt(JOptionPane.showInputDialog
        (null, "Input Number of Entities:"));
            }catch(HeadlessException | NumberFormatException ex){System.exit(0);}
            
        }while(numOfBots == 0);
        
        
        //screen.addBoundary(0, 0, screen.SSIZE.width, 1);
        //screen.addBoundary(0, 0, 1, screen.SSIZE.height);
        
        //screen.addBoundary(0, screen.SSIZE.height, screen.SSIZE.width, 1);
        //screen.addBoundary(screen.SSIZE.width, 0, 1, screen.SSIZE.height);
        
        //screen.addBoundary(50, 430, 50, 50);
        //screen.addBoundary(150, 450, 50, 50);
        //screen.addBoundary(250, 350, 50, 50);
        //screen.addBoundary(450, 350, 50, 50);
        //screen.addBoundary(550, 250, 50, 50);
        //screen.addBoundary(650, 350, 50, 50);
        //screen.addBoundary(750, 250, 50, 50);
        //screen.addBoundary(850, 150, 50, 50);
        
        for(int a = 0; a < numOfGens; a++){//generations
            screen.generation++;
            
            for(int x = 0; x < numOfBots; x++){//entities
                Bot bot = (Bot) screen.addBot(new Random().nextInt(screen.SSIZE.width-10)+10,
                    new Random().nextInt(screen.SSIZE.height-10)+10,
                    new Random().nextInt(10)+10,new Random().nextInt(10)+10);
                bot.setRandColor();
                bot.setRandSpeed();
            }



            try{
                Thread.sleep(1000);
                for(Entity e: entities){
                    Bot b = (Bot) e;
                    
                    
                    b.smartMove();
                    //
                }
                Thread.sleep(3000);
                for(Entity e: entities){
                    Bot b = (Bot) e;
                    b.getBlaster().shoot(50);
                }
                
                Thread.sleep(10000);
                //get two random entities
                boolean v = entities.size() > 1;
                while(!v){

                    int r1 = 0,r2 = 0;
                    do{
                        r1 = new Random().nextInt(entities.size());
                        r2 = new Random().nextInt(entities.size());
                    }while(r1 == r2);


                    Bot b1 = (Bot) entities.get(r1);
                    Bot b2 = (Bot) entities.get(r2);
                    
                    if(deadEntities.contains(b1) || deadEntities.contains(b2)){
                        continue;
                    }
                    
                    b1.move = false;
                    b2.move = false;
                    
                    Thread.sleep(2000);
                    //if(b1.partner(b2)){
                        Bot bb = (Bot) b1.breedEntities(b2);
                        bb.smartMove();
                        
                        Thread.sleep(2000);
                        
                        bb.getBlaster().shoot(10);
                    //}else{
                        //b1.smartMove();
                        //b2.smartMove();
                    //}
                    Thread.sleep(10000);
                    if(entities.size()-deadEntities.size() == 1){
                        break;
                    }
                }

            }catch(Exception ex){Screen.log("MAIN EX " + ex);}
            Bot finalbot = (Bot) entities.get(0);
            //finalbot.stop();
        }
       // System.out.println("Final Bot");
        Bot finalbot = (Bot) entities.get(0);
        //finalbot.stop();
        //finalbot.getBlaster().shoot(2);
    }
}