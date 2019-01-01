package my.edu.tarc.e_queue;

public class TrackQueueData {
    public int position, myQNumber, currentlyQ_ing;

    public TrackQueueData() {
    }

    public TrackQueueData(int position, int myQNumber) {
        this.position = position;
        this.myQNumber = myQNumber;
    }
}
