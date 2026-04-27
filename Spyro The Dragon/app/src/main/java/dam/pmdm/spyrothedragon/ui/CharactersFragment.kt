package dam.pmdm.spyrothedragon.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.adapters.CharactersAdapter
import dam.pmdm.spyrothedragon.databinding.FragmentCharactersBinding
import dam.pmdm.spyrothedragon.models.Character
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class CharactersFragment : Fragment() {

    private var _binding: FragmentCharactersBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CharactersAdapter
    private val charactersList = mutableListOf<Character>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCharactersBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewCharacters
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CharactersAdapter(charactersList)
        recyclerView.adapter = adapter

        loadCharacters()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadCharacters() {
        try {
            val inputStream: InputStream =
                resources.openRawResource(R.raw.characters)

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var currentCharacter: Character? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "character" -> currentCharacter = Character()
                            "name" -> currentCharacter?.name = parser.nextText()
                            "description" -> currentCharacter?.description = parser.nextText()
                            "image" -> currentCharacter?.image = parser.nextText()
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "character" && currentCharacter != null) {
                            charactersList.add(currentCharacter)
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
