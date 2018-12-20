import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JBrainTetris extends JTetris{

	
	//les attributs
	
	protected JCheckBox brainMode;
	
	
	//le constructeur
	
	public JBrainTetris(int pixels) {
		super(pixels);
	}
	
	
	//les méthodes
	
	/**
	 * Sets the enabling of the start/stop buttons based on the gameOn state.
	 */
	private void enableButtons() {
		startButton.setEnabled(!gameOn);
		stopButton.setEnabled(gameOn);
	}
	
	
	public JComponent createControlPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		
		// BRAIN
		panel.add(new JLabel("Brain:"));
		brainMode = new JCheckBox("Brain active");
		panel.add(brainMode);
		
		
		// ADVERSAIRE
		panel.add(new JLabel("Adversaire:"));
		brainMode = new JCheckBox("Brain active");
		panel.add(brainMode);
		
		// SLIDER
		

		// COUNT
		countLabel = new JLabel("0");
		panel.add(countLabel);

		// SCORE
		scoreLabel = new JLabel("0");
		panel.add(scoreLabel);

		// TIME
		timeLabel = new JLabel(" ");
		panel.add(timeLabel);

		panel.add(Box.createVerticalStrut(12));

		// START button
		startButton = new JButton("Start");
		panel.add(startButton);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});

		// STOP button
		stopButton = new JButton("Stop");
		panel.add(stopButton);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopGame();
			}
		});

		enableButtons();

		JPanel row = new JPanel();

		// SPEED slider
		panel.add(Box.createVerticalStrut(12));
		row.add(new JLabel("Speed:"));
		speed = new JSlider(0, 200, 75); // min, max, current
		speed.setPreferredSize(new Dimension(100, 15));

		updateTimer();
		row.add(speed);

		panel.add(row);
		speed.addChangeListener(new ChangeListener() {
			// when the slider changes, sync the timer to its value
			public void stateChanged(ChangeEvent e) {
				updateTimer();
			}
		});

		testButton = new JCheckBox("Test sequence");
		panel.add(testButton);

		return panel;
	}


	
	
	
	/*
	 * A simple brain function. Given a board, produce a number that rates that
	 * board position -- larger numbers for worse boards. This version just
	 * counts the height and the number of "holes" in the board.
	 */
	public double rateBoard(Board board) {
		final int width = board.getWidth();
		final int maxHeight = board.getMaxHeight();

		int sumHeight = 0;
		int holes = 0;

		// Count the holes, and sum up the heights
		for (int x = 0; x < width; x++) {
			final int colHeight = board.getColumnHeight(x);
			sumHeight += colHeight;

			int y = colHeight - 2; // addr of first possible hole

			while (y >= 0) {
				if (!board.getGrid(x, y)) {
					holes++;
				}
				y--;
			}
		}

		double avgHeight = ((double) sumHeight) / width;

		// Add up the counts to make an overall score
		// The weights, 8, 40, etc., are just made up numbers that appear to
		// work
		return (8 * maxHeight + 40 * avgHeight + 1.25 * holes);
	}
	
	
	
	
	/**
	 * Given a piece and a board, returns a move object that represents the best
	 * play for that piece, or returns null if no play is possible. See the
	 * Brain interface for details.
	 */
	public Brain.Move bestMove(Board board, Piece piece, int limitHeight) {
		Brain.Move move = new Brain.Move();
		board = new Board(board);

		double bestScore = 1e20;
		int bestX = 0;
		int bestY = 0;
		Piece bestPiece = null;
		Piece current = new Piece(piece);

		board.commit();

		// loop through all the rotations
		while (true) {
			final int yBound = limitHeight - current.getHeight() + 1;
			final int xBound = board.getWidth() - current.getWidth() + 1;

			// For current rotation, try all the possible columns
			for (int x = 0; x < xBound; x++) {
				int y = board.dropHeight(current, x);
				if (y > yBound) { // piece does stick up too far
					continue;
				}
				int result = board.place(current, x, y);
				if (result <= Board.PLACE_ROW_FILLED) {
					if (result == Board.PLACE_ROW_FILLED) {
						board.clearRows();
					}

					double score = rateBoard(board);
					if (score < bestScore) {
						bestScore = score;
						bestX = x;
						bestY = y;
						bestPiece = current;
					}
				}

				board.undo();
			}
			
			current = current.computeNextRotation();
			if (current.equals(piece)) {
				break; // break if back to original rotation
			}
		}

		if (bestPiece == null) {
			return null; // could not find a play at all!
		} else {
			move.x = bestX;
			move.y = bestY;
			move.piece = bestPiece;
			move.score = bestScore;
			return move;
		}
	}
	
	
	
	
	/**
	 * Selects the next piece to use using the random generator set in
	 * startGame().
	 */
	public Piece pickNextPiece(int slider, int limitHeight) {
		
		int rand = (int) (100 * random.nextDouble());
		
		
		//Choix aléatoire.
		if(rand < slider) {
		int pieceNum;

		pieceNum = (int) (pieces.length * random.nextDouble());

		Piece piece = pieces[pieceNum];

		return (piece);
		}
		
		
		//Choix de la pièce la plus désavantageuse pas l'adversaire.
		else {
			
			Brain.Move move = bestMove(this.board, this.currentPiece, limitHeight);
			return move.piece;
			
			
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DefaultBrain DBrain = new DefaultBrain();
	    JBrainTetris JBTetris = new JBrainTetris(16);
		JFrame frame = createFrame(JBTetris);
		frame.setVisible(true);
		
		

	}

}
