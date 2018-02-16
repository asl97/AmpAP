package to.uk.asl97.amp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Source_reader implements Reader{
    private String filepath;
    private boolean convertToMillis;
    public Source_reader(String filepath, boolean convertToMillis){
        this.filepath = filepath;
        this.convertToMillis = convertToMillis;
    }

    public long read(){
        File f = new File(this.filepath);

        String text = null;

        try {
            FileInputStream fs = new FileInputStream(f);
            InputStreamReader sr = new InputStreamReader(fs);
            BufferedReader br = new BufferedReader(sr);

            text = br.readLine();

            br.close();
            sr.close();
            fs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Long value = null;

        if (text != null) {
            try	{
                value = Long.parseLong(text);
            } catch (NumberFormatException nfe) 	{
                value = null;
            }

            if (this.convertToMillis && value != null) {
                value = value / 1000; // convert to milliampere
            }
        }
        return value;

    }
}
