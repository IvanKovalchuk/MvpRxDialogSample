package com.kivsw.cloudcache;

import com.google.gson.annotations.Expose;

/**
 * Created by ivan on 9/25/17.
 */
public class CacheFileInfo {
    @Expose(serialize = false, deserialize = false)
    public Throwable errorUpdating = null; // if an error with server happens

    public String localName, // cache's file name
            remoteName; // url to the file
    long accessTime = 0;   // last access time
    long modifiedTime = 0; // server's modified time
    boolean localyModified = false; // file needs to be upoload




    CacheFileInfo() {
    }

    CacheFileInfo(String localFileName, String remoteFileName) {
        localName = localFileName;
        remoteName = remoteFileName;
    }
}
