package com.blakebarrett.snse

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.an.biometric.BiometricCallback
import com.an.biometric.BiometricManager
import com.blakebarrett.snse.db.AppDatabase
import com.blakebarrett.snse.db.Sentiment
import com.blakebarrett.snse.utils.ColorUtils
import com.blakebarrett.snse.utils.NotificationUtils
import com.blakebarrett.snse.utils.PreferenceUtil
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*


class MainActivity : AppCompatActivity(), ColorPickerDialogFragment.ColorPickerDialogListener {

    private var mSelectedColor = 0
    private var mAuthenticated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            save()
            reset()
            Snackbar.make(view, getString(R.string.thanks), Snackbar.LENGTH_LONG).show()
        }
        colorButton.setOnClickListener {
            showColorPickerDialog()
        }

        applySliderChangeListener(intensityBar)
        reset()
        applyStyling()
        registerNotifications()
    }

    private fun applySliderChangeListener(bar: SeekBar) {
        val minFontSize = 14
        val maxFontSize = 72
        bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val intensity = progress * 0.01
                val size = minFontSize + (intensity * maxFontSize)
                updateRadioFontSize(size)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun applyStyling() {
        val typeface = ResourcesCompat.getFont(this, R.font.signpainter)
        collapsingToolbar.setCollapsedTitleTypeface(typeface)
        collapsingToolbar.setExpandedTitleTypeface(typeface)

        val radios = arrayListOf<RadioButton>(radioSad, radioMeh, radioHappy)
        for (radio in radios) {
            radio.setOnClickListener { v ->
                val accentColor = if (mSelectedColor != 0) {
                    mSelectedColor
                } else {
                    getColor(R.color.colorAccent)
                }
                updateFeelingBackgroundColors(accentColor)
            }
        }
    }

    private fun updateFeelingBackgroundColors(color: Int) {
        val radios = arrayListOf<RadioButton>(radioSad, radioMeh, radioHappy)
        for (v in radios) {
            if (v.id == feelingRadioGroup.checkedRadioButtonId) {
                v.setBackgroundColor(color)
            } else {
                v.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun updateAllBackgroundColors(color: Int) {
        updateFeelingBackgroundColors(color)
        intensityBar.progressDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        intensityBar.thumb.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        fab.backgroundTintList = ColorStateList.valueOf(color)

        // These all need their accent color changed, not background.
//        colorButton.setBackgroundColor(color)
//        waterCheckBox.highlightColor = color
//        elaborateText.highlightColor = color
    }

    private fun updateRadioFontSize(value: Double) {
        radioSad.textSize = value.toFloat()
        radioMeh.textSize = value.toFloat()
        radioHappy.textSize = value.toFloat()
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

    private fun getCurrentSentiment(): Sentiment {
        val timestamp = System.currentTimeMillis() / 1000
        val feeling = when (feelingRadioGroup.checkedRadioButtonId) {
            R.id.radioSad -> getString(R.string.feelingSad)
            R.id.radioMeh -> getString(R.string.feelingMeh)
            R.id.radioHappy -> getString(R.string.feelingHappy)
            else -> String()
        }

        val intensity = intensityBar.progress
        val water = waterCheckBox.isChecked
        val elaborate = elaborateText.text.toString()
        val color = ColorUtils.toHexString(mSelectedColor.toBigInteger().toByteArray())

        return Sentiment(
            timestamp = timestamp,
            feeling = feeling,
            intensity = intensity,
            color = color,
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
        val defaultAccentColor = getColor(R.color.colorAccent)
        updateAllBackgroundColors(defaultAccentColor)
        mSelectedColor = 0
    }

    private fun showSettings() {
        val intent = Intent(this.applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun getBiometricCallback(): BiometricCallback {
        return object : BiometricCallback {
            override fun onSdkVersionNotSupported() {
                /*
                 *  Will be called if the device sdk version does not support Biometric authentication
                 */
            }

            override fun onBiometricAuthenticationNotSupported() {
                /*
                 *  Will be called if the device does not contain any fingerprint sensors
                 */
            }

            override fun onBiometricAuthenticationNotAvailable() {
                /*
                 *  The device does not have any biometrics registered in the device.
                 */
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
        val intent = Intent(this.applicationContext, SentimentListActivity::class.java)
        startActivity(intent)
    }

    /** Color picker stuff
     *
     * Thanks!: https://github.com/danielnilsson9/color-picker-view
     *
     **/
    fun showColorPickerDialog() {
        val fragment = ColorPickerDialogFragment.newInstance(
            0,
            null,
            null,
            Color.WHITE,
            false
        )
        fragment.show(
            fragmentManager,
            fragment.toString()
        )
    }

    override fun onDialogDismissed(dialogId: Int) {}

    override fun onColorSelected(dialogId: Int, color: Int) {
        mSelectedColor = color
        updateAllBackgroundColors(color)
    }
}
