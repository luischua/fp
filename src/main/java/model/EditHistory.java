package model;

import java.time.LocalDateTime;

public class EditHistory {
    private String userId;
    private LocalDateTime editedTime;

    public LocalDateTime getEditedTime() {
        return editedTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        editedTime = LocalDateTime.now();
    }
}
