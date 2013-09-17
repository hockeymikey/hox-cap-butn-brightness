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
package org.sleepydragon.capbutnbrightness;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.sleepydragon.capbutnbrightness.clib.CLib;
import org.sleepydragon.capbutnbrightness.clib.CLibConstants;
import org.sleepydragon.capbutnbrightness.clib.ClibException;
import org.sleepydragon.capbutnbrightness.clib.Stat;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Process;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

public class LevelsActivity extends Activity implements
        SeekBar.OnSeekBarChangeListener, TextView.OnEditorActionListener {

    private SeekBar currentsSeekBar;
    private EditText currentsEditText;
    private SeekBar brightnessSeekBar;
    private EditText brightnessEditText;
    private SeekBar lutCoefficientSeekBar;
    private EditText lutCoefficientEditText;

    private void initializeSliderProgress() {
        final int currents = this.readIntFromFile("currents");
        this.currentsSeekBar.setProgress(currents);
        this.currentsEditText.setText(String.valueOf(currents));

        final int brightness = this.readIntFromFile("brightness");
        this.brightnessSeekBar.setProgress(brightness);
        this.brightnessEditText.setText(String.valueOf(brightness));

        final int lutCoefficient = this.readIntFromFile("lut_coefficient");
        this.lutCoefficientSeekBar.setProgress(lutCoefficient);
        this.lutCoefficientEditText.setText(String.valueOf(lutCoefficient));
    }

    private void linkSlidersWithTextFields() {
        this.currentsSeekBar =
            (SeekBar) this.findViewById(R.id.currentsSeekBar);
        this.currentsEditText =
            (EditText) this.findViewById(R.id.currentsEditText);
        this.brightnessSeekBar =
            (SeekBar) this.findViewById(R.id.brightnessSeekBar);
        this.brightnessEditText =
            (EditText) this.findViewById(R.id.brightnessEditText);
        this.lutCoefficientSeekBar =
            (SeekBar) this.findViewById(R.id.lutCoefficientSeekBar);
        this.lutCoefficientEditText =
            (EditText) this.findViewById(R.id.lutCoefficientEditText);

        this.currentsSeekBar.setOnSeekBarChangeListener(this);
        this.brightnessSeekBar.setOnSeekBarChangeListener(this);
        this.lutCoefficientSeekBar.setOnSeekBarChangeListener(this);

        this.currentsEditText.setOnEditorActionListener(this);
        this.brightnessEditText.setOnEditorActionListener(this);
        this.lutCoefficientEditText.setOnEditorActionListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_levels);
        this.populateTestTextFields();
        this.linkSlidersWithTextFields();
        this.initializeSliderProgress();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // don't consume any event that is not an ENTER key being pressed
        if (event != null) {
            return false;
        }

        final String strValue = v.getText().toString();
        final int value;
        try {
            value = Integer.parseInt(strValue);
        } catch (final NumberFormatException e) {
            // invalid integer; do nothing
            return true;
        }

        final SeekBar seekBar;
        if (v == this.currentsEditText) {
            seekBar = this.currentsSeekBar;
        } else if (v == this.brightnessEditText) {
            seekBar = this.brightnessSeekBar;
        } else if (v == this.lutCoefficientEditText) {
            seekBar = this.lutCoefficientSeekBar;
        } else {
            seekBar = null;
        }

        if (seekBar != null) {
            if (value < 0 || value > seekBar.getMax()) {
                // value out of range; do nothing
                return true;
            }

            seekBar.setProgress(value);
            this.updateCapacitiveButtonsBacklight();
        }

        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        // avoid infinite loop of a programmatic update changing the value of
        // the edit text, changing the value of the seek bar, changing the
        // value of the edit text, and on and on
        if (!fromUser) {
            return;
        }

        final EditText textField;
        if (seekBar == this.currentsSeekBar) {
            textField = this.currentsEditText;
        } else if (seekBar == this.brightnessSeekBar) {
            textField = this.brightnessEditText;
        } else if (seekBar == this.lutCoefficientSeekBar) {
            textField = this.lutCoefficientEditText;
        } else {
            textField = null;
        }

        if (textField != null) {
            final String progressAsStr = Integer.toString(progress);
            textField.setText(progressAsStr);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // when the user lifts their finger off of the seek bar, update
        // the capacitive buttons backlight to reflect the newly-assigned value
        this.updateCapacitiveButtonsBacklight();
    }

    private void populateTestTextFields() {
        final TextView tv1 = (TextView) this.findViewById(R.id.textView1);
        final TextView tv2 = (TextView) this.findViewById(R.id.textView2);

        final Stat stat1 = new Stat();
        try {
            CLib.stat("/sys/class/leds/button-backlight/currents", stat1);
            tv1.setText("uid=" + stat1.getUid() + " gid=" + stat1.getGid()
                + " mode=" + stat1.getMode());
        } catch (final Exception e) {
            tv1.setText(e.toString());
        }

        final Stat stat2 = new Stat();
        try {
            CLib.stat("/sys/class/leds/button-backlight/zzyzx", stat2);
            tv2.setText("uid=" + stat2.getUid() + " gid=" + stat2.getGid()
                + " mode=" + stat2.getMode());
        } catch (final Exception e) {
            tv2.setText(e.toString());
        }
    }

    private int readIntFromFile(String filename) {
        final String path = "/sys/class/leds/button-backlight/" + filename;
        final byte[] buffer = new byte[128];
        final int readCount;
        try {
            final FileInputStream in = new FileInputStream(path);
            readCount = in.read(buffer);
            in.close();
        } catch (final IOException e) {
            return 0;
        }

        final String valueStr = new String(buffer, 0, readCount);
        final int value;
        try {
            value = Integer.parseInt(valueStr.trim());
        } catch (final NumberFormatException e) {
            return 0;
        }

        return value;
    }

    private void showError(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Error");
        builder.show();
    }

    private void updateCapacitiveButtonsBacklight() {
        final int currents = this.currentsSeekBar.getProgress();
        final int brightness = this.brightnessSeekBar.getProgress();
        final int lutCoefficient = this.lutCoefficientSeekBar.getProgress();

        final List<FileBrightness> list = new ArrayList<FileBrightness>();
        list.add(new FileBrightness("currents", currents));
        list.add(new FileBrightness("brightness", brightness));
        list.add(new FileBrightness("lut_coefficient", lutCoefficient));
        list.add(new FileBrightness("currents", currents));

        final int modeRW =
            CLibConstants.S_IRUSR | CLibConstants.S_IWUSR
                | CLibConstants.S_IRGRP | CLibConstants.S_IROTH;
        final int modeRO =
            CLibConstants.S_IRUSR | CLibConstants.S_IRGRP
                | CLibConstants.S_IROTH;
        Shell shell = null;
        int commandId = 0;

        try {
            for (final FileBrightness fileInfo : list) {
                final String filename = fileInfo.filename;
                final int value = fileInfo.value;

                final String path =
                    "/sys/class/leds/button-backlight/" + filename;

                try {
                    CLib.chmod(path, modeRW);
                } catch (final ClibException e) {
                    if (shell == null) {
                        try {
                            shell = RootTools.getShell(true);
                        } catch (final IOException e2) {
                            this.showError("unable to create root shell: " + e2);
                            return;
                        } catch (final TimeoutException e2) {
                            this.showError("timeout creating root shell: " + e2);
                            return;
                        } catch (final RootDeniedException e2) {
                            this.showError("root access denied: " + e2);
                            return;
                        }
                    }

                    final int uid = Process.myUid();
                    final String commandStr = "chown " + uid + " " + path;
                    final Command command =
                        new CommandCapture(commandId++, commandStr);
                    try {
                        shell.add(command);
                    } catch (final IOException e2) {
                        this.showError("unable to chown " + path + ": " + e2);
                        return;
                    }

                    final int exitCode;
                    try {
                        command.waitForFinish();
                        exitCode = command.exitCode();
                    } catch (final InterruptedException e2) {
                        this.showError("unable to wait for chown command to "
                            + " complete on " + path + ": " + e2);
                        return;
                    }

                    if (exitCode != 0) {
                        this.showError("chown command on " + path
                            + "completed with non-zero exit code: " + exitCode);
                        return;
                    }

                    try {
                        CLib.chmod(path, modeRW);
                    } catch (final ClibException e2) {
                        this.showError("chmod(modeRW) of " + path + "failed: "
                            + e2);
                        return;
                    }
                }

                final String valueStr = String.valueOf(value) + '\n';
                final byte[] valueBytes = valueStr.getBytes();
                try {
                    final FileOutputStream out = new FileOutputStream(path);
                    out.write(valueBytes);
                    out.close();
                } catch (final IOException e) {
                    this.showError("writing " + value + " to file failed: " + e);
                    return;
                }

                try {
                    CLib.chmod(path, modeRO);
                } catch (final ClibException e) {
                    this.showError("chmod(modeRO) of " + path + "failed: " + e);
                    return;
                }
            }
        } finally {
            if (shell != null) {
                try {
                    shell.close();
                } catch (final IOException e) {
                    // oh well
                }
            }
        }
    }

    private static class FileBrightness {
        public final String filename;
        public final int value;

        public FileBrightness(String filename, int value) {
            if (filename == null) {
                throw new NullPointerException("filename==null");
            }
            this.filename = filename;
            this.value = value;
        }
    }
}
