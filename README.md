##SnooZy Charger - Android Power Connection manager

[![SnooZy Charger][img_github]][link_snoozy_playstore]

###Battery Saver and Power Connection History

SnooZy Charger is a Power Connection manager. The app turns off the screen on power connection or disconnection, to save battery consumption by the phone's display. 

SnooZy can also be used as an auto-lock on unplug or simply as for power connection history. 

This app can be useful to save battery when using a **solar charger**, where power connectivity relies on weather conditions!

It was initially developed as a fix for battery drain issues on **Samsung Galaxy Nexus**: some devices randomly detect a power connection without any charger being actually connected to the phone. 
This would drain the battery because of the screen turning on to display the "Charging" message, multiple times per hour till screen timeout.

##Features
* Lightweight background service detecting power connection/disconnection.
* Power connection history saved in local database.
* Receive notifications with/out sound or vibration.
* Supports phones and tablets running Android 4.0 or later (API 14).

This app does NOT manage any other phone functionalities, such as WiFi, Bluetooth, GPS, etc. There are other apps for that…

##Links

* [Website][link_snoozy_website]
* [Privacy policy][link_snoozy_privacy]
* [SnooZy Charger on Google Play][link_snoozy_playstore]

[![Android app on Google Play][img_playstore_badge]][link_snoozy_playstore]

## Credits

* Developed by [Mudar Noufal][link_mudar_ca]  &lt;<mn@mudar.ca>&gt;
* Many thanks to G&A!

The Android app includes (thanks!) libraries and derivative work of the following projects:

* [AOSP][link_lib_aosp] &copy; The Android Open Source Project.
* [Android Support Library v4][link_lib_supportv4] &copy; The Android Open Source Project.
* [Android Asset Studio][link_lib_ui_utils] &copy; Google Inc, used to create icons assets.

These three projects are all released under the [Apache License v2.0][link_apache].

##Code license

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

[![Android app on Google Play][img_devices]][link_snoozy_playstore]

[link_snoozy_playstore]: http://play.google.com/store/apps/details?id=ca.mudar.snoozy
[img_github]: http://snoozy.mudar.ca/images/snoozy-github.png
[link_snoozy_website]: http://snoozy.mudar.ca/
[link_snoozy_privacy]: http://snoozy.mudar.ca/privacy.html
[link_mudar_ca]: http://www.mudar.ca/
[link_gpl]: http://www.gnu.org/licenses/gpl.html
[img_devices]: http://snoozy.mudar.ca/images/snoozy-devices.png
[img_playstore_badge]: http://snoozy.mudar.ca/images/en_app_rgb_wo_60.png
[link_lib_aosp]: http://source.android.com/
[link_lib_supportv4]: http://developer.android.com/tools/support-library/
[link_lib_ui_utils]: http://code.google.com/p/android-ui-utils/
[link_apache]: http://www.apache.org/licenses/LICENSE-2.0
