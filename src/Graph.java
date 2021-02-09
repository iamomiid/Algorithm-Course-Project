import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph {
    Set<Integer> nodes = new HashSet<>();
    HashMap<Integer, HashMap<Integer, Edge>> adjacency = new HashMap<>();
    Sorts.Algorithms sortAlgorithm;

    Graph(Path p, Sorts.Algorithms algorithm) throws IOException {
        Time.log("Read File");
        this.sortAlgorithm = algorithm;
        List<String> fileContent = Files.readAllLines(p);
        String fileName = p.getFileName().toString();
        String delimiter = fileName.substring(fileName.lastIndexOf('.')).equals(".csv") ? "," : "\t";

        for (String line : fileContent) {
            if (!line.equals("")) {
                String[] divided = line.trim().split(delimiter);
                int from = Integer.parseInt(divided[0]);
                int to = Integer.parseInt(divided[1]);

                this.adjacency.computeIfAbsent(from, k -> new HashMap<>());
                this.adjacency.computeIfAbsent(to, k -> new HashMap<>());


                this.adjacency.get(from).put(to, new Edge(from, to, 0));
                this.adjacency.get(to).put(from, new Edge(to, from, 0));
                this.nodes.add(from);
                this.nodes.add(to);
            }
        }

        Time.log("Read File");
        this.computeAllScores();
    }

    public Edge[] getAllEdges() {
        Collection<Edge> edges = this.adjacency.values()
                .stream()
                .map(HashMap::values)
                .reduce(new ArrayList<>(), (prev, cur) -> {
                    prev.addAll(cur);
                    return prev;
                });
        return edges.toArray(new Edge[0]);
    }

    private void computeAllScores() {
        Time.log("First Compute");
        for (HashMap<Integer, Edge> edges : this.adjacency.values()) {
            for (Edge edge : edges.values()) {
                edge.score = this.getScore(edge.from, edge.to);
            }
        }
        Time.log("First Compute");
    }

    private int getRank(int key) {
        return this.adjacency.get(key).size();
    }


    private List<Integer> getAllNeighbors(int i, int j) {
        List<Integer> result =
                Stream.concat(
                        this.adjacency.get(i).values().stream(),
                        this.adjacency.get(j).values().stream()
                )
                .map(x -> x.to)
                .distinct()
                .collect(Collectors.toList());
        result.add(i);
        result.add(j);
        return result;
    }

    private int getTriangleZ(int i, int j) {
        Set<Integer> neighborsI = this.adjacency.get(i).keySet();
        Set<Integer> neighborsJ = this.adjacency.get(j).keySet();
        Set<Integer> result = neighborsI
                .stream()
                .distinct()
                .filter(neighborsJ::contains)
                .collect(Collectors.toSet());
        return result.size();
    }

    private double getScore(int i, int j) {
        int rankI = this.getRank(i);
        int rankJ = this.getRank(j);

        if (rankI == 1 || rankJ == 1) {
            return Double.POSITIVE_INFINITY;
        }

        return 1.0 * (getTriangleZ(i, j) + 1) / (Math.min(rankI - 1, rankJ - 1));
    }


    private Set<Integer> DFS() {
        int v = this.nodes.iterator().next();
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> toVisit = new Stack();

        toVisit.push(v);

        while (!toVisit.isEmpty()) {
            int node = toVisit.pop();

            if (!visited.contains(node)) {
                visited.add(node);
                toVisit.addAll(this.adjacency.get(node).keySet());
            }
        }

        return visited;
    }

    private boolean isFinished() {
        Set<Integer> visited = this.DFS();
        System.out.println("DFS: " + visited.size());

        if (visited.size() != this.nodes.size()) {
            try {
                saveGraph(visited);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }


        return false;
    }

    private void saveGraph(Set<Integer> visited) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get("src/files/output.csv").toAbsolutePath().toString(), false));

        for (int node : this.nodes) {
            writer.append(String.valueOf(node)).append(",").append(visited.contains(node) ? "A" : "B").append("\n");
        }

        writer.close();
        System.out.println("Saved Output");
    }

    public void start() {
        long i = 0;
        while (!isFinished()) {
            Time.log("Iteration " + i);
            Edge[] edges = this.getAllEdges();
            Edge smallest = Sorts.findSmallest(edges, this.sortAlgorithm);
            System.out.println("Deleting " + smallest);
            List<Integer> neighbors = getAllNeighbors(smallest.from, smallest.to);
            System.out.println("Neighbors: " + neighbors.size());
            this.adjacency.get(smallest.from).remove(smallest.to);
            this.adjacency.get(smallest.to).remove(smallest.from);
            List<Edge> neighborsEdges = new ArrayList<>(neighbors
                    .stream()
                    .map(b -> this.adjacency.get(b).values())
                    .reduce(new ArrayList<>(), (prev, cur) -> {
                        prev.addAll(cur);
                        return prev;
                    }));
            System.out.println("Neighbor Edges: " + neighborsEdges.size());

            for (Edge neighborsEdge : neighborsEdges) {
                neighborsEdge.score = getScore(neighborsEdge.from, neighborsEdge.to);
            }

            Time.log("Iteration " + (i++));
        }
    }
}
