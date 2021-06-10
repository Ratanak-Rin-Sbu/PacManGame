// Name: Ratanak Rin
// Email: ratanak.rin@stonybrook.edu

public class Path {
    private int[][] dist;
    private World.CellState[][] state;
    private int ny, nx;
    
    public Path(int[][] dist, World.CellState[][] state) {
        this.dist = dist;
        this.state = state;
        ny = state.length;
        nx = state[0].length;
    }
    
    //Prefer dots than empty cells
    protected int cellDist(int x, int y) {
        return state[y][x] == World.CellState.Dot ? 1 : 2;
    }
    
    public void findDistance(Pos from) {
        Queue<Pos> queue = new HeapQueue<Pos>();
       
        //initialize dist to max int
        for(int y = 0; y < ny; y++)
            for(int x = 0; x < nx; x++)
                dist[y][x] = 998;//Integer.MAX_VALUE;
        
        //Update dist array to find the shortest path, where
        //the distance between cells are weighted by cellDist
        //
        //TODO: - Starting from the 'from' position,
        //        update dist[y][x] for all y and x such that
        //        dist[y][x] = dist[p.y][p.x] + d, where
        //        p is the position dequeued from queue and
        //        d is cellDist(x, y) value
        //      - Updating dist[y][x] should continue until
        //        queue becomes empty
        //
        dist[from.y][from.x] = 0;
        queue.enqueue(new Pos(from.x, from.y, 0));
        while(!queue.isEmpty()) {
            Pos p = queue.dequeue();
            for(int i = 0; i < 4; i++) {
                int x = (p.x + World.DX[i] + nx) % nx;
                int y = (p.y + World.DY[i] + ny) % ny;
                int d = cellDist(x, y);
                if( state[y][x] != World.CellState.Wall &&
                        dist[y][x] > dist[p.y][p.x] + d) {
                    dist[y][x] = dist[p.y][p.x] + d;
                    queue.enqueue(new Pos(x, y, dist[y][x]));
                }
            }
        }
    }
    
    //Hopefully, this can help your debugging
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < ny; y++) {
            for(int x = 0; x < nx; x++) {
                if(state[y][x] == World.CellState.Wall)
                    sb.append("###");
                else
                    sb.append(String.format("%3d", dist[y][x]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
