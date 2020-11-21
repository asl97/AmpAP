package to.uk.asl97.amp_ap.sources;

import com.manor.currentwidget.library.CurrentReaderFactory;

import to.uk.asl97.amp.Reader;

public class CurrentwidgetReader implements Reader {
    @Override
    public long read() {
        return CurrentReaderFactory.getValue();
    }

    @Override
    public boolean ready() {
        return true;
    }
}
