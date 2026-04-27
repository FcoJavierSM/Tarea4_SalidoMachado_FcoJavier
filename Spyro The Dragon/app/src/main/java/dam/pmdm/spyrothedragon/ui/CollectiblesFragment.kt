package dam.pmdm.spyrothedragon.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.adapters.CollectiblesAdapter
import dam.pmdm.spyrothedragon.databinding.FragmentCollectiblesBinding
import dam.pmdm.spyrothedragon.models.Collectible
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class CollectiblesFragment : Fragment() {

    private var _binding: FragmentCollectiblesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectiblesAdapter
    private val collectiblesList = mutableListOf<Collectible>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCollectiblesBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewCollectibles
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CollectiblesAdapter(collectiblesList)
        recyclerView.adapter = adapter

        loadCollectibles()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadCollectibles() {
        try {
            val inputStream: InputStream =
                resources.openRawResource(R.raw.collectibles)

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var currentCollectible: Collectible? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "collectible" -> currentCollectible = Collectible()
                            "name" -> currentCollectible?.name = parser.nextText()
                            "description" -> currentCollectible?.description = parser.nextText()
                            "image" -> currentCollectible?.image = parser.nextText()
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "collectible" && currentCollectible != null) {
                            collectiblesList.add(currentCollectible)
                        }
                    }
                }
                eventType = parser.next()
            }

            adapter.notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
