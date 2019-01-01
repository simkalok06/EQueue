package my.edu.tarc.e_queue;

public class TrackQueueData {
    public int myQNumber;
    public Organization organization;

    public TrackQueueData() {
    }

    public TrackQueueData(Organization organization, int myQNumber) {
        this.organization = organization;
        this.myQNumber = myQNumber;
    }
}
