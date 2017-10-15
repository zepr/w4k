/*
 *                 [[ W4K ]]
 *
 * Copyright (c) 2006 Glenn Sanson.
 *
 * This code is distributed under the GNU General Public License 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * This game is an entry of the javaunlimited.net 4K contest '06
 * 
 * How to build W4K.jar yourself :
 * > Compile with a java 1.4 target
 *     javac -source 1.4 -target 1.4 W.java
 * > Make a jar file
 *     jar cvf W4K.jar W.class
 * > Use JoGa v0.1(a) [http://www.nq4.de/] to optimize jar
 *     java -jar JoGa.jar (this creates a ./JoGaDIR/W4K_JOGA.jar file)
 * > Unzip W.class from the W4K_JOGA.jar file with 7-zip [http://www.7-zip.org/] or another tool
 *     7za x W4K_jg.jar W.class
 * > Create a META-INF/MANIFEST.MF file 
 *     Main-Class: W (+ 2 empty lines, or it won't work)
 * > Zip it again with Z-zip
 *     7za a -tzip -mx=9 W4K_final.jar W.class META-INF/MANIFEST.MF
 *
 * You should now have a 4096 bytes file named W4K_final.jar 
 *          
 *          [[ http://glenn.sanson.free.fr/w4k/ ]]
 */

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Random;

public class W extends Frame implements WindowListener, KeyListener {
        
    Image b; // Double buffer        
    int k; // keyboard management
        
    public W() {
        super("W4K");

        boolean computer = true;
        boolean demo = true;
        
        int[] m = new int[287809];
        Random rand = new Random(System.currentTimeMillis());
        
        MemoryImageSource source = new MemoryImageSource(641, 449, m, 0, 641);
        source.setAnimated(true);
        Image img = createImage(source);        
        
        setVisible(true);
        setResizable(false);
        setSize(641, 449);
        b = createImage(641, 449);
        Graphics g = b.getGraphics();        
        addWindowListener(this);
        addKeyListener(this);        
        
        while(true) {
            
            int tmpVar = 64;
            
            for (int y=0; y<449 ; y+=tmpVar) {
                for (int x=0; x<641 ; x+=tmpVar) {
                    m[y * 641 + x] = rand.nextInt() % 256;
                }
            }
            
            for (;tmpVar > 1 ; tmpVar = tmpVar >> 1) {
                for (int y=0 ; y<449 ; y+=tmpVar) {
                    for (int x=tmpVar ; x<641 ; x+=tmpVar) m[y*641 + x-(tmpVar >> 1)] = (m[y*641 + x-tmpVar] + m[y*641 + x]) >> 1;
                }
                
                for (int y=tmpVar ; y<449 ; y+=tmpVar) {
                    for (int x=0 ; x<641 ; x+=(tmpVar >> 1)) m[(y - (tmpVar >> 1)) * 641 + x] = (m[(y - tmpVar)*641 + x] + m[y * 641 + x]) >> 1;
                }
            }
            
            for (int i=0 ; i<287809 ; i++) {
                if (m[i] > 0) {
                    int top = 1;
                    while(i - top * 641 > 0 && m[i - top * 641] > 0xFF0000FF) top++;
                    
                    int bottom = 1;
                    while (i + bottom * 641 < 287809 && m[i + bottom * 641] > 0) bottom++;
                                    
                    if (top < 5) m[i] = rand.nextInt()>0 ? 0xFF0DAB0D : 0xFF00C800;
                    // Retirer ce cas si pb pour atteindre 4Ko 
                    else if (top == 5)    m[i] = 0xFF007848;
                    else {
                        if (i % 10 == 0) m[i] = 0xFFE89058;
                        else m[i] = (i/10)%2 == 0 ? 0xFF985818 : 0xFFC07830;
                        
                        tmpVar = Math.min(bottom, 16);
                        tmpVar = ((tmpVar * tmpVar) >> 3) + rand.nextInt() % 10;
                        
                        m[i] = 0xFF000000 
                        + (Math.min(255, ((m[i] >> 16) & 0xFF) + tmpVar) << 16)
                        + (Math.min(255, ((m[i] >> 8) & 0xFF) + tmpVar) << 8) 
                        + Math.min(255, (m[i] & 0xFF) + tmpVar);
                    }
                }
                else m[i] = 0xFF0000FF - ((i / 641) >> 1);
            }
            
            source.newPixels(0, 0, 641, 449, false);
                            
            // Init worms
            int[][] p = new int[8][8];
            
            // 0, 1 -> Worm position [x, y]
            // 2 -> Direction -1 = left / 1 = right 
            // 3 -> Used for body animation
            // 4 -> Weapon direction in degrees (-90 -> +90)
            // 5 -> Life (init : 100)
            // 6, 7 -> Movement (Explosion)
            
            for (int i=0 ; i<8 ; i++) {
                tmpVar = 0;
                p[i][0] = 320 + rand.nextInt() % 300;
                p[i][1] = 220 + rand.nextInt() % 200;
    
                while (tmpVar < 6 || m[p[i][0] + 641 * (p[i][1] + 1)] < 0xFF0000FF) {
                    if (m[p[i][0] + 641 * p[i][1]] > 0xFF0000FF || (p[i][1] > 449 - 10) ) {
                        tmpVar = 0;
                        p[i][0] = 320 + rand.nextInt() % 300;
                        p[i][1] = 220 + rand.nextInt() % 200;
                    }
                    
                    tmpVar++;
                    p[i][1]++;
                }
                p[i][2] = -1;
                p[i][5] = 100;
            }        
            
            // Names : Beatles against Daltons
            String[] names = {"John", "Joe", "Paul", "Jack", "George", "William", "Ringo", "Averell"};
                    
            int x = 0;
            int y = 0;
            int e = 0;
            
            int power = 0;
            double[] ball = new double[4]; // Ball
            ball[0] = -1.;
            
            int currentPlayer = 0;
            int refPlayer = 0;
            boolean fired = false;            
            
            tmpVar = 0;
            
            boolean victory = false;
            boolean play = true;
            int blink = 0;
            k = 0;
            
            while (play) {
                long time = System.currentTimeMillis();
                boolean noMoreAction = true;
                
                // Controls demo
                if (demo & k == 38) computer = true;
                if (demo & k == 40) computer = false;
                if (demo & (k == 32 | k == 10)) {
                    demo = false;
                    victory = true;
                    k = -1;
                }
                
                // Action
                for (int player=0 ; player<8 ; player++) {
                    if (victory & k == -1) play = false;
                    if (victory & k == 32) {
                        play = false;
                        demo = true;
                    }
                    else if (p[player][0] < 0); // Elimine de la carte
                    else if (p[player][6] != 0 || p[player][7] != 0) {
                        if (p[player][0] + p[player][6] < 5 
                              || p[player][0] + p[player][6] + 5 > 641
                              || p[player][1] + p[player][7] < 5
                              || p[player][1] + p[player][7] + 5 > 449) {
                            p[player][6] = 0;
                            p[player][7] = 0;
                            p[player][0] = -100;
                            fired = true; // Change player
                        }
                        else if (m[p[player][0] + p[player][6] + (p[player][1] + p[player][7]) * 641] > 0xFF0000FF) {
                            p[player][6] = 0;
                            p[player][7] = 0;
                            noMoreAction = false;
                        }
                        else if (m[p[player][0] + p[player][1] * 641] < 0xFF0000FF) {
                            double dx = ((double)p[player][6]) / 20.;
                            double dy = ((double)p[player][7]) / 20.;
    
                            boolean wall = false;
                            for (int i=0 ; i<20 ; i++)
                                wall |= m[p[player][0] + (int)(i * dx) + (p[player][1] + (int)(i * dy)) * 641] > 0xFF0000FF;
    
                            if (wall) {
                                p[player][6] = 0;
                                p[player][7] = 0;
                            }
    
                            p[player][0] += p[player][6];
                            p[player][1] += p[player][7];                        
                            noMoreAction = false;         
                            
                            if (!wall) p[player][7]++;
                        }
                        else {                        
                            p[player][0] += p[player][6];
                            p[player][1] += p[player][7];
                            p[player][7]++;
                            noMoreAction = false;
                        }
                    }
                    else if (m[p[player][0] + (p[player][1] + 1) * 641] < 0xFF0000FF) {
                        p[player][7] = 1;
                        noMoreAction = false;
                    }
                    else if (player != currentPlayer);
                    // Computer 
                    else if (!fired & (demo | (computer & player % 2 == 1)) & power < tmpVar) {
                        power += 5;
                    }
                    else if (!fired & (demo | (computer & player % 2 == 1)) & tmpVar == 0) {
                        // Find nearest worm
                        int nearest = -1;
                        int dist = 1 << 24;
                        
                        for (int i=(1 - (currentPlayer % 2)) ; i<8 ; i+= 2) {
                            if (p[i][0] > 0 && p[i][5] > 0) {
                                int newDist = (p[player][0] - p[i][0]) * (p[player][0] - p[i][0]) + (p[player][1] - p[i][1]) * (p[player][1] - p[i][1]);
                                if (newDist < dist) {
                                    nearest = i;
                                    dist = newDist;
                                }
                            }
                        }
                        
                        if (nearest != -1) { // At least one foe
                            // Orientation
                            p[player][2] = p[player][0] < p[nearest][0] ? 1 : -1;
                            // New direction (if needed)
                            int angle = (int)(180. * Math.atan(((double)(p[nearest][1] - p[player][1])) / ((double)Math.abs(p[player][0] - p[nearest][0]))) / Math.PI);
                            
                            if (p[player][4] < angle) {
                                p[player][4] += 5;
                            }
                            else if (p[player][4] > angle + 5) {
                                p[player][4] -= 5;
                            }
                            else {
                                p[player][4] = angle;
                                tmpVar = 5 + Math.min(95, dist >> 6);
                            }
                        }
                    }
                    // keyboard management 
                    else if ((k == 37 | k == 39) & !(demo | (computer & player % 2 == 1))) {
                        p[player][2] = k-38;
                        p[player][3]++;
                        p[player][6] = p[player][2];
                        p[player][7] = -2;
                    }
                    else if (k == 38 & !(demo | (computer & player % 2 == 1))) {
                        if (p[player][4] > -90) p[player][4] -= 5;
                    }
                    else if (k == 40 & !(demo | (computer & player % 2 == 1))) {
                        if (p[player][4] < 90) p[player][4] += 5;
                    }
                    else if (k == 32 & ball[0] == -1. & !(demo | (computer & player % 2 == 1))) {
                        if (power < 100) power+= 5;
                    }
                    else if (power > 0 && ball[0] == -1.) {
                        ball[0] = p[player][0];
                        ball[1] = p[player][1] - 5;
                        ball[2] = p[player][2] * (double)power * 0.2 * Math.cos(p[player][4] * Math.PI / 180.);
                        ball[3] = (double)power * 0.2 * Math.sin(p[player][4] * Math.PI / 180.);
                        fired = true;
                        noMoreAction = false;
                        tmpVar = 0;
                    }
                }
                
                // Redraw
                if (e >= 1) {
                    for (int i=0 ; i<287809 ; i++) {
                        int d = (i % 641 - x) * (i % 641 - x) + (i / 641 - y) * (i / 641 - y); 
                        
                        if (d < 400) m[i] = d + 1 >= e ? 0xFF0000FF - ((i / 641) >> 1) : 0xFFFFFFFF;
                    }        
                    e >>= 1;
                    
                    source.newPixels(0, 0, 641, 449, false);                
                }
                
                g.drawImage(img, 0, 0, this);
                
                g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 14));
                                
                for (int i=0 ; i<8 ; i++) {
                    g.setColor((i==currentPlayer & ((blink >> 3) % 2) == 0) ? Color.RED : Color.WHITE); 
                    g.drawString(names[i], p[i][0] - 3*names[i].length(), p[i][1] - 27);
                    
                    g.setColor(i%2==0?Color.GREEN:Color.YELLOW);
    
                    if (p[i][5] > 0) {
                        g.fillOval(p[i][0] - 4, p[i][1] - 6 + (p[i][3]%2), 8, 8);
                        g.fillOval(p[i][0] - 2 - p[i][2] * 7, p[i][1]-2 - (p[i][3]%2), 4, 4);
                        g.setColor(i%2==0?new Color(0x00CA00):new Color(0xCACA00));
                        g.fillOval((int)(p[i][0] - 2.5 - p[i][2] * 4.5), p[i][1]-4 + (p[i][3]%2), 5, 5);
                        g.setColor(Color.PINK);
                        g.fillOval(p[i][0] - 4 + p[i][2] * 3, p[i][1] - 10, 8, 8);
                        g.setColor(Color.BLACK);
                        g.drawLine(p[i][0] + 6 * p[i][2], p[i][1] - 7, p[i][0] + 6 * p[i][2], p[i][1] - 6);
                        g.drawLine(p[i][0] + 4 * p[i][2], p[i][1] - 7, p[i][0] + 4 * p[i][2], p[i][1] - 6);
                        g.setColor(Color.WHITE);
                        g.drawLine(p[i][0] + 6 * p[i][2], p[i][1] - 8, p[i][0] + 6 * p[i][2], p[i][1] - 8);
                        g.drawLine(p[i][0] + 4 * p[i][2], p[i][1] - 8, p[i][0] + 4 * p[i][2], p[i][1] - 8);     
                        g.drawString(String.valueOf(p[i][5]), p[i][0] - 10, p[i][1] - 14);
                    }
                    else {
                        g.fillRect(p[i][0] - 4, p[i][1] - 11, 9, 11);
                        g.setColor(Color.BLACK);
                        g.drawLine(p[i][0], p[i][1] - 8, p[i][0], p[i][1] - 2);
                        g.drawLine(p[i][0] - 2, p[i][1] - 6, p[i][0] + 2, p[i][1] - 6);
                        g.setColor(Color.WHITE);
                    }                
                }
                            
                // Cross
                if (!fired) {
                    g.drawLine((int)(p[currentPlayer][0] - 3 + p[currentPlayer][2] * 20 * Math.cos(p[currentPlayer][4] * Math.PI / 180.)), (int)(p[currentPlayer][1] - 5 + 20 * Math.sin(p[currentPlayer][4] * Math.PI / 180.)), (int)(p[currentPlayer][0] + 3 + 20 * p[currentPlayer][2] * Math.cos(p[currentPlayer][4] * Math.PI / 180.)), (int)(p[currentPlayer][1] - 5 + 20 * Math.sin(p[currentPlayer][4] * Math.PI / 180.)));
                    g.drawLine((int)(p[currentPlayer][0] + p[currentPlayer][2] * 20 * Math.cos(p[currentPlayer][4] * Math.PI / 180.)), (int)(p[currentPlayer][1] - 8 + 20 * Math.sin(p[currentPlayer][4] * Math.PI / 180.)), (int)(p[currentPlayer][0] + 20 * p[currentPlayer][2] * Math.cos(p[currentPlayer][4] * Math.PI / 180.)), (int)(p[currentPlayer][1] - 2 + 20 * Math.sin(p[currentPlayer][4] * Math.PI / 180.)));
                }
                
                // Power
                if (power != 0) {
                    g.setColor(new Color(0x9999FF));
                       g.fillRect(15, 30, 100, 10);
                       for (int i=0 ; i<power ; i++) {
                           g.setColor(new Color(0xFFC800 - (i<<9)));
                           g.drawLine(15+i, 29, 15+i, 41);
                       }
                }
                            
                if (demo) {
                    g.setColor(Color.WHITE);
                    //g.drawString("4K contest '06", 276, 255);                    
                    g.drawString("javaunlimited.net '06", 250, 255);
                    g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 144));
                    g.drawString("W4K", 160, 240);
                    g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 32));
                    g.setColor((computer & ((blink >> 3) % 2) == 0) ? Color.RED : Color.WHITE);
                    g.drawString("1 Player", 260, 305);
                    g.setColor((!computer & ((blink >> 3) % 2) == 0) ? Color.RED : Color.WHITE);
                    g.drawString("2 Players", 250, 340);
                }               
                                   
                // Water
                for (int i=2 ; i>=0 ; i--) {
                    g.setColor(new Color(0x0000FF + 0x666600 * i));
                    for (int j=0 ; j<641 ; j++) {
                        g.drawLine(j, (int)(Math.cos((double)(j+(blink<<(3-i))) / 10.) * (5. - i) + 442. - i * 3), j, 449);
                    }
                }                
                
                g.setColor(Color.WHITE);                
                
                // Ball
                if (ball[0] != -1.) {
                    noMoreAction = false;
                                    
                    for (int step=0 ; step<20 ; step++) {
                        ball[0] += ball[2] / 20;
                        ball[1] += ball[3] / 20;
                        
                        // Test collision avec les vers
                        for (int i=0 ; i<8 ; i++) {
                            if (i != currentPlayer) { 
                                noMoreAction |= ball[0] > p[i][0] - 1 
                                        && ball[0] < p[i][0] + 1
                                        && ball[1] > p[i][1] - 8
                                        && ball[1] < p[i][1];
                            }
                        }                    
                        
                        if (ball[0] < 5 || ball[0] + 5 > 641
                                || ball[1] < 0 || ball[1] + 5 > 449) { 
                            power = 0;
                            ball[0] = -1.;
                            noMoreAction = true;
                            break;
                        }
                        else if (m[(int)ball[0] + (int)ball[1] * 641] > 0xFF0000FF || noMoreAction) {
                            x = (int)ball[0];
                            y = (int)ball[1];
                            
                            // Gestion collision explosion + joueurs
                            for (int i=0 ; i<8 ; i++) {
                                int dist = ((p[i][0] - x) * (p[i][0] - x) + (p[i][1] - y) * (p[i][1] - y)); 
                                
                                if (dist < 400) {
                                    p[i][5] -= (400 - dist) >> 3;                            
                                    p[i][6] = noMoreAction ? (int)(ball[2] / 2) : ((p[i][0] - x) /2); // x ejection
                                    p[i][7] = (dist - 500) / 40; // y ejection
                                }
                            }
                            
                            power = 0;                    
                            ball[0] = -1.;
                            e = 400;                        
                            noMoreAction = false;
                            break;
                        }
                        
                        if ((!demo & !(computer & (currentPlayer % 2) == 1)) | power < 100) ball[3] += 0.02;
                    }
                    
                    g.fillOval((int)ball[0] - 1, (int)ball[1] - 1, 3, 3);
                }
                
                if (fired & noMoreAction) {
                    // Check game statut                
                    byte[] alive = new byte[2];
                    for (int i=0 ; i<8 ; i++) {
                        if (p[i][0] > 0 && p[i][5] > 0) {
                            alive[i%2]++;
                        }
                    }
                    
                    if (alive[0] * alive[1] == 0) {
                        if (demo) {
                            play = false;
                        }
                        else {
                            g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 32));
                            g.drawString((alive[0]==alive[1]?"Draw game!!!":(alive[0]==0?"Dalton":"Beatles")+" Win!"), 240, 240);
                            
                            victory = true;
                        }
                    }
                    else {
                        refPlayer++;
                        currentPlayer = refPlayer % 8;                    
                        while (p[currentPlayer][0] < 0 || p[currentPlayer][5] <= 0) {
                            currentPlayer += 2;
                            currentPlayer %= 8;
                        }
                        
                        fired = false;
                    }
                }
                            
                // Refresh
                try {
                    Thread.sleep(50 + time - System.currentTimeMillis());
                } catch (Exception ex) {}
    
                paint(this.getGraphics());
                
                blink++;
                
                if (demo & blink > 2400) play = false;
            }
        }
    }
    
    public void paint(Graphics g) {
        if (b != null) g.drawImage(b, 0, 0, this);
    }

    public void windowClosing(WindowEvent w) {
        System.exit(0);
    }
    
    public void windowOpened(WindowEvent w) {}
    public void windowClosed(WindowEvent w) {}
    public void windowIconified(WindowEvent w) {}
    public void windowDeiconified(WindowEvent w) {}
    public void windowActivated(WindowEvent w) {}
    public void windowDeactivated(WindowEvent w) {}

    public void keyPressed(KeyEvent e) {
        k = e.getKeyCode();
    }

    public void keyReleased(KeyEvent e) {
        if (k == e.getKeyCode()) {
            k = 0;
        }
    }

    public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        new W();
    }    
}
