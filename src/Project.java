import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Project {
    private static Tuple2<Path, Sorts.Algorithms> getCommand() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Enter command: (ex: RUN Quick test1.csv)");
            String input = sc.nextLine();

            String[] divided = input.split(" ");

            if (divided.length != 3) {
                System.out.println("Bad Input Length");
                continue;
            }

            if (!divided[0].equals("RUN")) {
                System.out.println("Bad First Command");
                continue;
            }

            Sorts.Algorithms algo = Sorts.isAlgorithm(divided[1]);

            if(algo == null){
                System.out.println("Bad sort algorithm");
                continue;
            }

            Path p = Paths.get("src/files/" + divided[2]);

            if (!Files.exists(p)) {
                System.out.println("File doesn't exist.");
                continue;
            }

            sc.close();
            return new Tuple2<>(p, algo);
        }
    }

    public static void main(String[] args) {
        try {
            Tuple2<Path, Sorts.Algorithms> command = Project.getCommand();
            Graph g = new Graph(command.first, command.second);
            g.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
