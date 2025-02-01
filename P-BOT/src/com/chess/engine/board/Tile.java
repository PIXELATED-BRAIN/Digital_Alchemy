package src.com.chess.engine.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import src.com.chess.engine.pieces.Piece;

public abstract class Tile {

   protected final int tileCoordinate;

   private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = creatAllPossibleEmptyTiles();

   private static Map<Integer, EmptyTile> creatAllPossibleEmptyTiles(){

        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for(int i = 0; i < 64; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }
                return Collections.unmodifiableMap(emptyTileMap);
    }

    public static Tile creaTile(final int tileCoordinate, final Piece piece){
        return piece != null ? new OccupiedTile(tileCoordinate, piece): EMPTY_TILES_CACHE.get(tileCoordinate);
    }
    
    private Tile(int tileCoordinate){
           this.tileCoordinate=tileCoordinate;
    }
   
    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile{

        EmptyTile(final int coordinate){
            super(coordinate);
        }

        @Override
        public boolean isTileOccupied(){
            return false;
        }

        @Override
        public Piece getPiece(){
            return null;
        }
    }

    public static final class OccupiedTile extends Tile{
    
        private final Piece pieceOnTile;

        OccupiedTile(int tileCoordinate, Piece pieceOnTile){
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }
          
        @Override
        public boolean isTileOccupied(){
            return true;
        }

        @Override
        public Piece getPiece(){
            return this.pieceOnTile;
        }
    }
}
