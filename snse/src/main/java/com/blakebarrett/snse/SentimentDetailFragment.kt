package com.blakebarrett.snse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blakebarrett.snse.databinding.SentimentDetailBinding
import com.blakebarrett.snse.db.AppDatabase
import com.blakebarrett.snse.db.Sentiment
import kotlin.math.max

/**
 * A fragment representing a single Sentiment detail screen.
 * This fragment is either contained in a [SentimentListActivity]
 * in two-pane mode (on tablets) or a [SentimentDetailActivity]
 * on handsets.
 */
class SentimentDetailFragment : Fragment() {

    companion object {
        const val ARG_ITEM_ID = "timestamp"
    }

    private var mBinding: SentimentDetailBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SentimentDetailBinding.inflate(inflater, container, false).let {
            mBinding = it
            return binding.root
        }
    }

    override fun onResume() {
        super.onResume()

        item?.let {

            if (it.feeling == "") {
                binding.feelingsDetailTextViewParent.visibility = View.GONE
            } else {
                binding.feelingsDetailTextView.text = it.feeling
                binding.feelingsDetailTextView.textSize = max((96 * (it.intensity * 0.01)), 10.0).toFloat()
            }

            binding.waterImageView.setBackgroundResource(if (it.water) R.drawable.ic_water else R.drawable.ic_water_off)

            if (it.elaborate == "") {
                binding.elaborateTextParent.visibility = View.GONE
            } else {
                binding.elaborateText.text = it.elaborate
            }

            (activity as? SentimentDetailActivity)?.let { activity ->
                activity.toolbarLayout.let { toolbar ->
                    toolbar.title = it.prettyDate()
                    it.colorInt().let { color ->
                        if (color != 0) {
                            toolbar.setBackgroundColor(color)
                        }
                    }
                }
            }
        }
    }

    private val item : Sentiment?
        get() {
            requireContext().applicationContext.let { context ->
                arguments?.let {
                    val sentinel : Long = -1337
                    val timestamp = it.getLong(ARG_ITEM_ID, sentinel)
                    if (timestamp != sentinel) {
                        AppDatabase
                            .getInstance(context)
                            .sentimentDao()
                            .findByTimestamp(timestamp).let { sentiment ->
                                return sentiment
                            }
                    }
                }
            }
            return null
        }
}
