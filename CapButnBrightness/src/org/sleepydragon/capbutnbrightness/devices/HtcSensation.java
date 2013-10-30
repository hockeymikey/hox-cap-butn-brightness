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

import org.sleepydragon.capbutnbrightness.debug.DebugFilesProvider;

/**
 * A specialization of CapacitiveButtonsBacklightBrightness for the HTC
 * Sensation.
 */
public class HtcSensation implements CapacitiveButtonsBacklightBrightness,
        DebugFilesProvider {

    public static final String BUTTONS_BACKLIGHT_DIR =
        "/sys/class/leds/button-backlight";
    public static final String CURRENTS_PATH = BUTTONS_BACKLIGHT_DIR
        + "/currents";
    public static final String BRIGHTNESS_PATH = BUTTONS_BACKLIGHT_DIR
        + "/brightness";
    public static final String LUT_COEFFICIENT_PATH = BUTTONS_BACKLIGHT_DIR
        + "/lut_coefficient";

    public HtcSensation() {
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
        final boolean bExists = FileHelper.fileExists(BRIGHTNESS_PATH);
        final boolean supported = bExists;
        return supported;
    }

    public void set(int level, int options) throws SetException {
        if (level < 0 || level > 100) {
            throw new IllegalArgumentException("invalid level: " + level);
        }

        // on the Sensation, there is nothing to do when the screen turns on
        // and the brightness level is set to "off".
        final boolean inResponseToScreenOn =
            ((options & OPTION_SCREEN_ON) == OPTION_SCREEN_ON);
        if (inResponseToScreenOn && level == 0) {
            return;
        }

        RootHelper.verifyRooted();
        RootHelper.verifyRootAccessGranted();

        final boolean currentsFileExists = new File(CURRENTS_PATH).isFile();
        final boolean dim = (level != 0 && level != 100);
        if (dim && !currentsFileExists) {
            throw new DimBrightnessNotSupportedException(
                "file does not exist: " + CURRENTS_PATH);
        }
        if (currentsFileExists) {
            RootHelper.chmod("666", CURRENTS_PATH);
        }
        RootHelper.chmod("666", BRIGHTNESS_PATH);

        final boolean backlightOn = (level != 0);
        if (!backlightOn) {
            FileHelper.writeToFile(0, BRIGHTNESS_PATH);
            if (currentsFileExists) {
                FileHelper.writeToFile(0, CURRENTS_PATH);
            }
        } else {
            final int currents = dim ? 3 : 8;
            if (!inResponseToScreenOn || dim) {
                if (currentsFileExists) {
                    FileHelper.writeToFile(currents, CURRENTS_PATH);
                }
            }
            FileHelper.writeToFile(255, BRIGHTNESS_PATH);
            if (currentsFileExists) {
                FileHelper.writeToFile(currents, CURRENTS_PATH);
            }
        }
        RootHelper.chmod("444", BRIGHTNESS_PATH);
        if (currentsFileExists) {
            RootHelper.chmod("444", CURRENTS_PATH);
        }
    }

    public void setDefault() throws SetException {
        this.set(100, 0);
        final boolean currentsFileExists = new File(CURRENTS_PATH).isFile();
        if (currentsFileExists) {
            RootHelper.chmod("644", CURRENTS_PATH);
        }
        RootHelper.chmod("644", BRIGHTNESS_PATH);
    }
}
