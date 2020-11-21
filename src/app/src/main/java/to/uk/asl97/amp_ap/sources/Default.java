package to.uk.asl97.amp_ap.sources;

import to.uk.asl97.amp.AmpAvg;
import to.uk.asl97.amp.Reader;

public class Default {
    public static class amp_avg_reader implements Reader {
        AmpAvg ampAvg;
        public amp_avg_reader(AmpAvg ampAvg){
            this.ampAvg = ampAvg;
        }

        @Override
        public long read(){
            return this.ampAvg.avg;
        }

        @Override
        public boolean ready() {
            return this.ampAvg.has_avg;
        }
    }
}

