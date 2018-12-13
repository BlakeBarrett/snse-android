package com.blakebarrett.snse.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    public static final String PREFERENCES_CHANGED = "the preferences have been changed at";

    private SharedPreferences sharedPreferences;

    private static class Blocker {
    }

    private static PreferenceUtil instance;

    PreferenceUtil(final Blocker singletonBlocker) {
    }

    public static final PreferenceUtil getInstance(final Context context) {
        if (instance == null) {
            instance = new PreferenceUtil(new PreferenceUtil.Blocker());
        }
        instance.sharedPreferences = context.getApplicationContext().getSharedPreferences(
                PreferenceUtil.class.toString(),
                Context.MODE_PRIVATE);
        return instance;
    }

    public Long getLong(final String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public boolean getBool(final String key) {
        // The default value for preferences in the Settings UI is enabled;
        // however, the preference isn't saved until its toggled; this makes
        // this getter have the same level of "truthiness" as the UI.
        return sharedPreferences.getBoolean(key, true);
    }

    public String getString(final String key) {
        return sharedPreferences.getString(key, "");
    }

    public void savePref(final String key, final Long value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public boolean getPreferencesDirty() {
        return getLong(PREFERENCES_CHANGED) > 0;
    }

    public void setPreferencesDirty() {
        savePref(PREFERENCES_CHANGED, System.currentTimeMillis());
    }

    public void setPreferencesClean() {
        savePref(PREFERENCES_CHANGED, (long) 0);
    }
}
