package game.server;

import java.util.ArrayList;
import java.util.Iterator;

public class GameBoard {
	private gamePiece[][] board = new gamePiece[8][8];

	GameBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = (i + 1) % 2; j < 8; j += 2) {
				addPiece(new gamePiece("blue", i, j), i, j);
			}
		}

		for (int i = 5; i < 8; i++) {
			for (int j = (i + 1) % 2; j < 8; j += 2) {
				addPiece(new gamePiece("red", i, j), i, j);
			}
		}
	}

	private boolean addPiece(gamePiece piece, int row, int column) {
		if (board[row][column] == null) {
			board[row][column] = piece;
			return true;
		}
		return false;
	}

	public String getTeam(int row, int column) {
		return board[row][column].getTeam();
	}

	public void movePiece(int initialRow, int initialColumn, int destinationRow, int destinationColumn) {
		String str;

		int forward = 0;

		
		//TODO: ADD SWITCH STATEMENT FOR TEAM FORWARD DIR
		/**
		 * switch ((str = getTeam(initialRow, initialColumn).toLowerCase()).hashCode())
		 * {case 112785: if (str.equals("red")) break; break; case 3027034: if
		 * (!str.equals("blue")) { break label78; forward = -1; break label81; } else {
		 * forward = 1; } break; } label78: int forward = 0;
		 */

		ArrayList<Integer> jump = jumpPosible(board[initialRow][initialColumn].getTeam().toLowerCase());

		if (jump.size() > 0) {
			for (int i = 0; i < jump.size(); i += 4) {
				if ((destinationRow == ((Integer) jump.get(i)).intValue())
						&& (destinationColumn == ((Integer) jump.get(i + 1)).intValue())
						&& (initialRow == ((Integer) jump.get(i + 2)).intValue())
						&& (initialColumn == ((Integer) jump.get(i + 3)).intValue())) {
					gamePiece temp = board[initialRow][initialColumn];
					board[destinationRow][destinationColumn] = temp;
					temp.setPosition(destinationRow, destinationColumn);
					board[initialRow][initialColumn] = null;
					return;
				}
			}

			throw new IllegalStateException("Invalid Move");
		}
		if (jump.size() <= 0) {
			if (validMove(initialRow, initialColumn, destinationRow, destinationColumn,
					board[initialRow][initialColumn].getKing(), forward)) {
				gamePiece temp = board[initialRow][initialColumn];
				board[destinationRow][destinationColumn] = temp;
				temp.setPosition(destinationRow, destinationColumn);
				board[initialRow][initialColumn] = null;
				return;
			}

			throw new IllegalStateException("Invalid Move");
		}

		if ((forward == 1) && (destinationRow == 8)) {
			board[destinationRow][destinationColumn].king();
		}

		if ((forward == -1) && (destinationRow == 0)) {
			board[destinationRow][destinationColumn].king();
		}
	}

	private boolean validMove(int initialRow, int initialColumn, int destinationRow, int destinationColumn,
			boolean king, int forward) {
		if ((initialRow < 0) || (initialRow > 7) || (initialColumn < 0) || (initialColumn > 7) || (destinationRow < 0)
				|| (destinationRow > 7) || (destinationColumn < 0) || (destinationColumn > 7)) {
			return false;
		}

		if (board[initialRow][initialColumn] == null) {
			return false;
		}

		if (board[destinationRow][destinationColumn] != null) {
			System.out.println(board[destinationRow][destinationColumn]);
			return false;
		}

		if (Math.abs(destinationColumn - initialColumn) != 1) {
			return false;
		}

		if (king) {
			if (Math.abs(destinationRow - initialRow) != 1) {
				return false;
			}
		} else if (destinationRow - initialRow != forward) {
			return false;
		}

		return true;
	}

	private ArrayList<Integer> jumpPosible(String team) {
		ArrayList<gamePiece> teamPieces = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((board[i][j] != null) && (board[i][j].getTeam().equals(team))) {
					teamPieces.add(board[i][j]);
				}
			}
		}

		Iterator<gamePiece> teamPieceIterator = teamPieces.iterator();

		int dir;

		if (team.equals("red")) {
			dir = 1;
		} else {
			dir = -1;
		}

		Integer longestJump = Integer.valueOf(0);
		ArrayList<Integer> jumpPos = new ArrayList<>();

		while (teamPieceIterator.hasNext()) {
			gamePiece currentPiece = (gamePiece) teamPieceIterator.next();
			int[] pos = currentPiece.getPosition();
			checkJumpNextPieces(dir, pos, currentPiece.getTeam(), longestJump, 0, jumpPos, pos);
		}

		return jumpPos;
	}

	private void checkJumpNextPieces(int dir, int[] pos, String team, Integer longestJump, int recursiveJumps,
			ArrayList<Integer> finalJumpPos, int[] startingPos) {
		if ((pos[0] + dir >= 0) && (pos[0] + dir < 8) && (pos[1] + 1 >= 0) && (pos[1] + 1 < 8)
				&& (board[(pos[0] + dir)][(pos[1] + 1)] != null)
				&& (!board[(pos[0] + dir)][(pos[1] + 1)].getTeam().equals(team)) && (pos[0] + 2 * dir >= 0)
				&& (pos[1] + 2 >= 0) && (pos[0] + 2 * dir < 8) && (pos[1] + 2 < 8)
				&& (board[(pos[0] + 2 * dir)][(pos[1] + 2)] == null)) {
			int[] newPos = { pos[0] + 2 * dir, pos[1] + 2 };
			recursiveJumps++;

			if (recursiveJumps == longestJump.intValue()) {
				finalJumpPos.add(Integer.valueOf(newPos[0]));
				finalJumpPos.add(Integer.valueOf(newPos[1]));
				finalJumpPos.add(Integer.valueOf(startingPos[0]));
				finalJumpPos.add(Integer.valueOf(startingPos[1]));
			} else if (recursiveJumps > longestJump.intValue()) {
				longestJump = Integer.valueOf(longestJump.intValue() + 1);
				finalJumpPos.clear();
				finalJumpPos.add(Integer.valueOf(newPos[0]));
				finalJumpPos.add(Integer.valueOf(newPos[1]));
				finalJumpPos.add(Integer.valueOf(startingPos[0]));
				finalJumpPos.add(Integer.valueOf(startingPos[1]));
			}

			checkJumpNextPieces(dir, newPos, team, longestJump, recursiveJumps, finalJumpPos, startingPos);
		}

		if ((pos[0] + dir >= 0) && (pos[0] + dir < 8) && (pos[1] - 1 >= 0) && (pos[1] - 1 < 8)
				&& (board[(pos[0] + dir)][(pos[1] - 1)] != null)
				&& (!board[(pos[0] + dir)][(pos[1] - 1)].getTeam().equals(team)) && (pos[0] + 2 * dir >= 0)
				&& (pos[1] - 2 >= 0) && (pos[0] + 2 * dir < 8) && (pos[1] + 2 < 8)
				&& (board[(pos[0] + 2 * dir)][(pos[1] - 2)] == null)) {
			int[] newPos = { pos[0] + 2 * dir, pos[1] - 2 };
			recursiveJumps++;

			if (recursiveJumps == longestJump.intValue()) {
				finalJumpPos.add(Integer.valueOf(newPos[0]));
				finalJumpPos.add(Integer.valueOf(newPos[1]));
				finalJumpPos.add(Integer.valueOf(startingPos[0]));
				finalJumpPos.add(Integer.valueOf(startingPos[1]));
			} else if (recursiveJumps > longestJump.intValue()) {
				longestJump = Integer.valueOf(longestJump.intValue() + 1);
				finalJumpPos.clear();
				finalJumpPos.add(Integer.valueOf(newPos[0]));
				finalJumpPos.add(Integer.valueOf(newPos[1]));
				finalJumpPos.add(Integer.valueOf(startingPos[0]));
				finalJumpPos.add(Integer.valueOf(startingPos[1]));
			}

			checkJumpNextPieces(dir, newPos, team, longestJump, recursiveJumps, finalJumpPos, startingPos);
		}
	}

	public gamePiece[][] getBoard() {
		return board;
	}

	/**
	 * public String toString() { 
	 * //TODO: FIX 
	 * StringBuilder toString = new
	 * StringBuilder(); for (int i = 0; i < 8; i++) { toString.append("{ "); for
	 * (int j = 0; j < 8; j++) { if (board[i][j] == null) { toString.append("X "); }
	 * else { String str; switch ((str =
	 * board[i][j].toString().toLowerCase()).hashCode()) {case 112785: if
	 * (str.equals("red")) break; break; case 93818879: if (!str.equals("black")) {
	 * break; toString.append("R "); continue; } else { toString.append("B "); }
	 * break; } toString.append("X "); } }
	 * 
	 * 
	 * 
	 * toString.append("}\n"); } return toString.toString(); }
	 */

	public class gamePiece {
		private String team = null;
		private boolean king = false;
		private boolean initialized = false;

		private int row;

		private int column;

		gamePiece(String team, int row, int column) {
			this.row = row;
			this.column = column;
			this.team = team;
			initialized = true;
		}

		public String getTeam() {
			if (initialized) {
				return team;
			}
			throw new SecurityException("Game Piece not Initialized");
		}

		public boolean getKing() {
			if (initialized) {
				return king;
			}
			throw new SecurityException("Game Piece not Initialized");
		}

		public void king() {
			king = true;
		}

		public int[] getPosition() {
			int[] temp = { row, column };
			return temp;
		}

		public void setPosition(int row, int column) {
			this.row = row;
			this.column = column;
		}

		public String toString() {
			if (initialized) {
				return team;
			}
			return null;
		}
	}
}
