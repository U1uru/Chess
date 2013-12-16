package chesster;

import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Stack;

import org.codehaus.jackson.map.ObjectMapper;

public class Board {
	
	static final byte noPiece = 0;
	static final byte playerPawn = 2;
	static final byte oppPawn = -2;
	static final byte playerKnight = 6;
	static final byte oppKnight = -6;
	static final byte playerBishop = 7;
	static final byte oppBishop = -7;
	static final byte playerRook = 11;
	static final byte oppRook = -11;
	static final byte playerQueen = 20;
	static final byte oppQueen = -20;
	static final byte playerKing = 127;
	static final byte oppKing = -127;
	
	static final String teamNumber = "106";
	static final String teamSecret = "32f19b81";
	
	static boolean firstMove = true;
	static String gameID;
	static boolean isWhite;
	
	boolean playerKingFound, oppKingFound;
	
	byte[][] board;
	String moveToMake;
	
	public Board(String gameID, boolean playerIsWhite){
		
		Board.gameID = gameID;
		Board.isWhite = playerIsWhite;
		
		playerKingFound = false;
		oppKingFound = false;
		
		board = new byte[8][8];
		if(playerIsWhite){
			board[0][0] = oppRook;
			board[0][1] = oppKnight;
			board[0][2] = oppBishop;
			board[0][3] = oppQueen;
			board[0][4] = oppKing;
			board[0][5] = oppBishop;
			board[0][6] = oppKnight;
			board[0][7] = oppRook;
			for(int i = 0;i < 8;i++){
				board[1][i] = oppPawn;
				board[6][i] = playerPawn;
			}
			board[7][0] = playerRook;
			board[7][1] = playerKnight;
			board[7][2] = playerBishop;
			board[7][3] = playerQueen;
			board[7][4] = playerKing;
			board[7][5] = playerBishop;
			board[7][6] = playerKnight;
			board[7][7] = playerRook;
		}
		else{
			board[0][0] = playerRook;
			board[0][1] = playerKnight;
			board[0][2] = playerBishop;
			board[0][3] = playerQueen;
			board[0][4] = playerKing;
			board[0][5] = playerBishop;
			board[0][6] = playerKnight;
			board[0][7] = playerRook;
			for(int i = 0;i < 8;i++){
				board[1][i] = playerPawn;
				board[6][i] = oppPawn;
			}
			board[7][0] = oppRook;
			board[7][1] = oppKnight;
			board[7][2] = oppBishop;
			board[7][3] = oppQueen;
			board[7][4] = oppKing;
			board[7][5] = oppBishop;
			board[7][6] = oppKnight;
			board[7][7] = playerRook;
		}
	}
	
	public Board(Board old){
		playerKingFound = false;
		oppKingFound = false;
		this.board = new byte[8][8];
		for(int i = 0;i < 8;i++){
			for(int j = 0;j < 8;j++)
				this.board[i][j] = old.board[i][j];
		}
	}
	
	//utility eval function
	public int eval(){
		int value = 0;
		int piece;
		for(int i = 0;i < 8;i++){
			for(int j = 0;j < 8;j++){
				piece = board[i][j];
				if(piece == playerKing)
					value += 1000*piece;
				else if(piece == oppKing)
					value += 1000*piece;
				else
					value += piece;
			}
		}
		return value;
	}
	
	public boolean kingsFound(){
		for(int i = 0;i < 8;i++){
			for(int j = 0;j < 8; j++){
				if(board[i][j] == playerKing)
					playerKingFound = true;
				if(board[i][j] == oppKing)
					oppKingFound = true;
			}
		}
		return playerKingFound && oppKingFound;
	}
	
	public void move(String moveString){
		if(moveString == null || moveString.equals(""))
			return;
		int file1 = moveString.toLowerCase().charAt(1)-'a';
		int rank1 = (Integer.parseInt(moveString.substring(2,3))-8)*-1;
		byte piece = board[rank1][file1];
		board[rank1][file1] = noPiece;
		int file2 = moveString.toLowerCase().charAt(3)-'a';
		int rank2 = (Integer.parseInt(moveString.substring(4,5))-8)*-1;
		//en passant
		if((piece==playerPawn||piece==oppPawn) && file1 != file2 && board[rank2][file2] == noPiece)
			board[rank1][file2] = noPiece;
		board[rank2][file2] = piece;
		//promotion
		if(moveString.length() == 6){
			byte proPiece;
			String promotion = moveString.substring(5).toUpperCase();
			if(promotion.equals("Q"))
				proPiece = playerQueen;
			else if(promotion.equals("R"))
				proPiece = playerRook;
			else if(promotion.equals("B"))
				proPiece = playerBishop;
			else//knight only remaining option
				proPiece = playerKnight;
			if(piece < 0)
				proPiece = (byte) (proPiece*-1);
		}
		//castling
		if(piece != playerKing && piece != oppKing)
			return;
		if(moveString.toLowerCase().equals("ke1c1"))
			move("ra1d1");
		else if(moveString.toLowerCase().equals("ke1g1"))
			move("rh1f1");
		else if(moveString.toLowerCase().equals("ke8c8"))
			move("ra8d8");
		else if(moveString.toLowerCase().equals("ke8g8"))
			move("rh8f8");
	}
	
	public Stack<String> generateMoves(int test){
		Stack<String> moveStack = new Stack<String>();
		//test == 1 --> player move
		if(isWhite && test == 1 || !isWhite && test == 0){//moving "bottom" pieces
			for(int i = 7;i >= 0;i--){
				for(int j = 7;j >= 0;j--){
					if(board[i][j]*test > 0){
						Stack<String> tempStack = generateMoves(i,j);
						while(!tempStack.isEmpty())
							moveStack.push(tempStack.pop());
					}
				}
			}
		} else{
			for(int i = 0;i < 8;i++){
				for(int j = 0;j < 8;j++){
					if(board[i][j]*test > 0){
						Stack<String> tempStack = generateMoves(i,j);
						while(!tempStack.isEmpty())
							moveStack.push(tempStack.pop());
					}
				}
			}
		}
		return moveStack;
		//different ordering basically meant to
		//mess around a bit with what order
		//moves are put on stack
	}
	
	private Stack<String> generateMoves(int i,int j){
		Stack<String> moves = new Stack<String>();
		byte piece = board[i][j];
		int side = 1;
		if(piece < 0)
			side = -1;
		//king
		if(piece == playerKing || piece == oppKing){
			try{if(board[i+1][j]*side <= 0)moves.push("K"+getMoveStringCoord(i,j,i+1,j));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i-1][j]*side <= 0)moves.push("K"+getMoveStringCoord(i,j,i-1,j));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i][j+1]*side <= 0)moves.push("K"+getMoveStringCoord(i,j,i,j+1));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i][j-1]*side <= 0)moves.push("K"+getMoveStringCoord(i,j,i,j-1));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i+1][j+1]*side <= 0)moves.push("K"+getMoveStringCoord(i,j,i+1,j+1));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i-1][j+1]*side <= 0)moves.push("K"+getMoveStringCoord(i,j,i-1,j+1));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i+1][j-1]*side <= 0)moves.push("K"+getMoveStringCoord(i,j,i+1,j-1));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i-1][j-1]*side <= 0)moves.push("K"+getMoveStringCoord(i,j,i-1,j-1));}catch(ArrayIndexOutOfBoundsException e){}
		}
		//queen
		if(piece == playerQueen || piece == oppQueen){
			int k = i;
			while(k < 7){
				k++;
				if(board[k][j]*side > 0)break;
				if(board[k][j]*side < 0){
					moves.push("Q" + getMoveStringCoord(i,j,k,j));
					break;
				}
				else
					moves.push("Q" + getMoveStringCoord(i,j,k,j));
			}
			k = i;
			while(k > 0){
				k--;
				if(board[k][j]*side > 0)break;
				if(board[k][j]*side < 0){
					moves.push("Q" + getMoveStringCoord(i,j,k,j));
					break;
				}
				else
					moves.push("Q" + getMoveStringCoord(i,j,k,j));
			}
			k = j;
			while(k < 7){
				k++;
				if(board[i][k]*side > 0)break;
				if(board[i][k]*side < 0){
					moves.push("Q" + getMoveStringCoord(i,j,i,k));
					break;
				}
				else
					moves.push("Q" + getMoveStringCoord(i,j,i,k));
			}
			k = j;
			while(k > 0){
				k--;
				if(board[i][k]*side > 0)break;
				if(board[i][k]*side < 0){
					moves.push("Q" + getMoveStringCoord(i,j,i,k));
					break;
				}
				else
					moves.push("Q" + getMoveStringCoord(i,j,i,k));
			}
			k = i;
			int l = j;
			while(k < 7 && l < 7){
				k++;
				l++;
				if(board[k][l]*side > 0)break;
				if(board[k][l]*side < 0){
					moves.push("Q" + getMoveStringCoord(i,j,k,l));
					break;
				}
				else
					moves.push("Q" + getMoveStringCoord(i,j,k,l));
			}
			k = i;
			l = j;
			while(k < 7 && l > 0){
				k++;
				l--;
				if(board[k][l]*side > 0)break;
				if(board[k][l]*side < 0){
					moves.push("Q" + getMoveStringCoord(i,j,k,l));
					break;
				}
				else
					moves.push("Q" + getMoveStringCoord(i,j,k,l));
			}
			k = i;
			l = j;
			while(k > 0 && l < 7){
				k--;
				l++;
				if(board[k][l]*side > 0)break;
				if(board[k][l]*side < 0){
					moves.push("Q" + getMoveStringCoord(i,j,k,l));
					break;
				}
				else
					moves.push("Q" + getMoveStringCoord(i,j,k,l));
			}
			k = i;
			l = j;
			while(k > 0 && l > 0){
				k--;
				l--;
				if(board[k][l]*side > 0)break;
				if(board[k][l]*side < 0){
					moves.push("Q" + getMoveStringCoord(i,j,k,l));
					break;
				}
				else
					moves.push("Q" + getMoveStringCoord(i,j,k,l));
			}
		}
		//rook
		if(piece == playerRook || piece == oppRook){
			int k = i;
			while(k < 7){
				k++;
				if(board[k][j]*side > 0)break;
				if(board[k][j]*side < 0){
					moves.push("R" + getMoveStringCoord(i,j,k,j));
					break;
				}
				else
					moves.push("R" + getMoveStringCoord(i,j,k,j));
			}
			k = i;
			while(k > 0){
				k--;
				if(board[k][j]*side > 0)break;
				if(board[k][j]*side < 0){
					moves.push("R" + getMoveStringCoord(i,j,k,j));
					break;
				}
				else
					moves.push("R" + getMoveStringCoord(i,j,k,j));
			}
			k = j;
			while(k < 7){
				k++;
				if(board[i][k]*side > 0)break;
				if(board[i][k]*side < 0){
					moves.push("R" + getMoveStringCoord(i,j,i,k));
					break;
				}
				else
					moves.push("R" + getMoveStringCoord(i,j,i,k));
			}
			k = j;
			while(k > 0){
				k--;
				if(board[i][k]*side > 0)break;
				if(board[i][k]*side < 0){
					moves.push("R" + getMoveStringCoord(i,j,i,k));
					break;
				}
				else
					moves.push("R" + getMoveStringCoord(i,j,i,k));
			}
		}
		//bishop
		if(piece == playerBishop || piece == oppBishop){
			int k = i;
			int l = j;
			while(k < 7 && l < 7){
				k++;
				l++;
				if(board[k][l]*side > 0)break;
				if(board[k][l]*side < 0){
					moves.push("B" + getMoveStringCoord(i,j,k,l));
					break;
				}
				else
					moves.push("B" + getMoveStringCoord(i,j,k,l));
			}
			k = i;
			l = j;
			while(k < 7 && l > 0){
				k++;
				l--;
				if(board[k][l]*side > 0)break;
				if(board[k][l]*side < 0){
					moves.push("B" + getMoveStringCoord(i,j,k,l));
					break;
				}
				else
					moves.push("B" + getMoveStringCoord(i,j,k,l));
			}
			k = i;
			l = j;
			while(k > 0 && l < 7){
				k--;
				l++;
				if(board[k][l]*side > 0)break;
				if(board[k][l]*side < 0){
					moves.push("B" + getMoveStringCoord(i,j,k,l));
					break;
				}
				else
					moves.push("B" + getMoveStringCoord(i,j,k,l));
			}
			k = i;
			l = j;
			while(k > 0 && l > 0){
				k--;
				l--;
				if(board[k][l]*side > 0)break;
				if(board[k][l]*side < 0){
					moves.push("B" + getMoveStringCoord(i,j,k,l));
					break;
				}
				else
					moves.push("B" + getMoveStringCoord(i,j,k,l));
			}
		}
		//knight
		if(piece == playerKnight || piece == oppKnight){
			try{if(board[i+1][j+2]*side<=0)moves.push("N" + getMoveStringCoord(i,j,i+1,j+2));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i+2][j+1]*side<=0)moves.push("N" + getMoveStringCoord(i,j,i+2,j+1));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i-1][j+2]*side<=0)moves.push("N" + getMoveStringCoord(i,j,i-1,j+2));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i-2][j+1]*side<=0)moves.push("N" + getMoveStringCoord(i,j,i-2,j+1));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i+1][j-2]*side<=0)moves.push("N" + getMoveStringCoord(i,j,i+1,j-2));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i+2][j-1]*side<=0)moves.push("N" + getMoveStringCoord(i,j,i+2,j-1));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i-1][j-2]*side<=0)moves.push("N" + getMoveStringCoord(i,j,i-1,j-2));}catch(ArrayIndexOutOfBoundsException e){}
			try{if(board[i-2][j-1]*side<=0)moves.push("N" + getMoveStringCoord(i,j,i-2,j-1));}catch(ArrayIndexOutOfBoundsException e){}
		}
		//pawn
		if(piece == playerPawn || piece == oppPawn){
			int pawnMove = 1;
			int doubleRow = 1;
			if(isWhite && side>0 || !isWhite && side<0){
				pawnMove = -1;
				doubleRow = 6;
			}
			if(i == doubleRow && board[i+2*pawnMove][j] == noPiece)
				moves.push("P" + getMoveStringCoord(i,j,i+2*pawnMove,j));
			try{
				if(board[i+pawnMove][j] == noPiece){
					if(i+pawnMove == 0 || i+pawnMove == 7)
						moves.push("P" + getMoveStringCoord(i,j,i+pawnMove,j)+"Q");
					else
						moves.push("P" + getMoveStringCoord(i,j,i+pawnMove,j));
				}
			}catch(ArrayIndexOutOfBoundsException e){}
			
			try{
				if(board[i+pawnMove][j+1]*side < 0){
					if(i+pawnMove == 0 || i+pawnMove == 7)
						moves.push("P" + getMoveStringCoord(i,j,i+pawnMove,j+1)+"Q");
					else
						moves.push("P" + getMoveStringCoord(i,j,i+pawnMove,j+1));
				}
			}catch(ArrayIndexOutOfBoundsException e){}
			
			try{
				if(board[i + pawnMove][j-1]*side < 0){
					if(i+pawnMove == 0 || i+pawnMove == 7)
						moves.push("P" + getMoveStringCoord(i,j,i+pawnMove,j-1)+"Q");
					else
						moves.push("P" + getMoveStringCoord(i,j,i+pawnMove,j-1));
				}
			}catch(ArrayIndexOutOfBoundsException e){}
		}
		return moves;
	}
	
	
	private String getMoveStringCoord(int i,int j, int k, int l){
		return ""+(char)('a'+j) + ((i-8)*-1) + (char)(l+'a') + ((k-8)*-1);
	}
	
	//to be used when defeat is imminent
	public void flipBoard(){
		for(int i = 0;i < 8;i++){
			for(int j = 0;j < 8;j++)
				board[i][j] = noPiece;
		}
	}
	
	public void play(){
		String poll = "http://www.bencarle.com/chess/poll/"+Board.gameID+"/"+Board.teamNumber+"/"+Board.teamSecret;
		ObjectMapper mapper = new ObjectMapper();
		while(true){
			String line = "";
			Map<String,Object> data;
			
			try{URL url = new URL(poll);
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			line = reader.readLine();
			reader.close();
			data = mapper.readValue(line, Map.class);}catch(IOException e){System.out.println("read");continue;}
			double sl = (Double) data.get("secondsleft");
			if(sl < 300)
				Search.setMaxDepth(4);
			if((Boolean) data.get("ready")){
				String move = (String) data.get("lastMove");
				if(move != null)
					move(move);
				String moveString = Search.search(this,0,-1000000000,1000000000,1);
				move(moveString);
				String moveURL = "http://www.bencarle.com/chess/move/"+
				Board.gameID+"/"+Board.teamNumber+"/"+Board.teamSecret+"/"+moveString;
				
				try{URL url2 = new URL(moveURL);
				InputStream inputStream2 = url2.openConnection().getInputStream();
				inputStream2.close();}catch(IOException e){System.out.println("write");continue;}
			}
		}
	}
	
	public String toString(){
		String text = "";
		for(int i = 0;i < 8;i++){
			for(int j = 0;j < 8;j++)
				text += getPieceLetter(board[i][j]) + " ";
			text += "\n";
		}
		return text;
	}
	
	private String getPieceLetter(byte num){
		if(num == noPiece)
			return "0";
		if(num == playerPawn || num == oppPawn)
			return "P";
		if(num == playerKnight || num == oppKnight)
			return "N";
		if(num == playerBishop || num == oppBishop)
			return "B";
		if(num == playerRook || num == oppRook)
			return "R";
		if(num == playerQueen || num == oppQueen)
			return "Q";
		return "K";
	}
	
	public static void main(String[] args){
		
		String gameID = "1272"; // before running set these vars with values
		boolean isWhite = true; // from http://www.bencarle.com/chess/startgame
		
		Board board = new Board(gameID,isWhite);
		board.play();
//		board.move("Pe2e4");
//		board.move("Pe2e4");
//		board.move("Pe7e5");
//		board.move("Bf1c4");
//		board.move("Qd8h4");
//		board.move("Qd1f3");
//		System.out.println(board);
//		System.out.println(Search.search(board,0,-1000000000,1000000000,1));
	}
}
