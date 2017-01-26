/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toh;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import static toh.TOH.scale;
/**
 *
 * @author Sam
 */
public class TOH extends JPanel {
    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;
    public static int SCALER = 1;
    public static double milliSecondTimer;
    public static double delta;
    public static ArrayList<Integer> keysDown;
    public static Tower[] towers;
    public static int scale = 50; //1 = 50 pixels
    public static boolean firstPress;
    public static int from;
    public static int moves;
    public static int[] arr;
    public static int len;
    public static int currentT;
    public static boolean pause;
    public static int counter;
    
    public TOH(){
        //Init
        keysDown = new ArrayList<Integer>();
        towers = new Tower[3];
        
        pause = true;
        
        len = 3;
        reset();
        
        firstPress = false;
        moves = 0;
        
        //Other
        KeyListener listener = new MyKeyListener();
        addKeyListener(listener);
        MouseListener mListener = new MyKeyListener();
        addMouseListener(mListener);
        setFocusable(true);   
    }
    public void reset(){
        moves = 0;
        arr = new int[len];
        for (int i = 0; i < len; i++){
            arr[i] = len - i;
        }
        int[] arr2 = new int[] {};
        int[] arr3 = new int[] {};
        towers[0] = new Tower(arr, (WIDTH / 3) - 250, HEIGHT / 2);
        towers[1] = new Tower(arr2, (WIDTH / 3) + 250, HEIGHT / 2);
        towers[2] = new Tower(arr3, (WIDTH / 3) + 750, HEIGHT / 2);
    }
    
    public static void main(String[] args) throws InterruptedException{
        JFrame frame = new JFrame("Tower of Hanoi");
        TOH app = new TOH();
        frame.setSize((int)(WIDTH * SCALER),(int)(HEIGHT * SCALER));
        frame.add(app);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.requestFocus();
        long lastLoopTime = System.nanoTime();
        int fps = 0, lastFpsTime = 0, lastMilliSecondTimer = 0, count = 1;
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        //Game Loop
        while(true){
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            delta = updateLength / ((double)OPTIMAL_TIME);
            lastFpsTime += updateLength;
            lastMilliSecondTimer += updateLength;
            fps++;
            if (lastFpsTime > 100000000 * count){
               milliSecondTimer += 0.1;
               count++;
            }
            if (lastFpsTime >= 1000000000){
                System.out.println("(FPS: "+fps+")");
                //milliSecondTimer += 1;
                lastFpsTime = 0;
                fps = 0;
                count = 1;
            }
            app.repaint();
            Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 );
        }
    }
    public void AI(){
        if (moves % 2 ==0){ //odd turn
            for (int i = 0; i < 3; i++){
                int size = towers[i].blocks.size();
                if (size > 0){
                    if (towers[i].blocks.get(size - 1) == 1){
                        from = i;
                    }
                }
            }
            int x;
            if (len % 2 != 0){
                x = -1;
            } else {
                x = 1;
            }
            for (int i = 0; i < 2; i++){
                    int temp = (from + x) % 3;
                    if (temp<0) temp += 3;
                    if (from != temp){
                        if (mover(from, temp, true)){
                            return;
                        }
                    }
                }
        } else{
            int x = 0;
            if (len % 2 != 0){
                x = 1;
            } else{
                x = -1;
            }
            for (from = 0; from < 3; from++){
                for (int i = 0; i < 3; i++){
                    int temp = (from + x * i) % 3;
                    if (temp<0) temp += 3;
                    if (towers[from].pointer > 0 && from != temp){
                        if (towers[from].blocks.get(towers[from].pointer - 1) != 1){
                            if (mover(from, temp, true)){
                                return;
                            }
                        } 
                    }
                }
            }
        }
    }
    public boolean mover(int from, int to, boolean ai){
        int size = towers[from].blocks.get(towers[from].blocks.size() - 1);
        Color tempC = towers[from].rect.get(towers[from].rect.size() - 1).colour;
        if (towers[to].push(size, tempC, ai)){
            towers[from].pop();
            moves++;
            if (ifWon()){
                //Game Won Code
                System.out.println("WON");
            }
            return true;
        }
        return false;
    }
    public boolean ifWon(){
        if (towers[towers.length - 1].blocks.size() > 1){
            int counter = 0;
            for (int i = 0; i < towers[towers.length - 1].blocks.size() ; i++){
                if (towers[towers.length - 1].blocks.get(i) != arr[i]){
                    return false;
                }
                counter++;
            }
            if (counter != arr.length){
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
    //Window Painter
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.setFocusable(true);
        this.requestFocusInWindow();
        counter++;
        if (counter % (10 - len) == 0){
            if (!pause) AI();
        }
        //Game loop, but everything time related * delta to get seconds.
        g.drawString("Moves = " + moves, 10, 10);
        int nPow = (int) Math.pow(2, len);
        g.drawString("Optimal Moves = " + (nPow - 1), 10, 20);
        g.drawString("Press Escape to reset the board.", 10, 30);
        g.drawString("Press Up and Down to increase or decrease the number of boxes.", 10, 40);
        g.drawString("Press Space to find the optimal solution.", 10, 50);
        for (int i = 0; i < towers.length; i++){
            towers[i].paint(g2d);
        }
                                    
    }
    
    //Listens for button presses
    public class MyKeyListener implements KeyListener, MouseListener{

        public void action(){
            if (keysDown.contains(KeyEvent.VK_ESCAPE)){
                reset();
            }
            if (keysDown.contains(KeyEvent.VK_SPACE)){
                pause = !pause;
            }
            if (keysDown.contains(KeyEvent.VK_UP)){
                if (len < 11){
                    len++;
                    pause = true;
                    reset();
                }
            }
            if (keysDown.contains(KeyEvent.VK_DOWN)){
                if (len > 1){
                    len--;
                    pause = true;
                    reset();
                }
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
                       
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (!keysDown.contains(e.getKeyCode())){
               keysDown.add(e.getKeyCode()); 
            }
            action();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            keysDown.remove(new Integer(e.getKeyCode()));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!firstPress){
                int temp = whichBox(e);
                if (temp != -1){
                    firstPress = true;
                    from = temp;
                }                
            } else {
                int temp = whichBox(e);
                if (temp != -1){
                    firstPress = false;
                    mover(from, temp, false);
                }
            }
        }
        
        public int whichBox(MouseEvent e){
            for (int i = 0 ; i < towers.length; i++){
                if (towers[i].border[1].contains(new Point(e.getXOnScreen(), e.getYOnScreen()))){
                    return i;
                }
            }
            return -1;
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseEntered(MouseEvent e) {
           //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
class Tower {
    ArrayList<Integer> blocks;
    ArrayList<Rectangle>  rect;
    Rectangle[] border;
    int pointer;
    int[] pos;
    
    public Tower(int[] arr, int x, int y){
        blocks = new ArrayList<Integer>();
        rect = new  ArrayList<Rectangle>();
        border = new Rectangle[2];
        pos = new int[]{ x , y};
        border[0] = new Rectangle((int) (pos[0] - 250), 100, 500, 500);
        border[1] = new Rectangle((int) (pos[0] - 240), 110, 480, 480);
        pointer = 0;
        for (int i = 0; i < arr.length; i++){
            Random rand = new Random();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            Color c = new Color(r, g, b);
            push(arr[i], c, false);
        }
    }
    //put new item on stack
    public boolean push(int size, Color c, boolean ai){
        //implement overflow when you get the chance
        boolean can = false;
        if (pointer == 0){
            can = true;
        } else if (blocks.get(pointer - 1) >= size){
            can = true;
        }
        if(can){
            blocks.add(size);
            double hs = size; hs /= 2;
            rect.add(new Rectangle((int) (pos[0] - (hs * scale)) ,pos[1] - (pointer * 30) , size * scale, 30));
            rect.get(rect.size() - 1).colour = c;
            pointer++;
            return true;
        } else {
            if (!ai){
                JOptionPane.showMessageDialog(null, "ERROR, incompatible move");
            }
            return false;
        }
        
    }
    //remove item from top of the stack
    public void pop(){
        if(pointer > -1){
            int temp = blocks.get(blocks.size() - 1);
            blocks.remove(blocks.size() - 1);
            rect.remove(blocks.size());
            pointer--;
        }
    }
    public void paint(Graphics2D g) {
        border[0].colour = Color.DARK_GRAY;
        border[0].paint(g);
        border[1].colour = Color.white;
        border[1].paint(g);
        for (int i = 0; i < rect.size() ; i++){
            rect.get(i).paint(g);
        }
    }
    
}
//Define other objects.
class Rectangle extends Rectangle2D.Float {

    Color colour;

    public Rectangle(int x, int y, int rx, int ry) {
        super(x, y, rx, ry);
        this.colour = Color.black;
    }

    public void paint(Graphics2D g) {
        g.setColor(colour);
        g.fill(this);
    }
}



/* Useful shortcuts:
grid (tab) = grid layout every 10 pixels, on a 100 pixel = 1 metre scale, that is 10 cm.
grid2 (tab) = grid layout every 100 pixels, on a 100 pixel = 1 metre scale, that is 1m.
Ball (tab) = creates the Ball class.
Rectangle (tab) = creates the Rectangle class.
*/