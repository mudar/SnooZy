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

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import ca.mudar.snoozy.R;

import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class AboutActivity extends BaseActivity implements
        View.OnClickListener {
    private static final String TAG = makeLogTag(AboutActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ViewCompat.setElevation(findViewById(R.id.about_header),
                getResources().getDimensionPixelSize(R.dimen.headerbar_elevation));

        final Resources res = getResources();

        /**
         * Display version number in the About header.
         */
        ((TextView) findViewById(R.id.about_subtitle))
                .setText(String.format(res.getString(R.string.about_project_version),
                        res.getString(R.string.app_version)));

        /**
         * Handle web links.
         */
        findViewById(R.id.about_source_code).setOnClickListener(this);
        findViewById(R.id.about_credits).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.about_credits) {
            openWebPage(R.string.url_about_credits);
        } else if (id == R.id.about_source_code) {
            openWebPage(R.string.url_about_source_code);
        }
    }

    private void openWebPage(int res) {
        final String url = getResources().getString(res);
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
