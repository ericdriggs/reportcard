package io.github.ericdriggs.reportcard.model;

public enum FaultContextType {
    ERROR((byte) 1),
    FAILURE((byte) 2),
    FLAKY_ERROR((byte) 3),
    FLAKY_FAILURE((byte) 4),
    RERUN_ERROR((byte) 5),
    RERUN_FAILURE((byte) 6);

    final byte id;

    FaultContextType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}
