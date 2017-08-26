import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameOfLife {
    boolean setup = false;
    Pane pane;
    static ArrayList<ArrayList<Cell>> cells;
    private final static int CELL_X_SIZE = 30;
    private final static int CELL_Y_SIZE = 30;
    // indexed by number of living cells in a row - 1
    private static int[] frequencies = new int[1920 / CELL_X_SIZE];

    public static void main(String[] args) {
	GameOfLife gol = new GameOfLife();
	gol.setupFrame();
	gol.setupFrequencies();
	Timer timer = new Timer();
	timer.schedule(new PlaySound(), 0, 400);

    }

    private void setupFrame() {
	JFrame frame = new JFrame();
	frame.setSize(1920, 1080);
	frame.setUndecorated(true);
	frame.setResizable(false);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	pane = new Pane();

	cells = pane.genCells();
	pane.selectSetup(cells);

	KeyList kl = new KeyList(pane, cells);
	frame.addKeyListener(kl);
	frame.add(pane);
	frame.setVisible(true);
    }

    private void setupFrequencies() {
	int maxLiveCountRow = 1920 / CELL_X_SIZE;
	for (int i = 0; i < maxLiveCountRow; i++) {
	    boolean valid = false;
	    int freq = 200 + (3000 * i / maxLiveCountRow);
	    int pos = 0;
	    while (!valid) {
		pos = new Random().nextInt(maxLiveCountRow);
		if (frequencies[pos] == 0) {
		    valid = true;
		}
	    }
	    frequencies[pos] = freq;
	}
	for (int x : frequencies) {
	    System.out.println(x);
	}
    }

    public class Cell {
	Rectangle rect;
	State origState;
	State finalState;
    }

    public enum State {
	LIVING, DEAD
    }

    public class KeyList extends KeyAdapter {
	Pane pane;
	ArrayList<ArrayList<Cell>> cells;

	public KeyList(Pane pane, ArrayList<ArrayList<Cell>> cells) {
	    this.pane = pane;
	    this.cells = cells;
	}

	@Override
	public void keyPressed(KeyEvent e) {
	    // Player has finished their design
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		setup = true;
	    }
	    // Restarts so that they can re-choose the initial living cells
	    if (e.getKeyCode() == KeyEvent.VK_R) {
		setup = false;
		pane.selectSetup(cells);
	    }
	    // Clears the board of any living cells
	    if (e.getKeyCode() == KeyEvent.VK_C) {
		for (ArrayList<Cell> cell : cells) {
		    for (Cell c : cell) {
			c.origState = State.DEAD;
			c.finalState = State.DEAD;
		    }
		}
	    }
	}
    }

    public class Pane extends JPanel {
	private static final long serialVersionUID = 1L;
	ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();

	public void selectSetup(final ArrayList<ArrayList<Cell>> cells) {
	    // Makes sure that all the cells are set to dead initially
	    for (ArrayList<Cell> cell : cells) {
		for (Cell c : cell) {
		    c.origState = State.DEAD;
		    c.finalState = State.DEAD;
		}
	    }

	    // Listens for clicks by the user for the initial setup
	    // So that they can choose the cells that start living
	    addMouseListener(new MouseAdapter() {

		public void mousePressed(MouseEvent e) {
		    if (!setup) {
			// Finds the column and row from the click
			int column = (int) Math.floor(e.getX() / CELL_X_SIZE);
			int row = (int) Math.floor(e.getY() / CELL_Y_SIZE);

			// Sets the selected cell to living
			cells.get(row).get(column).origState = State.LIVING;
		    }
		}

	    });

	}

	public int numLivingNeighbours(ArrayList<ArrayList<Cell>> cells, ArrayList<Cell> cell, Cell c) {

	    int num = 0;
	    int row = cells.indexOf(cell);
	    int column = cell.indexOf(c);

	    // Finds the living cells directly around the cell in question
	    for (int y = row - 1; y <= row + 1; y++) {
		for (int x = column - 1; x <= column + 1; x++) {
		    try {
			if (y != row || x != column) {
			    if (cells.get(y).get(x).origState == State.LIVING) {
				num++;
			    }
			}
		    } catch (Exception e) {
		    }

		}
	    }

	    // If the cell is at the top or bottom checks for living cells as if
	    // the grid is wrapped
	    if (row == 0 || row == cells.size() - 1) {
		int y = row == 0 ? cells.size() - 1 : 0;
		for (int x = column - 1; x <= column + 1; x++) {
		    try {
			if (cells.get(y).get(x).origState == State.LIVING) {
			    num++;
			}
		    } catch (Exception e) {
		    }
		}
	    }

	    // If the cell is at the left or right hand side checks for living
	    // cells as if the grid is wrapped
	    if (column == 0 || column == cells.get(0).size() - 1) {
		int x = column == 0 ? cells.get(0).size() - 1 : 0;
		for (int y = row - 1; y <= row + 1; y++) {
		    try {
			if (cells.get(y).get(x).origState == State.LIVING) {
			    num++;
			}
		    } catch (Exception e) {
		    }
		}
	    }
	    // If the cell is in one of the corners
	    if ((row == 0 || row == cells.size() - 1) && (column == 0 || column == cells.get(0).size() - 1)) {
		try {
		    if (cells.get(row == 0 ? cells.size() - 1 : 0)
			    .get(column == 0 ? cells.get(0).size() - 1 : 0).origState == State.LIVING) {
			num++;
		    }
		} catch (Exception e) {
		}
	    }
	    if (num > 8) {
		System.out.println("NUM: " + num);
	    }
	    return num;
	}

	public void playGame(ArrayList<ArrayList<Cell>> cells) {
	    // NOTE: origState and finalState are used so that the all the cells
	    // in the grid can be analysed simultaneously
	    for (ArrayList<Cell> cell : cells) {
		for (Cell c : cell) {
		    int living = numLivingNeighbours(cells, cell, c);
		    // Decides whether the cell should live or die based on the
		    // rules of the game
		    if (living < 2 && c.origState == State.LIVING) {
			c.finalState = State.DEAD;
		    }
		    if ((living == 2 || living == 3) && c.origState == State.LIVING) {
			c.finalState = State.LIVING;
		    }
		    if (living > 3 && c.origState == State.LIVING) {
			c.finalState = State.DEAD;
		    }
		    if (living == 3 && c.origState == State.DEAD) {
			c.finalState = State.LIVING;
		    }
		}
	    }

	    // Sets all the origStates to the finalStates
	    for (ArrayList<Cell> cell : cells) {
		for (Cell c : cell) {
		    c.origState = c.finalState;
		}
	    }

	}

	public ArrayList<ArrayList<Cell>> genCells() {

	    // Creates the grid by generating all the rectangles using the sizes
	    // specified
	    for (int yPos = 0; yPos <= 1080 - CELL_Y_SIZE; yPos += CELL_Y_SIZE) {
		ArrayList<Cell> temp = new ArrayList<Cell>();
		for (int xPos = 0; xPos <= 1920 - CELL_X_SIZE; xPos += CELL_X_SIZE) {

		    Rectangle rectangle = new Rectangle(xPos, yPos, CELL_X_SIZE, CELL_Y_SIZE);
		    Cell cell = new Cell();
		    cell.rect = rectangle;
		    cell.origState = State.DEAD;
		    temp.add(cell);
		}
		cells.add(temp);
	    }

	    return cells;

	}

	@Override
	public void paint(Graphics g) {
	    super.paintComponent(g);

	    Graphics2D g2d = (Graphics2D) g.create();

	    // Paints each cell, if living paints black, if dead paints white
	    for (ArrayList<Cell> cell : cells) {
		for (Cell c : cell) {
		    if (c.origState == State.LIVING) {
			g2d.setColor(Color.BLACK);
		    } else {
			g2d.setColor(Color.WHITE);
		    }

		    g2d.fill(c.rect);
		    g2d.setColor(Color.BLACK);
		    g2d.draw(c.rect);
		}

	    }

	    // Only if the game is setup it begins to play the game
	    if (setup) {
		playGame(cells);
	    }
	    repaint();

	}

    }

    static class PlaySound extends TimerTask {

	// Alternative frequency generation
	public void run() {
	    for (ArrayList<Cell> cell : cells) {
		int liveCount = 0;
		for (Cell c : cell) {
		    if (c.finalState == State.LIVING) {
			liveCount++;
		    }
		}
		if (liveCount != 0) {
		    int maxLiveCountRow = 1920 / CELL_X_SIZE;
		    int freq = 200 + (20000 * liveCount / maxLiveCountRow);
		    // int freq = frequencies[liveCount - 1];
		    new Tone(freq, 0.1);
		}

	    }

	}

    }
}
