package com.wos.services.model;

/**
 * Bucket or object owner
 */
public class Owner {
    private String displayName;

    private String id;

    /**
     * Obtain the owner name.
     * 
     * @return Owner name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the owner name.
     * 
     * @param displayName
     *            Owner name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Obtain the ID of the domain to which the owner belongs.
     * 
     * @return ID of the domain to which the owner belongs
     */
    public String getId() {
        return id;
    }

    /**
     * Set the ID of the domain to which the owner belongs.
     * 
     * @param id
     *            ID of the domain to which the owner belongs
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Owner [displayName=" + displayName + ", id=" + id + "]";
    }

}
