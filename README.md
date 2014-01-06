##SnooZy Charger - Android Power Connection manager

[![SnooZy Charger][img_github]][link_snoozy_playstore]

###Battery Saver and Power Connection History

SnooZy Charger is a Power Connection manager. The app turns off the screen on power connection or disconnection, to save battery consumption by the phone's display. 

SnooZy can also be used as an auto-lock on unplug or simply as for power connection history. 

This app can be useful to save battery when using a **solar charger**, where power connectivity relies on weather conditions! It was initially developed as a fix for battery drain issues on **Samsung Galaxy Nexus**.

##Features
* Lightweight background service detecting power connection/disconnection.
* Power connection history saved in local database.
* Receive notifications with/out sound or vibration.
* Supports phones and tablets running Android 4.0 or later (API 14).

This app does NOT manage any other phone functionalities, such as WiFi, Bluetooth, GPS, etc. There are other apps for that…

##Galaxy Nexus Ghost Charging
Some Samsung Galaxy Nexus devices randomly detect a power connection without any charger being actually connected to the phone. This would drain the battery because of the screen turning on to display the "Charging" message, multiple times per hour till screen timeout. 

Forums reporting this bug:

* [Galaxy Nexus battery/charging problem?][link_forum_6]
* [My Galaxy Nexus goes crazy (battery/charging issue)][link_forum_1]
* [VBUS TUNA OTG wakelock due to usb port problem and battery drain][link_forum_4]
* [Help! Screen keeps turning back on after turning it off. ][link_forum_5]
* [Galaxy Nexus Charging Issues][link_forum_2]
* [Galaxy Nexus - Screen keeps turning on when it's locked][link_forum_3]

SnooZy Charger was developed as a (partial) software solution for this issue: it saves battery consumption by turning off the display on these phantom connections, if the user is not currently using the phone. However, it does not fix the random detections. Some hardware solutions are suggested by users reporting this issue.

##Links

* [Website][link_snoozy_website]
* [Privacy policy][link_snoozy_privacy]
* [SnooZy Charger on Google Play][link_snoozy_playstore]

[![Android app on Google Play][img_playstore_badge]][link_snoozy_playstore]

## Credits

* Developed by [Mudar Noufal][link_mudar_ca] &lt;<mn@mudar.ca>&gt;
* Norwegian translation by [Daniel Aleksandersen][link_aeyoun] &lt;<code@daniel.priv.no>&gt;
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
[link_aeyoun]: https://github.com/Aeyoun
[link_gpl]: http://www.gnu.org/licenses/gpl.html
[img_devices]: http://snoozy.mudar.ca/images/snoozy-devices.png
[img_playstore_badge]: http://snoozy.mudar.ca/images/en_app_rgb_wo_60.png
[link_lib_aosp]: http://source.android.com/
[link_lib_supportv4]: http://developer.android.com/tools/support-library/
[link_lib_ui_utils]: http://code.google.com/p/android-ui-utils/
[link_apache]: http://www.apache.org/licenses/LICENSE-2.0

[link_forum_1]: http://forum.xda-developers.com/showthread.php?t=1427539
[link_forum_2]: http://code.google.com/p/android/issues/detail?id=23789
[link_forum_3]: http://productforums.google.com/d/topic/mobile/PA5JZH-Oj4o/discussion
[link_forum_4]: http://forum.xda-developers.com/showthread.php?t=2157431
[link_forum_5]: http://forum.xda-developers.com/showthread.php?t=2328444
[link_forum_6]: http://forum.xda-developers.com/showthread.php?t=1757178