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

import org.sleepydragon.capbutnbrightness.clib.CLib;
import org.sleepydragon.capbutnbrightness.clib.Stat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LevelsActivity extends Activity implements View.OnClickListener {

    public void onClick(View view) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_levels);

        TextView tv1 = (TextView) this.findViewById(R.id.textView1);
        TextView tv2 = (TextView) this.findViewById(R.id.textView2);

        Stat stat1 = new Stat();
        try {
            CLib.stat("/sys/class/leds/button-backlight/currents", stat1);
            tv1.setText("uid=" + stat1.getUid() + " gid=" + stat1.getGid()
                + " mode=" + stat1.getMode());
        } catch (Exception e) {
            tv1.setText(e.toString());
        }

        Stat stat2 = new Stat();
        try {
            CLib.stat("/sys/class/leds/button-backlight/zzyzx", stat2);
            tv2.setText("uid=" + stat2.getUid() + " gid=" + stat2.getGid()
                + " mode=" + stat2.getMode());
        } catch (Exception e) {
            tv2.setText(e.toString());
        }

    }

}
