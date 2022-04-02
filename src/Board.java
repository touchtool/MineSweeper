import java.util.Random;

public class Board {
    private Cell [][] cell;
    private int size;
    private int mineCount;
    private Random random= new Random();
    public Board(int size, int mineCount) {
        this.size = size;
        this.mineCount = mineCount;
        initCell();
        seedMine();
        generateNumber();
    }

    public void initCell(){
        cell = new Cell[size][size];
        for (int row = 0; row < size; row++){
            for (int column = 0; column < size; column++){
                cell[row][column] = new Cell();
            }
        }
    }

    public void seedMine(){
        int seeded = 0;
        while (seeded < mineCount){
            int row = random.nextInt(size);
            int column = random.nextInt(size);
            Cell cell = getCell(row, column);
                if (cell.isMine()){
                    continue;
                }
                cell.setMine(true);
                seeded++;
        }
    }

    public void generateNumber(){
        for (int row = 0; row < size; row++){
            for (int column = 0; column < size; column++){
                Cell cell = getCell(row, column);
                if (cell.isMine()){
                    continue;
                }

                int [][] pairs = {
                        {-1, -1}, {-1, 0}, {-1, 1},
                        {0, -1},/* CELL */ {0, 1},
                        {1, -1}, {1, 0}, {1, 1}
                };
                int count = 0;
                for (int [] pair : pairs) {
                    Cell adj = getCell(row + pair[0], column + pair[1]);
                    if (adj != null && adj.isMine()){
                        count++;
                    }
                }
                cell.setAdjacentMines(count);
            }
        }
    }

    public void uncover(int row, int column) {
        Cell cell = getCell(row, column);
        if (cell == null) {
            return;
        }
        if (cell.isCovered()) {
            cell.setCovered(false);
            if (cell.getAdjacentMines() == 0 && !cell.isMine()) {
                int[][] pairs = {
                        {-1, -1}, {-1, 0}, {-1, 1},
                        {0, -1},/* CELL */ {0, 1},
                        {1, -1}, {1, 0}, {1, 1}
                };
                for (int[] pair : pairs) {
                    uncover(row + pair[0], column + pair[1]);
                }
            }
        }
    }

    public boolean mineUncovered() {
        for (int row = 0; row < size; row++){
            for (int column = 0; column < size; column++){
                Cell cell = getCell(row, column);
                if (cell.isMine() && !cell.isCovered()){
                    return true;
                }
            }
        }
        return false;
    }

    public Cell getCell(int row, int column){
        if (row < 0 || column < 0 || row >= size || column >= size){
            return null;
        }
        return cell[row][column];
    }
}
