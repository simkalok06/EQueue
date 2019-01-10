package my.edu.tarc.e_queue;

import java.sql.Timestamp;

public class HistoryData {
    int organizationID;
    String qTime;
    String accountID;

    public HistoryData(int organizationID, String qTime, String accountID) {
        this.organizationID = organizationID;
        this.qTime = qTime;
        this.accountID = accountID;
    }
}
