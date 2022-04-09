import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Game extends JFrame {
    private Board board;
    private int boardSize = 20;
    private GridUI gridUI;
    private int mineCount = 40;
    private int flagCount = 40;
    private JButton restart;

    public Game(){
        setLayout(new BorderLayout());
        board = new Board(boardSize, mineCount);
        gridUI = new GridUI();
        restart = new JButton("Restart");
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board = new Board(boardSize, mineCount);
                gridUI = new GridUI();
                repaint();
            }
        });
        add(restart, BorderLayout.SOUTH);
        add(gridUI, BorderLayout.NORTH);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void start(){
//        restart.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                board = new Board(boardSize, mineCount);
//                gridUI = new GridUI();
//            }
//        });
//        add(restart);
//        pack();
        setVisible(true);
    }

    class GridUI extends JPanel{
        public static final int CELL_PIXEL_SIZE = 30;
        private Image imageCell;
        private Image imageFlag;
        private Image imageMine;

        public GridUI(){
            setPreferredSize(new Dimension(CELL_PIXEL_SIZE * boardSize, CELL_PIXEL_SIZE * boardSize));
            imageFlag = new ImageIcon("imgs/Flag.png").getImage();
            imageCell = new ImageIcon("imgs/Cell.png").getImage();
            imageMine = new ImageIcon("imgs/Mine.png").getImage();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    int row = e.getY() / CELL_PIXEL_SIZE;
                    int column = e.getX() / CELL_PIXEL_SIZE;
                    Cell cell = board.getCell(row, column);
                    int countCover = 0;
                    int countFlag = 0;
                    
                    if (!cell.isCovered()){
                        return;
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (countFlag < mineCount) {
                           if (cell.isCovered()){
                            cell.setFlagged(!cell.isFlagged());
                            if (cell.isFlagged() && flagCount > 0) {
                                flagCount--;
                            } else {
                                flagCount++;
                            }
                            } 
                        } else {
                            cell.setFlagged(false);
                        }
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        if (!cell.isFlagged()) {
                            board.uncover(row, column);
                            if (board.mineUncovered()){
                                JOptionPane.showMessageDialog(Game.this,
                                    "You lose!",
                                    "You hit the mine",
                                    JOptionPane.WARNING_MESSAGE);
                                board = new Board(boardSize, mineCount);
                                gridUI = new GridUI();
                            }
                        }
                    }
                    for (int tempRow = 0; tempRow < boardSize; tempRow++){
                        for (int tempColumn = 0; tempColumn < boardSize; tempColumn++){
                            Cell cellCondition = board.getCell(tempRow, tempColumn);
                            if (cellCondition.isCovered()){
                                countCover++;
                            }
                            if (cellCondition.isFlagged()) {
                                countFlag++;
                            }
                        }
                    }
                    if (countCover == mineCount) {
                        JOptionPane.showMessageDialog(Game.this,
                                "You win!",
                                "Congratulations",
                                JOptionPane.INFORMATION_MESSAGE);
                        board = new Board(boardSize, mineCount);
                        gridUI = new GridUI();
                    }
                    repaint();
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int row = 0; row < boardSize; row++){
                for (int column = 0; column < boardSize; column++){
                    paintCell(g, row, column);
                }
            }
            g.setFont(new Font("default", Font.BOLD, 16));
            g.setColor(Color.black);
            g.drawString("Flags: " + flagCount, 20, 20);
        }

        private void  paintCell(Graphics g, int row, int column){
            Color [] colors = {
                null,
                Color.blue.darker(),
                Color.green.darker(),
                Color.orange.darker(),
                Color.magenta.darker(),
                Color.red.darker(),
                Color.yellow.darker(),
                Color.pink.darker(),
                Color.black.darker()
            };
            int x = column * CELL_PIXEL_SIZE;
            int y = row * CELL_PIXEL_SIZE;
            Cell cell = board.getCell(row, column);
            if (cell.isCovered()) {
                g.drawImage(imageCell, x, y, CELL_PIXEL_SIZE, CELL_PIXEL_SIZE, null, null);
            if (cell.isFlagged()) {
                g.drawImage(imageFlag, x, y, CELL_PIXEL_SIZE, CELL_PIXEL_SIZE, null, null);
            }
            } else {
                g.setColor(Color.gray);
                g.fillRect(x, y, CELL_PIXEL_SIZE, CELL_PIXEL_SIZE);
                g.setColor(Color.lightGray);
                g.fillRect(x+1, y+1, CELL_PIXEL_SIZE, CELL_PIXEL_SIZE);
                // need to be on uncovered cell
                if (cell.isMine()) {
                    g.drawImage(imageMine, x, y, CELL_PIXEL_SIZE, CELL_PIXEL_SIZE, null, null);
                } else if (cell.getAdjacentMines() > 0){
                    g.setFont(new Font("default", Font.BOLD, 16));
                    g.setColor(colors[cell.getAdjacentMines()]);
                    g.drawString(cell.getAdjacentMines() + "",
                            x + (int)(CELL_PIXEL_SIZE * 0.35),
                            y + (int)(CELL_PIXEL_SIZE*0.65));
                }
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
