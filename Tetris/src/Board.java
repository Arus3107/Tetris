import java.util.Arrays;
import java.util.List;

/**
 * Represents a Tetris board -- essentially a 2-d grid of booleans. Supports
 * tetris pieces and row clearing. Has an "undo" feature that allows clients to
 * add and remove pieces efficiently. Does not do any drawing or have any idea
 * of pixels. Instead, just represents the abstract 2-d board.
 */
public class Board {

	private int width;
	private int height;

	protected int[] heights;
	protected int[] widths;

	protected boolean[][] grid;
	private boolean committed;

	protected int[] backupHeights;
	protected int[] backupWidths;
	protected boolean[][] backupGrid;

	/**
	 * Creates an empty board of the given width and height measured in blocks.
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;

		this.grid = new boolean[width][height];
		this.committed = true;
		// YOUR CODE HERE

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.grid[i][j] = false;
			}
		}

		this.heights = new int[width];
		this.widths = new int[height];

		this.backupHeights = new int[width];
		this.backupWidths = new int[height];
		this.backupGrid = new boolean[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.heights[i] = 0;
				this.widths[j] = 0;
				this.backupGrid[i][j] = false;
				this.backupHeights[i] = 0;
				this.backupWidths[j] = 0;
			}
		}
	}

	public Board(Board b) {
		this.committed = b.committed;
		this.grid = b.grid;
		this.height = b.height;
		this.width = b.width;
		this.widths = b.widths;
		this.heights = b.heights;
		this.backupGrid = b.backupGrid;
		this.backupHeights = b.backupHeights;
		this.backupWidths = b.backupWidths;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the max column height present in the board. For an empty board this
	 * is 0.
	 */
	public int getMaxHeight() {
		// YOUR CODE HERE
		int maxHeight = 0;
		for (int n : this.heights) {
			if (n > maxHeight) {
				maxHeight = n;
			}
		}

		return maxHeight;
	}

	/**
	 * Given a piece and an x, returns the y value where the piece would come to
	 * rest if it were dropped straight down at that x.
	 * 
	 * <p>
	 * Implementation: use the skirt and the col heights to compute this fast --
	 * O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {
		// YOUR CODE HERE

		boolean[][] empty = new boolean[this.width][this.width];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				empty[i][j] = false;
			}
		}

		if (this.grid.equals(empty)) {
			return 0;
		} else {
			int[] dist = new int[piece.getWidth()];
			for (int k = 0; k < piece.getWidth(); k++) {
				int top = piece.getSkirt().get(k);
				int bot = this.heights[x + k];
				dist[k] = Math.abs(top - bot);

			}

			int min = dist[0];
			int y = 0;
			for (int k = 1; k < dist.length; k++) {
				if (min > dist[k]) {
					min = dist[k];
					y = k;
				}
			}

			int diffSkirt = Math.abs(piece.getSkirt().get(y) - piece.getSkirt().get(0));

			return heights[x] - 1 - diffSkirt;
		}

	}

	/**
	 * Returns the height of the given column -- i.e. the y value of the highest
	 * block + 1. The height is 0 if the column contains no blocks.
	 */
	public int getColumnHeight(int x) {
		// YOUR CODE HERE
		return this.heights[x];
	}

	/**
	 * Returns the number of filled blocks in the given row.
	 */
	public int getRowWidth(int y) {
		// YOUR CODE HERE
		return this.widths[y];
	}

	/**
	 * Returns true if the given block is filled in the board. Blocks outside of the
	 * valid width/height area always return true.
	 */
	public boolean getGrid(int x, int y) {
		// YOUR CODE HERE
		if (x < this.width && y < this.height) {
			return this.grid[x][y];
		}
		return true;
	}

	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;

	/**
	 * Attempts to add the body of a piece to the board. Copies the piece blocks
	 * into the board grid. Returns PLACE_OK for a regular placement, or
	 * PLACE_ROW_FILLED for a regular placement that causes at least one row to be
	 * filled.
	 * 
	 * <p>
	 * Error cases: A placement may fail in two ways. First, if part of the piece
	 * may falls out of bounds of the board, PLACE_OUT_BOUNDS is returned. Or the
	 * placement may collide with existing blocks in the grid in which case
	 * PLACE_BAD is returned. In both error cases, the board may be left in an
	 * invalid state. The client can use undo(), to recover the valid, pre-place
	 * state.
	 */
	public int place(Piece piece, int x, int y) {
		if (!this.committed) {
			throw new RuntimeException("can only place object if the board has been commited");
		}
		// YOUR CODE HERE
		// Empêche deux appels successifs de la méthode.
		this.committed = false;

		// Placement de la pièce si possible.
		for (TPoint p : piece.getBody()) {
			if (x + p.x >= this.width || y + p.y >= this.height) {
				return PLACE_OUT_BOUNDS;
			} else if (this.getGrid(x + p.x, y + p.y)) {
				return PLACE_BAD;
			}

			else {
				this.grid[x + p.x][y + p.y] = true;
			}
		}
		// Mise à jour de heights et widths.
		for (int i = 0; i < this.width; i++) {
			loop: for (int j = this.height - 1; j >= 0; j--) {
				if (this.grid[i][j]) {
					this.heights[i] = j + 1;
					break loop;
				}
			}
		}

		for (int i = 0; i < this.height; i++) {
			int fill = 0;
			for (int j = 0; j < this.width; j++) {
				if (this.grid[j][i]) {
					fill++;
				}
			}

			this.widths[i] = fill;
		}

		// Détermine si au moins une ligne a été remplie.

		for (TPoint p : piece.getBody()) {
			int filled = 0;
			for (int i = 0; i < this.width; i++) {
				if (this.getGrid(i, y + p.y)) {
					filled++;
				}

			}
			if (filled == this.width) {
				return PLACE_ROW_FILLED;
			}
		}
		return PLACE_OK;
	}

	/**
	 * Deletes rows that are filled all the way across, moving things above down.
	 * Returns the number of rows cleared.
	 */
	public int clearRows() {
		// YOUR CODE HERE

		// Créé un tableau de booléens dont chaque case correspond à la valeur de vérité
		// de la proposition suivante: "la ligne est remplie" à cette ordonnée.

		boolean[] filled = new boolean[this.height];
		int clearedRows = 0;
		for (int i = 0; i < this.height; i++) {
			int counter = 0;
			for (int j = 0; j < this.width; j++) {
				if (this.grid[j][i]) {
					counter++;
				}
			}

			if (counter == this.width) {
				filled[i] = true;
			}
		}

		// Calcule le nombre de lignes qui seront supprimées.

		for (int i = 0; i < filled.length; i++) {
			if (filled[i]) {
				clearedRows++;
			}
		}

		/*
		 * Parcours chaque ligne et si une ligne est pleine alors chaque ligne à partir
		 * de celle-là et au-dessus prend les valeurs de celle juste au-dessus. La ligne
		 * la plus haute prend que des valeurs false car elle sera vide. Le tableau
		 * associant un numéro de ligne au fait qu'elle soit ou non remplie voit
		 * également ses éléments descendre à partir de la ligne remplie. Le parcours
		 * des lignes doit être décrémenter car deux lignes succéssives peuvent être
		 * remplies.
		 */

		boolean cleared = false;
		for (int i = 0; i < this.height; i++) {

			if (cleared) {
				i--;
				cleared = false;
			}
			if (filled[i]) {

				cleared = true;
				if (i < this.height - 1) {
					for (int k = i; k < this.height; k++) {
						if (k < this.height - 1) {
							filled[k] = filled[k + 1];
							for (int j = 0; j < this.width; j++) {

								this.grid[j][k] = this.grid[j][k + 1];
							}
						}

						else {
							filled[k] = false;
							for (int j = 0; j < this.width; j++) {
								this.grid[j][k] = false;

							}
						}

					}

				}

				else {
					for (int j = 0; j < this.width; j++) {
						this.grid[j][i] = false;
					}
				}
			}
		}

		// Mise à jour de heights et widths.

		for (int i = 0; i < this.width; i++) {
			loop: for (int j = this.height - 1; j >= 0; j--) {
				if (this.grid[i][j]) {
					this.heights[i] = j + 1;
					break loop;
				}
			}
		}

		for (int i = 0; i < this.height; i++) {
			int fill = 0;
			for (int j = 0; j < this.width; j++) {
				if (this.grid[j][i]) {
					fill++;
				}
			}

			this.widths[i] = fill;
		}

		return clearedRows;
	}

	/**
	 * Reverts the board to its state before up to one place and one clearRows(); If
	 * the conditions for undo() are not met, such as calling undo() twice in a row,
	 * then the second undo() does nothing. See the overview docs.
	 */
	public void undo() {
		// YOUR CODE HERE
		if (!this.committed) {
			this.committed = true;	
			for (int i=0; i<this.getWidth();i++) {
				this.grid[i] = Arrays.copyOf(this.backupGrid[i], this.getHeight());
			}
			this.heights = Arrays.copyOf(this.backupHeights, this.getWidth()); 
			this.widths = Arrays.copyOf(this.backupWidths, this.getHeight()); 
		}
	}

	/**
	 * Puts the board in the committed state.
	 */
	public void commit() {
		// YOUR CODE HERE

		if (!this.committed) {
			// Sauvegarde du Board

			/*
			 * this.backupGrid = this.grid.clone(); this.backupHeights =
			 * this.heights.clone(); this.backupWidths = this.widths.clone();
			 */
			for (int i=0; i<this.getWidth();i++) {
				this.backupGrid[i] = Arrays.copyOf(this.grid[i], this.getHeight());
			}
			this.backupHeights = Arrays.copyOf(this.heights, this.getWidth()); 
			this.backupWidths = Arrays.copyOf(this.widths, this.getHeight()); 

			this.committed = true;
		}
	}

	/*
	 * Renders the board state as a big String, suitable for printing. This is the
	 * sort of print-obj-state utility that can help see complex state change over
	 * time. (provided debugging utility)
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = this.height - 1; y >= 0; y--) {
			buff.append('|');
			for (int x = 0; x < this.width; x++) {
				if (getGrid(x, y))
					buff.append('+');
				else
					buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x = 0; x < this.width + 2; x++)
			buff.append('-');
		return buff.toString();
	}

	// Only for unit tests
	protected void updateWidthsHeights() {
		Arrays.fill(this.widths, 0);

		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (this.grid[i][j]) {
					this.widths[j] += 1;
					this.heights[i] = Math.max(j + 1, this.heights[i]);
				}
			}
		}
	}

}
