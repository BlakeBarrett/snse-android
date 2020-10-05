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
import kotlin.math.max

/**
 * A fragment representing a single Sentiment detail screen.
 * This fragment is either contained in a [SentimentListActivity]
 * in two-pane mode (on tablets) or a [SentimentDetailActivity]
 * on handsets.
 */
class SentimentDetailFragment : Fragment() {

    private var item: Sentiment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                val dao =
                    AppDatabase.getInstance(this.requireContext().applicationContext).sentimentDao()
                val timestamp =
                    (activity?.intent?.extras?.get(ARG_ITEM_ID) ?: it.get(ARG_ITEM_ID)) as Long
                item = dao.findByTimestamp(timestamp)
                activity?.toolbar_layout?.title = item?.prettyDate()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.sentiment_detail, container, false)
    }

    override fun onResume() {
        super.onResume()

        item?.let {

            if (it.feeling == "") {
                feelingsDetailTextViewParent.visibility = View.GONE
            } else {
                feelingsDetailTextView.text = it.feeling
                feelingsDetailTextView.textSize = max((96 * (it.intensity * 0.01)), 10.0).toFloat()
            }

            waterImageView.setBackgroundResource(if (it.water) R.drawable.ic_water else R.drawable.ic_water_off)

            if (it.elaborate == "") {
                elaborateTextParent.visibility = View.GONE
            } else {
                elaborateText.text = it.elaborate
            }

            val color = it.colorInt()
            if (color != 0) {
                activity?.toolbar_layout?.setBackgroundColor(color)
            }
        }
    }

    companion object {
        const val ARG_ITEM_ID = "timestamp"
    }
}
