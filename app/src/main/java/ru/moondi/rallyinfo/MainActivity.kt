package ru.moondi.rallyinfo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import ru.moondi.rallyinfo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private val localDataSource = LocalDataSource()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        initDrawer()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SiteFragment.newInstance()).commit()
        }
        remoteConfig = Firebase.remoteConfig
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        val defaults = mapOf(
            URL_RESULTS_KEY to URL_RESULTS_VALUE,
            URL_SITE_NEWS_KEY to URL_SITE_NEWS_VALUE,
            URL_STREAM_KEY to URL_STREAM_VALUE,
        )

        val configSettings = remoteConfigSettings {
            remoteConfig.ensureInitialized()
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(defaults)
        fetch()
    }

    private fun saveUrlResults() {
        val urlResult = remoteConfig.getString(URL_RESULTS_KEY)
        localDataSource.saveDataUrlResultsToSharedPreferences(urlResult)
    }

    private fun saveUrlStream() {
        val urlStream = remoteConfig.getString(URL_STREAM_KEY)
        localDataSource.saveDataUrlStreamToSharedPreferences(urlStream)
    }

    private fun saveUrlSiteNews() {
        val urlSiteNews = remoteConfig.getString(URL_SITE_NEWS_KEY)
        localDataSource.saveDataUrlSiteNewsToSharedPreferences(urlSiteNews)
    }

    private fun saveUrlDakarResults() {
        val urlResultsDakar = remoteConfig.getString(URL_RESULTS_DAKAR_KEY)
        localDataSource.saveDataUrlResultsDakarToSharedPreferences(urlResultsDakar)
    }


    private fun fetch() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUrlResults()
                    saveUrlStream()
                    saveUrlSiteNews()
                    saveUrlDakarResults()
                } else {
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return when (item.itemId) {
            R.id.menu_results -> {
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.fragment_container, ResultsFragment.newInstance()).commitAllowingStateLoss()
                true
            }
            R.id.menu_results_dakar -> {
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.fragment_container, ResultsDakarFragment.newInstance()).commitAllowingStateLoss()
                true
            }
            R.id.menu_video -> {
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.fragment_container, StreamFragment.newInstance()).commitAllowingStateLoss()
                true
            }
            R.id.menu_news -> {
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.fragment_container, SiteFragment.newInstance()).commitAllowingStateLoss()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun initDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_main -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SiteFragment.newInstance()).addToBackStack(null).commitAllowingStateLoss()
                }
                R.id.nav_wrc_results -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ResultsFragment.newInstance()).addToBackStack(null).commitAllowingStateLoss()

                }
                R.id.nav_wrc_stream -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StreamFragment.newInstance()).addToBackStack(null).commitAllowingStateLoss()

                }
                R.id.nav_dakar_results -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ResultsDakarFragment.newInstance()).addToBackStack(null).commitAllowingStateLoss()

                }
                R.id.nav_video_wrc -> {
                    openFragment(WebViewFragment(), URL_VIDEO_WRC)
                }
                R.id.nav_video_dakar -> {
                    openFragment(WebViewFragment(), URL_VIDEO_DAKAR)
                }
                R.id.nav_news_wrc -> {
                    openFragment(WebViewFragment(), URL_NEWS_WRC)
                }
                R.id.nav_news_erc -> {
                    openFragment(WebViewFragment(), URL_NEWS_ERC)
                }
                R.id.nav_news_dakar -> {
                    openFragment(WebViewFragment(), URL_NEWS_DAKAR)
                }
                R.id.nav_exit -> {
                    this.finishAffinity()
                }

            }
            true
        }
    }

    private fun openFragment(fragment: Fragment, url: String){
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.fragment_container, WebViewFragment.newInstance(Bundle().apply {
                    putString(URL_KEY, url)
                }))
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
    }

    override fun onBackPressed() {
        val resultsFragment = supportFragmentManager.findFragmentById(R.id.fragment_results)
        if (resultsFragment != null) {

        } else {
            super.onBackPressed()
        }
    }
}