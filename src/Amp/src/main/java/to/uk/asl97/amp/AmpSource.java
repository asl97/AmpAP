package to.uk.asl97.amp;

// Unknown sources
/*
/sys/class/power_supply/battery/batt_attr_text
/sys/class/power_supply/battery/current_max
/sys/class/power_supply/battery/smem_text
*/

import org.apache.commons.collections4.map.LinkedMap;

import java.io.File;

class Source {
    String filepath;
    boolean convertToMillis;
    Source(String filepath, boolean convertToMillis){
        this.filepath = filepath;
        this.convertToMillis = convertToMillis;
    }
}

public class AmpSource {

    public LinkedMap<String, Reader> linkedmap_cur;
    public LinkedMap<String, Reader> linkedmap_avg;

    // TODO: Sort by order of preferences
    static private Source[] source_cur = {
            new Source("/sys/class/power_supply/ab8500_fg/current_now", true),
            new Source("/sys/class/power_supply/android-battery/current_now", false),
            new Source("/sys/class/power_supply/battery/batt_chg_current", false),
            new Source("/sys/class/power_supply/battery/batt_current", false),
            new Source("/sys/class/power_supply/battery/batt_current_adc", false),
            new Source("/sys/class/power_supply/battery/batt_current_now", false),
            new Source("/sys/class/power_supply/battery/charger_current", false),
            new Source("/sys/class/power_supply/battery/current_now", false),
            new Source("/sys/class/power_supply/Battery/current_now", false),
            new Source("/sys/class/power_supply/bq27520/current_now", true),
            new Source("/sys/class/power_supply/ds2784-fuelgauge/current_now", true),
            new Source("/sys/class/power_supply/max17042-0/current_now", false),
            new Source("/sys/class/power_supply/max170xx_battery/current_now", true),
            new Source("/sys/devices/platform/cpcap_battery/power_supply/usb/current_now", false),
            new Source("/sys/devices/platform/ds2784-battery/getcurrent,", true),
            new Source("/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/battery/current_now", false),
            new Source("/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/ds2746-battery/current_now", false),
            new Source("/sys/devices/platform/msm-charger/power_supply/battery_gauge/current_now", false),
            new Source("/sys/devices/platform/mt6329-battery/FG_Battery_CurrentConsumption", false),
            new Source("/sys/EcControl/BatCurrent", false),
            new Source("/sys/class/power_supply/battery/batt_current_ua_now", true)
    };

    // TODO: Sort by order of preferences
    static private Source[] source_avg = {
            new Source("/sys/class/power_supply/battery/current_avg", false),
            new Source("/sys/class/power_supply/da9052-bat/current_avg", false),
            new Source("/sys/class/power_supply/battery/BatteryAverageCurrent", false),
            new Source("/sys/devices/platform/battery/power_supply/battery/BatteryAverageCurrent", false),
            new Source("/sys/devices/platform/mt6320-battery/power_supply/battery/BatteryAverageCurrent", false),
            new Source("/sys/class/power_supply/battery/batt_current_ua_avg", true)
    };

    public AmpSource(){
        linkedmap_cur = new LinkedMap<>();
        linkedmap_avg = new LinkedMap<>();
        File f;

        for (Source s: source_cur)
        {
            String path = s.filepath;
            f = new File (path);
                if (f.exists())
                    linkedmap_cur.put(path,new Source_reader(path, s.convertToMillis));
        }

        for (Source s: source_avg)
        {
            String path = s.filepath;
            f = new File (path);
            if (f.exists())
                linkedmap_avg.put(path,new Source_reader(path, s.convertToMillis));
        }

    }
}
