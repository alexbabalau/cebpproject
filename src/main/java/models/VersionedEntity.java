package models;

public abstract class VersionedEntity {
    private Integer version = 0;

    public VersionedEntity(){

    }

    public VersionedEntity(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
