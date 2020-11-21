package to.uk.asl97.amp_ap.sources;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;

import to.uk.asl97.amp.Reader;
import to.uk.asl97.amp_ap.MainActivity;

public class Lollipop {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static
    class a5_current_avg implements Reader {

        private final MainActivity mainActivity;

        public a5_current_avg(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public long read() {
            BatteryManager mBatteryManager = (BatteryManager) mainActivity.getSystemService(Context.BATTERY_SERVICE);
            long cur = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
            if (cur != Long.MIN_VALUE) {
                return cur/1000;
            }
            return 0;
        }

        @Override
        public boolean ready() {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static
    class a5_current_now implements Reader {

        private final MainActivity mainActivity;

        public a5_current_now(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public long read() {
            BatteryManager mBatteryManager = (BatteryManager) mainActivity.getSystemService(Context.BATTERY_SERVICE);
            long cur = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            if (cur != Long.MIN_VALUE) {
                return cur/1000;
            }
            return 0;
        }

        @Override
        public boolean ready() {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static
    class a5_energy_counter implements Reader {
        private final MainActivity mainActivity;
        private long lastCur;

        public a5_energy_counter(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
            this.lastCur = 0;
        }

        @Override
        public long read() {
            BatteryManager mBatteryManager = (BatteryManager) mainActivity.getSystemService(Context.BATTERY_SERVICE);
            long cur = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
            if (cur == Long.MIN_VALUE) {
                return 0;
            }

            long out = this.lastCur - cur;
            this.lastCur = cur;
            return out;
        }

        @Override
        public boolean ready() {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static
    class a5_charge_counter implements Reader {
        private final MainActivity mainActivity;
        private long lastCur;

        public a5_charge_counter(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
            this.lastCur = 0;
        }

        @Override
        public long read() {
            BatteryManager mBatteryManager = (BatteryManager) mainActivity.getSystemService(Context.BATTERY_SERVICE);
            long cur = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            if (cur == Long.MIN_VALUE) {
                return 0;
            }

            long out = this.lastCur - cur;
            this.lastCur = cur;
            return out;
        }

        @Override
        public boolean ready() {
            return true;
        }
    }
}
