package dam.pmdm.spyrothedragon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.models.Collectible

class CollectiblesAdapter(
    private val list: List<Collectible>
) : RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder>() {

    private val collectibleImages = mapOf(
        "dragon_eggs" to R.drawable.dragon_eggs,
        "gems" to R.drawable.gems,
        "skill_points" to R.drawable.skill_points,
        "spirit_crates" to R.drawable.spirit_crates,
        "dragonflies" to R.drawable.dragonflies,
        "egg_thief" to R.drawable.egg_thief
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectiblesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview, parent, false)
        return CollectiblesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectiblesViewHolder, position: Int) {
        val collectible = list[position]
        holder.nameTextView.text = collectible.name

        val drawableRes = collectibleImages[collectible.image] ?: R.drawable.placeholder
        holder.imageImageView.setImageResource(drawableRes)
    }

    override fun getItemCount(): Int = list.size

    class CollectiblesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val imageImageView: ImageView = itemView.findViewById(R.id.image)
    }
}
