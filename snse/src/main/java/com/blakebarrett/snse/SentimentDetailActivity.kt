package com.blakebarrett.snse

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.blakebarrett.snse.db.AppDatabase
import com.blakebarrett.snse.db.Sentiment
import kotlinx.android.synthetic.main.activity_sentiment_detail.*

/**
 * An activity representing a single Sentiment detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [SentimentListActivity].
 */
class SentimentDetailActivity : AppCompatActivity() {

    lateinit var mSentiment: Sentiment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sentiment_detail)
        setSupportActionBar(detail_toolbar)

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
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val itemId = intent.getLongExtra(SentimentDetailFragment.ARG_ITEM_ID, 0)
            val fragment = SentimentDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(
                        SentimentDetailFragment.ARG_ITEM_ID,
                        itemId
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.sentiment_detail_container, fragment)
                .commit()

            this.mSentiment =
                AppDatabase.getInstance(applicationContext).sentimentDao().findByTimestamp(itemId)
        }
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
                true
            }
            R.id.action_delete -> {
                AppDatabase.getInstance(applicationContext).sentimentDao().delete(mSentiment)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}
