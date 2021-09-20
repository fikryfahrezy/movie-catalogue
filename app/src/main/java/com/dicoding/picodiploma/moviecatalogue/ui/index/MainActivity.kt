package com.dicoding.picodiploma.moviecatalogue.ui.index

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * NOTE: How to Make Splash Screen
         * https://developer.android.com/topic/performance/vitals/launch-time#themed
         * https://android.jlelse.eu/launch-screen-in-android-the-right-way-aca7e8c31f52
         * https://stackoverflow.com/a/15832037/12976234
         * https://www.codepolitan.com/mudah-membuat-splash-screen-dengan-android-studio-5a8d2310894d1
         */
        setTheme(R.style.Theme_MovieCatalogue)
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.hide()

        val navView: BottomNavigationView = viewBinding.navView

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_movie, R.id.navigation_show, R.id.navigation_bookmarked)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
