package com.blakebarrett.snse

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.blakebarrett.snse.db.AppDatabase
import com.blakebarrett.snse.db.Sentiment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            save()
            reset()
            Snackbar.make(view, getString(R.string.thanks), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        reset()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId) {
            R.id.action_settings -> showSettings()
            R.id.action_history -> showHistory()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getCurrentSentiment(): Sentiment {
        val timestamp = System.currentTimeMillis() / 1000
        val feeling = when(feelingRadioGroup.checkedRadioButtonId) {
            R.id.radioSad -> getString(R.string.feelingSad)
            R.id.radioMeh -> getString(R.string.feelingMeh)
            R.id.radioHappy -> getString(R.string.feelingHappy)
            else -> String()
        }
        val intensity = intensityBar.progress
        val water = waterCheckBox.isChecked
        val elaborate = elaborateText.text.toString()
        return Sentiment(
            timestamp = timestamp,
            feeling = feeling,
            intensity = intensity,
            color = "",
            water = water,
            elaborate = elaborate
        )
    }

    private fun save() {
        val currentSentiment = getCurrentSentiment()
        AppDatabase.getInstance(applicationContext).sentimentDao().insert(currentSentiment)
    }

    private fun reset() {
        feelingRadioGroup.clearCheck()
        waterCheckBox.isChecked = false
        intensityBar.progress = 50
        elaborateText.text.clear()
    }

    private fun showColorPicker() {

    }

    private fun showSettings() {

    }

    private fun showHistory() {
        // TODO: Lock behind biometric authentication.
        val intent = Intent(this.applicationContext, SentimentListActivity::class.java)
        startActivity(intent)
    }
}
