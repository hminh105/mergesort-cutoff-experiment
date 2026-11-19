public class MergeSort {
    // So sánh qua Comparable
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    // Trộn 2 nửa đã được sắp xếp: a[lo..mid] va a[mid+1..hi].
    private static void merge(Comparable[] a, Comparable[] aux,
                              int lo, int mid, int hi) {
        // Sao chép đoạn cần trộn sang mảng phụ trước khi ghi kết quả lên a.
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        int i = lo;
        int j = mid + 1;

        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > hi) {
                a[k] = aux[i++];
            } else if (less(aux[j], aux[i])) {
                a[k] = aux[j++];
            } else {
                // Nếu bằng nhau thì lấy phần tử bên trái trước để giữ tính ổn định.
                a[k] = aux[i++];
            }
        }
    }

    // Insertion Sort chỉ sắp xếp 1 đoạn a[lo..hi].
    private static void insertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            Comparable key = a[i];
            int j = i;

            while (j > lo && less(key, a[j - 1])) {
                a[j] = a[j - 1];
                j--;
            }
            a[j] = key;
        }
    }

    private static void sort(Comparable[] a, Comparable[] aux,
                             int lo, int hi, int cutoff) {
        if (hi <= lo) {
            return;
        }

        // CUTOFF là kích thước tối đa của đoạn đã được chuyển sang Insertion Sort.
        if (cutoff > 1 && hi - lo + 1 <= cutoff) {
            insertionSort(a, lo, hi);
            return;
        }

        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid, cutoff);
        sort(a, aux, mid + 1, hi, cutoff);
        merge(a, aux, lo, mid, hi);
    }

    // cutoff = 0 là Mergesort gốc, dùng làm mốc so sánh.
    public static void sort(Comparable[] a, int cutoff) {
        if (a == null) {
            throw new IllegalArgumentException("Array must not be null");
        }
        if (cutoff < 0) {
            throw new IllegalArgumentException("Cutoff must be zero or greater");
        }

        Comparable[] aux = new Comparable[a.length];
        sort(a, aux, 0, a.length - 1, cutoff);
    }

    // Tiện ích để gọi Mergesort gốc mà không cần truyền cutoff.
    public static void sort(Comparable[] a) {
        sort(a, 0);
    }
    public static void main(String[] args) {
        Integer[] a1 = {5, 2, 9, 1, 5, 6, 3, 8, 4, 7};
        sort(a1, 0); // Chạy Mergesort gốc
        System.out.println("Cutoff = 0: " + java.util.Arrays.toString(a1));

        Integer[] a2 = {5, 2, 9, 1, 5, 6, 3, 8, 4, 7};
        sort(a2, 7); // Chạy Mergesort có cutoff = 7
        System.out.println("Cutoff = 7: " + java.util.Arrays.toString(a2));
    }
}
