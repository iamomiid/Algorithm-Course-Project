import java.util.Random;
import java.util.Stack;

public class Sorts {
    private static Random rand = new Random();

    enum Algorithms {
        Quick,
        Bubble,
        Insertion,
        Merge
    }

    public static Algorithms isAlgorithm(String input) {
        for (Algorithms c : Algorithms.values()) {
            if (c.name().equals(input)) {
                return c;
            }
        }
        return null;
    }

    public static Edge findSmallest(Edge[] edges, Algorithms algorithm) {
        switch (algorithm) {
            case Quick:
                Sorts.quickSort(edges);
                break;

            case Bubble:
                Sorts.bubbleSort(edges);
                break;

            case Insertion:
                Sorts.insertionSort(edges);
                break;

            case Merge:
                Sorts.mergeSort(edges, edges.length);
                break;
        }

        return edges[0];
    }

    private static void insertionSort(Edge[] input) {
        for (int i = 1; i < input.length; i++) {
            Edge key = input[i];
            int j = i - 1;
            while (j >= 0 && input[j].score > key.score) {
                input[j + 1] = input[j];
                j = j - 1;
            }
            input[j + 1] = key;
        }
    }

    private static void bubbleSort(Edge[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (arr[j].score > arr[j + 1].score) {
                    Edge temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
    }

    private static void quickSort(Edge[] arr) {
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        stack.push(arr.length - 1);

        while (!stack.isEmpty()) {
            int end = stack.pop();
            int begin = stack.pop();

            if (end - begin < 2) {
                continue;
            }

            int partitionIndex = partition(arr, begin, end);

            if (partitionIndex - 1 > begin) {
                stack.push(begin);
                stack.push(partitionIndex - 1);
            }

            if (partitionIndex + 1 < end) {
                stack.push(partitionIndex + 1);
                stack.push(end);
            }
        }
    }

    private static int partition(Edge[] arr, int begin, int end) {
        Edge pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
//            if (arr[j].score < pivot.score || (arr[j].score == pivot.score && Sorts.rand.nextInt(2) == 1)) {
//                i++;
//
//                Edge swapTemp = arr[i];
//                arr[i] = arr[j];
//                arr[j] = swapTemp;
//            }

            if (arr[j].score <= pivot.score) {
                i++;

                Edge swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        Edge swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }


    private static void mergeSort(Edge[] a, int n) {
        if (n < 2) {
            return;
        }

        int mid = n / 2;
        Edge[] l = new Edge[mid];
        Edge[] r = new Edge[n - mid];

        System.arraycopy(a, 0, l, 0, mid);

        System.arraycopy(a, mid, r, 0, n - mid);

        mergeSort(l, mid);
        mergeSort(r, n - mid);

        merge(a, l, r, mid, n - mid);
    }

    private static void merge(Edge[] a, Edge[] l, Edge[] r, int left, int right) {

        int i = 0, j = 0, k = 0;

        while (i < left && j < right) {
            if (l[i].score <= r[j].score) {
                a[k++] = l[i++];
            } else {
                a[k++] = r[j++];
            }
        }

        while (i < left) {
            a[k++] = l[i++];
        }

        while (j < right) {
            a[k++] = r[j++];
        }
    }
}
