package com.kivsw.cloudcache.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * This class keeps and controls cached files
 * the class may
 */
public class CacheData {


    private class MapData {
        Map<String, CacheFileInfo> map = new HashMap<>();
        long lastId;
    }

    MapData data; // all data to be saved

    String path, mapFileName;
    long maxDataSize;
    int maxFileCount;

    public CacheData(String path, String fileName, long maxSize, int maxFileCount)
    {
        if(path.charAt(path.length()-1)=='/')    this.path = path;
        else   this.path = path+'/';

        this.mapFileName = fileName;

        if(maxSize<0) maxSize=0;
        this.maxDataSize =maxSize;

        if(maxFileCount<1) maxFileCount=1;
        this.maxFileCount=maxFileCount;
    }

    synchronized public String generateNewCacheFileName() {
        return String.format("%s%08d.cache",path, data.lastId++);
    }

    protected Map<String, CacheFileInfo> getMapData() throws IOException{
        if(data==null)
            doLoadCacheMap();
        return data.map;
    }

    public synchronized CacheFileInfo get(String remoteFilePath) throws IOException{

        CacheFileInfo cfi = getMapData().get(remoteFilePath);
        if(cfi !=null) {
            File f=new File(cfi.localName); // check if the cache file exists
            if(!f.exists())
                return null;

            cfi.accessTime = System.currentTimeMillis();
            doSaveCacheMap();
        }
        return cfi;
    }


    /**
     * add a new file into this cache
     * @param cfi
     * @throws IOException
     */
    public synchronized void put(String remoteFilePath, CacheFileInfo cfi) throws IOException{

        CacheFileInfo oldCfi =  getMapData().get(remoteFilePath);
        if(oldCfi!=null)
          if(!oldCfi.localName.equals(cfi.localName))
             doDeleteItem(cfi.remoteName);

        cfi.accessTime = System.currentTimeMillis();
        cfi.remoteName = remoteFilePath;
        getMapData().put(cfi.remoteName, cfi);

        trimCache();

        doSaveCacheMap();
    }

    /**
     * delete a file from this cache
     * @param filePath
     * @throws IOException
     */

    public synchronized void delete(String filePath) throws IOException{

        doDeleteItem(filePath);
        doSaveCacheMap();
    };
    private boolean doDeleteItem(String filePath) throws IOException
    {
        CacheFileInfo cfi = getMapData().remove(filePath);
        if (cfi!=null && cfi.localName!=null)
        {
            File file = new File(cfi.localName);
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * deletes extra files
     */

    private synchronized void trimCache() throws IOException
    {

        // sorts by access time descent order
        ArrayList<CacheFileInfo> files = new ArrayList(getMapData().values());
        Collections.sort(files, new Comparator<CacheFileInfo>(){
            @Override
            public int compare(CacheFileInfo o1, CacheFileInfo o2) {
                long r=(o2.accessTime - o1.accessTime);
                if(r<0) return -1;
                if(r>0) return 1;
                return 0;
            }
        });

        // deletes extra files
        int i=files.size()-1;
        for(; i>=maxFileCount; i--)
        {
            doDeleteItem(files.get(i).remoteName);
        };

        // calculates total size of files
        long[] sizes=new long[i+1];
        long totalSize=0;
        for(i=sizes.length-1;i>=0;i--)
        {
            File f = new File(files.get(i).localName);
            sizes[i] = f.length();
            totalSize += f.length();
        }

        // delete extra files
        for(i=sizes.length-1; (i>0) && (totalSize> maxDataSize); i--)
        {
            totalSize -= sizes[i];
            doDeleteItem(files.get(i).remoteName);
        };

        // delete garbage files
        File dir = new File(path);
        String allFiles[]=dir.list();
        for(String fileName:allFiles)
        {
            if(fileName.lastIndexOf(".cache")<0)
                continue;
            if(isCacheFile(fileName, files))
                continue;

            File f=new File(path, fileName);
            f.delete();
        }

      //  doSaveCacheMap();
    }

    /**
     * check whether  fileName is a file of this cache
     * @param fileName
     * @return
     */
    private boolean isCacheFile(String fileName, ArrayList<CacheFileInfo> files) {

        for(CacheFileInfo cfi:files)
        {
            int separatorPos = cfi.localName.lastIndexOf("/");
            if(separatorPos<0) continue;
            String aCacheFile=cfi.localName.substring(separatorPos+1);

            if(fileName.equals(aCacheFile))
                return true;
        }

        return false;
    }
    /**
     * savese map data
     * @throws IOException
     */
    protected synchronized void doSaveCacheMap() throws IOException
    {
        File dir=new File(path);
        dir.mkdirs();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        String filedata=gson.toJson(this.data);

        FileWriter writer=new FileWriter(path + mapFileName);//(cacheDir+"/"+mapFileName);
        try {
            writer.write(filedata);
        }finally {
            writer.close();
        }
    }

    /**
     * load map data
     * @throws IOException
     */
    protected synchronized void doLoadCacheMap() throws IOException
    {
        File dir=new File(path);
        dir.mkdirs();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        FileReader reader = null;
        StringBuilder json=new StringBuilder();
        try{
            char data[] = new char[8*1024];
            int sz;
            reader = new FileReader(path + mapFileName);
            while((sz=reader.read(data))>0)
            {
                json.append(data, 0, sz);
            };
        }
        catch(Exception e) {
            if(reader !=null) reader.close();
            data = new MapData();
            return;
        }

        data = gson.fromJson(json.toString(), MapData.class);
    }

}
