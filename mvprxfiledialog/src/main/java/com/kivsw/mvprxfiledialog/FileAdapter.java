package com.kivsw.mvprxfiledialog;



        import android.content.Context;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;


        import com.kivsw.cloud.disk.IDiskIO;

        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.List;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

//import java.util.Locale;

/**
 * This adapter helps to visualize ResourceInfo
 */
class FileAdapter  extends BaseListAdapter
{



    private boolean isAllowedDir=true, isAllowedFile=true, isAllowedHidden=true; // file type filter
    private ArrayList<Pattern> filters;  // file name filters
    private String usedWildCard="";



    private List<IDiskIO.ResourceInfo> fileList=null, visibleFileList=null; // null value means that it's necessary to rebuild fileList

    //----------------------------------------
    /** Creates an entitle of FileAdapter
     * @param cnt
     */
    public FileAdapter(Context cnt)
    {
        super(cnt);
        filters = new ArrayList<Pattern>();
        visibleFileList = new ArrayList<>();
    }
    //----------------------------------------
    /** returns position number of the Dir in fileList
     * @param Dir
     * @return position number of the Dir in fileList
     */
    public int getDirPosition(String Dir) {

        int i;
        for(i=0;i<fileList.size();i++)
        {
            if(0==fileList.get(i).name().compareTo(Dir))
                return i;
        }
        return -1;

    }
    //----------------------------------------
    /**
     * @return File list of the curent directory
     */
    public List<IDiskIO.ResourceInfo>  getFileList()
    {
        return fileList;
    };

    //----------------------------------------
    /** this method gets the file list of the path
     *
     */
    protected void setData(List<IDiskIO.ResourceInfo> newFileList)
    {
        if(newFileList==null)
            this.fileList=new ArrayList();
        else
            this.fileList=newFileList;
        updateFileList();
    }

    protected void updateFileList()
    {
        visibleFileList.clear();
        if(fileList!=null)
        for(IDiskIO.ResourceInfo item:fileList)
        {
            if(checkFilter(item))
                visibleFileList.add(item);
        }

        Collections.sort(visibleFileList, new Comparator<IDiskIO.ResourceInfo>()
        {
            @Override
            public int compare(IDiskIO.ResourceInfo lhs, IDiskIO.ResourceInfo rhs) {

                int r=0;
                if(lhs.isFolder()==rhs.isFolder())
                    r= lhs.name().compareToIgnoreCase(rhs.name());
                else
                {
                    if(lhs.isFolder()) r=-1;
                    else r=1;
                }
                return r;
            }
        });

        observersOnChanges();

    }

    //----------------------------------------
    /** this method sets the filter
     *
     * @param isAllowedDir boolean value that allow to show directories
     * @param isAllowedFile boolean value that allow to show files
     */
    void setFilter(boolean isAllowedDir, boolean isAllowedFile)
    {
        this.isAllowedDir=isAllowedDir;
        this.isAllowedFile=isAllowedFile;

        updateFileList(); // rebuild the file list
    }
    //----------------------------------------
    /** This method sets the file name filter.
     *  wildCard can hold a number of filters separated by ';'. null or "" value cancels any filter
     * @param wildCard
     * @return
     */
    boolean setFilter(String wildCard)
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

        updateFileList(); // rebuild the file list
        return r;
    }
    //----------------------------------------
    /** this method returns the file filters
     *
     * @return
     */
    String getFilter()
    {
        return usedWildCard;
    }
    //----------------------------------------
    /** function checks whether file 'fi' should be shown
     *
     * @param fi
     * @return true if the filter allows this file fi
     */
    boolean checkFilter(IDiskIO.ResourceInfo fi)
    {
        boolean r=true;

        // check up file type
        if(fi.isFolder())    r = r && isAllowedDir;
        if(!fi.isFolder()) 	r = r && isAllowedFile;
        //if(fi.isHidden) r = r && isAllowedHidden;

        // check up file mask, if it's necessary
        if(r && (!filters.isEmpty()) && (!fi.isFolder()))
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
    //----------------------------------------
    /**  return amount of the files in fileList
     *  @return
     */
    public int getCount() {

        return visibleFileList.size();
    }

    //----------------------------------------
    public Object getItem(int position) {

        if((position<0)||(position>=getCount()))
            return null;

        return visibleFileList.get(position);

    }

    //----------------------------------------


    //----------------------------------------
    /** return and create (if necessary) the view of a file
     *
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        String s;

        class ViewHolder
        {
            TextView fileName, fileInfo;
            ImageView image;
            LinearLayout linearLayout;
            int position;
        }
        ViewHolder viewHolder;

        if(convertView==null)
        {
            convertView = View.inflate(context, R.layout.file_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView) convertView.findViewById(R.id.textViewFileName);
            viewHolder.fileInfo = (TextView) convertView.findViewById(R.id.textViewFileInfo);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.linearLayout = (LinearLayout)  convertView.findViewById(R.id.linearLayout);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.position = position;
        if((position>=0)&&(position<getCount()))
        {
            IDiskIO.ResourceInfo fi=visibleFileList.get(position);

            // chooses background  colour
            if((position&1)==0)
            {
                viewHolder.linearLayout.setBackgroundColor(0);
            }
            else
            {
                int color=viewHolder.fileName.getCurrentTextColor();
                color = (color & 0x00FFFFFF) | 0x11000000;
                viewHolder.linearLayout.setBackgroundColor(color);
            }

            // chooses file/dir picture
            if(fi.isFolder())
            {
                if((position==0) && 0==fi.name().compareTo("..")) viewHolder.image.setImageResource(R.drawable.icodirup);
                else viewHolder.image.setImageResource(R.drawable.icodir);
            }
            else
            if(fi.isFile())  viewHolder.image.setImageResource(R.drawable.icofile);
            else viewHolder.image.setImageResource(R.drawable.icospecial);

            // file name
            viewHolder.fileName.setText(fi.name());

            if(!fi.isFolder())
            {
                // the file size
                if(fi.size()<1024)
                    s=(String.format("%4db ",fi.size()));
                else if(fi.size()<1024*1024)
                    s=(String.format("%4dk ",fi.size()/1024));
                else if(fi.size()<1024*1024*1024)
                    s=(String.format("%4dM ",fi.size()/(1024*1024)));
                else if(fi.size()<1024*1024*1024*1024)
                    s=(String.format("%4dG ",fi.size()/(1024*1024*1024)));
                else
                    s=(String.format("%4dT ",fi.size()/(1024*1024*1024*1024)));

                viewHolder.fileInfo.setText(s);
            }
            else
            if(!fi.isFolder() || !fi.name().equals(".."))
            {
                // date and time when the file was modified
                s = String.format("%tF\n%tT", fi.modified(), fi.modified());
                //s = s+String.format("\n%tT\n%tF", fi.Modified, fi.Modified);
                viewHolder.fileInfo.setText(s);


            }
            else
                viewHolder.fileInfo.setText("");
        }

        return convertView;
    }




}
//-------------------------------------------------------------------------------
//-------------------------------------------------------------------------------


