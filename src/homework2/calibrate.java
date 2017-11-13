//
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.TreeSet;
//
///**
// *
// * @author prati
// */
//class FruitBoard implements Cloneable{
//    int[][] fruit_board;
//    int[][] picked_board;
//    int move_row, move_col;
//    TreeSet<Integer> updated_columns; 
//    int score;
//    int fruits_picked;
//    int chance; // 0 for me 1 for opponent
//
//
//    public Object clone(int [][] matrix, int ch) throws CloneNotSupportedException
//    {
//        FruitBoard frb_new = (FruitBoard)super.clone();
//        frb_new.fruit_board = new int[calibrate.dimension][calibrate.dimension];
//        for(int i =0 ;i < calibrate.dimension; i++){
//            for (int j = 0; j < calibrate.dimension; j++) {
//                frb_new.fruit_board[i][j] = matrix[i][j];
//            }
//        }
//        frb_new.picked_board = new int[calibrate.dimension][calibrate.dimension];
//        frb_new.move_row = 0;
//        frb_new.move_col = 0;
//        frb_new.updated_columns = new TreeSet<Integer>();
////        frb_new.score = 0;
//        frb_new.fruits_picked = 0;
//        frb_new.chance = ch;
//        return frb_new;
//    }
//    
//    public FruitBoard(int dim) {
//        fruit_board = new int[dim][dim];
//        updated_columns = new TreeSet<Integer>();
//        score = 0;
//    }
//    
//    public void score(){
//        fruits_picked += 1;
//        score = (fruits_picked ) * (fruits_picked);
//    }
//    public int nextChance(){
//        return this.chance == 0 ? 1 : 0;
//    }
//    
//    public boolean isGameOver(){
//        for(int i =0 ;i < calibrate.dimension; i++){
//            for (int j = 0; j < calibrate.dimension; j++) {
//                if(this.picked_board[i][j] != -1){
//                    return false;
//                } 
//            }
//        }
//        return true;
//    }
//}
//public class calibrate {
//    static int dimension = 26;
//    public static void main(String[] args) throws FileNotFoundException, IOException, CloneNotSupportedException {
//        PrintWriter file_write = new PrintWriter("calibrate.txt", "UTF-8");
//        double node_count = 100000;
//        FruitBoard frb = new FruitBoard(dimension);
//        long startTime = System.nanoTime();
//        for (double i = 0; i < node_count; i++) {
//            FruitBoard frb_new = (FruitBoard)frb.clone(frb.fruit_board, dimension);
//        }
//        long endTime = System.nanoTime();
//        double node_creation_time = (endTime - startTime)/node_count;
//        file_write.print(node_creation_time /1000000000.0);
//        file_write.close();
//    }
//}
