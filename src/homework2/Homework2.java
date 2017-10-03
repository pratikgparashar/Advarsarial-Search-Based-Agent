
package homework2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;


/**
 *
 * @author prati
 */
class FruitBoard implements Cloneable{
    int[][] fruit_board;
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
        frb_new.move_row = 0;
        frb_new.move_col = 0;
        frb_new.updated_columns = new TreeSet<Integer>();
        frb_new.score = 0;
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
        this.score = (fruits_picked ) * (fruits_picked);
    }
}

public class Homework2 {

    /**
     * @param args the command line arguments
     */
    static int dimension;
    static int fruit_type;
    static double remaining_time;
    
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
                }
            }
            initial_board.fruit_board[i] = r;
//            row = row.replace("*", "-1");
//            r = Stream.of(o).mapToInt(n -> Integer.parseInt(n)).toArray();
        }
        print_sol(initial_board);
        int[] p = new int[]{1};
        FruitBoard frb_new = (FruitBoard)initial_board.clone(initial_board.fruit_board,0);
        frb_new = pick_and_update_number(frb_new,0,8,6,true);
//        int[] update_columns_n = new int[dimension];
//        update_columns_n = .toArray();
        frb_new = apply_gravity(frb_new,frb_new.updated_columns);
        System.out.println("ORIGINAL BOARD : ");
        print_sol(initial_board);
        System.out.println("NEW BOARD : ");
        print_sol(frb_new);
        System.out.println(frb_new.fruit_board == initial_board.fruit_board);
        System.out.println(frb_new == initial_board);
        System.out.println(frb_new.fruits_picked == initial_board.fruits_picked);
        System.out.println(frb_new.score == initial_board.score);
        System.out.println(frb_new.move_col == initial_board.move_col);
        System.out.println(frb_new.updated_columns == initial_board.updated_columns);
    }
    
    static void print_sol(FruitBoard frb) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter file_write = new PrintWriter("output.txt", "UTF-8");
        file_write.println("OUTPUT FILE");
        file_write.print(frb.move_col);
        file_write.println(frb.move_row);
        for(int[] row : frb.fruit_board){
            System.out.println(Arrays.toString(row));
//            file_write.print(Arrays.toString(row));
//            file_write.print("HELLP");
            
            for(int col : row){
                if(col == -1)
                    file_write.print(col + " ");
                else
                    file_write.print(" "+col + " ");
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
                System.out.println("I ka value : " + i + " VAL :" + frb.fruit_board[i][j]);
                if(frb.fruit_board[i][j] == -1 ){
                    System.out.println("* mila : " + i + " " + j);
                    top_star = i;
                    if(last_star == -2){
                        System.out.println("LAST STAR UPDATE HUA");
                        last_star = i;
                    }
                }
                else{
                    System.out.println("NUMBER MILA ,Top Star :" + top_star + " last_star :" + last_star);
                    for(int k = last_star, l = top_star - 1; k >= top_star && l >= 0; k--,l-- ){
                        System.out.println("K : " + k + "  L :" + l);
                        if(frb.fruit_board[l][j] == -1){
                            break;
                        }
                        else{
                            System.out.println("NUMBRE SWAP HUA : " +k+","+j + "&&" +l+","+j);
                            frb.fruit_board[k][j] = frb.fruit_board[l][j];
                            frb.fruit_board[l][j] = -1;
                            if(i !=  dimension-1) i++;
                        }
                    }
                    last_star = -2;
                }
            }
        }
        return frb;
    }
    
    static FruitBoard pick_and_update_number(FruitBoard frb,int num, int row, int col, boolean update){
        if(frb.fruit_board[row][col] == num){ 
            if(update) frb.fruit_board[row][col] = -1;
            frb.updated_columns.add(col);
            frb.score(); 
            if(row !=0){
                frb = pick_and_update_number(frb, num, row-1, col,update);
            }
            if(col != 0){
                frb = pick_and_update_number(frb, num, row, col - 1,update);
            }
            if(row != dimension - 1){
                frb = pick_and_update_number(frb, num, row+1, col,update);
            }
            if(col != dimension - 1){
                frb = pick_and_update_number(frb, num, row, col+1, update);
            }
        }    
        
        return frb;
    }
    
    static FruitBoard playMiniMax(FruitBoard frb, int depth){
        int MAX_SCORE = -999999999, MIN_SCORE = 999999999;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if(frb.fruit_board[i][j] != -1){
                }
            }
        }
        return frb;
    }
}
