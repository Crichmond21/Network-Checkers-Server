package game.server;

public class gameBoard {
	private gamePiece[][] board = new gamePiece[8][8];
	
	gameBoard(){
		//Place red pieces on board
		for(int i = 0; i < 3; i ++) {
			for(int j = ((i+1)%2); j < 8; j += 2) {
				addPiece(new gamePiece("red"), i, j);
			}
		}
		
		for(int i = 5; i < 8; i++) {
			for(int j = ((i+1)%2); j < 8; j += 2) {
				addPiece(new gamePiece("black"), i, j);
			}
		}
		
		//Testing printing out pieces to make sure starting position is correct
		/*
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println("");
		}
		*/
	}
	
	/**
	 *  Adds pieces in specified row and collumn
	 * @param piece game piece to add
	 * @param row row to add in
	 * @param column column to add in
	 */
	private boolean addPiece(gamePiece piece, int row, int column) {
		if (board[row][column] == null) {
			board[row][column] = piece;
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Moves piece from initial position to destination. Throws illegal state exception if not valid move
	 * @param initialRow
	 * @param initialColumn
	 * @param destinationRow
	 * @param destinationColumn
	 */
	public void movePiece(int initialRow, int initialColumn, int destinationRow, int destinationColumn){
		if(validMove(initialRow, initialColumn, destinationRow, destinationColumn, board[initialRow][initialColumn].getKing())) {
			board[destinationRow][destinationColumn] = board[initialColumn][initialRow];
			board[initialColumn][initialRow] = null;
		}else {
			throw new IllegalStateException("Invalid Move");
		}
	}
	
	/**
	 * returns true if valid move
	 * @param initialRow
	 * @param initialColumn
	 * @param destinationRow
	 * @param destinationColumn
	 * @param king
	 * @return true if valid false if not
	 */
	private boolean validMove(int initialRow, int initialColumn, int destinationRow, int destinationColumn, boolean king) {
		//check that variables are in bounds
		if(initialRow < 0 || initialRow > 7 || initialColumn < 0 || initialColumn > 7 || destinationRow < 0 || destinationRow > 7 || destinationColumn < 0 || destinationColumn > 7) {
			return false;
		}
		
		//check for game piece in position
		if(board[initialRow][initialColumn] == null) {
			return false;
		}
		
		//check for empty space
		if(board[destinationRow][destinationColumn] != null) {
			return false;
		}
		
		//check if correctly moving sideways
		if(Math.abs(destinationRow - initialRow) != 1) {
			return false;
		}
		
		//check if correctly moving forward/backwards
		if(king) {
			if (Math.abs(destinationColumn - initialColumn) != 1) {
				return false;
			}
		}else {
			if(destinationColumn - initialColumn != 1) {
				return false;
			}
		}
		
		//TODO implement jump checking
		
		return true;
	}
	
	/**
	 * class to define game pieces, their king status, and the team they are on
	 * @author richmondc1
	 *
	 */
	private class gamePiece{
		private String team = null;
		private boolean king = false;
		private boolean initialized = false;
		
		/**
		 * Initializes game piece with String team
		 * @param team String of red or black to set which team the piece is on
		 */
		gamePiece(String team){
			this.team = team;
			initialized = true;
		}
		
		/**
		 * Gets team 
		 * @return team color as a string
		 */
		public String getTeam() {
			if(initialized) {
				return team;
			}else {
				throw new SecurityException("Game Piece not Initialized");
			}
		}
		
		/**
		 * Gets king status
		 * @return boolean status of king
		 */
		public boolean getKing() {
			if(initialized) {
				return king;
			}else {
				throw new SecurityException("Game Piece not Initialized");
			}
		}
		
		/**
		 * Sets piece to be king
		 */
		public void king() {
			king = true;
		}
		
		@Override
		public String toString() {
			return team;
		}
	}
}
