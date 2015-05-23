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

package ca.mudar.snoozy.model;

import ca.mudar.snoozy.util.LogUtils;

public class HistorySection {
    private static final String TAG = LogUtils.makeLogTag(HistorySection.class);

    private int id;
    private int ordinalDay;
    private int total;
    private int offset;

    public HistorySection(int id, int ordinalDay, int total, int offset) {
        this.id = id;
        this.ordinalDay = ordinalDay;
        this.total = total;
        this.offset = offset;
    }

    public int getOrdinalDay() {
        return ordinalDay;
    }

    public int getTotal() {
        return total;
    }

    public int getHeaderPosition() {
        return offset;
    }

    @Override
    public String toString() {
        return "HistorySections{" +
                "id=" + id +
//                ", ordinalDay=" + ordinalDay +
                ", total=" + total +
                ", offset=" + offset +
                '}';
    }

    public boolean contains(int position) {
        return (position >= offset) && (position <= (offset + total));
    }

    public int computeRawPosition(int position) {
        return position - (id + 1);
    }

    public boolean isHeader(int position) {
        return position == offset;
    }
}
