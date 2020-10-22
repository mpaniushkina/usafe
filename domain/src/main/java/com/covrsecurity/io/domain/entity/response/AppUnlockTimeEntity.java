package com.covrsecurity.io.domain.entity.response;

import com.covrsecurity.io.domain.entity.LockTypeEntity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUnlockTimeEntity implements Serializable {

    private static final String FORMAT_DATE_NORMAL_STR = "dd.MM.yyyy HH:mm:ss:sss";
    private static final DateFormat FORMAT = new SimpleDateFormat(FORMAT_DATE_NORMAL_STR);

    private final int attemptsLeft;
    private final long unlockedTime;
    private final LockTypeEntity lockType;

    public AppUnlockTimeEntity(int attemptsLeft, long unlockedTime, LockTypeEntity lockType) {
        this.attemptsLeft = attemptsLeft;
        this.unlockedTime = unlockedTime;
        this.lockType = lockType;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public long getUnlockedTime() {
        return unlockedTime;
    }

    public LockTypeEntity getLockType() {
        return lockType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppUnlockTimeEntity that = (AppUnlockTimeEntity) o;

        if (attemptsLeft != that.attemptsLeft) return false;
        if (unlockedTime != that.unlockedTime) return false;
        return lockType == that.lockType;
    }

    @Override
    public int hashCode() {
        int result = attemptsLeft;
        result = 31 * result + (int) (unlockedTime ^ (unlockedTime >>> 32));
        result = 31 * result + (lockType != null ? lockType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AppUnlockTimeEntity{" +
                "attemptsLeft=" + attemptsLeft +
                ", unlockedTime=" + unlockedTime +
                ", unlockedTimeFormatted=" + FORMAT.format(new Date(unlockedTime)) +
                ", lockType=" + lockType +
                '}';
    }
}
