package com.blakebarrett.snse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blakebarrett.snse.db.AppDatabase
import com.blakebarrett.snse.db.Sentiment
import kotlinx.android.synthetic.main.activity_sentiment_detail.*
import kotlinx.android.synthetic.main.sentiment_detail.*

/**
 * A fragment representing a single Sentiment detail screen.
 * This fragment is either contained in a [SentimentListActivity]
 * in two-pane mode (on tablets) or a [SentimentDetailActivity]
 * on handsets.
 */
class SentimentDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: Sentiment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                val dao = AppDatabase.getInstance(this.requireContext().applicationContext).sentimentDao()
                val timestamp = (activity?.intent?.extras?.get(ARG_ITEM_ID) ?: 0) as Long
                item = dao.findByTimestamp(timestamp)
                activity?.toolbar_layout?.title = item?.prettyDate()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.sentiment_detail, container, false)
        return rootView
    }

    override fun onResume() {
        super.onResume()

        item?.let {
            feelingsDetailTextView.text = it.feeling
            intensityBar.progress = it.intensity
            waterCheckBox.isChecked = it.water
            elaborateText.text = it.elaborate
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "timestamp"
    }
}
