/*
    SnooZy Charger
    Power Connection manager. Turn the screen off on power connection
    or disconnection, to save battery consumption by the phone's display.

    Copyright (C) 2013 Mudar Noufal <mn@mudar.ca>

    This file is part of SnooZy Charger.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.mudar.snoozy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;

public class BaseActivity extends Activity {
    private static final String SEND_INTENT_TYPE = "text/plain";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            // Respond to the action bar's Up/Home button
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_eula) {
            Intent intent = new Intent(this, EulaActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            /*
             Native sharing
              */
            final Bundle extras = new Bundle();
            extras.putString(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_intent_title));
            extras.putString(Intent.EXTRA_TEXT, Const.URL_PLAYSTORE);

            final Intent sendIntent = new Intent();
            sendIntent.putExtras(extras);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType(this.SEND_INTENT_TYPE);
            startActivity(sendIntent);
        } else if (item.getItemId() == R.id.action_rate) {
            /*
             Launch Playstore to rate app
              */
            final Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setData(Uri.parse(Const.URL_PLAYSTORE));
            startActivity(viewIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
