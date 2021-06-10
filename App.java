import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class App {
    private World world;
    private StageCanvas canvas;
	private Timer timer;
    private JButton btnPlay;
    private JLabel lblScore;
	private Deque<Integer> keyCodeQueue;

    private enum GameState { Playing, Paused, GameOver }
    private GameState gameState;
    
	public void start() {
        JFrame frame = new JFrame();
        frame.setTitle("PAC MAN");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //rootPane
        Container rootPane = frame.getContentPane();
        rootPane.setLayout(new BoxLayout(rootPane, BoxLayout.Y_AXIS));

		//Canvas
		gameState = GameState.Playing;
        world = new World(map1);
        canvas = new StageCanvas(world);
		canvas.setFocusable(true);
		keyCodeQueue = new CircularlyListDeque<>();
		canvas.addKeyListener(new KeyHandler());
		rootPane.add(canvas);

		//Score
		lblScore = new JLabel("Score: " + 0);

		//Play/Pause Button
		btnPlay = new JButton();
		btnPlay.setText("Pause");
		btnPlay.addActionListener(e->onPlayPause());

		//Score, Button box
		Box hButtons = Box.createHorizontalBox();
		hButtons.add(lblScore);
		hButtons.add(Box.createHorizontalStrut(20));
		hButtons.add(btnPlay);
		rootPane.add(Box.createVerticalStrut(10));
		rootPane.add(hButtons);
		rootPane.add(Box.createVerticalStrut(10));

		frame.pack();
		frame.setVisible(true);        

		//timer
		timer = new Timer(250/*ms*/, e -> onTimerTick());
		timer.start();

		//Background audio
		BackgroundAudio.play();
	}

	private void onTimerTick() {
		handleKey();
	    boolean gameOver = world.step();
	    if(gameOver) {
            timer.stop();
			BackgroundAudio.pause();
	        gameState = GameState.GameOver;
            btnPlay.setText("New Game");
			btnPlay.requestFocus();
	    }
	    else {
	        lblScore.setText("Score: " + world.getScore());
	        canvas.repaint();
			canvas.requestFocus();
	        //timeline.setRate(1.0 + world.getStageCount()/2.0);
	        int delay = 50 + 50 * Math.max(0, 5 - world.getStageCount());
	        timer.setDelay(delay);
	    }
	}	

	private void onPlayPause() {
	    switch(gameState) {
        case GameOver:
            world.init(map1);
	    case Paused:
            btnPlay.setText("Pause");
			timer.restart();
			BackgroundAudio.resume();
            gameState = GameState.Playing;
            break;
	    case Playing:
            btnPlay.setText("Play");
            timer.stop();
			BackgroundAudio.pause();
            gameState = GameState.Paused;
            break;
	    }
    }

	private class KeyHandler implements KeyListener {
		public void keyPressed(KeyEvent e) {
			keyCodeQueue.addLast(e.getKeyCode());
		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}

	private void handleKey() {
		if(keyCodeQueue.isEmpty())
			return;
		int keyCode = keyCodeQueue.removeFirst();

	    switch(keyCode) {
            case KeyEvent.VK_UP:    world.turnSouth(); break; 
            case KeyEvent.VK_DOWN:  world.turnNorth(); break; 
            case KeyEvent.VK_RIGHT: world.turnEast();  break; 
            case KeyEvent.VK_LEFT:  world.turnWest();  break;
            default:    break; 
        }
	}
	
	public static void main(String[] args) {
		new App().start();
	}

    private static final char[][] map1 = {
            "############################".toCharArray(),
            "#    *       ##       *    #".toCharArray(),
            "# #### ##### ## ##### #### #".toCharArray(),
            "# #### ##### ## ##### #### #".toCharArray(),
            "# #### ##### ## ##### #### #".toCharArray(),
            "#                          #".toCharArray(),
            "# #### ## ######## ## #### #".toCharArray(),
            "#      ##    ##    ##      #".toCharArray(),
            "###### ####  ##  #### ######".toCharArray(),
            "###### #            # ######".toCharArray(),
            "###### #            # ######".toCharArray(),
            "###### #  ###  ###  # ######".toCharArray(),
            "          #      #          ".toCharArray(),
            "          #      #          ".toCharArray(),
            "###### #  ########  # ######".toCharArray(),
            "###### #            # ######".toCharArray(),
            "###### #            # ######".toCharArray(),
            "###### ####  ##  #### ######".toCharArray(),
            "#      ##    ##    ##      #".toCharArray(),
            "# #### ## ######## ## #### #".toCharArray(),
            "#                          #".toCharArray(),
            "# #### ##### ## ##### #### #".toCharArray(),
            "# #### ##### ## ##### #### #".toCharArray(),
            "# #### ##### ## ##### #### #".toCharArray(),
            "#    *       ##       *    #".toCharArray(),
            "############################".toCharArray()
        };
}
