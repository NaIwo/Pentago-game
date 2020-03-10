/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.naiveplayer;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

import javax.lang.model.type.NullType;

@SuppressWarnings("Duplicates")
public class NaivePlayer extends Player {

    private Random random = new Random(0xdeadbeef);

    static int MAX = 1000;
    static int MIN = -1000;

    @Override
    public String getName() {
        return "Bartosz Przybył 136785 Iwo Naglik 136774";
    }


    @Override
    public Move nextMove(Board b) {
        Logger log = Logger.getLogger(NaivePlayer.class.getName()); //java.util.logging.Logger
        List<Move> moves = b.getMovesFor(getColor());
        int[] val = minmax(3, true, moves, MIN, MAX, b.clone(), getColor(),  getTime(), System.currentTimeMillis());
        return moves.get(val[1]);
    }

    private static int[] minmax(int depth, boolean max_player, List<Move> moves, int alpha, int beta, Board board, Color color, long time, long timeLeft) {

        Logger log = Logger.getLogger(NaivePlayer.class.getName()); //java.util.logging.Logger
        int[] out = new int[2];

        if (depth == 0 || (time - time/60 <= System.currentTimeMillis() - timeLeft)) {
            out[0] = heuristic(board, color);
            return out;
        }

        if (board.getWinner(color) == color) {
            out[0] = 1000;
            return out;
        }
        else if (board.getWinner(getOpponent(color)) == getOpponent(color)) {
            out[0] = -2000;
            return out;
        }

        if (max_player) {
            out[0] = Integer.MIN_VALUE;
            for (int i = 0; i < moves.size(); i++) {
                board.doMove(moves.get(i));
                int values[] = minmax(depth - 1, false, board.getMovesFor(getOpponent(color)), alpha, beta, board.clone(), color,  time, timeLeft);
                board.undoMove(moves.get(i));
                if (values[0] > out[0]) out[1] = i;
                out[0] = Math.max(out[0], values[0]);
                alpha = Math.max(alpha, out[0]);
                if (beta <= alpha) break;
            }
        } else {
            out[0] = Integer.MAX_VALUE;
            for (int i = 0; i < moves.size(); i++) {
                board.doMove(moves.get(i));
                int values[] = minmax(depth - 1, true, board.getMovesFor(color), alpha, beta, board.clone(), color,  time, timeLeft);
                board.undoMove(moves.get(i));
                if (values[0] < out[0]) out[1] = i;
                out[0] = Math.min(out[0], values[0]);
                beta = Math.min(beta, out[0]);
                if (beta <= alpha) break;
            }
        }

        return out;
    }

    // =============================== KONIEC MAXMIN ===============================================================================================


    private static int heuristic(Board board, Color myColor){
        Logger log = Logger.getLogger(NaivePlayer.class.getName());
        int quality = 0;
        int row_mine = 0;
        int row_opponent = 0;
        if (board.getWinner(myColor) == myColor) quality += 1000;
        else if (board.getWinner(getOpponent(myColor)) == getOpponent(myColor)) quality -= 2000;
        else {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if (board.getState(i, j) == getOpponent(myColor) && board.getState(i, j + 1) == getOpponent(myColor)) {
                        quality -= row_opponent + 1;
                        row_opponent++;
                    } else row_opponent = 0;

                    if(board.getState(i, j) == myColor && board.getState(i, j+1) == myColor) {
                        quality += row_mine + 0.5;
                        row_mine += 0.5;
                    } else row_mine = 0;
                }
            }

            row_mine = 0;
            row_opponent = 0;

            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if (board.getState(j, i) == getOpponent(myColor) && board.getState(j, i + 1) == getOpponent(myColor)) {
                        quality -= row_opponent + 1;
                        row_opponent++;
                    } else row_opponent = 0;

                    if(board.getState(j, i) == myColor && board.getState(j, i+1) == myColor) {
                        quality += row_mine + 0.5;
                        row_mine += 0.5;
                    } else row_mine = 0;
                }
            }
        }

        //log.info(String.valueOf(quality));
        return quality;
    }
    public static boolean checker(Board b, int i, int j, Color color) {
        try {
            if (b.getState(i, j) == color)
                return true;
        } catch (IndexOutOfBoundsException e) {
            ;
        }
        return false;
    }

}





