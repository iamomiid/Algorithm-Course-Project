import java.util.HashMap;

public class Time {
    private static HashMap<String, Long> times = new HashMap<>();

    public static void log(String name){
        long now = System.nanoTime();

        if(Time.times.get(name) == null){
            Time.times.put(name, System.nanoTime());
        }else{
            long before = Time.times.get(name);
            Time.times.remove(name);
            System.out.println("[" + name + "] - Elapsed: " + ((now - before) / 1_000_000_000.0) + "s");
        }
    }
}
