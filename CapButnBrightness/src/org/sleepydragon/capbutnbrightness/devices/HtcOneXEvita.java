/*
 * This file is part of Capacitive Buttons.
 *
 * Capacitive Buttons is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Capacitive Buttons is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Capacitive Buttons.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sleepydragon.capbutnbrightness.devices;

import java.io.File;

import org.sleepydragon.capbutnbrightness.Constants;
import org.sleepydragon.capbutnbrightness.IntFileRootHelper;
import org.sleepydragon.capbutnbrightness.debug.DebugFilesProvider;

import android.util.Log;

/**
 * A specialization of CapacitiveButtonsBacklightBrightness for the HTC One X.
 */
public class HtcOneXEvita implements CapacitiveButtonsBacklightBrightness,
        DebugFilesProvider {

    public static final String BUTTONS_BACKLIGHT_DIR =
        "/sys/class/leds/button-backlight";
    public static final String CURRENTS_PATH = BUTTONS_BACKLIGHT_DIR
        + "/currents";
    public static final String BRIGHTNESS_PATH = BUTTONS_BACKLIGHT_DIR
        + "/brightness";
    public static final String LUT_COEFFICIENT_PATH = BUTTONS_BACKLIGHT_DIR
        + "/lut_coefficient";

    public HtcOneXEvita() {
    }

    public FileInfo[] getDebugFiles() {
        return new FileInfo[] { new FileInfo(CURRENTS_PATH, FileContents.INT),
            new FileInfo(BRIGHTNESS_PATH, FileContents.INT),
            new FileInfo(LUT_COEFFICIENT_PATH, FileContents.INT), };
    }

    public int getDefaultDimLevel() {
        return 50;
    }

    public boolean isSupported() {
        final boolean exists = new File(CURRENTS_PATH).exists();
        return exists;
    }

    public void set(int level, int options,
            IntFileRootHelper.OperationNotifier notifier)
            throws IntFileRootHelper.IntWriteException,
            DimBrightnessNotSupportedException {
        if (level < 0 || level > 100) {
            throw new IllegalArgumentException("invalid level: " + level);
        }

        final boolean backlightOn = (level != 0);
        IntFileRootHelper intFile = new IntFileRootHelper(notifier);

        try {
            if (!backlightOn) {
                intFile.write(CURRENTS_PATH, 0);
            } else {
                final boolean dim = (level != 100);
                final int currents = dim ? 1 : 3;
                intFile.write(CURRENTS_PATH, currents);
            }
        } finally {
            intFile.close();
        }
    }

    public void setDefault(IntFileRootHelper.OperationNotifier notifier)
            throws IntFileRootHelper.IntWriteException {
        try {
            this.set(100, 0, notifier);
        } catch (DimBrightnessNotSupportedException e) {
            throw new RuntimeException("should never happen: " + e);
        }

        try {
            IntFileRootHelper.makeWritable(CURRENTS_PATH);
        } catch (IntFileRootHelper.ChmodFailedException e) {
            Log.w(Constants.LOG_TAG,
                "unable to make file writeable when restoring default: "
                    + CURRENTS_PATH, e);
        }
        try {
            IntFileRootHelper.makeWritable(BRIGHTNESS_PATH);
        } catch (IntFileRootHelper.ChmodFailedException e) {
            Log.w(Constants.LOG_TAG,
                "unable to make file writeable when restoring default: "
                    + BRIGHTNESS_PATH, e);
        }
    }
}
