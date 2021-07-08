package com.blakebarrett.snse

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.an.biometric.BiometricCallback
import com.an.biometric.BiometricManager
import com.blakebarrett.snse.databinding.ActivityScrollingBinding
import com.blakebarrett.snse.databinding.ContentScrollingBinding
import com.blakebarrett.snse.db.AppDatabase
import com.blakebarrett.snse.db.Sentiment
import com.blakebarrett.snse.db.SentimentDAO
import com.blakebarrett.snse.utils.ColorUtils
import com.blakebarrett.snse.utils.NotificationUtils
import com.blakebarrett.snse.utils.PreferenceUtil
import com.blakebarrett.snse.R
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity (
    private var mSelectedColor: Int = 0,
    private var mAuthenticated: Boolean = false
) : AppCompatActivity(), ColorPickerDialogFragment.ColorPickerDialogListener {

    private lateinit var binding: ContentScrollingBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var collapsingToolbar: CollapsingToolbarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityScrollingBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            fab = it.fab
            setSupportActionBar(it.toolbar)
            collapsingToolbar = it.collapsingToolbar
        }
        fab.setOnClickListener { view ->
            save()
            reset()
            Snackbar.make(view, getString(R.string.thanks), Snackbar.LENGTH_LONG).show()
        }

        binding = ContentScrollingBinding.inflate(layoutInflater).also {
            it.colorButton.setOnClickListener {
                showColorPickerDialog()
            }
            applySliderChangeListener(it.intensityBar)
        }
        reset()
        applyStyling()
        registerNotifications()
    }

    private fun applySliderChangeListener(
        bar: SeekBar,
        minFontSize: Int = 14,
        maxFontSize: Int = 72,
        changeListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val intensity = progress * 0.01
                val size = minFontSize + (intensity * maxFontSize)
                updateRadioFontSize(size)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    ) {
        bar.setOnSeekBarChangeListener(changeListener)
    }

    private fun applyStyling() {
        val typeface = ResourcesCompat.getFont(this, R.font.signpainter)
        with(collapsingToolbar) {
            setCollapsedTitleTypeface(typeface)
            setExpandedTitleTypeface(typeface)
        }

        arrayListOf<RadioButton>(
            binding.radioSad,
            binding.radioMeh,
            binding.radioHappy
        ).forEach { radio ->
            radio.setOnClickListener { _ ->
                updateFeelingBackgroundColors(
                    if (mSelectedColor != 0) {
                        mSelectedColor
                    } else {
                        getColor(R.color.colorAccent)
                    }
                )
            }
        }
    }

    private fun updateFeelingBackgroundColors(value: Int) {
        arrayListOf<RadioButton>(
            binding.radioSad,
            binding.radioMeh,
            binding.radioHappy
        ).forEach {
            it.setBackgroundColor(
                if (it.id == binding.feelingRadioGroup.checkedRadioButtonId) value else Color.TRANSPARENT
            )
        }
    }

    private fun updateAllBackgroundColors(color: Int) {
        updateFeelingBackgroundColors(color)
        binding.intensityBar.apply {
            progressDrawable.setTint(color)
            thumb.setTint(color)
        }
        binding.elaborateText.highlightColor = color
        fab.backgroundTintList = ColorStateList.valueOf(color)
    }

    private fun updateRadioFontSize(value: Double) {
        binding.radioSad.textSize = value.toFloat()
        binding.radioMeh.textSize = value.toFloat()
        binding.radioHappy.textSize = value.toFloat()
    }

    private fun registerNotifications() {
        NotificationUtils.applicationDidLaunch(this)
        if (PreferenceUtil.getInstance(this).preferencesDirty) {
            NotificationUtils.scheduleAlarm(this)
        }
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

    private fun getCurrentSentiment(
        timestamp: Long = System.currentTimeMillis() / 1000,
        feeling: String = when (binding.feelingRadioGroup.checkedRadioButtonId) {
            R.id.radioSad -> getString(R.string.feelingSad)
            R.id.radioMeh -> getString(R.string.feelingMeh)
            R.id.radioHappy -> getString(R.string.feelingHappy)
            else -> String()
        },
        intensity: Int = binding.intensityBar.progress,
        water: Boolean = binding.waterCheckBox.isChecked,
        elaborate: String = binding.elaborateText.text.toString(),
        color: String = ColorUtils.toHexString(mSelectedColor.toBigInteger().toByteArray())
    ): Sentiment {
        return Sentiment(
            timestamp = timestamp,
            feeling = feeling,
            intensity = intensity,
            color = color,
            water = water,
            elaborate = elaborate
        )
    }

    private fun save(
        db: AppDatabase = AppDatabase.getInstance(applicationContext),
        dao: SentimentDAO = db.sentimentDao(),
        sentiment: Sentiment = getCurrentSentiment()
    ) {
        dao.insert(sentiment)
    }

    private fun reset() {
        binding.feelingRadioGroup.clearCheck()
        binding.waterCheckBox.isChecked = false
        binding.intensityBar.progress = 50
        binding.elaborateText.text.clear()
        getColor(R.color.colorAccent).let { defaultAccentColor ->
            binding.elaborateText.highlightColor = defaultAccentColor
            updateAllBackgroundColors(defaultAccentColor)
        }
        mSelectedColor = 0
    }

    private fun showSettings() {
        Intent(this.applicationContext, SettingsActivity::class.java).let {
            startActivity(it)
        }
    }

    private fun getBiometricCallback(): BiometricCallback {
        return object : BiometricCallback {
            override fun onSdkVersionNotSupported() {
                /*
                 *  Will be called if the device sdk version does not support Biometric authentication
                 */
                mAuthenticated = true
                startHistoryActivity()
            }

            override fun onBiometricAuthenticationNotSupported() {
                /*
                 *  Will be called if the device does not contain any fingerprint sensors
                 */
                mAuthenticated = true
                startHistoryActivity()
            }

            override fun onBiometricAuthenticationNotAvailable() {
                /*
                 *  The device does not have any biometrics registered in the device.
                 */
                mAuthenticated = true
                startHistoryActivity()
            }

            override fun onBiometricAuthenticationPermissionNotGranted() {
                /*
                 *  android.permission.USE_BIOMETRIC permission is not granted to the app
                 */
            }

            override fun onBiometricAuthenticationInternalError(error: String) {
                /*
                 *  This method is called if one of the fields such as the title, subtitle,
                 * description or the negative button text is empty
                 */
            }

            override fun onAuthenticationFailed() {
                /*
                 * When the fingerprint does not match with any of the fingerprints registered on the device,
                 * then this callback will be triggered.
                 */
                mAuthenticated = false
            }

            override fun onAuthenticationCancelled() {
                /*
                 * The authentication is cancelled by the user.
                 */
            }

            override fun onAuthenticationSuccessful() {
                /*
                 * When the fingerprint is has been successfully matched with one of the fingerprints
                 * registered on the device, then this callback will be triggered.
                 */
                mAuthenticated = true
                startHistoryActivity()
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
                /*
                 * This method is called when a non-fatal error has occurred during the authentication
                 * process. The callback will be provided with an help code to identify the cause of the
                 * error, along with a help message.
                 */
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                /*
                 * When an unrecoverable error has been encountered and the authentication process has
                 * completed without success, then this callback will be triggered. The callback is provided
                 * with an error code to identify the cause of the error, along with the error message.
                 */
            }
        }
    }

    private fun showHistory() {
        if (mAuthenticated) {
            startHistoryActivity()
            return
        }
        try {
            // Biometric Authentication stuff
            // Shout-out to anitaa1990 for the SDK!
            // https://github.com/anitaa1990/Biometric-Auth-Sample/
            BiometricManager.BiometricBuilder(this@MainActivity)
                .setTitle(getString(R.string.authenticate))
                .setSubtitle(getString(R.string.auth_subtitle))
                .setDescription(getString(R.string.auth_description))
                .setNegativeButtonText(getString(R.string.cancel))
                .build()
                .authenticate(getBiometricCallback())
        } catch (exception: RuntimeException) {
            // IF the user hasn't setup any security, nothing we can do.
            // You can lead a horse to water, but you can't make 'em drink.
            mAuthenticated = true
            startHistoryActivity()
        }
    }

    private fun startHistoryActivity() {
        startActivity(
            Intent(
                this.applicationContext,
                SentimentListActivity::class.java
            )
        )
    }

    /** Color picker stuff
     *
     * Thanks!: https://github.com/danielnilsson9/color-picker-view
     *
     **/
    private fun showColorPickerDialog(
        color: Int = if (this.mSelectedColor != 0) mSelectedColor else Color.WHITE
    ) {
        ColorPickerDialogFragment.newInstance(
            0,
            null,
            null,
            color,
            false
        ).let {
            it.show(
                fragmentManager,
                ""
            )
        }
    }

    override fun onDialogDismissed(dialogId: Int) {}

    override fun onColorSelected(dialogId: Int, color: Int) {
        mSelectedColor = color
        updateAllBackgroundColors(color)
    }
}
