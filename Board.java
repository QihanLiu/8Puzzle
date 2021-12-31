import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final char[][] blocks;
    private int dist = 0;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        int boardsize = tiles.length;
        blocks = new char[boardsize][boardsize];
        for (int i = 0; i < boardsize; i++) {
            for (int j = 0; j < boardsize; j++) {
                blocks[i][j] = (char) tiles[i][j];
            }
        }
        dist = manhattan();
    }

    private Board(int[][] tiles, int inputdist) {
        int boardsize = tiles.length;
        blocks = new char[boardsize][boardsize];
        for (int i = 0; i < boardsize; i++) {
            for (int j = 0; j < boardsize; j++) {
                blocks[i][j] = (char) tiles[i][j];
            }
        }
        dist = inputdist;
    }

    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(blocks.length + "\n");
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                sb.append(String.format("%2d ", (int) blocks[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return blocks.length;
    }

    // number of tiles out of place
    public int hamming() {
        int hamdist = 0;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                if (blocks[i][j] != 0 && blocks[i][j] != (char) i * blocks.length + j + 1) {
                    hamdist++;
                }
            }
        }
        return hamdist;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        if (dist != 0) {
            return dist;
        }
        int manhdist = 0, di, dj, temp;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                if (blocks[i][j] != 0 && blocks[i][j] != (char) i * blocks.length + j + 1) {
                    temp = (int) blocks[i][j] - 1;
                    di = i - temp / blocks.length;
                    dj = j - temp % blocks.length;
                    manhdist += Math.abs(di) + Math.abs(dj);
                }
            }
        }
        return manhdist;
    }

    private int updatemanhattan(int x1, int y1, int x2, int y2) { // Only for neigbour
        int manhdist = dist, d1 = 0, d2 = 0, dx, dy, temp;
        char b1 = blocks[x1][y1], b2 = blocks[x2][y2];
        if (b1 != 0) {
            temp = (int) b1 - 1;
        } else {
            temp = (int) b2 - 1;
        }
        dx = temp / blocks.length;
        dy = temp % blocks.length;
        d1 += Math.abs(x1 - dx) + Math.abs(y1 - dy);
        d2 += Math.abs(x2 - dx) + Math.abs(y2 - dy);

        if (d1 > d2) {
            manhdist++;
        } else if (d1 < d2) {
            manhdist--;
        }
        dist = manhdist;
        return manhdist;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (y == this) {
            return true;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board that = (Board) y;
        if (that.dimension() != this.dimension()) {
            return false;
        }
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                if (this.blocks[i][j] != that.blocks[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> neighbors = new Queue<Board>();
        int row = 0, col = 0;
        search: for (row = 0; row < blocks.length; row++) {
            for (col = 0; col < blocks.length; col++) {
                if (blocks[row][col] == 0) {
                    break search;
                }
            }
        }
        if (col < blocks.length - 1) {
            neighbors.enqueue(swap(row, col, row, col + 1));
        }
        if (row < blocks.length - 1) {
            neighbors.enqueue(swap(row, col, row + 1, col));
        }
        if (row > 0) {
            neighbors.enqueue(swap(row, col, row - 1, col));
        }
        if (col > 0) {
            neighbors.enqueue(swap(row, col, row, col - 1));
        }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length - 1; j++) {
                if (blocks[i][j] != 0 && blocks[i][j + 1] != 0) {
                    Board bdtwin = swap(i, j, i, j + 1);
                    bdtwin.dist = 0;
                    bdtwin.dist = bdtwin.manhattan();
                    return bdtwin;
                }
            }
        }
        throw new RuntimeException();
    }

    private Board swap(int x1, int y1, int x2, int y2) {
        int[][] newblocks = new int[blocks.length][blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                newblocks[i][j] = (int) blocks[i][j];
            }
        }
        int temp = newblocks[x1][y1];
        newblocks[x1][y1] = newblocks[x2][y2];
        newblocks[x2][y2] = temp;

        Board newbd = new Board(newblocks, dist);
        newbd.dist = newbd.updatemanhattan(x1, y1, x2, y2);
        return newbd;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        In in = new In("example.txt");
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board bd = new Board(blocks);
        StdOut.println(bd.toString());
        Board twin = bd.twin();
        StdOut.println(twin.toString());
        StdOut.println("recorded: " + twin.dist + ", accurate: " + twin.manhattan());

    }
}