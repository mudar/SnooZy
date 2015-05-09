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
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;

public class EulaActivity extends BaseActivity {
    private static final String ASSETS_URI = "file:///android_asset/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eula);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setElevation(getActionBarToolbar(),
                getResources().getDimensionPixelSize(R.dimen.headerbar_elevation));

        loadWebView((WebView) findViewById(R.id.webview));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void loadWebView(WebView v) {
        // Set basic style
        v.setBackgroundColor(getResources().getColor(R.color.bg_webview));
        v.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // Open links in external browser
        v.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        // Load HTML content from assets
        v.loadUrl(ASSETS_URI + Const.LocalAssets.LICENSE);
    }
}
