package com.example.mapmidtermproject.data

import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.models.NewsArticle

/**
 * Menyediakan data dummy berita kesehatan untuk aplikasi Glucare.
 * Semua gambar harus disimpan di folder: res/drawable/
 * dengan nama sesuai yang digunakan di sini.
 */
object NewsData {
    fun getNewsList(): List<NewsArticle> {
        return listOf(
            NewsArticle(
                id = 1,
                title = "5 Gejala Awal Diabetes yang Sering Diabaikan",
                summary = "Sering haus, lelah berlebihan, dan luka yang sulit sembuh bisa jadi tanda awal diabetes.",
                date = "2 Oktober 2025",
                imageCode = "diabetes_symptoms",
                content = "Diabetes tipe 2 sering berkembang perlahan tanpa gejala mencolok. Lima tanda awal yang sering diabaikan antara lain: sering merasa haus (polidipsia), sering buang air kecil (poliuria), kelelahan ekstrem, penglihatan kabur, dan luka yang lama sembuh. Jika Anda mengalami lebih dari dua gejala ini, segera periksa kadar gula darah."
            ),
            NewsArticle(
                id = 2,
                title = "Pentingnya Pola Makan Sehat untuk Penderita Diabetes",
                summary = "Mengatur asupan karbohidrat dan memilih makanan dengan indeks glikemik rendah sangat penting.",
                date = "1 Oktober 2025",
                imageCode = "healthy_diabetic_meal",
                content = "Pola makan sehat untuk penderita diabetes mencakup konsumsi makanan tinggi serat, rendah lemak jenuh, dan karbohidrat kompleks seperti beras merah, oat, dan kacang-kacangan. Hindari gula tambahan dan minuman manis. Makan dalam porsi kecil tapi sering juga membantu menjaga kadar gula darah stabil sepanjang hari."
            ),
            NewsArticle(
                id = 3,
                title = "Olahraga Ringan yang Aman Dilakukan Setiap Hari",
                summary = "Jalan kaki 30 menit sehari bisa menurunkan risiko komplikasi diabetes hingga 40%.",
                date = "30 September 2025",
                imageCode = "walking_exercise",
                content = "Olahraga ringan seperti jalan kaki, bersepeda santai, yoga, atau berenang sangat direkomendasikan untuk penderita diabetes. Aktivitas fisik membantu tubuh menggunakan insulin lebih efisien dan menurunkan kadar gula darah. Lakukan minimal 30 menit per hari, 5 hari seminggu, dan selalu pantau gula darah sebelum dan sesudah berolahraga."
            ),
            NewsArticle(
                id = 4,
                title = "Mitos dan Fakta Seputar Diabetes yang Perlu Anda Tahu",
                summary = "Diabetes bukan disebabkan hanya oleh makan manis — ini fakta medis yang sering disalahpahami.",
                date = "28 September 2025",
                imageCode = "diabetes_myths_facts",
                content = "Banyak mitos beredar: 'Diabetes menular', 'Penderita diabetes tidak boleh makan nasi', atau 'Hanya orang gemuk yang kena diabetes'. Faktanya, diabetes tipe 2 dipengaruhi oleh genetik, gaya hidup, dan usia — bukan hanya konsumsi gula. Penderita tetap bisa makan nasi dalam porsi terkontrol. Edukasi yang tepat adalah kunci pengelolaan diabetes yang baik."
            ),
            NewsArticle(
                id = 5,
                title = "Cara Membaca Label Makanan untuk Penderita Diabetes",
                summary = "Fokus pada total karbohidrat, bukan hanya gula, saat memilih makanan kemasan.",
                date = "25 September 2025",
                imageCode = "food_label_reading",
                content = "Saat membeli makanan kemasan, penderita diabetes harus memperhatikan bagian 'Total Karbohidrat' dalam label nutrisi, bukan hanya 'Gula'. Karbohidrat total mencakup gula, serat, dan pati — semuanya memengaruhi kadar gula darah. Pilih produk dengan serat tinggi (>3g per sajian) dan hindari yang mengandung sirup jagung fruktosa tinggi (HFCS)."
            ),
            NewsArticle(
                id = 6,
                title = "Hubungan Stres dan Kadar Gula Darah",
                summary = "Stres kronis dapat meningkatkan hormon kortisol yang memicu kenaikan gula darah.",
                date = "22 September 2025",
                imageCode = "stress_and_blood_sugar",
                content = "Stres tidak hanya memengaruhi mental, tapi juga fisik. Saat stres, tubuh melepaskan hormon kortisol dan adrenalin yang dapat meningkatkan produksi glukosa oleh hati — meski Anda tidak makan. Ini bisa menyebabkan lonjakan gula darah. Teknik relaksasi seperti meditasi, pernapasan dalam, atau mendengarkan musik bisa membantu menstabilkan kadar gula."
            ),
            NewsArticle(
                id = 7,
                title = "Makanan Super untuk Penderita Diabetes",
                summary = "Brokoli, kacang almond, dan ikan berlemak seperti salmon sangat direkomendasikan.",
                date = "20 September 2025",
                imageCode = "superfoods_for_diabetes",
                content = "Beberapa 'makanan super' sangat baik untuk penderita diabetes: brokoli (kaya serat dan antioksidan), kacang almond (lemak sehat dan magnesium), ikan salmon (omega-3), serta kayu manis (dapat meningkatkan sensitivitas insulin). Konsumsi secara rutin dalam porsi wajar untuk hasil terbaik."
            ),
            NewsArticle(
                id = 8,
                title = "Pentingnya Pemeriksaan Kaki bagi Penderita Diabetes",
                summary = "Neuropati diabetik bisa menyebabkan luka tanpa rasa sakit — periksa kaki setiap hari!",
                date = "18 September 2025",
                imageCode = "diabetic_foot_care",
                content = "Kerusakan saraf (neuropati) akibat diabetes bisa membuat kaki kehilangan sensasi nyeri. Luka kecil bisa berkembang jadi infeksi serius tanpa disadari. Penderita diabetes disarankan memeriksa kaki setiap hari, menjaga kebersihan, pakai sepatu yang nyaman, dan segera ke dokter jika ada luka, kemerahan, atau bengkak."
            ),
            NewsArticle(
                id = 9,
                title = "Aplikasi Digital untuk Bantu Kelola Diabetes",
                summary = "Catat asupan makanan, olahraga, dan kadar gula darah dalam satu genggaman.",
                date = "15 September 2025",
                imageCode = "healthy_diabetic_meal",
                content = "Teknologi kini memudahkan pengelolaan diabetes. Aplikasi seperti Glucare memungkinkan pengguna mencatat kadar gula darah, makanan, aktivitas fisik, dan obat harian. Beberapa aplikasi bahkan bisa menghasilkan laporan mingguan untuk dibawa ke dokter. Ini membantu pasien dan tenaga medis membuat keputusan lebih akurat."
            ),
            NewsArticle(
                id = 10,
                title = "Tidur Cukup, Kunci Stabilitas Gula Darah",
                summary = "Kurang tidur dapat meningkatkan resistensi insulin dan nafsu makan berlebihan.",
                date = "12 September 2025",
                imageCode = "good_sleep_health",
                content = "Tidur kurang dari 6 jam per malam dikaitkan dengan peningkatan risiko diabetes tipe 2. Kurang tidur mengganggu keseimbangan hormon leptin dan ghrelin, yang mengatur rasa lapar, serta meningkatkan resistensi insulin. Usahakan tidur 7–8 jam setiap malam untuk menjaga metabolisme gula tetap stabil."
            )
        )
    }
}