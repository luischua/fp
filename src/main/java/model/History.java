package model;

import java.util.Date;

public class History {
    private String userId;
    private Date editedTime;

    public Date getEditedTime() {
        return editedTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        editedTime = new Date();
    }
}
