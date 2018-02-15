package com.kivsw.cloudcache.data;

import com.google.gson.annotations.Expose;

/**
 * information about a cached file
 */
public class CacheFileInfo {
    @Expose(serialize = false, deserialize = false)
    public Throwable errorUpdating = null; // if an error with server happens

    public String localName, // cache's file name
            remoteName; // url to the file
    public long accessTime = 0;   // last access time
    public long modifiedTime = 0; // server's modified time
    public boolean localyModified = false; // file needs to be upoload




    public CacheFileInfo() {
    }

    public CacheFileInfo(String localFileName, String remoteFileName) {
        localName = localFileName;
        remoteName = remoteFileName;
    }
}
