/*
 * This file is part of Capacitive Button Brightness.
 * 
 * Capacitive Button Brightness is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Capacitive Button Brightness is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Capacitive Button Brightness.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sleepydragon.capbutnbrightness;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Persistently stores the capacitive button brightness setting.
 */
public class Settings {

    public static final String PREFS_NAME = "CapButtonBrightness";

    private static final String LEVEL_OFF = "off";
    private static final String LEVEL_DIM = "dim";
    private static final String LEVEL_BRIGHT = "bright";
    private static final int PREFS_MODE = Context.MODE_MULTI_PROCESS;
    private static final String PREFS_KEY_BRIGHTNESS_LEVEL = "level";
    private static final String PREFS_KEY_SET_BRIGHTNESS_ON_BOOT =
        "setBrightnessOnBoot";

    private final Context context;

    /**
     * Creates a new Settings object.
     * 
     * @param context the context to use to read and write the persistent
     * setting.
     * @throws NullPointerException if context==null
     */
    public Settings(Context context) {
        if (context == null) {
            throw new NullPointerException("context==null");
        }
        this.context = context;
    }

    /**
     * Gets the saved value of the capacitive button brightness.
     * 
     * @return the saved level, or null, not set, or set to an invalid value.
     * @see #setLevel(CapButtonBrightness.Level)
     */
    public CapButtonBrightness.Level getLevel() {
        final SharedPreferences prefs = this.getSharedPreferences();
        final String value = prefs.getString(PREFS_KEY_BRIGHTNESS_LEVEL, null);
        final CapButtonBrightness.Level level;
        if (value == null) {
            level = null;
        } else if (value.equals(LEVEL_OFF)) {
            level = CapButtonBrightness.Level.OFF;
        } else if (value.equals(LEVEL_DIM)) {
            level = CapButtonBrightness.Level.DIM;
        } else if (value.equals(LEVEL_BRIGHT)) {
            level = CapButtonBrightness.Level.BRIGHT;
        } else {
            level = null;
        }
        return level;
    }

    /**
     * Gets the SharedPreferences object from this.context from which the
     * settings are retrieved and to which the settings are written.
     * 
     * @return the SharedPreferences
     */
    private SharedPreferences getSharedPreferences() {
        final SharedPreferences prefs =
            this.context.getSharedPreferences(PREFS_NAME, PREFS_MODE);
        return prefs;
    }

    /**
     * Gets whether or not the capacitive button brightness should be set when
     * the device boots up.
     * 
     * @return true if the capacitive button brightness should be set when the
     * device boots up; false if it should not.
     * @see #setSetBrightnessOnBootEnabled(boolean)
     */
    public boolean isSetBrightnessOnBootEnabled() {
        final SharedPreferences prefs = this.getSharedPreferences();
        final boolean enabled =
            prefs.getBoolean(PREFS_KEY_SET_BRIGHTNESS_ON_BOOT, true);
        return enabled;
    }

    /**
     * Sets the saved value of the capacitive button brightness.
     * 
     * @param level the level to set; may be null, in which case any saved level
     * will be cleared.
     * @see #getLevel()
     */
    public void setLevel(CapButtonBrightness.Level level) {
        final String value;
        switch (level) {
            case OFF:
                value = LEVEL_OFF;
                break;
            case DIM:
                value = LEVEL_DIM;
                break;
            case BRIGHT:
                value = LEVEL_BRIGHT;
                break;
            default:
                value = null;
                break;
        }

        final SharedPreferences prefs = this.getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        if (value == null) {
            editor.remove(PREFS_KEY_BRIGHTNESS_LEVEL);
        } else {
            editor.putString(PREFS_KEY_BRIGHTNESS_LEVEL, value);
        }
        editor.commit();
    }

    /**
     * Sets whether or not the capacitive button brightness should be set when
     * the device boots up.
     * 
     * @param enabled true indicates that the capacitive button brightness
     * should be set when the device boots up; false indicates that it should
     * not.
     * @see #isSetBrightnessOnBootEnabled()
     */
    public void setSetBrightnessOnBootEnabled(boolean enabled) {
        final SharedPreferences prefs = this.getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREFS_KEY_SET_BRIGHTNESS_ON_BOOT, enabled);
        editor.commit();
    }

}