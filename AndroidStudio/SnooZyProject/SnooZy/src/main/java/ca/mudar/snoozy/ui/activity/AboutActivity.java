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

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.Menu;
import android.widget.TextView;

import ca.mudar.snoozy.R;

import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class AboutActivity extends BaseActivity {
    private static final String TAG = makeLogTag(AboutActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Resources res = getResources();

        /**
         * Display version number in the About header.
         */
        ((TextView) findViewById(R.id.about_project_version))
                .setText(String.format(res.getString(R.string.about_project_version),
                        res.getString(R.string.app_version)));

        /**
         * Handle web links.
         */
        MovementMethod method = LinkMovementMethod.getInstance();
        ((TextView) findViewById(R.id.about_text_credits)).setMovementMethod(method);
        ((TextView) findViewById(R.id.about_footer_website)).setMovementMethod(method);
        ((TextView) findViewById(R.id.about_text_usage_extra)).setText(
                Html.fromHtml(res.getString(R.string.about_text_usage_extra)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }
}
