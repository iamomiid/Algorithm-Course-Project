public class Edge {

    public int from;
    public int to;
    public double score;

    Edge(int from, int to, double score) {
        this.from = from;
        this.to = to;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", score=" + score +
                '}';
    }
}
