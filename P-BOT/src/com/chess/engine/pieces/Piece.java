package src.com.chess.engine.pieces;

import java.util.List;

import src.com.chess.engine.Alliance;
import src.com.chess.engine.board.Board;
import src.com.chess.engine.board.Move;

public abstract class Piece {
    
    protected final int piecePosition;
    protected final Alliance pieceAlliance;

    Piece(final int piecePosition, final Alliance pieceAlliance){
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
    }

    public abstract List<Move>calculateLegalMoves(final Board board);
}


