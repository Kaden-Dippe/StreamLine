package org.mobiledevsberkeley.streamline;

import java.io.Serializable;

/**
 * Created by nzp on 10/14/17.
 */

public class User implements Serializable {

    private String pid;
    private String displayName;

    public User() {
        this.pid = "";
        this.displayName = "";
    }

    public User(String pid, String displayName) {
        this.pid = pid;
        this.displayName = displayName;
    }

    public String getPid() {
        return pid;
    }

    public String getDisplayName() {
        return displayName;
    }
}
