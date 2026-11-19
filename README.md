# Khảo sát thực nghiệm giá trị CUTOFF tối ưu cho thuật toán Mergesort

Dự án bài tập môn **Cấu trúc Dữ liệu và Giải thuật** nhằm tìm hiểu và đo đạc thực nghiệm ngưỡng `CUTOFF` (chuyển sang Insertion Sort đối với mảng con có kích thước nhỏ) cho cài đặt Top-down Mergesort chuẩn bài giảng trên cấu hình máy tính cá nhân.

* **Sinh viên thực hiện:** Trần Hoàng Minh
* **MSSV:** 25023494
* **Ngôn ngữ:** Java (JDK 25.0.2 LTS)

---

## 1. Mục tiêu thí nghiệm

Các phiên bản giáo trình *Algorithms* (Sedgewick & Wayne) đề xuất ngưỡng `CUTOFF` bằng `7` (bản cũ) hoặc xấp xỉ `10` (bản mới). Tuy nhiên, giá trị tối ưu thực tế phụ thuộc vào phần cứng (CPU, bộ nhớ đệm), trình biên dịch JIT của JVM, chi tiết cài đặt thuật toán và phân phối dữ liệu đầu vào.

Thí nghiệm này khảo sát **15 mức `CUTOFF`** (`0, 2, 4, 6, 7, 8, 10, 12, 15, 20, 25, 30, 40, 50, 64`) trên **9 bộ dữ liệu** (3 kích thước $\times$ 3 phân phối) để đánh giá hiệu năng và đề xuất khoảng `CUTOFF` tối ưu trên máy thí nghiệm.

---

## 2. Môi trường thực nghiệm

| Thành phần | Thông số |
| :--- | :--- |
| **CPU** | 12th Gen Intel(R) Core(TM) i5-12450H |
| **RAM** | 8 GB |
| **Hệ điều hành** | Windows 11 (64-bit) |
| **Java (JDK)** | `java version "25.0.2" 2026-01-20 LTS` |
| **IDE** | IntelliJ IDEA |

---

## 3. Cấu trúc thư mục dự án

```text
MERGESORT CUTOFF/
├── src/
│   ├── MergeSort.java          # Cài đặt Top-down Mergesort theo bài giảng + Insertion Sort (half exchanges)
│   ├── DataGenerator.java      # Sinh 9 bộ dữ liệu thí nghiệm cố định (seed = 42L)
│   └── Experiment.java         # Script chạy thực nghiệm (warm-up, xáo trộn 9 vòng, xuất CSV)
├── data/                       # 9 tệp dữ liệu đầu vào (.txt) cho N = 100k, 500k, 1M
├── results/
│   ├── results.csv             # Thống kê tổng hợp cho 135 tổ hợp (không lưu từng lượt đo riêng)
│   ├── chart_n1m_by_type.png   # Biểu đồ thời gian chạy (Median & Mean) tại N = 1.000.000
│   └── chart_speedup_n1m.png   # Biểu đồ tỷ lệ tăng tốc (%) theo Median tại N = 1.000.000
├── report/                     # Báo cáo thí nghiệm chi tiết (PDF)
├── ai-logs/                    # Nhật ký tóm tắt các trao đổi với AI
└── README.md                   # Tài liệu mô tả dự án và hướng dẫn chạy
```

---

## 4. Hướng dẫn biên dịch và chạy lại thí nghiệm

### Cách 1: Chạy trực tiếp trên IntelliJ IDEA
1. Mở dự án trong IntelliJ IDEA (cấu hình Project SDK là **JDK 25**).
2. Chạy `src/DataGenerator.java` để tạo (hoặc tái tạo) 9 tệp dữ liệu trong thư mục `data/`.
3. Chạy `src/Experiment.java` để chạy toàn bộ 9 vòng đo xáo trộn và xuất kết quả ra `results/results.csv`.

### Cách 2: Chạy bằng dòng lệnh (Terminal)
Mở Terminal tại thư mục gốc của dự án và chạy lần lượt:

```bash
# 1. Biên dịch mã nguồn
javac -d out src/MergeSort.java src/DataGenerator.java src/Experiment.java

# 2. Sinh bộ dữ liệu thí nghiệm (nếu chưa có trong thư mục data/)
java -cp out DataGenerator

# 3. Chạy thí nghiệm đo thời gian và xuất results/results.csv
java -cp out Experiment
```

---

## 5. Tóm tắt kết quả nổi bật ($N = 1.000.000$)

Mỗi kịch bản được làm nóng JVM (warm-up), xáo trộn ngẫu nhiên thứ tự 15 mức `CUTOFF` qua **9 vòng đo** (`SHUFFLE_SEED = 2026L`) và đánh giá chính bằng **Trung vị (Median)** để hạn chế nhiễu từ GC/hệ điều hành:

| Phân phối dữ liệu | Gốc (`C=0`) | `C=7` (Cũ) | `C=10` (Mới) | Tốt nhất (Median) | Cải thiện |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Random** | 961.745 ms | 815.797 ms (+15.18%) | 847.943 ms (+11.83%) | **C = 30** (692.709 ms) | **+27.97%** |
| **Nearly Sorted** | 621.798 ms | 543.067 ms (+12.66%) | 575.449 ms (+7.45%) | **C = 50** (458.077 ms) | **+26.33%** |
| **Reverse Sorted** | 561.041 ms | 610.690 ms (-8.85%) | 573.753 ms (-2.27%) | **C = 8** (523.979 ms) | **+6.61%** |

### Kết luận chính
* **Lựa chọn chính: `CUTOFF = 25`.** Theo tiêu chí cân bằng speedup median trung bình và mức suy giảm xấu nhất trên 9 kịch bản, `C = 25` đạt trung bình **+14.49%**, trường hợp xấu nhất **-2.69%**. Đây là lựa chọn phù hợp nhất trong các mức đã thử cho bộ dữ liệu này, không phải giá trị tối ưu phổ quát.
* **Lựa chọn thận trọng: `CUTOFF = 8`.** Nếu ưu tiên giảm rủi ro chậm hơn baseline, `C = 8` có trường hợp xấu nhất **-1.01%** và speedup trung bình **+11.69%**; `C = 10` lần lượt là **-2.27%** và **+11.46%**.
* **Cutoff tốt nhất thay đổi theo dữ liệu và kích thước.** Tại `N = 1.000.000`, median thấp nhất lần lượt là `C = 30` cho Random, `C = 50` cho Nearly Sorted và `C = 8` cho Reverse. Không nên coi `C = 15–30` là vùng an toàn chung: `C = 30` có trường hợp **-51.80%** trên Nearly Sorted, `N = 500.000`; `C = 64` giảm **-38.27%** trên Reverse, `N = 1.000.000`.

CSV lưu thống kê tổng hợp; từng thời gian của 9 lượt đo chưa được ghi thành dòng riêng. Xem báo cáo PDF trong thư mục **`report/`**.
