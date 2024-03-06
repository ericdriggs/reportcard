package io.github.ericdriggs.reportcard.persist;

public enum StorageType {

    HTML(1),
    JSON(2),
    LOG(3),
    OTHER(4),
    XML(5);


    StorageType(int storageTypeId) {
        this.storageTypeId = storageTypeId;
    }

    final int storageTypeId;

    public int getStorageTypeId() {
        return storageTypeId;
    }

}
