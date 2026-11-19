import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Experiment {
    private static final int[] CUTOFFS = {0, 2, 4, 6, 7, 8, 10, 12, 15, 20, 25, 30, 40, 50, 64};
    private static final int WARMUP_ROUNDS = 2;    // 2 vòng chạy nháp (đủ mọi cutoff)
    private static final int MEASURED_ROUNDS = 9;  // 9 vòng đo thật (số lẻ để lấy median chính xác)
    private static final long SHUFFLE_SEED = 2026L;
    private static final String OUTPUT_CSV = "results/results.csv";

    private static boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i].compareTo(a[i - 1]) < 0) return false;
        }
        return true;
    }

    private static void shuffleIndices(int[] indices, Random rand) {
        for (int i = indices.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = indices[i];
            indices[i] = indices[j];
            indices[j] = tmp;
        }
    }

    private static void globalWarmup() {
        System.out.println("Dang lam nong JVM (Global Warm-up)...");
        Integer[] dummy = new Integer[50_000];
        for (int i = 0; i < dummy.length; i++) {
            dummy[i] = dummy.length - i;
        }
        for (int i = 0; i < 6; i++) {
            MergeSort.sort(Arrays.copyOf(dummy, dummy.length), (i % 2 == 0) ? 0 : 10);
        }
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Paths.get("results"));
        globalWarmup();

        Random shuffleRand = new Random(SHUFFLE_SEED);
        int numCutoffs = CUTOFFS.length;
        int[] order = new int[numCutoffs];
        for (int i = 0; i < numCutoffs; i++) order[i] = i;

        try (BufferedWriter csv = Files.newBufferedWriter(Paths.get(OUTPUT_CSV))) {
            csv.write("data_type,n,cutoff,median_ms,mean_ms,stddev_ms,min_ms,max_ms,speedup_median_pct,speedup_mean_pct");
            csv.newLine();

            for (String type : DataGenerator.TYPES) {
                for (int n : DataGenerator.SIZES) {
                    String path = DataGenerator.getFilePath(type, n);
                    Integer[] original = DataGenerator.loadFromFile(path);

                    System.out.printf("%n=== Dang do: type=%s | N=%,d (xao tron thu tu, %d vong) ===%n",
                            type, n, MEASURED_ROUNDS);

                    // Warm-up riêng cho bộ dữ liệu này
                    for (int w = 0; w < WARMUP_ROUNDS; w++) {
                        shuffleIndices(order, shuffleRand);
                        for (int idx : order) {
                            Integer[] copy = Arrays.copyOf(original, original.length);
                            MergeSort.sort(copy, CUTOFFS[idx]);
                        }
                    }

                    // Đo thật: mỗi vòng xáo trộn lại thứ tự 15 mức CUTOFF
                    double[][] timesMs = new double[numCutoffs][MEASURED_ROUNDS];
                    for (int r = 0; r < MEASURED_ROUNDS; r++) {
                        shuffleIndices(order, shuffleRand);
                        for (int idx : order) {
                            int cutoff = CUTOFFS[idx];
                            Integer[] copy = Arrays.copyOf(original, original.length);

                            long start = System.nanoTime();
                            MergeSort.sort(copy, cutoff);
                            long end = System.nanoTime();

                            if (!isSorted(copy)) {
                                throw new AssertionError("Loi! Mang chua duoc sap xep dung voi cutoff=" + cutoff);
                            }
                            timesMs[idx][r] = (end - start) / 1_000_000.0;
                        }
                    }

                    // Tính thống kê và xuất ra CSV theo thứ tự cutoff tăng dần
                    double baselineMedian = -1.0;
                    double baselineMean = -1.0;
                    int bestCutoff = 0;
                    double bestMedian = Double.MAX_VALUE;

                    for (int idx = 0; idx < numCutoffs; idx++) {
                        int cutoff = CUTOFFS[idx];
                        double[] arr = timesMs[idx];
                        double[] sorted = Arrays.copyOf(arr, arr.length);
                        Arrays.sort(sorted);

                        double min = sorted[0];
                        double max = sorted[sorted.length - 1];
                        double median = (sorted.length % 2 == 1)
                                ? sorted[sorted.length / 2]
                                : (sorted[sorted.length / 2 - 1] + sorted[sorted.length / 2]) / 2.0;

                        double sum = 0.0;
                        for (double t : arr) sum += t;
                        double mean = sum / arr.length;

                        double sqDiff = 0.0;
                        for (double t : arr) sqDiff += (t - mean) * (t - mean);
                        double stddev = Math.sqrt(sqDiff / (arr.length - 1));

                        if (cutoff == 0) {
                            baselineMedian = median;
                            baselineMean = mean;
                        }
                        double speedupMedianPct = ((baselineMedian - median) / baselineMedian) * 100.0;
                        double speedupMeanPct = ((baselineMean - mean) / baselineMean) * 100.0;

                        if (median < bestMedian) {
                            bestMedian = median;
                            bestCutoff = cutoff;
                        }

                        csv.write(String.format(Locale.US,
                                "%s,%d,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.2f,%.2f",
                                type, n, cutoff, median, mean, stddev, min, max,
                                speedupMedianPct, speedupMeanPct));
                        csv.newLine();

                        System.out.printf(Locale.US,
                                "  cutoff=%2d | median=%8.3f ms | mean=%8.3f ms | std=%6.3f ms | cai thien(med)=%+6.2f%%%n",
                                cutoff, median, mean, stddev, speedupMedianPct);
                    }
                    System.out.printf(Locale.US,
                            "--> TOT NHAT (%s, N=%,d): cutoff = %d (median = %.3f ms)%n",
                            type, n, bestCutoff, bestMedian);
                }
            }
        }
        System.out.println("\nHOAN TAT! Da xuat ket qua moi ra file: " + OUTPUT_CSV);
    }
}