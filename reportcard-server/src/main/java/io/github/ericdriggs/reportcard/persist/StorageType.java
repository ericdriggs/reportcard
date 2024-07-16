package io.github.ericdriggs.reportcard.persist;

import java.util.HashMap;
import java.util.Map;

public enum StorageType {

    HTML(1),
    JSON(2),
    LOG(3),
    OTHER(4),
    TAR_GZ(5),
    XML(6),
    ZIP(7),
    JUNIT(8),
    ;

    StorageType(int storageTypeId) {
        this.storageTypeId = storageTypeId;
    }

    final int storageTypeId;

    public int getStorageTypeId() {
        return storageTypeId;
    }

    final static Map<Integer, StorageType> idStorageTypeMap = new HashMap<>();

    static {
        for (StorageType s : StorageType.values()) {
            idStorageTypeMap.put(s.getStorageTypeId(), s);
        }
    }

    public static StorageType fromStorageTypeId(int storageTypeId) {
        return idStorageTypeMap.get(storageTypeId);
    }

}
