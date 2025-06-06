/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

import java.time.LocalDateTime;

public class DropOff {
    private int dropOffId;
    private int userId;
    private int variantId;
    private LocalDateTime dropOffTime;

    public DropOff(int dropOffId, int userId, int variantId, LocalDateTime dropOffTime) {
        this.dropOffId = dropOffId;
        this.userId = userId;
        this.variantId = variantId;
        this.dropOffTime = dropOffTime;
    }

    public DropOff(int userId, int variantId, LocalDateTime dropOffTime) {
        this(0, userId, variantId, dropOffTime);
    }

    // Getters and setters
    public int getDropOffId() {
        return dropOffId;
    }

    public void setDropOffId(int dropOffId) {
        this.dropOffId = dropOffId;
    }

    public int getUserId() {
        return userId;
    }

    public int getVariantId() {
        return variantId;
    }

    public LocalDateTime getDropOffTime() {
        return dropOffTime;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public void setDropOffTime(LocalDateTime dropOffTime) {
        this.dropOffTime = dropOffTime;
    }
}

