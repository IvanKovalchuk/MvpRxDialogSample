package com.kivsw.mvprxfiledialog.data;

import com.kivsw.cloud.disk.IDiskRepresenter;

/**
 * Created by ivan on 7/10/18.
 */

public interface IFileSystemPath {
    void setFullPath(String uripath);
    String getFullPath();
    String getPath();
    IDiskRepresenter getCurrentDisk();
    String up();
    void addDir(String dir);
    int getDepth();
}
