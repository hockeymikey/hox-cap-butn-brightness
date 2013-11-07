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

import org.sleepydragon.capbutnbrightness.IntFileRootHelper;

/**
 * A specialization of CapacitiveButtonsBacklightBrightness for the HTC One
 * (m7).
 */
public class HtcOne extends CapacitiveButtonsBacklightBrightness {

    public String[] getRequiredFiles() {
        return new String[] { CURRENTS_PATH };
    }

    public void set(int level, int options,
            IntFileRootHelper.OperationNotifier notifier)
            throws IntFileRootHelper.IntWriteException,
            DimBrightnessNotSupportedException {
        if (level < 0 || level > 100) {
            throw new IllegalArgumentException("invalid level: " + level);
        }

        final IntFileRootHelper intFile = new IntFileRootHelper(notifier);
        try {
            final boolean backlightOn = (level != 0);
            if (!backlightOn) {
                intFile.write(CURRENTS_PATH, 0);
            } else {
                final boolean dim = (level != 100);
                final int currents = dim ? 3 : 20;
                intFile.write(CURRENTS_PATH, currents);
            }
            makeAllFilesReadOnly(intFile);
        } finally {
            intFile.close();
        }
    }

}
