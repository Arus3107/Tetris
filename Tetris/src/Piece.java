import java.util.List;
import java.util.ArrayList;


/**
 * An immutable representation of a tetris piece in a particular rotation. Each
 * piece is defined by the blocks that make up its body.
 * 
 * Typical client code looks like...
 * 
 * <pre>
 * Piece pyra = new Piece(PYRAMID_STR); // Create piece from string
 * int width = pyra.getWidth(); // 3
 * Piece pyra2 = pyramid.computeNextRotation(); // get rotation
 * 
 * Piece[] pieces = Piece.getPieces(); // the array of all root pieces
 * </pre>
 */
public class Piece {

	// String constants for the standard 7 Tetris pieces
	public static final String STICK_STR = "0 0 0 1 0 2 0 3";
	public static final String L1_STR = "0 0 0 1 0 2 1 0";
	public static final String L2_STR = "0 0 1 0 1 1 1 2";
	public static final String S1_STR = "0 0 1 0 1 1 2 1";
	public static final String S2_STR = "0 1 1 1 1 0 2 0";
	public static final String SQUARE_STR = "0 0 0 1 1 0 1 1";
	public static final String PYRAMID_STR = "0 0 1 0 1 1 2 0";

	// Attributes
	private List<TPoint> body;
	private List<Integer> skirt;
	private int width;
	private int height;
	
	static private Piece[] pieces; // singleton static array of first rotations
	
	
	
	
	// Constructors

	/**
	 * Defines a new piece given a TPoint[] array of its body. Makes its own
	 * copy of the array and the TPoints inside it.
	 */
	public Piece(List<TPoint> points) {
	    // YOUR CODE HERE
		this.skirt = new ArrayList<>();
		this.body = points;
		
		
		this.width = 0;
		for(TPoint p : points) {
			
			if(p.x>this.width) {
				this.width = p.x;
			}
			
			if(p.y>this.height) {
				this.height = p.y;
			}
		}
		this.width++;
		this.height++;
		
		
		//Construction  de skirt
		
		for(int i = 0; i<4; i++) {
			int min = 3;
			int compteur = 0;
			for(TPoint p : points) {
				if(p.x == i) {
					compteur ++;
					if(min>p.y) {
						min = p.y;
						
					}
				}
			}
			if(compteur != 0) {
		this.skirt.add(min);
			}
		}	
		
	}
	
	/**
	 * Alternate constructor, takes a String with the x,y body points all
	 * separated by spaces, such as "0 0 1 0 2 0 1 1". (provided)
	 */
	public Piece(String points) {
		this(parsePoints(points));
	}

	public Piece(Piece piece) {
	    // YOUR CODE HERE
		
		
		this.body = piece.body;
		this.height = piece.height;
		this.width = piece.width;
		this.skirt = piece.skirt;
	}
	
	
	
	// Methods


	/**
	 * Given a string of x,y pairs ("0 0 0 1 0 2 1 0"), parses the points into a
	 * TPoint[] array. (Provided code)
	 */
	private static List<TPoint> parsePoints(String rep) {
	    // YOUR CODE HERE
		
		
		//Place les subString séparés par un espace dans un tableau puis créé les quatres TPoint à partir du tableau dont les éléments sont transformés en int.
		
		
		List<TPoint> points = new ArrayList<>();
		String couples[] = rep.split(" ");
		
		for(int i=0; i<8; i+=2) {
			TPoint p = new TPoint(Integer.parseInt(couples[i]),Integer.parseInt(couples[i+1]));
			points.add(p);
		}
		
		
	    return points;
	}
	
	/**
	 * Returns the width of the piece measured in blocks.
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the piece measured in blocks.
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns a reference to the piece's body. The caller should not modify this
	 * list.
	 */
	public List<TPoint> getBody() {
		return this.body;
	}

	/**
	 * Returns a reference to the piece's skirt. For each x value across the
	 * piece, the skirt gives the lowest y value in the body. This is useful for
	 * computing where the piece will land. The caller should not modify this
	 * list.
	 */
	public List<Integer> getSkirt() {
		return this.skirt;
	}
	
	/**
	 * Returns a new piece that is 90 degrees counter-clockwise rotated from the
	 * receiver.
	 */
	public Piece computeNextRotation() {
	    // YOUR CODE HERE
		List<Integer> square = new ArrayList<>();
		square.add(0);
		square.add(0);
		
		//Le cas du carré est trivial, il suffit de retourner le carré.
		if(this.body.toString() == SQUARE_STR) {
			return this;
		}
		
		//Dans les autres cas, il faut faire une rotation.
		else {
			
			//Création d'une matrice 4*4 dont les éléments valent true si la case contient un TPoint de la pièce et faux sinon.
			
			boolean[][] matrix = new boolean[4][4];
			for(int i=0; i<4; i++) {
				for(int j=0; j<4; j++) {
					TPoint p = new TPoint(j,i);
					matrix[3-i][j] = this.body.contains(p);
					
				}
			}
			
			
			//Rotation de la matrice de 90° vers la gauche par permutation circulaire.
			
			for(int k=0; k<3; k++) {
				boolean temp = matrix[k][0];
				matrix[k][0] = matrix[0][3-k];
				matrix[0][3-k] = matrix[3-k][3];
				matrix[3-k][3] = matrix[3][k];
				matrix[3][k] = temp;
			}
			
			boolean temp1 = matrix[1][1];
			matrix[1][1] = matrix[1][2];
			matrix[1][2] = matrix[2][2];
			matrix[2][2] = matrix[2][1];
			matrix[2][1] = temp1;
		
		
		List<TPoint> points = new ArrayList<>();
		
		
		//"Suppression" des colonnes vides sur la gauche de la nouvelle matrice pour repasser dans le système de coordonnées de la pièce.
		
		int min=4;
		for(int m=0; m<4; m++) {
			for(int n=0; n<4; n++) {
				if(matrix[m][n] && min>n) {
						min=n;
				}
			}
		}
		
		/*Exctraction des coordonnéées des points de la pièce à partir de la matrice. Si le point de la matrice vaut true alors on repasse 
		  dans le système de la pièce et on ajoute le nouveau point dans une liste qui, une fois complète, constituera le body de la pièce après rotation. */
		
		for(int m=0; m<4; m++) {
			for(int n=min; n<4; n++) {
				if(matrix[m][n]) {
					TPoint p = new TPoint(n-min,3-m);
					points.add(p);
				}
			}
		}
		
		//Création de la nouvelle pièce à partir des TPoint déterminés.
		
		Piece P = new Piece(points);
		return P;
		}
		
	    
	}

	/**
	 * Returns true if two pieces are the same -- their bodies contain the same
	 * points. Interestingly, this is not the same as having exactly the same
	 * body arrays, since the points may not be in the same order in the bodies.
	 * Used internally to detect if two rotations are effectively the same.
	 */
	public boolean equals(Object obj) {
	    // YOUR CODE HERE
	    int n=0;
	    
	    /*Si l'objet est une pièce, pour chaque point du body de la pièce en paramètre incrémente de 1 un compteur s'il corresond 
	     * à un point du body de la pièce sur laquelle s'applique la méthode. Renvoie vrai si le compteur vaut 4.
	    */
	    if(obj instanceof Piece) {
	    	for(TPoint points : ((Piece) obj).body){
	    		for(TPoint points1 : this.body) {
	    			if(points.equals(points1)) {
	    				n++;
	    			}
	    		}
	    	}
	    	return n == 4;
	    }
	    
	    
	    //Si l'objet est un String, applique la première partie de la méthode à la pièce créée à partir du String.
	    
	    if(obj instanceof String) {
	    	List<TPoint> Points1 = parsePoints((String)obj);
	    	Piece Points = new Piece(Points1);
	    	
	    	//List<TPoint> Points1 = parsePoints(this.toString());
	    	
	    	return this.equals(Points);
	    	
	    }
	    
	    return false;
	}

	public String toString() {
	    // YOUR CODE HERE
		
		
		//Renvoie la concaténation des abscisses et ordonnées de chaque points séparés par un espace moins le dernier espace.
		
		String body = "";
		for(TPoint p : this.body) {
			body = body + p.x + " " + p.y + " ";
		}
		body = body.substring(0,15);
		// System.out.println(body);
		return body;
	}

	/**
	 * Returns an array containing the first rotation of each of the 7 standard
	 * tetris pieces in the order STICK, L1, L2, S1, S2, SQUARE, PYRAMID. The
	 * next (counterclockwise) rotation can be obtained from each piece with the
	 * {@link #fastRotation()} message. In this way, the client can iterate
	 * through all the rotations until eventually getting back to the first
	 * rotation. (provided code)
	 */
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces == null) {
			Piece.pieces = new Piece[] { 
					new Piece(STICK_STR), 
					new Piece(L1_STR),
					new Piece(L2_STR), 
					new Piece(S1_STR),
					new Piece(S2_STR),
					new Piece(SQUARE_STR),
					new Piece(PYRAMID_STR)};
		}

		return Piece.pieces;
	}

}
