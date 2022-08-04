package ru.practicum.shareit.booking.dto;

public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static Status getActualStatus(Boolean isApproved, Boolean isCanceled) {
        if (!isApproved && !isCanceled) {
            return WAITING;
        } else if (isApproved && !isCanceled) {
            return APPROVED;
        } else if (!isApproved) {
            return CANCELED;
        } else return REJECTED;
    }
}