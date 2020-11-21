package to.uk.asl97.amp;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Arrays;

public class AmpAvg {
    private CircularFifoQueue<Long> data;
    private Long max;
    private Long min;
    private AmpStats last_stat;
    private int i;
    public boolean has_avg;
    public long avg;
    public long cur;

    public AmpAvg() {
        this.data = new CircularFifoQueue<>(30);
    }

    public void init(Long current){
        this.max = current;
        this.min = current;
        this.last_stat = new AmpStats(this.has_avg, this.avg, current, this.max, this.min);
        this.put(current);
    }

    public AmpStats put(Long current){
        if (current.equals(this.cur) && this.i < 10){
            this.i += 1;
            return this.last_stat;
        } else {
            this.i = 0;
            this.cur = current;
        }
        if (current > this.max){this.max = current;}
        if (current < this.min){this.min = current;}
        this.data.add(current);


        if (data.isAtFullCapacity()){
            this.has_avg = true;

            Long total = (long)0;
            Long[] arr = data.toArray(new Long[0]);
            Arrays.sort(arr);
            for (int i = 10; i < 20; i++) {
                total += arr[i];
            }
            this.avg = total / 10;
        }else{
            this.has_avg = false;
            this.avg = 0;
        }

        this.last_stat = new AmpStats(this.has_avg, this.avg, current, this.max, this.min);
        return this.last_stat;
    }

    public void clear(){
        this.i = 0;
        this.data.clear();
    }
}
