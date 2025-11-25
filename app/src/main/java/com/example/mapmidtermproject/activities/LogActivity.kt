package com.example.mapmidtermproject.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.settings.SettingsActivity
import com.example.mapmidtermproject.utils.CustomMarkerView
import com.example.mapmidtermproject.utils.FirestoreHelper
import com.example.mapmidtermproject.utils.FoodLog
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*

class LogActivity : AppCompatActivity() {

    private lateinit var etFoodName: TextInputEditText
    private lateinit var etSugarLevel: TextInputEditText
    private lateinit var lineChart: LineChart
    private lateinit var tvSelectedDate: TextView

    private var allLogs: List<FoodLog> = listOf()
    private var selectedDate: Calendar = Calendar.getInstance()
    private var currentFilterType = "DAY" // Default: Hari Ini
    private var dbListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        // Binding Views
        etFoodName = findViewById(R.id.etFoodName)
        etSugarLevel = findViewById(R.id.etSugarLevel)
        lineChart = findViewById(R.id.lineChart)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)

        val btnSave = findViewById<MaterialButton>(R.id.btnSaveLog)
        val btnPickDate = findViewById<MaterialButton>(R.id.btnPickDate)
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)

        setupChart()
        updateDateLabel()

        // 1. Logic Simpan Data
        btnSave.setOnClickListener {
            val food = etFoodName.text.toString()
            val sugarStr = etSugarLevel.text.toString()

            if (food.isNotEmpty() && sugarStr.isNotEmpty()) {
                val sugar = sugarStr.toIntOrNull()
                if (sugar != null) {
                    FirestoreHelper.saveFoodLog(food, sugar, {
                        Toast.makeText(this, "Data tersimpan!", Toast.LENGTH_SHORT).show()
                        etFoodName.text?.clear()
                        etSugarLevel.text?.clear()
                        // Tidak perlu loadData() karena listener otomatis update
                    }, {
                        Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                    })
                }
            } else {
                Toast.makeText(this, "Mohon lengkapi data", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Logic Pilih Tanggal
        btnPickDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate.set(year, month, day)
                updateDateLabel()
                applyFilter() // Re-filter data saat tanggal berubah
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 3. Logic Tab Filter (Hari, Minggu, Bulan)
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnSortDay -> currentFilterType = "DAY"
                    R.id.btnSortWeek -> currentFilterType = "WEEK"
                    R.id.btnSortMonth -> currentFilterType = "MONTH"
                }
                applyFilter()
            }
        }

        setupBottomNavigation()
    }

    override fun onStart() {
        super.onStart()
        // AKTIFKAN REALTIME LISTENER
        dbListener = FirestoreHelper.listenToFoodLogs { logs ->
            allLogs = logs
            applyFilter() // Update grafik otomatis begitu data masuk
        }
    }

    override fun onStop() {
        super.onStop()
        // Matikan listener untuk hemat baterai
        dbListener?.remove()
    }

    private fun setupChart() {
        lineChart.description.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.setExtraOffsets(0f, 0f, 0f, 10f)
    }

    private fun updateDateLabel() {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        tvSelectedDate.text = "Basis: ${sdf.format(selectedDate.time)}"
    }

    private fun applyFilter() {
        if (allLogs.isEmpty()) return

        val filteredLogs = mutableListOf<FoodLog>()
        val calLog = Calendar.getInstance()

        allLogs.forEach { log ->
            calLog.time = log.timestamp

            // Cek Kesamaan Hari/Tahun
            val sameDay = calLog.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR) &&
                    calLog.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)

            when (currentFilterType) {
                "DAY" -> if (sameDay) filteredLogs.add(log)
                "WEEK" -> {
                    // Cek range 7 hari ke belakang
                    val diff = selectedDate.timeInMillis - log.timestamp.time
                    val daysDiff = diff / (1000 * 60 * 60 * 24)
                    if (daysDiff in 0..7) filteredLogs.add(log)
                }
                "MONTH" -> {
                    if (calLog.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                        calLog.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)) {
                        filteredLogs.add(log)
                    }
                }
            }
        }
        updateChart(filteredLogs)
    }

    private fun updateChart(logs: List<FoodLog>) {
        val sortedLogs = logs.sortedBy { it.timestamp }
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        val dateFormat = if (currentFilterType == "DAY") SimpleDateFormat("HH:mm", Locale.getDefault())
        else SimpleDateFormat("dd/MM", Locale.getDefault())

        sortedLogs.forEachIndexed { index, log ->
            entries.add(Entry(index.toFloat(), log.bloodSugar.toFloat()))
            labels.add(dateFormat.format(log.timestamp))
        }

        if (entries.isEmpty()) {
            lineChart.clear()
            lineChart.setNoDataText("Tidak ada data pada periode ini.")
            return
        }

        val dataSet = LineDataSet(entries, "Gula Darah (mg/dL)")
        dataSet.color = getColor(R.color.blue)
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f
        dataSet.setCircleColor(getColor(R.color.blue))
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawValues(false) // Nilai sembunyi, muncul pas diklik

        // POPUP DETAIL
        val marker = CustomMarkerView(this, R.layout.custom_marker_view, sortedLogs)
        marker.chartView = lineChart
        lineChart.marker = marker

        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        lineChart.xAxis.granularity = 1f
        lineChart.data = LineData(dataSet)
        lineChart.animateY(800)
        lineChart.invalidate()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_log
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_log -> true
                R.id.nav_camera -> {
                    startActivity(Intent(this, AnalysisActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}