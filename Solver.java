import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

public class Solver {
    private Node bestnode = null;
    private int count = 0;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        boolean isSolved = false;
        MinPQ<Node> searchnodes = new MinPQ<Node>();
        searchnodes.insert(new Node(initial));

        Node twinnode = new Node(initial.twin());
        twinnode.istwin = true;
        searchnodes.insert(twinnode);

        while (!isSolved) {
            bestnode = searchnodes.delMin();

            if (bestnode.blocks.isGoal()) {
                if (bestnode.istwin) {
                    break;
                } else {
                    isSolved = true;
                    break;
                }
            } else {
                for (Board neighbor : bestnode.blocks.neighbors()) {
                    if (bestnode.prenode == null || !neighbor.equals(bestnode.prenode.blocks)) {
                        count++;
                        searchnodes.insert(new Node(neighbor, bestnode));
                    }
                }
            }
        }
    }

    private class Node implements Comparable<Node> {
        private int moves = 0;
        private final Board blocks;
        private Node prenode = null;
        private boolean istwin;
        private final int priority;

        public Node(Board blocks) {
            this.blocks = blocks;
            this.priority = blocks.manhattan() + moves;
        }

        public Node(Board blocks, Node preblocks) {
            this.blocks = blocks;
            this.prenode = preblocks;
            this.moves = preblocks.moves + 1;
            this.priority = blocks.manhattan() + moves;
            this.istwin = preblocks.istwin;
        }

        public int compareTo(Node that) {
            return this.priority - that.priority;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        if (bestnode == null || bestnode.istwin) {
            return false;
        }
        return true;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (isSolvable()) {
            return bestnode.moves;
        }
        return -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (bestnode.istwin) {
            return null;
        } else {
            Stack<Board> solstack = new Stack<Board>();
            solstack.push(bestnode.blocks);
            Node bestbackup = bestnode;
            while (bestnode.prenode != null) {
                solstack.push(bestnode.prenode.blocks);
                bestnode = bestnode.prenode;
            }
            bestnode = bestbackup;
            return solstack;
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In("example.txt");
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("inserts: " + solver.count);
            StdOut.println("Minimum number of moves = " + solver.moves());
            // for (Board board : solver.solution())
            // StdOut.println(board);
        }
    }

}