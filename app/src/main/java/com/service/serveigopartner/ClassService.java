package com.service.serveigopartner;

public class ClassService {

    String head;
    boolean checked=false;
    long price;
    long commission;
    String uid;

    public String getHead() {
        return head;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getPrice() {
        return price;
    }

    public long getCommission() {
        return commission;
    }

    public String getUid() {
        return uid;
    }
}
