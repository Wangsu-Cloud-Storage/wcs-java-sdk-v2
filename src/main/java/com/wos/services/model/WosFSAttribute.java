package com.wos.services.model;

/**
 * File or folder properties
 */
public class WosFSAttribute extends ObjectMetadata {
    private int mode = -1;

    /**
     * Query the file or folder type.
     *
     * @return File or folder type
     */
    public int getMode() {
        return mode;
    }

    /**
     * Set the file or folder type.
     *
     * @param mode File or folder type
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
}
