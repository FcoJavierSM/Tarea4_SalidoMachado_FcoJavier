package dam.pmdm.spyrothedragon.guide

import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import dam.pmdm.spyrothedragon.MainActivity
import dam.pmdm.spyrothedragon.R

class GuideOverlayView(context: Context) : FrameLayout(context) {

    private val prefs = context.getSharedPreferences("spyro_guide", Context.MODE_PRIVATE)
    private var step = 0
    private var mediaPlayer: MediaPlayer? = null
    var onComplete: (() -> Unit)? = null

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        isClickable = true
        isFocusable = true
    }

    fun shouldShow() = !prefs.getBoolean("done", false)

    fun start() = showStep(0)

    private fun advance() {
        if (step >= 5) {
            complete()
        } else {
            ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                duration = 200
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(a: android.animation.Animator) {
                        showStep(++step)
                        ObjectAnimator.ofFloat(this@GuideOverlayView, "alpha", 0f, 1f).apply {
                            duration = 300
                            start()
                        }
                    }
                })
                start()
            }
        }
    }

    private fun complete() {
        prefs.edit().putBoolean("done", true).apply()
        releaseMedia()
        ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
            duration = 400
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(a: android.animation.Animator) {
                    (parent as? ViewGroup)?.removeView(this@GuideOverlayView)
                    onComplete?.invoke()
                    (context as? MainActivity)?.binding?.navView?.selectedItemId = R.id.nav_characters
                }
            })
            start()
        }
    }

    private fun showStep(s: Int) {
        step = s
        removeAllViews()
        when (s) {
            0 -> guideBienvenida()
            1 -> guidePantalla2()
            2 -> guidePantalla3()
            3 -> guidePantalla4()
            4 -> guidePantalla5()
            5 -> guidePantalla6()
        }
        if (s != 0) playSound()
    }

    private fun guideBienvenida() {
        setBackgroundColor(Color.TRANSPARENT)
        val view = LayoutInflater.from(context).inflate(R.layout.guide_bienvenida, this, false)
        addView(view)

        val spyro = view.findViewById<ImageView>(R.id.guide_spyro)
        val bigTitle = view.findViewById<View>(R.id.guide_big_title)
        val subtitle = view.findViewById<View>(R.id.guide_subtitle)
        val btnSkip = view.findViewById<Button>(R.id.guide_btn_skip)
        val btnComenzar = view.findViewById<Button>(R.id.guide_btn_comenzar)

        btnComenzar.background = ovalBackground("#fbdf6a")
        btnSkip.background = ovalBackground("#fbdf6a")
        btnSkip.setOnClickListener { complete() }
        btnComenzar.setOnClickListener { playSound(); advance() }

        ObjectAnimator.ofFloat(spyro, "scaleX", 0f, 1.15f, 1f).apply { duration = 850; start() }
        ObjectAnimator.ofFloat(spyro, "scaleY", 0f, 1.15f, 1f).apply { duration = 850; start() }
        ObjectAnimator.ofFloat(bigTitle, "alpha", 0f, 1f).apply { duration = 600; startDelay = 150; start() }
        ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f).apply { duration = 600; startDelay = 650; start() }
        ObjectAnimator.ofFloat(spyro, "translationY", 0f, -px(14).toFloat(), 0f).apply {
            duration = 1700; repeatCount = ObjectAnimator.INFINITE; repeatMode = ObjectAnimator.REVERSE; startDelay = 900; start()
        }
    }

    private fun guidePantalla2() {
        setBackgroundColor(Color.parseColor("#66000000"))
        val view = LayoutInflater.from(context).inflate(R.layout.guide_pantalla2, this, false)
        addView(view)
        navigateTo(R.id.navigation_characters)
        (context as? MainActivity)?.binding?.navView?.menu?.findItem(R.id.nav_characters)?.isChecked = true

        val indicator = view.findViewById<View>(R.id.guide_indicator)
        val container = view.findViewById<View>(R.id.guide_container)
        val btnSkip = view.findViewById<TextView>(R.id.guide_btn_skip)
        val btnNext = view.findViewById<Button>(R.id.guide_btn_next)

        btnSkip.setOnClickListener { complete() }
        btnNext.background = ovalBackground("#fbdf6a")
        btnNext.setOnClickListener { advance() }

        animateContainer(container)

        indicator.translationX = 0f
        indicator.translationY = 0f
        indicator.visibility = View.VISIBLE
        animateIndicator(indicator)

        btnNext.alpha = 0f
        ObjectAnimator.ofFloat(btnNext, "alpha", 0f, 1f).apply { duration = 400; start() }
    }

    private fun guidePantalla3() {
        setBackgroundColor(Color.parseColor("#66000000"))
        val view = LayoutInflater.from(context).inflate(R.layout.guide_pantalla3, this, false)
        addView(view)
        navigateTo(R.id.navigation_worlds)
        (context as? MainActivity)?.binding?.navView?.menu?.findItem(R.id.nav_worlds)?.isChecked = true
        inflateBubbleCommon(view)
        val indicator = view.findViewById<View>(R.id.guide_indicator)
        animateIndicator(indicator)
    }

    private fun guidePantalla4() {
        setBackgroundColor(Color.parseColor("#66000000"))
        val view = LayoutInflater.from(context).inflate(R.layout.guide_pantalla4, this, false)
        addView(view)
        navigateTo(R.id.navigation_collectibles)
        (context as? MainActivity)?.binding?.navView?.menu?.findItem(R.id.nav_collectibles)?.isChecked = true
        inflateBubbleCommon(view)
        val indicator = view.findViewById<View>(R.id.guide_indicator)
        indicator.translationX = 0f
        indicator.translationY = 0f
        indicator.visibility = View.VISIBLE
        animateIndicator(indicator)
    }

    private fun guidePantalla5() {
        setBackgroundColor(Color.parseColor("#66000000"))
        val view = LayoutInflater.from(context).inflate(R.layout.guide_pantalla5, this, false)
        addView(view)
        inflateBubbleCommon(view)
        val indicator = view.findViewById<View>(R.id.guide_indicator)
        indicator.translationX = 0f
        indicator.translationY = 0f
        indicator.visibility = View.VISIBLE
        animateIndicator(indicator)
    }

    private fun navigateTo(destId: Int) {
        val host = (context as? MainActivity)?.supportFragmentManager?.findFragmentById(R.id.navHostFragment) ?: return
        NavHostFragment.findNavController(host).navigate(destId)
    }

    private fun inflateBubbleCommon(view: View) {
        val btnSkip = view.findViewById<TextView>(R.id.guide_btn_skip)
        val btnNext = view.findViewById<Button>(R.id.guide_btn_next)

        btnSkip.setOnClickListener { complete() }
        btnNext.background = ovalBackground("#fbdf6a")
        btnNext.setOnClickListener { advance() }
    }

    private fun guidePantalla6() {
        setBackgroundColor(Color.parseColor("#F01a0a3d"))
        val view = LayoutInflater.from(context).inflate(R.layout.guide_pantalla6, this, false)
        addView(view)

        val root = view.findViewById<View>(R.id.guide_root)
        val img = view.findViewById<ImageView>(R.id.guide_img)
        val btnStart = view.findViewById<Button>(R.id.guide_btn_start)

        btnStart.background = ovalBackground(color = "#9C27B0")
        btnStart.setOnClickListener { playSound(); complete() }

        ObjectAnimator.ofFloat(root, "alpha", 0f, 1f).apply { duration = 700; start() }
        ObjectAnimator.ofFloat(img, "rotationY", 0f, 360f).apply { duration = 1000; start() }
        ObjectAnimator.ofFloat(img, "translationY", 0f, -px(12).toFloat(), 0f).apply {
            duration = 1600; repeatCount = ObjectAnimator.INFINITE; repeatMode = ObjectAnimator.REVERSE; startDelay = 1000; start()
        }
    }

    private fun animateIndicator(v: View) {
        ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.9f, 1f).apply { duration = 900; repeatCount = ObjectAnimator.INFINITE; start() }
        ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.9f, 1f).apply { duration = 900; repeatCount = ObjectAnimator.INFINITE; start() }
        ObjectAnimator.ofFloat(v, "alpha", 1f, 0.2f, 1f).apply { duration = 900; repeatCount = ObjectAnimator.INFINITE; start() }
    }

    private fun animateContainer(container: View) {
        container.alpha = 0f
        ObjectAnimator.ofFloat(container, "alpha", 0f, 1f).apply { duration = 400; start() }
    }

    private fun ovalBackground(color: String) = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(Color.parseColor(color))
    }

    private fun playSound() {
        releaseMedia()
        val id = context.resources.getIdentifier("sonidospyro", "raw", context.packageName)
        if (id != 0) {
            mediaPlayer = MediaPlayer.create(context, id)
            mediaPlayer?.start()
        }
    }

    private fun releaseMedia() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun px(dp: Int) = (dp * context.resources.displayMetrics.density).toInt()
}
