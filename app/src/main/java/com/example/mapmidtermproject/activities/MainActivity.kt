package com.example.mapmidtermproject.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.adapters.CarouselAdapter
import com.example.mapmidtermproject.adapters.NewsAdapter
import com.example.mapmidtermproject.settings.SettingsActivity
import com.example.mapmidtermproject.viewmodels.NewsViewModel
import com.example.mapmidtermproject.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var carouselViewPager: ViewPager2
    private lateinit var auth: FirebaseAuth
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var viewModel: NewsViewModel
    private lateinit var userViewModel: UserViewModel

    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        if (carouselViewPager.adapter != null && carouselViewPager.adapter!!.itemCount > 0) {
            val nextItem = (carouselViewPager.currentItem + 1) % carouselViewPager.adapter!!.itemCount
            carouselViewPager.setCurrentItem(nextItem, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val tvViewAll: TextView = findViewById(R.id.tvViewAll)
        val tvGreeting: TextView = findViewById(R.id.tvGreeting)

        // KLIK VIEW ALL -> SLIDE
        tvViewAll.setOnClickListener {
            startActivity(Intent(this, AllNewsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // CAROUSEL SETUP
        carouselViewPager = findViewById(R.id.carouselViewPager)
        carouselAdapter = CarouselAdapter(emptyList()) { article ->
            // KLIK CAROUSEL -> SLIDE
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("EXTRA_ARTICLE", article)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        carouselViewPager.adapter = carouselAdapter

        carouselViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 4000)
            }
        })

        // NEWS LIST SETUP
        val rvNews = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvNews)
        rvNews.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(emptyList()) { article ->
            // KLIK LIST -> SLIDE
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("EXTRA_ARTICLE", article)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        rvNews.adapter = newsAdapter

        viewModel.newsList.observe(this) { list ->
            newsAdapter.updateData(list)
            val carouselItems = list.take(5)
            carouselAdapter.updateData(carouselItems)
        }
        viewModel.loadNews()

        userViewModel.userProfile.observe(this) { profile ->
            val name = profile?.username ?: auth.currentUser?.displayName ?: "User"
            tvGreeting.text = "Halo, $name!"
        }
        userViewModel.loadProfile()

        setupBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 4000)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        if (bottomNav.selectedItemId != R.id.nav_home) {
            bottomNav.selectedItemId = R.id.nav_home
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                // BOTTOM NAV -> FADE (Default Tema, jadi tidak perlu overridePendingTransition)
                R.id.nav_log -> {
                    startActivity(Intent(this, LogActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    true
                }
                R.id.nav_camera -> {
                    startActivity(Intent(this, AnalysisActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    true
                }
                else -> false
            }
        }
    }
}