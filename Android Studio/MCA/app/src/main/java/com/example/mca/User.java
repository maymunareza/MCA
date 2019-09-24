package com.example.mca;

import java.util.Date;

public class User {
    String member_id;
    Date sessionExpiryDate;

    public void setUsername(String memberID) {
        this.member_id = memberID;
    }

    public void setSessionExpiryDate(Date sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }

    public String get_member_id() {
        return member_id;
    }

    public Date getSessionExpiryDate() {
        return sessionExpiryDate;
    }
}
