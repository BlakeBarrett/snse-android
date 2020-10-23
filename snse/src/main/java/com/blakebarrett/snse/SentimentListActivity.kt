package com.blakebarrett.snse

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.blakebarrett.snse.db.AppDatabase
import com.blakebarrett.snse.db.Sentiment
import kotlinx.android.synthetic.main.activity_sentiment_list.*
import kotlinx.android.synthetic.main.sentiment_list.*
import kotlinx.android.synthetic.main.sentiment_list_content.view.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [SentimentDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class SentimentListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private lateinit var sentiments: List<Sentiment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sentiment_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (sentiment_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        sentiments = AppDatabase.getInstance(this.applicationContext).sentimentDao().getAll()
        setupRecyclerView(sentiment_list)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onResume() {
        super.onResume()
        sentiments = AppDatabase.getInstance(this.applicationContext).sentimentDao().getAll()
        sentiment_list.adapter?.notifyDataSetChanged()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, sentiments, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: SentimentListActivity,
        private val values: List<Sentiment>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { view ->
                (view.tag as Sentiment).let { item ->
                    if (twoPane) {
                        SentimentDetailFragment().apply {
                            arguments = Bundle().apply {
                                putLong(SentimentDetailFragment.ARG_ITEM_ID, item.timestamp)
                            }
                        }.let {fragment ->
                            parentActivity.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.sentiment_detail_container, fragment)
                                .commit()
                        }
                    } else {
                        view.context.startActivity(
                            Intent(
                                view.context,
                                SentimentDetailActivity::class.java
                            ).apply {
                                putExtra(
                                    SentimentDetailFragment.ARG_ITEM_ID,
                                    item.timestamp
                                )
                            }
                        )
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.sentiment_list_content,
                        parent,
                        false
                    )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            values[position].let { item ->
                holder.idView.text = item.feeling
                holder.contentView.text = item.elaborate
                holder.backgroundView.setBackgroundColor(item.colorInt())

                with(holder.itemView) {
                    tag = item
                    setOnClickListener(onClickListener)
                }
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
            val backgroundView: View = view.id_background
        }
    }
}
