import java.awt.*;
import java.awt.image.*;

import java.io.*;

import javax.imageio.*;
import javax.swing.*;

public class StageCanvas extends JPanel {
    private final int CELL_W = 30;  //multiple of 6
    private final int CELL_H = 30;
    //double buffering: viewing image, drawing image
    private BufferedImage vImage, dImage;
    
    private static final String IMG_NAME[][] = new String[][] {
        new String[] { "BlinkyS.png", "PinkyS.png", "InkyS.png", "ClydeS.png", "PacManS.png" },
        new String[] { "BlinkyE.png", "PinkyE.png", "InkyE.png", "ClydeE.png", "PacManE.png" },
        new String[] { "BlinkyN.png", "PinkyN.png", "InkyN.png", "ClydeN.png", "PacManN.png" },
        new String[] { "BlinkyW.png", "PinkyW.png", "InkyW.png", "ClydeW.png", "PacManW.png" },
        new String[] { "BlinkyN.png", "PinkyN.png", "InkyN.png", "ClydeN.png", "PacMan.png" },
        new String[] { "BlinkyW.png", "PinkyW.png", "InkyW.png", "ClydeW.png", "PacMan.png" },
        new String[] { "BlinkyS.png", "PinkyS.png", "InkyS.png", "ClydeS.png", "PacMan.png" },
        new String[] { "BlinkyE.png", "PinkyE.png", "InkyE.png", "ClydeE.png", "PacMan.png" },
        //blue mode
        new String[] { "BlueS.png", "BlueS.png", "BlueS.png", "BlueS.png", "PacManS.png" },
        new String[] { "BlueE.png", "BlueE.png", "BlueE.png", "BlueE.png", "PacManE.png" },
        new String[] { "BlueN.png", "BlueN.png", "BlueN.png", "BlueN.png", "PacManN.png" },
        new String[] { "BlueW.png", "BlueW.png", "BlueW.png", "BlueW.png", "PacManW.png" },
        new String[] { "BlueN.png", "BlueN.png", "BlueN.png", "BlueN.png", "PacMan.png" },
        new String[] { "BlueW.png", "BlueW.png", "BlueW.png", "BlueW.png", "PacMan.png" },
        new String[] { "BlueS.png", "BlueS.png", "BlueS.png", "BlueS.png", "PacMan.png" },
        new String[] { "BlueE.png", "BlueE.png", "BlueE.png", "BlueE.png", "PacMan.png" },
    };
    
    private World world;
    private Image imageSet[][][];
    private Image image[][];
    private Timer timer1, timer2;
    
    public StageCanvas(World world) {
        this.world = world;

        //preferred size
        int h = CELL_H * World.height;
        int w = CELL_W * World.width;
        this.setPreferredSize(new Dimension(w, h));

        //double buffering
        this.vImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR); 
        this.dImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

        imageSet = new Image[4][4][IMG_NAME[0].length];
        try {
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < IMG_NAME[0].length; j++) {
                    imageSet[0][i][j] = ImageIO.read(StageCanvas.class.getResource(IMG_NAME[i   ][j]));
                    imageSet[1][i][j] = ImageIO.read(StageCanvas.class.getResource(IMG_NAME[i+4 ][j]));
                    imageSet[2][i][j] = ImageIO.read(StageCanvas.class.getResource(IMG_NAME[i+8 ][j]));
                    imageSet[3][i][j] = ImageIO.read(StageCanvas.class.getResource(IMG_NAME[i+12][j]));
                }
            }

            image = imageSet[0];
        } catch(IOException e) {
            e.printStackTrace();
        }
       
        timer1 = new Timer(100, e -> {
            image = world.getBlueMode() ? imageSet[2] : imageSet[0];
            repaint();
        });
        timer1.start();

        timer2 = new Timer(500, e -> {
            image = world.getBlueMode() ? imageSet[3] : imageSet[1];
            repaint();
        });
        timer2.start();
    }

    public void repaint() {
        Graphics gc = this.getGraphics();
        if(gc == null)
            return;

        Graphics2D g = (Graphics2D)dImage.getGraphics();    
        int width  = dImage.getWidth();
        int height = dImage.getHeight();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width,  height);

        for(int y = 0; y < World.height; y++)
            for(int x = 0; x < World.width; x++)
                drawWall(g, new Pos(x, y), Color.BLUE, 6);                
                
        for(int y = 0; y < World.height; y++) {
            for(int x = 0; x < World.width; x++) {
                Pos pos = new Pos(x, y);
                drawDot(g, pos);
                drawWall(g, pos, Color.CYAN, 1);                
            }
        }
        
        Pos posPacMan = world.getPacMan().getPos(); 
        drawImage(g, posPacMan, world.getPacMan().getDir(), World.NUM_GHOST);
        
        for(int i = 0; i < World.NUM_GHOST; i++) {
            Pos posGhost = world.getGhost(i).getPos();
            //BlendMode bm = posPacMan.equals(posGhost) ? BlendMode.ADD : BlendMode.SRC_OVER;
            int bm = 0;
            drawImage(g, world.getGhost(i).getPos(), world.getGhost(i).getDir(), i, bm);
        }

        gc.drawImage(dImage, 0, 0, null);
        //double buffering
        BufferedImage t = vImage; vImage = dImage; dImage = t;        
    }
    
    private void drawDot(Graphics g, Pos pos) {
        if(world.getState(pos.x, pos.y) == World.CellState.Dot) {
            g.setColor(Color.GRAY);
            g.fillOval(pos.x * CELL_W + CELL_W/2 - 3,  pos.y * CELL_H + CELL_H/2 - 3,  6, 6);
        }
        else if(world.getState(pos.x, pos.y) == World.CellState.PowerPellet) {
            g.setColor(Color.RED);
            g.fillOval(pos.x * CELL_W + CELL_W/2 - 8,  pos.y * CELL_H + CELL_H/2 - 8,  16, 16);
        }
    }
    
    private void drawImage(Graphics g, Pos pos, int dir, int imgIndex) {
        drawImage(g, pos, dir, imgIndex, 0/*blend mode*/);
    }
    private void drawImage(Graphics g, Pos pos, int dir, int imgIndex, int bm) {
        Graphics2D g2 = (Graphics2D) g;
        //g.setGlobalBlendMode(bm);
        g2.drawImage(image[dir][imgIndex],  pos.x * CELL_W,  pos.y * CELL_H, CELL_W, CELL_H, null);
        //g.drawImage(image[dir][imgIndex],  pos.x * CELL_W,  pos.y * CELL_H, CELL_W, CELL_H);
        //g.setGlobalBlendMode(BlendMode.SRC_OVER);
        //g.setGlobalAlpha(1.0);
    }
    
    private boolean validX(int x) { return 0 <= x && x < World.width; }
    private boolean validY(int y) { return 0 <= y && y < World.height; }
    private void drawWall(Graphics g, Pos pos) {
        //drawWall(g, pos, Color.BLUE, 5);
        drawWall(g, pos, Color.CYAN, 1);
    }
    private void drawWall(Graphics g, Pos pos, Color clr, int stroke) {
        Graphics2D g2 = (Graphics2D) g;
        if(world.getState(pos.x, pos.y) != World.CellState.Wall)
           return;
        
        int[] dx = new int[] { 1, -1, -1,  1};
        int[] dy = new int[] {-1, -1,  1,  1};

        g.setColor(clr);
        g2.setStroke(new BasicStroke(stroke));
        for(int i = 0; i < 4; i++) {
            int nx = pos.x + dx[i];
            int ny = pos.y + dy[i];
            
            if(validY(ny) && world.getState(pos.x, ny) == World.CellState.Wall &&     //outside arc
               validX(nx) && world.getState(nx, pos.y) == World.CellState.Wall ) {
                if(world.getState(nx, ny) != World.CellState.Wall) {
                    int cx = pos.x * CELL_W + CELL_W/3 + dx[i] * CELL_W/2;
                    int cy = pos.y * CELL_H + CELL_H/3 + dy[i] * CELL_H/2;
                    g.drawArc(cx, cy, CELL_W/3, CELL_H/3, i * 90 + 180, 90);
                }
            }
            else if((!validY(ny) || world.getState(pos.x, ny) != World.CellState.Wall) && //inside arc
                    (!validX(nx) || world.getState(nx, pos.y) != World.CellState.Wall) ) {
                int cx = pos.x * CELL_W + CELL_W/3 + dx[i] * CELL_W/6;
                int cy = pos.y * CELL_H + CELL_H/3 + dy[i] * CELL_H/6;
                g.drawArc(cx, cy, CELL_W/3, CELL_H/3, i * 90, 90);
            }
            else if((!validY(ny) || world.getState(pos.x, ny) == World.CellState.Wall) && //v line
                    (!validX(nx) || world.getState(nx, pos.y) != World.CellState.Wall) ) {
                int x  = pos.x * CELL_W + CELL_W/2 + dx[i] * CELL_W/3;
                int y1 = pos.y * CELL_H + CELL_H/2 + dy[i] * CELL_H/2;
                int y2 = pos.y * CELL_H + CELL_H/2 + dy[i] * CELL_H/6;
                g.drawLine(x, y1, x, y2);
            }
            else if((!validY(ny) || world.getState(pos.x, ny) != World.CellState.Wall) && //h line
                    (!validX(nx) || world.getState(nx, pos.y) == World.CellState.Wall) ) {
                int y  = pos.y * CELL_H + CELL_H/2 + dy[i] * CELL_H/3;
                int x1 = pos.x * CELL_W + CELL_W/2 + dx[i] * CELL_W/2;
                int x2 = pos.x * CELL_W + CELL_W/2 + dx[i] * CELL_W/6;
                g.drawLine(x1, y, x2, y);
            }
            if(!validX(nx) || world.getState(nx, pos.y) != World.CellState.Wall) {
                int x  = pos.x * CELL_W + CELL_W/2 + dx[i] * CELL_W/3;
                int y1 = pos.y * CELL_H + CELL_H/2 - CELL_H/6;
                int y2 = pos.y * CELL_H + CELL_H/2 + CELL_H/6;
                g.drawLine(x, y1, x, y2);
            }
            if(!validY(ny) || world.getState(pos.x, ny) != World.CellState.Wall) {
                int y  = pos.y * CELL_H + CELL_H/2 + dy[i] * CELL_H/3;
                int x1 = pos.x * CELL_W + CELL_W/2 - CELL_W/6;
                int x2 = pos.x * CELL_W + CELL_W/2 + CELL_W/6;
                g.drawLine(x1, y, x2, y);
            }
        }
    }
}
