package to.uk.asl97.amp;

public class AmpStats
{
    public boolean has_avg;
    public long avg;
    public long cur;
    public long max;
    public long min;

    public AmpStats(boolean has_avg, long avg, long cur, long max, long min)
    {
        this.has_avg = has_avg;
        this.avg = avg;
        this.cur = cur;
        this.max = max;
        this.min = min;
    }
}