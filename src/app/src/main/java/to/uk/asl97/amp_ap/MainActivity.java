package to.uk.asl97.amp_ap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import to.uk.asl97.amp.Amp;
import to.uk.asl97.amp.AmpSource;
import to.uk.asl97.amp.AmpStats;
import to.uk.asl97.amp.Reader;
import to.uk.asl97.amp.Source_reader;

import com.manor.currentwidget.library.CurrentReaderFactory;

import java.util.Arrays;
import java.util.Locale;

class Spinner_cur implements AdapterView.OnItemSelectedListener{
    private MainActivity act;
    Spinner_cur(MainActivity act){
        this.act=act;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        this.act.c_source_cur = item;
        this.act.reader_cur = this.act.sources.linkedmap_cur.get(item);

        SharedPreferences sharedPref = this.act.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Source current", item).apply();

        this.act.update_information();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}

class Spinner_avg implements AdapterView.OnItemSelectedListener{
    private MainActivity act;
    Spinner_avg(MainActivity act){
        this.act=act;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        this.act.c_source_avg = item;
        this.act.reader_avg = this.act.sources.linkedmap_avg.get(item);

        SharedPreferences sharedPref = this.act.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Source average", item).apply();

        this.act.update_information();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}

class stats_clip implements View.OnTouchListener{

    private MainActivity act;
    private TextView stats;
    private ClipboardManager clip;
    stats_clip(MainActivity act, TextView stats, ClipboardManager clip){
        this.act = act;
        this.stats = stats;
        this.clip = clip;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData clip = ClipData.newPlainText("Copied Text", this.stats.getText());
            this.clip.setPrimaryClip(clip);

            Toast.makeText(this.act, "Copied text", Toast.LENGTH_SHORT).show();
            view.performClick();
        }
        return false;
    }
}

public class MainActivity extends Activity {
    private Handler handler = new Handler();
    Amp amp = new Amp();
    AmpSource sources;
    static final String BUILD_MODEL = Build.MODEL.toLowerCase(Locale.ENGLISH);

    Reader reader_cur = null;
    Reader reader_avg = null;
    String c_source_cur;
    String c_source_avg;

    private class currentwidget_reader implements Reader {
        @Override
        public long read() {
            return CurrentReaderFactory.getValue();
        }

        @Override
        public boolean ready() {
            return true;
        }
    }

    class amp_avg_reader implements Reader{
        Amp amp;
        amp_avg_reader(Amp amp){
            this.amp = amp;
        }

        @Override
        public long read(){
            return this.amp.avg;
        }

        @Override
        public boolean ready() {
            return this.amp.has_avg;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class a5_current_now implements Reader {

        @Override
        public long read() {
            BatteryManager mBatteryManager = (BatteryManager)getSystemService(Context.BATTERY_SERVICE);
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
    class a5_current_avg implements Reader {

        @Override
        public long read() {
            BatteryManager mBatteryManager = (BatteryManager)getSystemService(Context.BATTERY_SERVICE);
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

    private Reader get_default_reader_cur(){
        // tested by asl97
        if (BUILD_MODEL.contains("sm-t215")){
            return new Source_reader("/sys/class/power_supply/battery/batt_current_ua_now", true);
        }
        // If no unique default, uses 3rd party library
        return new currentwidget_reader();
    }

    private Reader get_default_reader_avg(){
        // tested by asl97
        if (BUILD_MODEL.contains("sm-t215")){
            return new Source_reader("/sys/class/power_supply/battery/batt_current_ua_avg", true);
        }
        // If no unique default, use the average of the current
        return new amp_avg_reader(this.amp);
    }

    private void sources_init(){
        // This is where we add our own readers
        this.sources.linkedmap_cur.put("__default__", get_default_reader_cur());
        this.sources.linkedmap_cur.put("currentwidget_reader", new currentwidget_reader());
        this.sources.linkedmap_avg.put("__default__", get_default_reader_avg());
        this.sources.linkedmap_avg.put("Current average", new amp_avg_reader(this.amp));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            this.sources.linkedmap_cur.put("a5 CURRENT_NOW", new a5_current_now());
            this.sources.linkedmap_avg.put("a5 CURRENT_AVG", new a5_current_avg());
        }
    }

    private void init(){
        this.sources = new AmpSource();
        sources_init();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        this.c_source_cur = sharedPref.getString("Source current", "");
        if (!this.c_source_cur.equals("")){
            this.reader_cur = this.sources.linkedmap_cur.get(this.c_source_cur);
        } else {
            this.reader_cur = get_default_reader_cur();
            this.c_source_cur = "__default__";
        }

        this.c_source_avg = sharedPref.getString("Source average", "");
        if (!this.c_source_avg.equals("")){
            this.reader_avg = this.sources.linkedmap_avg.get(this.c_source_avg);
        } else {
            this.reader_avg = get_default_reader_avg();
            this.c_source_avg = "__default__";
        }

        amp.init(this.reader_cur.read());
        this.update_information();
        handler.postDelayed(runnable, 333);
    }

    private void stop(){
        handler.removeCallbacks(runnable);
        this.amp.clear();
    }

    void update_information(){
        this.amp.clear();
        amp.init(this.reader_cur.read());
        TextView stats = findViewById(R.id.Stats);
        stats.setText(String.format(getString(R.string.Stats), this.c_source_cur, this.c_source_avg, BUILD_MODEL));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();

        AdapterView.OnItemSelectedListener handler_cur = new Spinner_cur(this);
        AdapterView.OnItemSelectedListener handler_avg = new Spinner_avg(this);

        String[] arr_cur = this.sources.linkedmap_cur.keySet().toArray(new String[0]);
        Spinner spinner_cur = findViewById(R.id.Source_cur);
        spinner_cur.setOnItemSelectedListener(handler_cur);
        ArrayAdapter<String> dataAdapter_cur = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr_cur);
        dataAdapter_cur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_cur.setAdapter(dataAdapter_cur);
        spinner_cur.setSelection(Arrays.asList(arr_cur).indexOf(this.c_source_cur));


        String[] arr_avg = this.sources.linkedmap_avg.keySet().toArray(new String[0]);
        Spinner spinner_avg = findViewById(R.id.Source_avg);
        spinner_avg.setOnItemSelectedListener(handler_avg);
        ArrayAdapter<String> dataAdapter_avg = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr_avg);
        dataAdapter_avg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_avg.setAdapter(dataAdapter_avg);
        spinner_avg.setSelection(Arrays.asList(arr_avg).indexOf(this.c_source_avg));

        TextView stats = findViewById(R.id.Stats);
        stats.setOnTouchListener(new stats_clip(this, stats, (ClipboardManager) getSystemService(CLIPBOARD_SERVICE)));
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            TextView average = findViewById(R.id.Average);
            TextView current = findViewById(R.id.Current);
            TextView min = findViewById(R.id.Min);
            TextView max = findViewById(R.id.Max);

            AmpStats data = amp.put(reader_cur.read());
            if (reader_avg.ready()) {
                average.setTextSize(128);
                average.setText(String.format(getString(R.string.average), reader_avg.read()));
            }else{
                average.setTextSize(64);
                average.setText(R.string.Gathering_Data);
            }

            current.setText(String.format(getString(R.string.current), data.cur));
            min.setText(String.format(getString(R.string.min), data.min));
            max.setText(String.format(getString(R.string.max), data.max));

            handler.postDelayed(this, 333);
        }
    };

    @Override
    protected void onPause() {
        this.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        this.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        this.init();
        super.onResume();
    }
}
