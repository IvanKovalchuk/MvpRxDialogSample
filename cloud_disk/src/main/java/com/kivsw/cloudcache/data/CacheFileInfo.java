package com.kivsw.cloudcache.data;

import com.google.gson.annotations.Expose;

import io.reactivex.annotations.Nullable;

/**
 * information about a cached file
 */
public class CacheFileInfo {
    @Expose(serialize = false, deserialize = false)
    public Throwable errorUpdating = null; // if an error with server happens

    @Expose(serialize = false, deserialize = false)
    public int progress=0;             // percent of downloading progress
    @Nullable public String localName; // cache's file name
    public String remoteName; // url to the file
    public long accessTime = 0;   // last access time
    public long modifiedTime = 0; // server's modified time
    public boolean localyModified = false; // file needs to be upoload




    public CacheFileInfo(String remoteFileName) {
        this(remoteFileName, null, 0);
    }

    public CacheFileInfo(String remoteFileName, @Nullable String localFileName, int progress ) {
        localName = localFileName;
        remoteName = remoteFileName;
        this.progress = progress;
    }
}
