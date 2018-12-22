package my.edu.tarc.e_queue;

public class Organization {
    public int id;
    public String name, address, phone, description;

    public Organization() {
    }

    public Organization(int id, String name, String address, String phone, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
    }
}