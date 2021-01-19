package com.blakebarrett.snse

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.blakebarrett.snse.databinding.ActivitySentimentDetailBinding
import com.blakebarrett.snse.databinding.SentimentDetailBinding
import com.blakebarrett.snse.db.AppDatabase
import com.blakebarrett.snse.db.Sentiment
import com.blakebarrett.snse.db.SentimentDAO
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * An activity representing a single Sentiment detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [SentimentListActivity].
 */
class SentimentDetailActivity(
    private var mSentiment: Sentiment? = null
) : AppCompatActivity() {

    private lateinit var binding: SentimentDetailBinding
    private lateinit var activityBinding: ActivitySentimentDetailBinding
    val toolbarLayout: CollapsingToolbarLayout
        get() {
            return activityBinding.toolbarLayout
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SentimentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityBinding = ActivitySentimentDetailBinding.inflate(layoutInflater).also {
            setSupportActionBar(it.detailToolbar)
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

        savedInstanceState?.let {
            return
        }

        // Create the detail fragment and add it to the activity
        // using a fragment transaction.
        intent.getLongExtra(SentimentDetailFragment.ARG_ITEM_ID, 0).let { itemId ->
            SentimentDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(
                        SentimentDetailFragment.ARG_ITEM_ID,
                        itemId
                    )
                }
            }.let { fragment ->
                supportFragmentManager.beginTransaction()
                    .add(R.id.sentiment_detail_container, fragment)
                    .commit()
            }

            fetch(itemId).let { sentiment ->
                this.mSentiment = sentiment
            }
        }
    }

    override fun onDestroy() {
        this.mSentiment = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_sentiment_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                finish()
            }
            R.id.action_delete -> {
                mSentiment?.let {
                    delete(it)
                }
                finish()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun fetch(
        itemId: Long,
        context: Context = applicationContext,
        database: AppDatabase = AppDatabase.getInstance(context),
        sentimentDao: SentimentDAO = database.sentimentDao()
    ): Sentiment {
        return sentimentDao.findByTimestamp(itemId)
    }

    private fun delete(
        sentiment: Sentiment,
        context: Context = applicationContext,
        database: AppDatabase = AppDatabase.getInstance(context),
        sentimentDao: SentimentDAO = database.sentimentDao()
    ) {
        sentimentDao.delete(sentiment)
    }
}
