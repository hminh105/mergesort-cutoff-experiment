import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class DataGenerator {
    private static final long SEED = 42L;
    private static final String DATA_DIR = "data";
    public static final int[] SIZES = {100_000, 500_000, 1_000_000};
    public static final String[] TYPES = {"random", "nearly_sorted", "reverse"};

    // 1. Mảng ngẫu nhiên hoàn toàn
    public static Integer[] generateRandom(int n, Random rand) {
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = rand.nextInt(n * 10);
        }
        return a;
    }

    // 2. Mảng gần như đã sắp xếp (đổi chỗ ngẫu nhiên 5% phần tử)
    public static Integer[] generateNearlySorted(int n, Random rand) {
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        int swaps = Math.max(1, (int) (n * 0.05));
        for (int s = 0; s < swaps; s++) {
            int i = rand.nextInt(n);
            int j = rand.nextInt(n);
            Integer tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return a;
    }

    // 3. Mảng sắp xếp ngược (giảm dần)
    public static Integer[] generateReverse(int n) {
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = n - i;
        }
        return a;
    }

    // Lưu mảng ra file .txt (dòng đầu là kích thước n, các dòng sau là từng phần tử)
    public static void saveToFile(Integer[] a, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(Integer.toString(a.length));
            writer.newLine();
            for (Integer val : a) {
                writer.write(val.toString());
                writer.newLine();
            }
        }
    }

    // Đọc mảng từ file .txt (để Experiment.java gọi lại)
    public static Integer[] loadFromFile(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            int n = Integer.parseInt(reader.readLine().trim());
            Integer[] a = new Integer[n];
            for (int i = 0; i < n; i++) {
                a[i] = Integer.parseInt(reader.readLine().trim());
            }
            return a;
        }
    }

    public static String getFilePath(String type, int n) {
        return DATA_DIR + "/" + type + "_" + n + ".txt";
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));
        Random rand = new Random(SEED);

        System.out.println("Dang sinh bo du lieu thi nghiem (seed = " + SEED + ")...");
        for (int n : SIZES) {
            saveToFile(generateRandom(n, rand), Paths.get(getFilePath("random", n)));
            saveToFile(generateNearlySorted(n, rand), Paths.get(getFilePath("nearly_sorted", n)));
            saveToFile(generateReverse(n), Paths.get(getFilePath("reverse", n)));
            System.out.println(" -> Da tao xong 3 file cho N = " + n);
        }
        System.out.println("Hoan tat! Kiem tra thu muc data/ cua ban.");
    }
}