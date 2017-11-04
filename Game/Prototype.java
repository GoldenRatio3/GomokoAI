import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

/** 
 *  This gomoku player uses the minimax algorithm
 *  and a heuristic evaluation
 **/
class Prototype extends GomokuPlayer {

	public Move chooseMove(Color[][] board, Color me) {
		int[] move = minimax(board, me, 4, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return new Move(move[0], move[1]);
	} // end chooseMove method

	/**
	 * Heuristic evaluation method based off how many spaces 
	 * the agent is to winning
	 */
	int evaluate(Color[][] board, Color me) {
		int won = nearWins(board, me ,0);
		int oneAway = nearWins(board, me, 1);
		int twoAway = nearWins(board, me, 2);
		int threeAway = nearWins(board, me, 3);

		if(me == Color.WHITE) {
			won += (nearWins(board, Color.BLACK, 0) * 2);
			oneAway += (nearWins(board, Color.BLACK, 1) * 2);
			twoAway += (nearWins(board, Color.BLACK, 2) * 2);
			threeAway += (nearWins(board, Color.BLACK, 3) );

		} else {
			won += (nearWins(board, Color.WHITE, 0) * 2);
			oneAway += (nearWins(board, Color.WHITE, 1) * 2);
			twoAway += (nearWins(board, Color.WHITE, 2) * 2);
			threeAway += (nearWins(board, Color.WHITE, 3) );
		}
		
		// weight the scores
		int totalScore = won * 100000 + oneAway * 100 + twoAway * 5 + threeAway * 1;
		return totalScore;
	}

	/**
	 * Minimax algorithm with alpha-beta pruning
	 */
	int[] minimax(Color[][] board, Color me, int depth, int alpha, int beta) {
		int[] bestScore = new int[3];
		ArrayList<String> moveList;
		Set<String> moveListSet = new HashSet<String>();
		ArrayList<String> moves = getPlayerMoves(board, me);
		for(String move: moves) {
			moveListSet.addAll(getNearMoves(board, move));
		}
		moveListSet.retainAll(getEmpties(board));

		if(moveListSet.isEmpty())
			moveList = new ArrayList<String>(getEmpties(board));
		else
			moveList = new ArrayList<String>(moveListSet);
		// reach required depth
		if(moveList.isEmpty() || depth == 0) {		
			return new int[] {-1, -1, evaluate(board, me)};
		}
		if(me == Color.WHITE) {
			int[] score = new int[] {-1, -1, Integer.MIN_VALUE};
			bestScore[2] = Integer.MIN_VALUE;
			for(String move: moveList) {
				// add new move to board
				int[] newMove = getMove(move);
				score = minimax(addMove(board, newMove, me), Color.BLACK, depth-1, alpha, beta);
				if(score[2] > bestScore[2]) {
					bestScore[0] = newMove[0];
					bestScore[1] = newMove[1];
					bestScore[2] = score[2];
				}

				alpha = Math.max(alpha, bestScore[2]);
				if(beta <= alpha) {
					break;
				}
			}
			return new int[] {bestScore[0], bestScore[1], bestScore[2]};
		} else {
			int[] score = new int[] {-1, -1, Integer.MAX_VALUE};
			bestScore[2] = Integer.MAX_VALUE;
			for(String move: moveList) {
				// add new move to board
				int[] newMove = getMove(move);
				score = minimax(addMove(board, newMove, me), Color.WHITE, depth-1, alpha, beta);
				if(score[2] < bestScore[2]) {
					bestScore[0] = newMove[0];
					bestScore[1] = newMove[1];
					bestScore[2] = score[2];
				}
				beta = Math.min(beta, bestScore[2]);				
				if(beta <= alpha) {			
					break;				
				}
			}
			return new int[] {bestScore[0], bestScore[1], bestScore[2]};
		}
	}

	/**
	 * Create new board and add current move
	 * @param  board board to copy
	 * @param  move  new move to add
	 * @param  me    the players color
	 * @return       new board with added move
	 */
	Color[][] addMove(Color[][] board, int[] move, Color me) {
		Color[][] newBoard = new Color[8][8];
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		newBoard[move[0]][move[1]] = me;
		return newBoard;
	}

	/**
	 * gets a list of empty board locations		
	 * @param  board 
	 * @return ArrayList of empty board locations
	 */					
	ArrayList<String> getEmpties(Color[][] board) {
		ArrayList<String> empties = new ArrayList<String>();
		for (int row = 0; row < GomokuBoard.ROWS; row++) {
			for (int col = 0; col < GomokuBoard.COLS; col++) {
				if (board[row][col] == null) {
					empties.add(row+" "+col);
				}
			}
		}
		return empties;
	}

	/**
	 * gets move from string
	 * @param  move    takes move of string type
	 * @return returns int array of i and j positions
	 */
	int[] getMove(String move) {
		String[] moves = move.split(" ");
		int[] pos = { Integer.parseInt(moves[0]), Integer.parseInt(moves[1]) };
		return pos;
	}

	/**
	 * find how close the agent is to winning
	 * @param  board current state
	 * @param  me    agents color
	 * @param  away  how many pieces they are away
	 * @return       how many times this occurs
	 */
	int nearWins(Color[][] board, Color me, int away) {
		int counter = 0;
		// find possible strings by rows
		for(int row=0; row<GomokuBoard.ROWS; row++) {
			String currentRow = getRow(board, row);
			// sliding window through row
			for(int i=0; i<4; i++) {
				int endIndex = i+5;
				String possibleString = currentRow.substring(i, endIndex);
				if(isValid(possibleString, away) && isValid(possibleString, 5-away, me)) {
					counter++;
				}
			}
		}
		// find possible strings by cols
		for(int col = 0; col < GomokuBoard.COLS; col++) {
			String currentCol = getCol(board, col);
			// sliding window through col
			for(int i=0; i<4; i++) {
				int endIndex = i+5;
				String possibleString = currentCol.substring(i, endIndex);
				if(isValid(possibleString, away) && isValid(possibleString, 5-away, me)) {
					counter++;
				}
			}
		}

		// find possible strings by diagonals
				 
		// top half of diags
	    for( int k = 4 ; k < GomokuBoard.COLS ; k++ ) {
	    	String currentDiagsTop = getDiagsTop(board, k);
	    	// sliding window through top diag
			for(int i=0; i<(currentDiagsTop.length()-4); i++) { 
				int endIndex = i+5;
				String possibleString = currentDiagsTop.substring(i, endIndex);
				if(isValid(possibleString, away) && isValid(possibleString, 5-away, me)) {
					counter++;
				}
			}	    	
	    }		
	    // bottom half of diags
		for( int k = GomokuBoard.COLS - 2 ; k >= 4 ; k-- ) {
			String currentDiagsBottom = getDiagsBottom(board, k);
	    	// sliding window through top diag
			for(int i=0; i<(currentDiagsBottom.length()-4); i++) { 
				int endIndex = i+5;
				String possibleString = currentDiagsBottom.substring(i, endIndex);
				if(isValid(possibleString, away) && isValid(possibleString, 5-away, me)) {
					counter++;
				}
			}	  
	    }
		return counter;
	}

	// get top diagonal and turn into parsable string
	String getDiagsTop(Color[][] board, int diagPos) {
		String diags = "";
		for( int j = 0 ; j <= diagPos ; j++ ) {
		    int i = diagPos - j;
		    if(board[i][j] == null)
		    	diags += "_";
		    else if(board[i][j] == Color.BLACK)
		    	diags += "b";
		    else if(board[i][j] == Color.WHITE)
		    	diags += "w";
		}
		return diags;
	}

	// get bottom diagonal and turn into parsable string
	String getDiagsBottom(Color[][] board, int diagPos) {
		String diags = "";
        for( int j = 0 ; j <= diagPos ; j++ ) {
            int i = diagPos - j;
		    if(board[GomokuBoard.COLS - j - 1][GomokuBoard.COLS - i - 1] == null)
		    	diags += "_";
		    else if(board[GomokuBoard.COLS - j - 1][GomokuBoard.COLS - i - 1] == Color.BLACK)
		    	diags += "b";
		    else if(board[GomokuBoard.COLS - j - 1][GomokuBoard.COLS - i - 1] == Color.WHITE)
		    	diags += "w";            
        }
		return diags;
	}

	// get row and turn into parsable string
	String getRow(Color[][] board, int rowPos) {
		String row = "";
		for(int i=0; i<GomokuBoard.COLS; i++) {
			if(board[rowPos][i] == null)
				row += "_";
			else if(board[rowPos][i] == Color.BLACK)
				row += "b";
			else if(board[rowPos][i] == Color.WHITE)
				row += "w";
		}	
		return row;
	}

	// get column and turn into parsable string
	String getCol(Color[][] board, int colPos) {
		String col = "";
		for(int i=0; i<GomokuBoard.ROWS; i++) {
			if(board[i][colPos] == Color.BLACK)
				col += "b";
			else if(board[i][colPos] == Color.WHITE)
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
		for(int i=0; i<possibleString.length(); i++)
			if(possibleString.charAt(i) == '_')
				count++;

		if(count == away)
			return true;
		else
			return false;
	}

	// checks to see if possible string is valid based off
	// how many agents colors there are
	boolean isValid(String possibleString, int away, Color me) {
		int count = 0;
		char color = '\0';
		if(me == Color.BLACK)
			color = 'b';
		else if(me == Color.WHITE)
			color = 'w';

		for(int i=0; i<possibleString.length(); i++)
			if(possibleString.charAt(i) == color)
				count++;

		if(count == away)
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
					moves.add(row+" "+col);
				}
			}
		}
		return moves;		
	}

	// get all moves that are near the given move
	ArrayList<String> getNearMoves(Color[][] board, String move) {
		int[] parsedMove = getMove(move);
		ArrayList<String> moves = new ArrayList<String>();
		if((parsedMove[0]-1 >= 0) && (parsedMove[1]-1 >= 0))
			moves.add(Integer.toString(parsedMove[0]-1)+" "+Integer.toString(parsedMove[1]-1));
		// TODO: ADD MORE
		return moves;
	}

} // end Prototype class
