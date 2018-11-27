package com.blakebarrett.snse.instantAppFeature

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            // TODO: Save values from entry form and reset.
            save()
            reset()
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        setupListeners()
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

    private fun setupListeners() {
        feelingRadioGroup.setOnCheckedChangeListener { feelingRadioGroup, checkedId ->

        }
//        intensityBar.setOnSeekBarChangeListener(this) // TODO: implement SeekBar.OnSeekBarChangeListener
        colorButton.setOnClickListener {
            showColorPicker()
        }
        waterCheckBox.setOnClickListener {

        }
        val elaborate = elaborateText.text
    }

    private fun save() {

    }

    private fun reset() {

    }

    private fun showSettings() {

    }

    private fun showHistory() {

    }

    private fun showColorPicker() {

    }
}
