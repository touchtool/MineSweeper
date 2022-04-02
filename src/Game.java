import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Game extends JFrame {
    private Board board;
    private int boardSize = 20;
    private GridUI gridUI;
    private int mineCount = 40;

    public Game(){
        board = new Board(boardSize, mineCount);
        gridUI = new GridUI();
        add(gridUI);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void start(){
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
                    if (!cell.isCovered()){
                        return;
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (cell.isCovered()){
                            cell.setFlagged(!cell.isFlagged());
                        }
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        if (!cell.isFlagged()) {
                            board.uncover(row, column);
                            if (board.mineUncovered()){
                                JOptionPane.showMessageDialog(Game.this,
                                        "You lose!",
                                        "You hit the mine",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        }
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
        }

        private void  paintCell(Graphics g, int row, int column){
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
                    g.setColor(Color.black);
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
