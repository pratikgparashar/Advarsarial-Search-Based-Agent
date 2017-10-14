
package homework2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;
import  java.util.*;


/**
 *
 * @author prati
 */
class FruitBoard implements Cloneable{
    int[][] fruit_board;
    int[][] picked_board;
    int move_row, move_col;
    TreeSet<Integer> updated_columns; 
    int score;
    int fruits_picked;
    int chance; // 0 for me 1 for opponent


    public Object clone(int [][] matrix, int ch) throws CloneNotSupportedException
    {
        FruitBoard frb_new = (FruitBoard)super.clone();
        frb_new.fruit_board = new int[Homework2.dimension][Homework2.dimension];
        for(int i =0 ;i < Homework2.dimension; i++){
            for (int j = 0; j < Homework2.dimension; j++) {
                frb_new.fruit_board[i][j] = matrix[i][j];
            }
        }
        frb_new.picked_board = new int[Homework2.dimension][Homework2.dimension];
        frb_new.move_row = 0;
        frb_new.move_col = 0;
        frb_new.updated_columns = new TreeSet<Integer>();
//        frb_new.score = 0;
        frb_new.fruits_picked = 0;
        frb_new.chance = ch;
        return frb_new;
    }
    
    public FruitBoard(int dim) {
        fruit_board = new int[dim][dim];
        updated_columns = new TreeSet<Integer>();
        score = 0;
    }
    
    public void score(){
        fruits_picked += 1;
        score = (fruits_picked ) * (fruits_picked);
    }
    public int nextChance(){
        return this.chance == 0 ? 1 : 0;
    }
    
    public boolean isGameOver(){
        for(int i =0 ;i < Homework2.dimension; i++){
            for (int j = 0; j < Homework2.dimension; j++) {
                if(this.picked_board[i][j] != -1){
                    return false;
                } 
            }
        }
        return true;
    }
}

public class Homework2 {

    /**
     * @param args the command line arguments
     */
    static int dimension;
    static int fruit_type;
    static double remaining_time;
//    static HashMap<Double, ArrayList<Integer[2]>> min_max_table=  new HashMap<Double, ArrayList<Integer[2]>>();
    
    public static void main(String[] args) throws FileNotFoundException, IOException, CloneNotSupportedException {
        // TODO code application logic here
        String row;
        int[] r;
        String[] o;
                
        BufferedReader br =  new BufferedReader(new FileReader("input.txt"));
        dimension = Integer.parseInt(br.readLine());
        fruit_type = Integer.parseInt(br.readLine());
        remaining_time = Double.parseDouble(br.readLine());
        FruitBoard initial_board = new FruitBoard(dimension);
        int available_child = 0;
        for (int i = 0; i < dimension; i++) {
            r = new int[dimension];
            row = br.readLine();
            o = row.split("");
            for (int j = 0; j < dimension; j++) {
                if(o[j].equals("*")){
                    r[j]=-1;
                }
                else{
                    r[j] = Integer.parseInt(o[j]);
                    available_child++;
                }
            }
            initial_board.fruit_board[i] = r;
        }
        print_sol(initial_board);
        int[] p = new int[]{1};
        FruitBoard frb_new = (FruitBoard)initial_board.clone(initial_board.fruit_board,0);
        int depth = findDynamicDepth(available_child,remaining_time * 1000);
        System.out.println("ORIGINAL BOARD : ");
        print_sol(initial_board);
//        frb_new = playMiniMax(frb_new, 1);
        System.out.println("DEPTH :" + depth);
        frb_new = playAlphaBeta(frb_new, depth, -999999999, 999999999);
        frb_new = apply_gravity(frb_new,frb_new.updated_columns);
        System.out.println("ANSSSS : " + frb_new.score + " MOVE : " + frb_new.move_row+"," + frb_new.move_col + "Fruits Picked : "+ frb_new.fruits_picked);
        System.out.println("NEW BOARD : ");
        print_sol(frb_new);
    }
    
    static void print_sol(FruitBoard frb) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter file_write = new PrintWriter("output.txt", "UTF-8");
        file_write.print((char)((65) + frb.move_col));
        file_write.println((frb.move_row + 1));
        for(int[] row : frb.fruit_board){
            System.out.println(Arrays.toString(row));
//            file_write.print(Arrays.toString(row));
//            file_write.print("HELLP");
            
//            for(int col : row){
//                if(col == -1)
//                    file_write.print(col + " ");
//                else
//                    file_write.print(" "+col + " ");
//            }
            for(int col : row){
                if(col == -1)
                    file_write.print("*" + "");
                else
                    file_write.print(""+col + "");
            }
            file_write.println("");
        }
        System.out.println("Score :"+frb.score);
        file_write.close();
    }
    
    static FruitBoard apply_gravity(FruitBoard frb, TreeSet<Integer> affected_cols){
        for(int j : affected_cols){
            int top_star  = dimension;
            int last_star = -2;
            for (int i = dimension - 1 ; i >= 0; i--) {
//                System.out.println("I ka value : " + i + " VAL :" + frb.fruit_board[i][j]);
                if(frb.fruit_board[i][j] == -1 ){
//                    System.out.println("* mila : " + i + " " + j);
                    top_star = i;
                    if(last_star == -2){
//                        System.out.println("LAST STAR UPDATE HUA");
                        last_star = i;
                    }
                }
                else{
//                    System.out.println("NUMBER MILA ,Top Star :" + top_star + " last_star :" + last_star);
                    for(int k = last_star, l = top_star - 1; k >= top_star && l >= 0; k--,l-- ){
//                        System.out.println("K : " + k + "  L :" + l);
                        if(frb.fruit_board[l][j] == -1){
                            break;
                        }
                        else{
//                            System.out.println("NUMBRE SWAP HUA : " +k+","+j + "&&" +l+","+j);
                            frb.fruit_board[k][j] = frb.fruit_board[l][j];
                            frb.fruit_board[l][j] = -1;
                            if(i !=  dimension-1) i++;
                        }
                    }
                    last_star = -2;
                }
            }
        }
//        System.out.println("GRVITY APPLY HUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return frb;
    }
    
    static FruitBoard pick_and_update_number(FruitBoard frb,int num, int row, int col, boolean update, int [][] visited, int score){
        if(frb.fruit_board[row][col] == num && visited[row][col] == 0  && frb.fruit_board[row][col] != -1){ 
            visited[row][col] = 1;
            frb.picked_board[row][col] = 1;
//            System.out.println("PICKEDD" + row + col);
            if(update)
            { 
                frb.fruit_board[row][col] = -1;
            }
            frb.updated_columns.add(col);
            score += 1;
//            System.out.println("NEW SCORE :###############################" + score);
//            frb.fruits_picked = score;
            frb.score(); 
//            frb.score = score;
            if(row !=0){
                frb = pick_and_update_number(frb, num, row-1, col,update, visited, score);
            }
            if(col != 0){
                frb = pick_and_update_number(frb, num, row, col - 1,update, visited, score);
            }
            if(row != dimension - 1){
                frb = pick_and_update_number(frb, num, row+1, col,update, visited, score);
            }
            if(col != dimension - 1){
                frb = pick_and_update_number(frb, num, row, col+1, update, visited, score);
            }
        }    
        
        return frb;
    }
    
    static FruitBoard playAlphaBeta(FruitBoard frb, int depth, int alpha, int beta) throws CloneNotSupportedException, FileNotFoundException, UnsupportedEncodingException{
        boolean printing = false;
        boolean alpha_on = true;
        if(printing){
        System.out.println("----INPUT------");
        System.out.println("Depth : "+ depth + " CHANCE : " + frb.chance + " NEXT CHANCE : " + frb.nextChance());
        print_sol(frb);
        System.out.println("//----INPUT------//");
        }
        int MAX_SCORE = Integer.MIN_VALUE, MIN_SCORE = Integer.MAX_VALUE;
        if(printing) System.out.println("AT CALL HASH CODE : " + frb.hashCode()  + "MAX : " + MAX_SCORE + "MIN : " + MIN_SCORE);
        if(depth == 0|| frb.isGameOver()){
            return frb;
        }
        FruitBoard bestBoard = (FruitBoard)frb.clone(frb.fruit_board, frb.nextChance());
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if(frb.fruit_board[i][j] != -1 && frb.picked_board[i][j] != 1){
                    if(printing){
                        System.out.println("AT CHILD LOOP HASH CODE : " + frb.hashCode()  + "MAX : " + MAX_SCORE + "MIN : " + MIN_SCORE);
                        System.out.println("BAAP KA HASH : " + frb.hashCode());
                    }
                    FruitBoard new_frb = (FruitBoard)frb.clone(frb.fruit_board,frb.nextChance());
                    new_frb = pick_and_update_number(new_frb, new_frb.fruit_board[i][j], i, j, true,new int[dimension][dimension],0);
                    new_frb = apply_gravity(new_frb,new_frb.updated_columns);
                    
                    if(printing) System.out.println("----CHILD------ MOVE : " + i + ", " + j + " HASH :" + new_frb.hashCode());
                    if(printing) print_sol(new_frb);
                    
                    frb.fruits_picked = 0;
                    new_frb.fruits_picked=0;
                    new_frb.move_col = j;
                    new_frb.move_row = i;
                    FruitBoard return_board = playAlphaBeta(new_frb, depth - 1, alpha, beta );
                    
                    
                    if(printing) System.out.println("RETURN ==  NEW FRB +================== : " + (return_board.hashCode() == new_frb.hashCode()));
                    
//                    if(frb.chance  == 0){
//                        new_frb.score =  return_board.score + new_frb.score;
//                    }
//                    else{
//                        new_frb.score =  return_board.score - new_frb.score;
//                    }
                    new_frb.score = frb.chance  == 0  ? return_board.score + new_frb.score : return_board.score - new_frb.score;
                    
                    if(printing){
                        System.out.println("RETURN SCORES : "  + return_board.score + " For Hash : " + frb.hashCode() );
                        print_sol(return_board);
                    }
                    boolean alpha_cut = false, beta_cut = false;

                    if(frb.chance  == 0 && new_frb.score > MAX_SCORE){
                        MAX_SCORE = new_frb.score;
                        if (MAX_SCORE >= beta){
                            beta_cut = true;
                        }
                        if(printing)System.out.println("AT MAX_UPDATE HASH CODE : " + frb.hashCode()  + "MAX : " + MAX_SCORE + "MIN : " + MIN_SCORE);
                        alpha = Math.max(alpha, MAX_SCORE);
                        bestBoard = new_frb;
                    }
                    if(frb.chance  == 1 && new_frb.score < MIN_SCORE){
                        MIN_SCORE = new_frb.score;
                        if (MIN_SCORE <= alpha){
                            alpha_cut = true;
                        }
                        if(printing)System.out.println("AT MIN_UPDATE HASH CODE : " + frb.hashCode()  + "MAX : " + MAX_SCORE + "MIN : " + MIN_SCORE);
                        beta = Math.min(beta, MIN_SCORE);
                        bestBoard = new_frb;
                    }
                    if(printing)System.out.println("//----CHILD------// "+" HASH :" + new_frb.hashCode());
                    if (alpha_on && (beta_cut || alpha_cut)) {
                        if(printing)System.out.println("PRUNEDDDD");
                        bestBoard.fruits_picked = 0;
                        bestBoard = pick_and_update_number(bestBoard, bestBoard.fruit_board[bestBoard.move_row][bestBoard.move_col], bestBoard.move_row, bestBoard.move_col, true,new int[dimension][dimension],0);
                        bestBoard = apply_gravity(bestBoard,bestBoard.updated_columns);
                        return bestBoard;
                    }
                }
            }
        }
        if(printing){
            System.out.println("----RETURN NORMAL : ------");
            print_sol(bestBoard);
            System.out.println("/////RETURN NORMAL : /////");
        }
        return bestBoard;
    }
    
    static int findDynamicDepth(int available_moves,double remaining_time) throws FileNotFoundException, IOException{
        if(available_moves == 1){
            return 1;
        }
//        double perNodeTime = remaining_time / (available_moves/2);
        double perNodeTime = remaining_time / (available_moves);
        BufferedReader br =  new BufferedReader(new FileReader("caliberate.txt"));
        double timePerNodeCalculated =  Double.parseDouble(br.readLine());
//        double depth = ((Math.log(((perNodeTime / timePerNodeCalculated) * (available_moves - 1)) + 1)/ Math.log(available_moves)) - 1); 
        double depth = ((Math.log(((perNodeTime / timePerNodeCalculated) * (available_moves)) + 1)/ Math.log(available_moves)) - 1); 
        if(depth < 0)
            return 1;
        if(depth >= available_moves)
            return available_moves;
        depth = 3;
        return (int)depth;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    static FruitBoard playMiniMax(FruitBoard frb, int depth) throws CloneNotSupportedException, FileNotFoundException, UnsupportedEncodingException{
//        System.out.println("INPUT FOR MIN MAX: " + "CHANCE : " + frb.chance + " NEXT CHANCE : " + frb.nextChance());
//        print_sol(frb);
        int MAX_SCORE = -999999999, MIN_SCORE = 999999999;
        if(depth == 0 || frb.isGameOver()){
            return frb;
        }
        FruitBoard bestBoard = (FruitBoard)frb.clone(frb.fruit_board, frb.nextChance());
        if(frb.chance != 4){
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if(frb.fruit_board[i][j] != -1 && frb.picked_board[i][j] != 1 ){
                        int prev_score =  frb.score;
                        FruitBoard new_frb = (FruitBoard)frb.clone(frb.fruit_board,frb.nextChance());
//                        System.out.println( "CALLLL TO PICK with" +i+j);
                        frb = pick_and_update_number(frb, frb.fruit_board[i][j], i, j, false,new int[dimension][dimension],0);
//                        new_frb.score = 0;
//                        new_frb.fruits_picked = 0;
                        if(frb.chance  == 0){
                            new_frb.score += frb.score;
                        }
                        else{
                            new_frb.score -= frb.score;
                        }
                        new_frb.move_col = j;
                        new_frb.move_row = i;
//                        System.out.println("FRUITS PICKING UP: " + frb.fruits_picked);
//                        System.out.println(" NEW_FRB == OLD_FRB "  + (new_frb.hashCode() == frb.hashCode()) + "NEXT CHANCE :" + new_frb.chance + "CURR SCORE : " + new_frb.score);
                        FruitBoard return_board = playMiniMax(new_frb, depth - 1 );
                        if(frb.chance  == 0 && return_board.score > MAX_SCORE){
//                            System.out.println("FRUITS MAXIMUM: " + return_board.fruits_picked);
                            MAX_SCORE = return_board.score;
                            bestBoard = return_board;
                        }
                        if(frb.chance  == 1 && return_board.score < MIN_SCORE){
                            System.out.println("FRUITS MIN: " + return_board.fruits_picked);
                            MIN_SCORE = return_board.score;
                            bestBoard = return_board;
                        }   
                    }
                }
            }
            bestBoard.fruits_picked = 0;
            bestBoard = pick_and_update_number(bestBoard, bestBoard.fruit_board[bestBoard.move_row][bestBoard.move_col], bestBoard.move_row, bestBoard.move_col, true,new int[dimension][dimension],0);
            bestBoard = apply_gravity(bestBoard,bestBoard.updated_columns);
            return bestBoard;
        }
        else if(frb.chance == 2){
//            FruitBoard bestBoard = (FruitBoard)frb.clone(frb.fruit_board, frb.nextChance());
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if(frb.fruit_board[i][j] != -1){
//                        frb = pick_and_update_number(frb, frb.fruit_board[i][j], i, j, false, new int[dimension][dimension],0);
                        System.out.println( "CALLLL TO PICK 2222");
                        FruitBoard new_frb = (FruitBoard)frb.clone(frb.fruit_board,frb.nextChance());
//                        new_frb.score = 0;
//                        new_frb.fruits_picked = 0;
                        FruitBoard return_board = playMiniMax(new_frb, depth - 1 );
                        if(return_board.score < MIN_SCORE){
                            return_board.move_col = j;
                            return_board.move_row = i;
                            MIN_SCORE = return_board.score;
                            bestBoard = return_board;
                        }    
                    }
                }
            }
            System.out.println( "CALLLL TO PICK 222233333333333333333");
            bestBoard = pick_and_update_number(bestBoard, bestBoard.fruit_board[bestBoard.move_row][bestBoard.move_col], bestBoard.move_row, bestBoard.move_col, true, new int[dimension][dimension],0);
            bestBoard = apply_gravity(bestBoard,bestBoard.updated_columns);
            return bestBoard;
        }
        return frb;
    }
    
//    static FruitBoard playAlphaBeta(FruitBoard frb, int depth, int alpha, int beta) throws CloneNotSupportedException, FileNotFoundException, UnsupportedEncodingException{
//        System.out.println("----INPUT------");
//            System.out.println("Depth : "+ depth + " CHANCE : " + frb.chance + " NEXT CHANCE : " + frb.nextChance());
//            print_sol(frb);
//        System.out.println("//----INPUT------//");
//        int MAX_SCORE = -999999999, MIN_SCORE = 999999999;
//        System.out.println("AT CALL HASH CODE : " + frb.hashCode()  + "MAX : " + MAX_SCORE + "MIN : " + MIN_SCORE);
//        if(depth == 0|| frb.isGameOver()){
////            frb = pick_and_update_number(frb, frb.fruit_board[frb.move_row][frb.move_col], frb.move_row, frb.move_col, false,new int[dimension][dimension],0);
//            return frb;
//        }
//        FruitBoard bestBoard = (FruitBoard)frb.clone(frb.fruit_board, frb.nextChance());
//        for (int i = 0; i < dimension; i++) {
//            for (int j = 0; j < dimension; j++) {
//                if(frb.fruit_board[i][j] != -1 ){//&& frb.picked_board[i][j] != 1){
//                    System.out.println("AT CHILD LOOP HASH CODE : " + frb.hashCode()  + "MAX : " + MAX_SCORE + "MIN : " + MIN_SCORE);
//                    System.out.println("BAAP KA HASH : " + frb.hashCode());
//                    int prev_score =  frb.score;
//                    FruitBoard new_frb = (FruitBoard)frb.clone(frb.fruit_board,frb.nextChance());
////                        System.out.println( "CALLLL TO PICK with" +i+j);
////                    frb = pick_and_update_number(frb, frb.fruit_board[i][j], i, j, false,new int[dimension][dimension],0);
//                    new_frb = pick_and_update_number(new_frb, new_frb.fruit_board[i][j], i, j, true,new int[dimension][dimension],0);
//                    new_frb = apply_gravity(new_frb,new_frb.updated_columns);
//                    System.out.println("----CHILD------ MOVE : " + i + ", " + j + " HASH :" + new_frb.hashCode());
//                    print_sol(new_frb);
////                        new_frb.score = 0;
////                        new_frb.fruits_picked = 0;
////                    System.out.println("FRUITS PICKING UP: " + frb.fruits_picked + "FOR CHANCE : " + frb.chance + " Depth :" + depth);
////                    if(frb.chance  == 0){
////                        new_frb.score = prev_score - new_frb.score;
////                    }
////                    else{
////                        new_frb.score = prev_score + new_frb.score;
////                    }
//                    frb.fruits_picked = 0;
//                    new_frb.fruits_picked=0;
//                    new_frb.move_col = j;
//                    new_frb.move_row = i;
//                    FruitBoard return_board = playAlphaBeta(new_frb, depth - 1, alpha, beta );
//                    System.out.println("RETURN ==  NEW FRB +================== : " + (return_board.hashCode() == new_frb.hashCode()));
////                    System.out.println("Depth : "+ depth + " CHANCE : " + frb.chance + " NEXT CHANCE : " + frb.nextChance());
//                    if(frb.chance  == 0){
//                        new_frb.score =  return_board.score + new_frb.score;
//                    }
//                    else{
//                        new_frb.score =  return_board.score - new_frb.score;
//                    }
//                    System.out.println("RETURN SCORES : "  + return_board.score + " For Hash : " + frb.hashCode() );
//                    print_sol(return_board);
//                    boolean alpha_cut = false, beta_cut = false;
//
//                    if(frb.chance  == 0 && new_frb.score > MAX_SCORE){
////                        System.out.println("FRUITS MAXIMUM: " + return_board.fruits_picked);
//                        MAX_SCORE = new_frb.score;
//                        if (MAX_SCORE >= beta){
//                            beta_cut = true;
//                        }
//                        System.out.println("AT MAX_UPDATE HASH CODE : " + frb.hashCode()  + "MAX : " + MAX_SCORE + "MIN : " + MIN_SCORE);
////                        System.out.println("### UPDATE SCORES MAX: "  + return_board.score +" Old Score: " + bestBoard.score+ " With Move : " + return_board.move_row + return_board.move_col);
//                        alpha = Math.max(alpha, MAX_SCORE);
//                        bestBoard = new_frb;
//                    }
//                    if(frb.chance  == 1 && new_frb.score < MIN_SCORE){
////                        System.out.println("FRUITS MIN: " + return_board.fruits_picked);
//                        MIN_SCORE = new_frb.score;
//                        if (MIN_SCORE <= alpha){
//                            alpha_cut = true;
//                        }
//                        System.out.println("AT MIN_UPDATE HASH CODE : " + frb.hashCode()  + "MAX : " + MAX_SCORE + "MIN : " + MIN_SCORE);
////                        System.out.println("### UPDATE SCORES MIN: "  + return_board.score);
//                        beta = Math.min(beta, MIN_SCORE);
//                        bestBoard = new_frb;
//                    }
//                    System.out.println("//----CHILD------// "+" HASH :" + new_frb.hashCode());
////                    if (beta_cut || alpha_cut) {
////                        System.out.println("PRUNEDDDD");
////                        bestBoard.fruits_picked = 0;
////                        bestBoard = pick_and_update_number(bestBoard, bestBoard.fruit_board[bestBoard.move_row][bestBoard.move_col], bestBoard.move_row, bestBoard.move_col, true,new int[dimension][dimension],0);
////                        bestBoard = apply_gravity(bestBoard,bestBoard.updated_columns);
////                        return bestBoard;
////                    }
//                }
//            }
//        }
//        System.out.println("----RETURN NORMAL : ------");
////        bestBoard.fruits_picked = 0;
////        bestBoard = pick_and_update_number(bestBoard, bestBoard.fruit_board[bestBoard.move_row][bestBoard.move_col], bestBoard.move_row, bestBoard.move_col, true,new int[dimension][dimension],0);
////        bestBoard = apply_gravity(bestBoard,bestBoard.updated_columns);
//        print_sol(bestBoard);
//        System.out.println("/////RETURN NORMAL : /////");
//        return bestBoard;
//    }
}
