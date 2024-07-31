package io.github.ericdriggs.reportcard.lock;

import java.util.ConcurrentModificationException;
import java.util.UUID;

public class LockNotAvailableException extends ConcurrentModificationException {
    private final UUID uuid;

    public LockNotAvailableException(UUID uuid) {
        super("lock not available for uuid: " + uuid);
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
