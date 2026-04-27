package dam.pmdm.spyrothedragon

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding
import dam.pmdm.spyrothedragon.guide.GuideOverlayView

class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.navHostFragment)

        navHostFragment?.let {
            navController = NavHostFragment.findNavController(it)
            NavigationUI.setupWithNavController(binding.navView, navController!!)
            NavigationUI.setupActionBarWithNavController(this, navController!!)
        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            selectedBottomMenu(menuItem)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_characters,
                R.id.navigation_worlds,
                R.id.navigation_collectibles -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
            applyActionBarTitleColor()
        }

        binding.root.post { maybeShowGuide() }
    }

    private fun maybeShowGuide() {
        val guide = GuideOverlayView(this)
        if (!guide.shouldShow()) return
        binding.navView.isEnabled = false
        for (i in 0 until binding.navView.menu.size()) {
            binding.navView.menu.getItem(i).isEnabled = false
        }
        menu?.findItem(R.id.action_info)?.isEnabled = false
        guide.onComplete = {
            binding.navView.isEnabled = true
            for (i in 0 until binding.navView.menu.size()) {
                binding.navView.menu.getItem(i).isEnabled = true
            }
            menu?.findItem(R.id.action_info)?.isEnabled = true
        }
        guide.elevation = 100f
        binding.root.addView(guide)
        guide.bringToFront()
        guide.start()
    }

    private fun selectedBottomMenu(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_characters ->
                navController?.navigate(R.id.navigation_characters)
            R.id.nav_worlds ->
                navController?.navigate(R.id.navigation_worlds)
            else ->
                navController?.navigate(R.id.navigation_collectibles)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        this.menu = menu
        menu.findItem(R.id.action_info)?.icon?.setTint(ContextCompat.getColor(this, R.color.yellow))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_info) {
            showInfoDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun applyActionBarTitleColor() {
        val title = supportActionBar?.title?.toString() ?: return
        val yellow = ContextCompat.getColor(this, R.color.yellow)
        supportActionBar?.title = SpannableString(title).apply {
            setSpan(ForegroundColorSpan(yellow), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_about)
            .setMessage(R.string.text_about)
            .setPositiveButton(R.string.accept, null)
            .show()
    }
}
