
/**
 * Author: Bradley Winter
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

/**
 * This gomoku player uses the minimax algorithm and a heuristic evaluation
 **/
class Prototype extends GomokuPlayer {

	public Move chooseMove(Color[][] board, Color me) {
		// Check if no moves have been played
		if (noMoves(board)) {
			// Then play in the middle
			return new Move(GomokuBoard.ROWS / 2 - 1, GomokuBoard.COLS / 2 - 1);
		}
		int[] winningMove = hasWinningMove(board, me);
		if (winningMove[0] != -1) {
			return new Move(winningMove[0], winningMove[1]);
		}

		int[] oppWinningMove = hasWinningMove(board, (me.equals(Color.WHITE)) ? Color.BLACK : Color.WHITE);
		if (oppWinningMove[0] != -1) {
			return new Move(oppWinningMove[0], oppWinningMove[1]);
		}

		int[] move = minimax(board, me, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		System.out.println("Move: " + move[0] + "," + move[1] + " with score of " + move[2]);
		return new Move(move[0], move[1]);
	} // end chooseMove method

	/**
	 * Checks to see if no moves have been played
	 */
	boolean noMoves(Color[][] board) {
		for (int row = 0; row < GomokuBoard.ROWS; row++) {
			for (int col = 0; col < GomokuBoard.COLS; col++) {
				if (board[row][col] != null) {
					return false;
				}
			}
		}
		return true;
	}

	int[] hasWinningMove(Color[][] board, Color me) {
		int wins = nearWins(board, me, 1, true);
		if (wins != 0) {
			for (int row = 0; row < GomokuBoard.ROWS; row++) {
				for (int col = 0; col < GomokuBoard.COLS; col++) {
					if (board[row][col] == null) {
						// Try move
						int[] move = { row, col };
						if (nearWins(addMove(board, move, me), me, 0, false) != 0) {
							return new int[] { row, col };
						}
					}
				}
			}
		}
		return new int[] { -1, -1 };
	}

	void printBoard(Color[][] board) {
		System.out.println("------- Start board print ---------");
		for (int row = 0; row < GomokuBoard.ROWS; row++) {
			for (int col = 0; col < GomokuBoard.COLS; col++) {
				if (board[row][col] == null) {
					System.out.print("_ ");
				} else if (board[row][col] == Color.BLACK) {
					System.out.print("B ");
				} else {
					System.out.print("W ");
				}
			}
			System.out.println();
		}
		System.out.println("------- End board print ---------");
	}

	/**
	 * Heuristic evaluation method based off how many spaces the agent is to winning
	 */
	int evaluate(Color[][] board, Color me) {
		int won = nearWins(board, me, 0, false) * 10000;
		int oneAway = nearWins(board, me, 1, false) * 100;
		int twoAway = nearWins(board, me, 2, false) * 50;
		int threeAway = nearWins(board, me, 3, false) * 1;

		if (won != 0) {
			/*
			 * System.out.println("----------  Turn Start ---------------");
			 * printBoard(board); System.out.println(me); System.out.println("WON ----> " +
			 * won); System.out.println(oneAway); System.out.println(twoAway);
			 * System.out.println(threeAway);
			 */
		}

		if (me == Color.WHITE) {
			won += (nearWins(board, Color.BLACK, 0, false) * 10000);
			oneAway += (nearWins(board, Color.BLACK, 1, false) * 100);
			twoAway += (nearWins(board, Color.BLACK, 2, false) * 50);
			threeAway += (nearWins(board, Color.BLACK, 3, false) * 1);

		} else {
			won += (nearWins(board, Color.WHITE, 0, false) * 10000);
			oneAway += (nearWins(board, Color.WHITE, 1, false) * 100);
			twoAway += (nearWins(board, Color.WHITE, 2, false) * 50);
			threeAway += (nearWins(board, Color.WHITE, 3, false) * 1);
		}

		if (won != 0) {
			/*
			 * System.out.println("WON ----> " + won); System.out.println(oneAway);
			 * System.out.println(twoAway); System.out.println(threeAway);
			 */
		}

		// weight the scores
		int totalScore = won * 100000 + oneAway * 100 + twoAway * 5 + threeAway * 1;

		return totalScore;
	}

	Color whosTurn(Color[][] board) {
		int whiteMoves = 0, blackMoves = 0;

		for (int row = 0; row < GomokuBoard.ROWS; row++) {
			for (int col = 0; col < GomokuBoard.COLS; col++) {
				if (board[row][col] == Color.WHITE) {
					whiteMoves++;
				} else if (board[row][col] == Color.BLACK) {
					blackMoves++;
				}

			}
		}

		if (whiteMoves < blackMoves) {
			return Color.WHITE;
		}
		return Color.BLACK;
	}

	/**
	 * Minimax algorithm with alpha-beta pruning
	 */
	int[] minimax(Color[][] board, Color me, int depth, int alpha, int beta, boolean isMaxTurn) {
		int[] bestScore = new int[3];
		ArrayList<String> moveList;
		Set<String> moveListSet = new HashSet<String>();
		ArrayList<String> moves = getPlayerMoves(board, me);
		for (String move : moves) {
			moveListSet.addAll(getNearMoves(board, move));
		}
		moveListSet.retainAll(getEmpties(board));

		if (moveListSet.isEmpty())
			moveList = new ArrayList<String>(getEmpties(board));
		else
			moveList = new ArrayList<String>(moveListSet);
		// reach required depth or game finished
		if (moveList.isEmpty() || depth == 0 || hasGameCompleted(board)) {
			return new int[] { -1, -1, evaluate(board, me) };
		}
		if (isMaxTurn) {
			int[] score = new int[] { -1, -1, Integer.MIN_VALUE };
			bestScore[2] = Integer.MIN_VALUE;
			for (String move : moveList) {
				// add new move to board
				int[] newMove = getMove(move);
				score = minimax(addMove(board, newMove, me), Color.BLACK, depth - 1, alpha, beta, false);
				if (score[2] > bestScore[2]) {
					bestScore[0] = newMove[0];
					bestScore[1] = newMove[1];
					bestScore[2] = score[2];
				}

				alpha = Math.max(alpha, bestScore[2]);
				if (beta <= alpha) {
					break;
				}
			}
			return new int[] { bestScore[0], bestScore[1], bestScore[2] };
		} else {
			int[] score = new int[] { -1, -1, Integer.MAX_VALUE };
			bestScore[2] = Integer.MAX_VALUE;
			for (String move : moveList) {
				// add new move to board
				int[] newMove = getMove(move);
				score = minimax(addMove(board, newMove, me), Color.WHITE, depth - 1, alpha, beta, true);
				if (score[2] < bestScore[2]) {
					bestScore[0] = newMove[0];
					bestScore[1] = newMove[1];
					bestScore[2] = score[2];
				}
				beta = Math.min(beta, bestScore[2]);
				if (beta <= alpha) {
					break;
				}
			}
			return new int[] { bestScore[0], bestScore[1], bestScore[2] };
		}
	}

	boolean hasGameCompleted(Color[][] board) {
		int whiteWins = nearWins(board, Color.WHITE, 0, false);
		int blackWins = nearWins(board, Color.BLACK, 0, false);

		boolean outcome = true;
		if (whiteWins == 0 && blackWins == 0) {
			outcome = false;
		}
		if (outcome) {
			// printBoard(board);
		}
		return outcome;
	}

	/**
	 * Create new board and add current move
	 * 
	 * @param board board to copy
	 * @param move  new move to add
	 * @param me    the players color
	 * @return new board with added move
	 */
	Color[][] addMove(Color[][] board, int[] move, Color me) {
		Color[][] newBoard = new Color[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		newBoard[move[0]][move[1]] = me;
		return newBoard;
	}

	/**
	 * gets a list of empty board locations
	 * 
	 * @param board
	 * @return ArrayList of empty board locations
	 */
	ArrayList<String> getEmpties(Color[][] board) {
		ArrayList<String> empties = new ArrayList<String>();
		for (int row = 0; row < GomokuBoard.ROWS; row++) {
			for (int col = 0; col < GomokuBoard.COLS; col++) {
				if (board[row][col] == null) {
					empties.add(row + " " + col);
				}
			}
		}
		return empties;
	}

	/**
	 * gets move from string
	 * 
	 * @param move takes move of string type
	 * @return returns int array of i and j positions
	 */
	int[] getMove(String move) {
		String[] moves = move.split(" ");
		int[] pos = { Integer.parseInt(moves[0]), Integer.parseInt(moves[1]) };
		return pos;
	}

	/**
	 * find how close the agent is to winning
	 * 
	 * @param board current state
	 * @param me    agents color
	 * @param away  how many pieces they are away
	 * @return how many times this occurs
	 */
	int nearWins(Color[][] board, Color me, int away, boolean debug) {
		int counter = 0;
		// find possible strings by rows
		for (int row = 0; row < GomokuBoard.ROWS; row++) {
			String currentRow = getRow(board, row);

			if (debug && away == 0) {
				System.out.println("currentRow: " + currentRow);
			}
			// sliding window through row
			for (int i = 0; i < 4; i++) {
				int endIndex = i + 5;
				String possibleString = currentRow.substring(i, endIndex);
				if (isValid(possibleString, away) && isValid(possibleString, 5 - away, me)) {
					if (debug) {
						System.out.print("Valid with away of " + away + " and colour of ");
					}
					counter++;
				}
			}
		}
		// find possible strings by cols
		for (int col = 0; col < GomokuBoard.COLS; col++) {
			String currentCol = getCol(board, col);
			// sliding window through col
			for (int i = 0; i < 4; i++) {
				int endIndex = i + 5;
				String possibleString = currentCol.substring(i, endIndex);
				if (isValid(possibleString, away) && isValid(possibleString, 5 - away, me)) {
					counter++;
				}
			}
		}

		// find possible strings by diagonals

		// top half of diags, increasing direction
		// 4,0 -> 0,4
		// through to
		// 0,7 -> 0,7
		for (int k = 4; k < GomokuBoard.COLS; k++) {
			String currentDiagsTop = getDiagsTop(board, k);
			// sliding window through top diag
			for (int i = 0; i < (currentDiagsTop.length() - 4); i++) {
				int endIndex = i + 5;
				String possibleString = currentDiagsTop.substring(i, endIndex);
				if (isValid(possibleString, away) && isValid(possibleString, 5 - away, me)) {
					counter++;
				}
			}
		}
		// bottom half of diags, increasing direction
		for (int k = GomokuBoard.COLS - 2; k >= 4; k--) {
			String currentDiagsBottom = getDiagsBottom(board, k);
			// sliding window through top diag
			for (int i = 0; i < (currentDiagsBottom.length() - 4); i++) {
				int endIndex = i + 5;
				String possibleString = currentDiagsBottom.substring(i, endIndex);
				if (isValid(possibleString, away) && isValid(possibleString, 5 - away, me)) {
					counter++;
				}
			}
		}

		// top half of diags, decreasing direction
		for (int k = GomokuBoard.COLS - 1; k >= 4; k--) {
			String currentDiagsDecreasingTop = getDiagsIncreasingTop(board, k);
			// sliding window through top diag
			for (int i = 0; i < (currentDiagsDecreasingTop.length() - 4); i++) {
				int endIndex = i + 5;
				String possibleString = currentDiagsDecreasingTop.substring(i, endIndex);
				if (isValid(possibleString, away) && isValid(possibleString, 5 - away, me)) {
					counter++;
				}
			}
		}

		// bottom half of diags, decreasing direction
		for (int k = 5; k < GomokuBoard.COLS; k++) {
			String currentDiagsDecreasingBottom = getDiagsIncreasingBottom(board, k);

			// sliding window through top diag
			for (int i = 0; i < (currentDiagsDecreasingBottom.length() - 4); i++) {
				int endIndex = i + 5;
				String possibleString = currentDiagsDecreasingBottom.substring(i, endIndex);
				if (isValid(possibleString, away) && isValid(possibleString, 5 - away, me)) {
					counter++;
				}
			}
		}

		return counter;
	}

	// get top diagonal and turn into parsable string
	String getDiagsTop(Color[][] board, int diagPos) {
		String diags = "";
		for (int j = 0; j <= diagPos; j++) {
			int i = diagPos - j;
			if (board[i][j] == null)
				diags += "_";
			else if (board[i][j] == Color.BLACK)
				diags += "b";
			else if (board[i][j] == Color.WHITE)
				diags += "w";
		}
		return diags;
	}

	// get bottom diagonal and turn into parsable string
	String getDiagsBottom(Color[][] board, int diagPos) {
		String diags = "";
		for (int j = 0; j <= diagPos; j++) {
			int i = diagPos - j;
			if (board[GomokuBoard.COLS - j - 1][GomokuBoard.COLS - i - 1] == null)
				diags += "_";
			else if (board[GomokuBoard.COLS - j - 1][GomokuBoard.COLS - i - 1] == Color.BLACK)
				diags += "b";
			else if (board[GomokuBoard.COLS - j - 1][GomokuBoard.COLS - i - 1] == Color.WHITE)
				diags += "w";
		}
		return diags;
	}

	// get increasing top diagonal and turn into parsable string
	String getDiagsIncreasingTop(Color[][] board, int diagPos) {
		String diags = "";
		int i = GomokuBoard.COLS - diagPos - 1;

		for (int j = 0; j <= diagPos; j++) {
			if (board[j][i] == null)
				diags += "_";
			else if (board[j][i] == Color.BLACK)
				diags += "b";
			else if (board[j][i] == Color.WHITE)
				diags += "w";

			i++;
		}
		return diags;
	}

	// get increasing bottom diagonal and turn into parsable string
	String getDiagsIncreasingBottom(Color[][] board, int diagPos) {
		String diags = "";
		int i = GomokuBoard.COLS - diagPos;

		for (int j = 0; j < diagPos; j++) {
			if (board[i][j] == null)
				diags += "_";
			else if (board[i][j] == Color.BLACK)
				diags += "b";
			else if (board[i][j] == Color.WHITE)
				diags += "w";

			i++;
		}
		return diags;
	}

	// get row and turn into parsable string
	String getRow(Color[][] board, int rowPos) {
		String row = "";
		for (int i = 0; i < GomokuBoard.COLS; i++) {
			if (board[rowPos][i] == null)
				row += "_";
			else if (board[rowPos][i] == Color.BLACK)
				row += "b";
			else if (board[rowPos][i] == Color.WHITE)
				row += "w";
		}
		return row;
	}

	// get column and turn into parsable string
	String getCol(Color[][] board, int colPos) {
		String col = "";
		for (int i = 0; i < GomokuBoard.ROWS; i++) {
			if (board[i][colPos] == Color.BLACK)
				col += "b";
			else if (board[i][colPos] == Color.WHITE)
				col += "w";
			else
				col += "_";
		}
		return col;
	}

	// checks to see if possible string is valid based
	// off how many empty spaces there are
	boolean isValid(String possibleString, int away) {
		int count = 0;
		for (int i = 0; i < possibleString.length(); i++)
			if (possibleString.charAt(i) == '_')
				count++;

		if (count == away)
			return true;
		else
			return false;
	}

	// checks to see if possible string is valid based off
	// how many agents colors there are
	boolean isValid(String possibleString, int away, Color me) {
		int count = 0;
		char color = '\0';
		if (me == Color.BLACK)
			color = 'b';
		else if (me == Color.WHITE)
			color = 'w';

		for (int i = 0; i < possibleString.length(); i++)
			if (possibleString.charAt(i) == color)
				count++;

		if (count == away)
			return true;
		else
			return false;

	}

	// get all possible player moves
	ArrayList<String> getPlayerMoves(Color[][] board, Color me) {
		ArrayList<String> moves = new ArrayList<String>();
		for (int row = 0; row < GomokuBoard.ROWS; row++) {
			for (int col = 0; col < GomokuBoard.COLS; col++) {
				if (board[row][col] == me) {
					moves.add(row + " " + col);
				}
			}
		}
		return moves;
	}

	// get all moves that are near the given move
	ArrayList<String> getNearMoves(Color[][] board, String move) {
		int[] parsedMove = getMove(move);
		// W = white move
		// C = checking if valid play
		ArrayList<String> moves = new ArrayList<String>();
		// C x x
		// x W x
		// x x x
		if ((parsedMove[0] - 1 >= 0) && (parsedMove[1] - 1 >= 0))
			moves.add(Integer.toString(parsedMove[0] - 1) + " " + Integer.toString(parsedMove[1] - 1));
		// x C x
		// x W x
		// x x x
		if (parsedMove[1] - 1 >= 0)
			moves.add(Integer.toString(parsedMove[0]) + " " + Integer.toString(parsedMove[1] - 1));
		// x x C
		// x W x
		// x x x
		if ((parsedMove[0] + 1 <= GomokuBoard.COLS) && (parsedMove[1] - 1 >= 0))
			moves.add(Integer.toString(parsedMove[0] + 1) + " " + Integer.toString(parsedMove[1] - 1));
		// x x x
		// C W x
		// x x x
		if ((parsedMove[0] - 1 >= 0))
			moves.add(Integer.toString(parsedMove[0] - 1) + " " + Integer.toString(parsedMove[1]));
		// x x x
		// x W C
		// x x x
		if (parsedMove[0] + 1 <= GomokuBoard.COLS)
			moves.add(Integer.toString(parsedMove[0] + 1) + " " + Integer.toString(parsedMove[1]));
		// x x x
		// x W x
		// C x x
		if ((parsedMove[0] - 1 >= 0) && (parsedMove[1] + 1 <= GomokuBoard.ROWS))
			moves.add(Integer.toString(parsedMove[0] - 1) + " " + Integer.toString(parsedMove[1] + 1));
		// x x x
		// x W x
		// x C x
		if (parsedMove[1] + 1 <= GomokuBoard.ROWS)
			moves.add(Integer.toString(parsedMove[0]) + " " + Integer.toString(parsedMove[1] + 1));
		// x x x
		// x W x
		// x x C
		if ((parsedMove[0] + 1 <= GomokuBoard.COLS) && (parsedMove[1] + 1 <= GomokuBoard.ROWS))
			moves.add(Integer.toString(parsedMove[0] + 1) + " " + Integer.toString(parsedMove[1] + 1));

		return moves;
	}

} // end Prototype class
