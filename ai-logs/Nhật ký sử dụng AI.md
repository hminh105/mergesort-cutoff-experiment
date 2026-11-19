# Nhật ký tóm tắt quá trình sử dụng AI (AI Usage Log)

* **Sinh viên thực hiện:** Trần Hoàng Minh  
* **MSSV:** 25023494  
* **Công cụ AI sử dụng:** Gemini (lập kế hoạch và hỗ trợ nội dung ban đầu) và OpenAI Codex (giải thích slide, rà soát code, dữ liệu và tài liệu trong phiên làm việc này)  
* **Dự án:** Thí nghiệm tìm giá trị `CUTOFF` tối ưu cho thuật toán Mergesort (Java — JDK 25.0.2 LTS)

---

## 1. Phạm vi sử dụng AI

Quá trình sử dụng AI tuân thủ yêu cầu của bài tập: dùng AI để thảo luận phương pháp thực nghiệm, hỗ trợ viết mã sinh bộ dữ liệu test, viết script chạy đo thời gian, trực quan hóa số liệu và hỗ trợ trình bày báo cáo; sinh viên trực tiếp kiểm tra cài đặt `MergeSort.java` theo nội dung bài giảng, chạy thực nghiệm trên máy cá nhân, đối chiếu kết quả và rà soát toàn bộ sản phẩm trước khi nộp.

---

## 2. Tóm tắt các giai đoạn trao đổi thực tế

### Giai đoạn 1: Phân tích đề bài & Lập kế hoạch
* **Trao đổi:** Làm rõ mục tiêu bài toán `CUTOFF`, ôn lại các nguyên tắc đo thực nghiệm trong chủ đề *Analysis of Algorithms* (tính tái lập, nhân bản mảng trước khi đo, làm nóng JVM, thống kê nhiều lần lặp) và xác nhận tính tương thích khi sử dụng sẵn **JDK 25** trên máy.
* **Kết quả:** Chốt cấu trúc thư mục dự án (`src/`, `data/`, `results/`, `report/`, `ai-logs/`) và lập lộ trình thực hiện trong 24 giờ.

### Giai đoạn 2: Rà soát cài đặt `MergeSort.java` theo bài giảng
* **Trao đổi:** Kiểm tra tệp `MergeSort.java` tự dựng đối chiếu với Slide 2.2 (Sedgewick & Wayne). Thảo luận để phân biệt sự khác nhau về hình thức trình bày code (code style) so với bản chất thuật toán, cũng như lưu ý kỹ thuật dịch chuyển phần tử (*half exchanges*) trong hàm `insertionSort`.
* **Kết quả:** Chốt bản `MergeSort.java` bám sát bài giảng (không trộn thêm các cải tiến khác để tách bạch ảnh hưởng của `CUTOFF`), bổ sung hàm `main` nhỏ để chạy kiểm thử nhanh tính đúng đắn.

### Giai đoạn 3: Xây dựng bộ dữ liệu & Script đo lần 1
* **Trao đổi:** Nhờ AI hỗ trợ viết `DataGenerator.java` (sinh 9 tệp dữ liệu cố định với `SEED = 42L` gồm 3 kích thước `100.000`, `500.000`, `1.000.000` × 3 phân phối `random`, `nearly_sorted`, `reverse`) và `Experiment.java` phiên bản đầu tiên (đo 7 lượt cho 15 mức `CUTOFF`, xuất ra `results/results.csv`).
* **Kết quả:** Sinh thành công 9 tệp dữ liệu trong `data/` và thu được bộ số liệu sơ bộ 135 dòng từ máy cá nhân.

### Giai đoạn 4: Đánh giá sai số & Cải tiến quy trình đo lần 2
* **Trao đổi:** Phân tích các hạn chế của lần đo đầu tiên (xuất hiện lượt đo nhiễu mạnh làm lệch giá trị trung bình `mean`, thứ tự chạy `CUTOFF` cố định từ `0` đến `64` dễ chịu ảnh hưởng nhiệt độ/tác vụ nền, và biểu đồ cũ cắt trục dọc làm khuyết điểm của mảng `reverse` cũng như vẽ đường `min` dễ gây hiểu nhầm).
* **Kết quả:** Nâng cấp `Experiment.java` lên **9 vòng đo có xáo trộn ngẫu nhiên thứ tự `CUTOFF`** (`SHUFFLE_SEED = 2026L`), sử dụng **Trung vị (Median)** làm chỉ số đánh giá chính song song với Mean/StdDev. Sinh viên tắt các ứng dụng nền, cắm sạc ổn định và chạy lại toàn bộ thí nghiệm để thu tệp `results/results.csv` mới.

### Giai đoạn 5: Trực quan hóa số liệu & Hoàn thiện tài liệu nộp
* **Trao đổi:** Đưa tệp `results.csv` mới cùng thông số cấu hình máy (`Intel Core i5-12450H`, `8 GB RAM`, `Windows 11`, `JDK 25.0.2 LTS`) để AI hỗ trợ vẽ lại 2 biểu đồ chuẩn (dùng Median/Mean, hiển thị đầy đủ trục dọc cho mảng `reverse`), tổng hợp các bảng thống kê, soạn bản thảo báo cáo (`.docx` để sinh viên tự mở chỉnh sửa) và khung `README.md`.
* **Kết quả:** Hoàn thiện biểu đồ trong `results/`, báo cáo trong `report/` và tài liệu mô tả dự án tại `README.md`.

---

## 3. Trao đổi với OpenAI Codex trong phiên này
* Đối chiếu yêu cầu chính thức với kế hoạch AI và slide bài giảng.
* Giải thích luồng `merge`, đệ quy `sort`, CUTOFF và Insertion Sort; hỗ trợ khởi đầu tệp `MergeSort.java`, sau đó sinh viên tự chỉnh sửa.
* Rà soát hai lần số liệu CSV/biểu đồ và báo cáo; chỉ ra các giới hạn đo và các điểm cần diễn đạt thận trọng.

Ghi chú: Tệp này là bản tóm tắt quá trình sử dụng AI, không phải bản sao nguyên văn đầy đủ của các cuộc trò chuyện. Nếu giảng viên yêu cầu transcript đầy đủ, cần đính kèm bản xuất từ từng công cụ AI.

## 4. Phân định phần việc thực hiện

| Hạng mục | Sinh viên thực hiện | AI hỗ trợ |
| :--- | :--- | :--- |
| **Cấu trúc & Cài đặt `MergeSort.java`** | Tạo project, đối chiếu code với slide 2.2, tự kiểm thử và chốt mã nguồn. | Giải thích điểm khác biệt về cú pháp/cách viết và kiểm tra logic. |
| **Dữ liệu & Chạy thí nghiệm** | Chạy `DataGenerator` và `Experiment` (2 lần đo thực tế trên máy cá nhân), xử lý điều kiện môi trường đo. | Viết mã nguồn `DataGenerator.java` và nâng cấp `Experiment.java`. |
| **Phân tích, Biểu đồ & Báo cáo** | Cung cấp số liệu đo thật, cấu hình máy, rà soát nhận xét và chỉnh sửa trực tiếp bản Word báo cáo. | Vẽ biểu đồ từ CSV, tổng hợp bảng thống kê, soạn bản thảo báo cáo và `README.md`. |

