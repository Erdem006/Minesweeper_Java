import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.random.*;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper
{
    private class MineTile extends JButton
    {
        int r;
        int c;

        public MineTile(int r, int c)
        {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;
    int numRows = 8;
    int numCols = numRows;
    int boardWidth = numRows * tileSize;
    int boardHeight = numCols * tileSize;

    JFrame frame = new JFrame("MINESWEEPER");
    JLabel textJLabel = new JLabel();
    JPanel textJPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JButton restartButton = new JButton("Restart");

    int mineCount = 10;
    int mineLeftInGame = mineCount;
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tileClicked = 0;
    boolean gameOver = false;

    Minesweeper()
    {
        // FRAME
        frame.setSize(boardWidth, boardHeight + 50);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // LABEL
        textJLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textJLabel.setHorizontalAlignment(JLabel.CENTER);
        textJLabel.setText("Minesweeper: " + Integer.toString(mineLeftInGame));
        textJLabel.setOpaque(true);

        // PANEL
        textJPanel.setLayout(new BorderLayout());
        textJPanel.add(textJLabel, BorderLayout.NORTH);
        textJPanel.add(restartButton, BorderLayout.SOUTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));

        // ADD
        frame.add(textJPanel, BorderLayout.NORTH);
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++)
        {
            for (int c = 0; c < numCols; c++)
            {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                tile.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        if (gameOver)
                        {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        // Left click
                        if (e.getButton() == MouseEvent.BUTTON1)
                        {
                            if (tile.getText().isEmpty())
                            {
                                if (mineList.contains(tile))
                                {
                                    RevealMines();
                                }
                                else
                                {
                                    CheckMine(tile.r, tile.c);
                                }
                            }
                        }
                        // Right click
                        else if (e.getButton() == MouseEvent.BUTTON3)
                        {
                            if (tile.getText().isEmpty() && tile.isEnabled())
                            {
                                tile.setText("❗");
                                mineLeftInGame -= 1;
                                textJLabel.setText("Minesweeper: " + Integer.toString(mineLeftInGame));
                            }
                            else if (tile.getText().equals("❗"))
                            {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        // Restart button
        restartButton.setFont(new Font("Arial", Font.BOLD, 25));
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        restartButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ResetGame();
            }
        });

        frame.setVisible(true);
        SetMines();
    }

    void SetMines()
    {
        mineList = new ArrayList<MineTile>();

        int mineLeft = mineCount;
        while (mineLeft > 0)
        {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile))
            {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    void RevealMines()
    {
        for (int i = 0; i < mineList.size(); i++)
        {
            MineTile tile = mineList.get(i);
            tile.setText("💥");
        }

        gameOver = true;
        textJLabel.setText("Game Over!");
        restartButton.setVisible(true);
    }

    void CheckMine(int r, int c)
    {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols)
        {
            return;
        }

        MineTile tile = board[r][c];

        if (!tile.isEnabled())
        {
            return;
        }
        tile.setEnabled(false);
        tileClicked += 1;

        int minesFound = 0;
        // Top
        minesFound += CountMine(r - 1, c - 1);
        minesFound += CountMine(r - 1, c);
        minesFound += CountMine(r - 1, c + 1);
        // Left and right
        minesFound += CountMine(r, c - 1);
        minesFound += CountMine(r, c + 1);
        // Bottom
        minesFound += CountMine(r + 1, c - 1);
        minesFound += CountMine(r + 1, c);
        minesFound += CountMine(r + 1, c + 1);

        if (minesFound > 0)
        {
            tile.setText(Integer.toString(minesFound));
        }
        else
        {
            tile.setText("");
            // Top
            CheckMine(r - 1, c - 1);
            CheckMine(r - 1, c);
            CheckMine(r - 1, c + 1);
            // Left and right
            CheckMine(r, c - 1);
            CheckMine(r, c + 1);
            // Bottom
            CheckMine(r + 1, c - 1);
            CheckMine(r + 1, c);
            CheckMine(r + 1, c + 1);
        }

        if (tileClicked == numRows * numCols - mineList.size())
        {
            gameOver = true;
            textJLabel.setText("Stage Cleared");
            restartButton.setVisible(true);
        }
    }

    int CountMine(int r, int c)
    {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols)
        {
            return 0;
        }
        if (mineList.contains(board[r][c]))
        {
            return 1;
        }
        return 0;
    }

    void ResetGame()
    {
        gameOver = false;
        mineLeftInGame = mineCount;
        tileClicked = 0;
        textJLabel.setText("Minesweeper: " + Integer.toString(mineLeftInGame));
        restartButton.setVisible(false);

        for (int r = 0; r < numRows; r++)
        {
            for (int c = 0; c < numCols; c++)
            {
                MineTile tile = board[r][c];
                tile.setText("");
                tile.setEnabled(true);
            }
        }

        SetMines();
    }

    public static void main(String[] args)
    {
        new Minesweeper();
    }
}