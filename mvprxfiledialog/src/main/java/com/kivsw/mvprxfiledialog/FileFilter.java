package com.kivsw.mvprxfiledialog;

import com.kivsw.cloud.disk.IDiskIO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class filter file list according a mask
 */

class FileFilter {

    private ArrayList<Pattern> filters=new ArrayList<Pattern>();;  // file name filters
    private String usedWildCard="";

    /**
     *
     * @param str
     * @return  true if str is a mask
     */
    public boolean isMask(String str)
    {
        if(str==null || str.isEmpty())
            return false;
        // str is a pattern in case it consists '*' or '?' symbols without the slash before
        int index;
        boolean r=false;
        String symbols="*?";
        for(int i=0;   !r && i<symbols.length();   i++)
        {
            index = str.lastIndexOf(symbols.charAt(i));
            if(index>=0)
            {
                if(index > 0)
                    r = str.charAt(index-1)!='\\';
                else
                    r=true;
            }
        }
        return r;
    };

    /**
     *
     */
    public boolean setMask(String wildCard)
    {
        boolean r=true;
        filters.clear();
        usedWildCard="";

        // translate wildCard into the corresponded Regular Expression

        try{
            if(wildCard!=null && !wildCard.isEmpty() && !wildCard.equals("*"))
            {
                int e,b;
                b=e=wildCard.length();
                while(e>0)
                {
                    b=wildCard.lastIndexOf (';', b-1);
                    if(b>0 && wildCard.charAt(b-1)=='\\') // ommit "\\;" sequence
                        continue;
                    String str=wildCard.substring(b+1, e);
                    if(str.length()>0) // ommit empty strings
                    {
                        str="^"+Pattern.quote(str)+"$"; // screen possible special symbols
                        str=str.replace("\\;", ";");
                        str=str.replace("*", "\\E.*\\Q"); // convert masks into the appropriate regular expressions
                        str=str.replace("?", "\\E.{1}\\Q");
                        filters.add(Pattern.compile(str));
                    }
                    e=b;
                }

                usedWildCard = wildCard;
            };

        }
        catch(Exception e)
        {
            filters.clear();
            r=false;
        };
        return r;
    }

    public List<IDiskIO.ResourceInfo> filterList(List<IDiskIO.ResourceInfo> fileList)
    {
        if( (usedWildCard==null) || (usedWildCard.isEmpty()) || (fileList==null) || (fileList.size()==0) )
           return fileList;

        ArrayList<IDiskIO.ResourceInfo> res=new ArrayList<>(fileList.size());
        for(IDiskIO.ResourceInfo item:fileList)
        {
            if(checkFilter(item))
                res.add(item);
        }
        return res;
    }

    protected 	/** function checks whether file 'fi' should be shown
     *
     * @param fi
     * @return true if the filter allows this file fi
     */
    boolean checkFilter(IDiskIO.ResourceInfo fi)
    {
        boolean r=true;

        // check up file mask, if it's necessary
        if(r && (!filters.isEmpty()) && (!fi.isFolder()) )
        {
            boolean rr=false;
            for(int i=filters.size()-1; !rr && i>=0;  i--)
            {
                Matcher matcher = filters.get(i).matcher(fi.name());
                rr|=matcher.find();
            }
            r=rr;
        }

        return r;

    }

    public String getWildCard() {
        if(usedWildCard==null || usedWildCard.isEmpty())
            return "";
        return usedWildCard;
    }
}
