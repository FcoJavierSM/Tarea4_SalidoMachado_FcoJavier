package dam.pmdm.spyrothedragon.adapters

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.models.Character
import dam.pmdm.spyrothedragon.ui.ScepterView

class CharactersAdapter(
    private val list: List<Character>
) : RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {

    private val characterImages = mapOf(
        "spyro" to R.drawable.spyro,
        "hunter" to R.drawable.hunter,
        "elora" to R.drawable.elora,
        "ripto" to R.drawable.ripto
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview, parent, false)
        return CharactersViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val character = list[position]
        holder.nameTextView.text = character.name

        val drawableRes = characterImages[character.image] ?: R.drawable.placeholder
        holder.imageImageView.setImageResource(drawableRes)

        if (character.image == "ripto") {
            holder.itemView.setOnLongClickListener {
                showScepterEasterEgg(it)
                true
            }
        } else {
            holder.itemView.setOnLongClickListener(null)
        }
    }

    private fun showScepterEasterEgg(anchor: View) {
        val context = anchor.context
        val density = context.resources.displayMetrics.density
        val scepter = ScepterView(context)

        val dialog = Dialog(context)
        dialog.setContentView(
            scepter,
            ViewGroup.LayoutParams(
                (220 * density).toInt(),
                (280 * density).toInt()
            )
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val rotate = ObjectAnimator.ofFloat(scepter, "sparkleAngle", 0f, 360f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        val shimmer = ObjectAnimator.ofFloat(scepter, "shimmerIntensity", 0.2f, 1f).apply {
            duration = 800
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val colorAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            Color.CYAN,
            Color.MAGENTA,
            Color.parseColor("#FFD700"),
            Color.GREEN,
            Color.parseColor("#FF4500"),
            Color.CYAN
        ).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { scepter.glowColor = it.animatedValue as Int }
        }

        val set = AnimatorSet().apply {
            playTogether(rotate, shimmer, colorAnim)
            start()
        }

        dialog.setOnDismissListener { set.cancel() }
        dialog.show()
    }

    override fun getItemCount(): Int = list.size

    class CharactersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val imageImageView: ImageView = itemView.findViewById(R.id.image)
    }
}
