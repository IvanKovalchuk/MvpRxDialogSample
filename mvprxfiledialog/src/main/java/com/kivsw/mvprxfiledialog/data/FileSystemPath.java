package com.kivsw.mvprxfiledialog.data;

import com.kivsw.cloud.DiskContainer;
import com.kivsw.cloud.disk.IDiskRepresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

/**
 * this is the base class for file dialogs
 */

public class FileSystemPath implements IFileSystemPath
{
    DiskContainer disks;
    private  IDiskRepresenter currentDisk;
    private List<String> pathSegments;

    public FileSystemPath(@NonNull DiskContainer disks)
    {
        pathSegments = new ArrayList();
        this.disks=disks;

    };

    //private DiskContainer.CloudFile cloudFile=null;
    @Override
    public void setFullPath(String path)
    {
        if(path != null) {
            DiskContainer.CloudFile cf=disks.parseFileName(path);
            if(cf!=null) {
                //cloudFile = cf;
                currentDisk = cf.diskRepresenter;
                pathSegments.clear();
                pathSegments.addAll(cf.uri.getPathSegments());
            }
        }
    };


    @Override
    public String getFullPath()
    {
        if(getCurrentDisk()==null)
            return "";

        return getCurrentDisk().getScheme()+"://" + getPath();
    };

    @Override
    public String getPath()
    {
        StringBuilder res = new StringBuilder();
        for(String segment:pathSegments) {
            res.append('/');
            res.append(segment);
        }
        res.append('/');
        return res.toString();
    };
    @Override
    public IDiskRepresenter getCurrentDisk()
    {
        return currentDisk;
    };
    @Override
    public String up()
    {
        String dir= pathSegments.remove(pathSegments.size()-1);
        return dir;
    };
    @Override
    public void addDir(String dir)
    {
        pathSegments.add(dir);
    };
    @Override
    public int getDepth()
    {
        return pathSegments.size();
    };

}