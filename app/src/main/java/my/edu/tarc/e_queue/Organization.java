package my.edu.tarc.e_queue;

public class Organization {
    public int ID, qNumber;
    public String name,address, phone,description;
    public double timePerCust;

    public Organization() {
    }

    public Organization(int ID, String name, String address, String phone, String description, int qNumber, double timePerCust) {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.qNumber = qNumber;
        this.timePerCust = timePerCust;
    }


}