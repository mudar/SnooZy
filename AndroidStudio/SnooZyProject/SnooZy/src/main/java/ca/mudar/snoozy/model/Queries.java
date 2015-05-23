package ca.mudar.snoozy.model;

import ca.mudar.snoozy.provider.ChargerContract;

public class Queries {

    public interface HistorySummaryQuery {
        int _TOKEN = 10;

        String[] PROJECTION = new String[]{
                ChargerContract.DailyHistory.JULIAN_DAY,
                ChargerContract.DailyHistory.TOTAL,
        };

        int JULIAN_DAY = 0;
        int TOTAL = 1;
    }
    public interface HistoryDetailsQuery {
        int _TOKEN = 20;
        String[] PROJECTION = new String[]{
                ChargerContract.History._ID,
                ChargerContract.History.IS_POWER_ON,
                ChargerContract.History.BATTERY_LEVEL,
                ChargerContract.History.TIME_STAMP,
                ChargerContract.History.IS_FIRST,
                ChargerContract.History.IS_LAST,
                ChargerContract.DailyHistory.TOTAL,
                ChargerContract.DailyHistory.JULIAN_DAY,
//                History.NOTIFY_GROUP,
//                History.ORDINAL_DAY
        };
        int _ID = 0;
        int IS_POWER_ON = 1;
        int BATTERY_LEVEL = 2;
        int TIME_STAMP = 3;
        int IS_FIRST = 4;
        int IS_LAST = 5;
        int TOTAL = 6;
        int JULIAN_DAY = 7;
//        final int NOTIFY_GROUP = 6;
//        final int ORDINAL_DAY = 7;
    }
}
