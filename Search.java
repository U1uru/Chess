package chesster;

import java.util.Date;
import java.util.Stack;

public class Search {

	static int MAX_DEPTH = 6;
	static Date startTime = new Date();
	
	public static int negaMax(Board board,int depth,int alpha,int beta,int color) {
		System.out.println("Searching at depth: "+depth+".");
		Date currentTime = new Date();
		System.out.println((currentTime.getTime()-startTime.getTime())/1000.0 + " seconds");
	    if (depth == MAX_DEPTH || !board.kingsFound() || (currentTime.getTime() - startTime.getTime()) > 1000){
	    	Date time = new Date();
	    	startTime.setTime(time.getTime());
	    	return color * board.eval();
	    }
	    int bestValue = -1000000000;
	    Stack<String> moves = board.generateMoves(color);
	    while(!moves.isEmpty()){
	    	Board newBoard = new Board(board);
	    	newBoard.move(moves.pop());
	        int value = -negaMax(newBoard,depth+1,-beta,-alpha,-color);
	        bestValue = Math.max(bestValue,value);
	        alpha = Math.max(alpha,value);
	        if(alpha >= beta)
	        	break;
	    }
	    return bestValue;
	}

	public static String search(Board board,int depth,int alpha,int beta,int color) {
		if(Board.firstMove && Board.isWhite){
			Board.firstMove = false;
			return "Pe2e4";
		}
		else if(Board.firstMove && !Board.isWhite){
			Board.firstMove = false;
			return "Pe7e5";
		}
		Stack<String> moves = board.generateMoves(color);
		String bestMove = "";
		int bestValue = -10000000;
		while(!moves.isEmpty()){
			String move = moves.pop();
			Board newBoard = new Board(board);
			newBoard.move(move);
			Date time = new Date();
			startTime.setTime(time.getTime());
			int value = -negaMax(newBoard,depth+1,-beta,-alpha,-color);
			if(value > bestValue){
				bestValue = value;
				bestMove = move;
			}
			alpha = Math.max(alpha,value);
			if(alpha >= beta)
				break;
		}
		return bestMove;
	}

	public static void setMaxDepth(int i){
		MAX_DEPTH = i;
	}
}
