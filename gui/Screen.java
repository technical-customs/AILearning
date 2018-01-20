package gui;

import entities.Bot;
import entities.Entity;
import entities.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class Screen extends JPanel implements ActionListener, Serializable{
    private static final long serialVersionUID = 1L;
    
    //Entites
    private volatile boolean moveBots;
    public static volatile List<Entity> entities;
    public static List<Entity> deadEntities;
    
    //SIZE
    public final int SWIDTH = 400;
    public final int SHEIGHT = SWIDTH / 16 * 9;
    public int SSCALE = 3;
    public Dimension SSIZE = new Dimension(SWIDTH * SSCALE, SHEIGHT * SSCALE);
    
    //GRAPHICS
    protected BufferStrategy bufferStrategy;
    
    //THREAD
    private volatile SwingWorker sw;
    private volatile Timer st;
    private volatile  boolean mainRunning;
    
    //FPS
    public static final int targetFPS = 30;
    
    public Screen(){
        entities = new LinkedList<>();
        deadEntities = new LinkedList<>();
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                setupGUI();
            }
        });
    }
    public Screen(Dimension size){
        entities = new LinkedList<>();
        deadEntities = new LinkedList<>();
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
        
        st = new Timer(10,this);
        st.setRepeats(true);
        st.start();
        
        log("Started Thread");
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
        log("Stopped Thread");
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
        
        //game draws
        Iterator<Entity> entityIter = entities.iterator();
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(deadEntities.contains(entity)){
                continue;
            }
            entity.drawer(g);
        }
    }
    
    //Entity
    public synchronized List<Entity> getEntities(){
        return entities;
    }
    public synchronized List<Entity> getDeadEntities(){
        return deadEntities;
    }
    public synchronized Entity addPlayer(){
        Entity entity = new Player(this);
        entities.add(entity);
        return entity;
    }
    public synchronized Entity addPlayer(int x, int y, int width, int height){
        Entity entity = new Player(this,x,y,width,height);
        entities.add(entity);
        return entity;
    }
    public synchronized Entity addBot(){
        Entity entity = new Bot(this);
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
        
        for(int a = 0; a < 1; a++){//generations
            for(int x = 0; x < 4; x++){//entities
                Bot bot = (Bot) screen.addBot();
                bot.setRandColor();
                bot.setRandSpeed();
                bot.setBounds(new Random().nextInt(screen.SSIZE.width-10)+10,
                    new Random().nextInt(screen.SSIZE.height-10)+10,
                    new Random().nextInt(10)+10,new Random().nextInt(10)+10);
            }



            try{
                Thread.sleep(1000);
                for(Entity e: entities){
                    Bot b = (Bot) e;
                    b.smartMove();
                    //Thread.sleep(1000);
                }


                Thread.sleep(5000);
                //get two random entities
                while(!entities.isEmpty()){

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
                    Bot bb = (Bot) b1.breedEntities(b2);
                    bb.smartMove();

                    Thread.sleep(10000);
                    if(entities.size()-deadEntities.size() == 1){
                        break;
                    }
                }
                System.out.println("END OF SIMULATION. Start with victor lap");
                Bot b = (Bot)entities.get(0);
                b.smartMove();

            }catch(Exception ex){Screen.log("MAIN EX " + ex);}
        }
    }
}