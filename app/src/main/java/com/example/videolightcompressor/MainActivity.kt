package com.example.videolightcompressor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.videolightcompressor.extensions.openStore
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rate -> {
                openStore()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return when (item.itemId) {
            R.id.nav_home, R.id.nav_gallery -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                navController.navigate(item.itemId)
                true
            }
            R.id.nav_rate_app -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                openStore()
                true
            }
            R.id.nav_contact_us -> {
                contactUs()
                true
            }
            R.id.nav_share_app -> {
                shareApp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareApp() {
        val subject = getString(R.string.app_name)
        val bodyEmail = String.format(
            Locale.US,
            getString(R.string.share_app_body_msg),
            "https://play.google.com/store/apps/details?id=$packageName"
        )
        startShareIntent(subject, null, bodyEmail)
    }

    private fun contactUs() {
        val subject2 = getString(R.string.subject_email, getString(R.string.app_name))
        val addresses = arrayOf(getString(R.string.support_email))
        startShareIntent(subject2, addresses, null)
    }

    private fun startShareIntent(
        subject: String?,
        addresses: Array<String>?,
        text: String?
    ) {
        val intentBuilder = ShareCompat.IntentBuilder.from(this)
        intentBuilder.setType("message/rfc822")
        if (addresses != null) intentBuilder.setEmailTo(addresses)
        if (subject != null) intentBuilder.setSubject(subject)
        if (text != null) intentBuilder.setText(text)
        intentBuilder.startChooser()
    }
}