
package homework2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
//import java.net.URI;
import java.net.URISyntaxException;
import java.net.*;
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
    int chance;
    int total_picked;// 0 for me 1 for opponent
//    int[][] picked_board = new int[Homework2.dimension][Homework2.dimension];

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
        score = (fruits_picked) * (fruits_picked) ;
    }
    public int nextChance(){
        return this.chance == 0 ? 1 : 0;
    }
    
    public boolean isGameOver(){
//        for(int i =0 ;i < Homework2.dimension; i++){
//            for (int j = 0; j < Homework2.dimension; j++) {
//                if(this.fruit_board[i][j] != -1){
//                    return false;
//                } 
//            }
//        }
//        return true;
        return this.total_picked == Homework2.all_fruit; 
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
    static int pruneCount = 0;
    static int childCount = 0;
    static int all_fruit = 0;
    static boolean depth_updated = false;
    static int available_child = 0;
    static int final_depth = -1;
    
    public static void main(String[] args) throws FileNotFoundException, IOException, CloneNotSupportedException, URISyntaxException {
        // TODO code application logic here
        String row;
        int[] r;
        String[] o;
        
                
        BufferedReader br =  new BufferedReader(new FileReader("input.txt"));

//        InputStream input = new URL( "file://192.168.1.1/shared_disk/input.txt" ).openStream();        
//        BufferedReader br =  new BufferedReader(new InputStreamReader(input));
        
        
        dimension = Integer.parseInt(br.readLine());
        all_fruit = dimension * dimension;
        fruit_type = Integer.parseInt(br.readLine());
        remaining_time = Double.parseDouble(br.readLine());
        FruitBoard initial_board = new FruitBoard(dimension);
        
        for (int i = 0; i < dimension; i++) {
            r = new int[dimension];
            row = br.readLine();
            o = row.split("");
            for (int j = 0; j < dimension; j++) {
                if(o[j].equals("*")){
                    r[j]=-1;
                    initial_board.total_picked++;
                }
                else{
                    r[j] = Integer.parseInt(o[j]);
                    available_child++;
//                    boolean not_considered = true;
//                    
//                    if(j != 0 && (initial_board.fruit_board[i][j-1] != r[j])){
//                       not_considered =  false;
//                       available_child++;
//                    }
//                    else if(not_considered && i != 0 && initial_board.fruit_board[i-1][j] != r[j]){ 
//                        available_child++;
//                    }
//                    else if(i==0 && j==0){
//                        available_child++;
//                    }

                }
            }
            initial_board.fruit_board[i] = r;
        }
        System.out.println("CHILDS : " + available_child);
        print_sol(initial_board);
        int[] p = new int[]{1};
        FruitBoard frb_new = (FruitBoard)initial_board.clone(initial_board.fruit_board,0);
        FruitBoard frb_new2 = (FruitBoard)initial_board.clone(initial_board.fruit_board,0);
        int depth = findDynamicDepth(available_child,remaining_time);
        System.out.println("ORIGINAL BOARD : ");
        print_sol(initial_board);
//        frb_new = playMiniMax(frb_new, 1);
        System.out.println("DEPTH :" + depth);
        frb_new = playAlphaBeta(frb_new, depth, -999999999, 999999999);
        frb_new = apply_gravity(frb_new,frb_new.updated_columns);
        System.out.println("ANSSSS : " + frb_new.score + " MOVE : " + frb_new.move_row+"," + frb_new.move_col + "Fruits Picked : "+ frb_new.fruits_picked);
        System.out.println("NEW BOARD : ");
        System.out.println("PRUNE : " + pruneCount);
        System.out.println("CHILD : " + childCount);
        print_sol(frb_new);
        
    }
    
    static void print_sol(FruitBoard frb) throws FileNotFoundException, UnsupportedEncodingException, IOException{
//        File file = new File("file://192.168.1.1/shared_disk/output.txt");
//        OutputStream file_writer_net = new FileOutputStream( new URL("file://192.168.1.1/shared_disk/output.txt" ).openStream());
        PrintWriter file_write = new PrintWriter("output.txt", "UTF-8");
//        PrintWriter file_writer_net = new PrintWriter( new FileOutputStream( file ));
        file_write.print((char)((65) + frb.move_col));
        file_write.println((frb.move_row + 1));
        System.out.print((char)((65) + frb.move_col));
        System.out.println((frb.move_row + 1));
//        file_writer_net.write((char)((65) + frb.move_col));
//        file_writer_net.write((frb.move_row + 1));
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
        System.out.println("Fruit :"+frb.fruits_picked);
        file_write.close();
//        copyFile("output.txt",  "file://192.168.1.1/shared_disk/output.txt");
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
//        if(frb.fruit_board[row][col] == num && visited[row][col] == 0  && frb.fruit_board[row][col] != -1){ 
        if(frb.fruit_board[row][col] == num && frb.fruit_board[row][col] != -1){ 
//            visited[row][col] = 1;
            frb.picked_board[row][col] = 1;
            frb.total_picked++;
//            System.out.println("PICKEDD" + row + col);
            frb.fruit_board[row][col] = -1;
//            if(update)
//            { 
//            }
            frb.updated_columns.add(col);
//            score += 1;
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
    
    static FruitBoard playAlphaBeta(FruitBoard frb, int depth, int alpha, int beta) throws CloneNotSupportedException, FileNotFoundException, UnsupportedEncodingException, IOException{
        boolean printing = false;
        boolean alpha_on = true;
        boolean call_dynamic_depth = false;
        int MAX_SCORE = Integer.MIN_VALUE, MIN_SCORE = Integer.MAX_VALUE;
        
        if(depth < 0 || frb.isGameOver()){
            return frb;
        }
        if(!depth_updated){
            depth_updated = true;
            available_child = 0;
            call_dynamic_depth = true;
        }
        FruitBoard historyBoard = (FruitBoard)frb.clone(frb.fruit_board, frb.nextChance());
        ArrayList<FruitBoard> child_list = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if(frb.fruit_board[i][j] != -1 && frb.picked_board[i][j] != 1){
                    if(call_dynamic_depth){
                        available_child++;
                    }
                    childCount++;
                    FruitBoard new_frb = (FruitBoard)frb.clone(frb.fruit_board,frb.nextChance());
                    new_frb = pick_and_update_number(new_frb, new_frb.fruit_board[i][j], i, j, true,new int[dimension][dimension],0);
                    historyBoard = pick_and_update_number(historyBoard, historyBoard.fruit_board[i][j], i, j, true,new int[dimension][dimension],0);
                    new_frb = apply_gravity(new_frb,new_frb.updated_columns);
                    for (int p = 0; p < dimension; p++) {
                        for (int q = 0; q < dimension; q++) {
                            if(new_frb.picked_board[p][q] == 1){
                                frb.picked_board[p][q] = new_frb.picked_board[p][q];
                            } 
                        }
                    }
                    new_frb.fruits_picked=0;
                    new_frb.move_col = j;
                    new_frb.move_row = i;
                    child_list.add(new_frb);
                    
                }
            }
        }
        if(call_dynamic_depth){
            final_depth = findDynamicDepth(available_child,remaining_time);
            depth = final_depth; 
        }
        Collections.sort(child_list, (FruitBoard p1, FruitBoard p2) -> p2.score - p1.score);
        FruitBoard bestBoard = (FruitBoard)frb.clone(frb.fruit_board, frb.nextChance());
        for (FruitBoard new_frb : child_list){
            frb.fruits_picked = 0;
            new_frb.score = frb.chance  == 0  ? frb.score + new_frb.score : + frb.score  - new_frb.score;
            FruitBoard return_board = playAlphaBeta(new_frb, depth - 1, alpha, beta );
            new_frb.score = frb.chance  == 0  ? return_board.score + new_frb.score : return_board.score - new_frb.score;

            boolean alpha_cut = false, beta_cut = false;

            if(frb.chance  == 0 && new_frb.score > MAX_SCORE){
                MAX_SCORE = new_frb.score;
                if (MAX_SCORE >= beta){
                    beta_cut = true;
                }
                alpha = Math.max(alpha, MAX_SCORE);
                bestBoard = new_frb;
            }
            if(frb.chance  == 1 && new_frb.score < MIN_SCORE){
                MIN_SCORE = new_frb.score;
                if (MIN_SCORE <= alpha){
                    alpha_cut = true;
                }
                beta = Math.min(beta, MIN_SCORE);
                bestBoard = new_frb;
            }
            if (alpha_on && (beta_cut || alpha_cut)) {
                pruneCount++;
                return bestBoard;
            }
        }
        return bestBoard;
    }
    
    
    static int findDynamicDepth(int available_moves,double remaining_time) throws FileNotFoundException, IOException{
        if(available_moves == 1) return 1;

        double perNodeTime = remaining_time / (available_moves/2);
            
        System.out.println("remaining:" + remaining_time);
        System.out.println("available :" + available_moves);
        System.out.println("perNode:" + perNodeTime);
        BufferedReader br =  new BufferedReader(new FileReader("caliberate.txt"));
        double timePerNodeCalculated =  Double.parseDouble(br.readLine());
        System.out.println("perNodeCalc:" + timePerNodeCalculated);
        double depth = ((Math.log(((perNodeTime / timePerNodeCalculated) * (available_moves - 1)) + 1)/ Math.log(available_moves)) - 1); 
//        double depth = ((Math.log(((perNodeTime / timePerNodeCalculated) * (available_moves- 1)))/ Math.log(available_moves - 1))); 
        System.out.println("DEPTH FLOAT : " + depth);
        if(depth < 0)
            return 1;
        if(depth >= available_moves)
            return available_moves;
//        depth = 3;
        return (int)depth;
    }
}
