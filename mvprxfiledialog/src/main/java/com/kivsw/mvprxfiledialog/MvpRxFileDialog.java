package com.kivsw.mvprxfiledialog;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.kivsw.cloud.DiskContainer;
import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.mvprxdialog.BaseMvpFragment;
import com.kivsw.mvprxdialog.Contract;
import com.kivsw.mvprxfiledialog.data.IFileSystemPath;

import java.util.List;


/**
 * This class is UI part for a fileDialog
 * Use the {@link MvpRxFileDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MvpRxFileDialog extends BaseMvpFragment
        implements FileListView.OnFileMenuItemClick, Contract.IView
{

    public MvpRxFileDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id of its presenter.
     * @return A new instance of fragment MvpRxFileDialog.
     */

    public static MvpRxFileDialog newInstance(long id, Bitmap icon, String title) {
        MvpRxFileDialog fragment = new MvpRxFileDialog();
        Bundle args = new Bundle();
        args.putLong(PRESENTER_ID, id);
        args.putParcelable(ICON_PARAM,icon);
        args.putString(TITLE_PARAM,title);

        fragment.setArguments(args);
        return fragment;
    }

    private View rootView;
    private Spinner diskSpinner;
    private TextView pathTextView;
    private EditText fileNameEdit;
    private FileListView fileListView;
    private ProgressBar progress;
    private Button okButton, cancelButton;
    private LinearLayout fileNameLayout;

    private DiskContainer disks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.file_dialog, container, false);

        setupTitle(rootView);

        pathTextView = (TextView) rootView.findViewById(R.id.currentPath);
        fileNameEdit = (EditText) rootView.findViewById(R.id.editFileName);
        fileNameEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_UP) && (event.getKeyCode()==KeyEvent.KEYCODE_ENTER))
                {
                    ((MvpRxFileDialogPresenter)getPresenter()).onOkClick();
                }
                return false;
            }
        });

        fileListView = (FileListView) rootView.findViewById(R.id.fileList);
        fileListView.setOnFileClick(new FileListView.OnFileClick(){
            @Override
            public void onFileClick(FileListView flf, IDiskIO.ResourceInfo fi, int position) {
                ((MvpRxFileDialogPresenter)getPresenter()).onFileClick(fi);
            }
        });
        fileListView.setOnDiskClick(new FileListView.OnDiskClick() {
            @Override
            public void onDiskClick(FileListView flf, IDiskRepresenter dsk, int position) {
                ((MvpRxFileDialogPresenter)getPresenter()).onDiskClick(dsk, position);
            }
        });
        fileListView.setOnMenuItemClickListener(this);

        fileNameLayout = (LinearLayout) rootView.findViewById(R.id.fileNameLayout);

        diskSpinner = (Spinner) rootView.findViewById(R.id.diskSpinner);
        //initDiskSpinner();

        progress = (ProgressBar) rootView.findViewById(R.id.progressBar);
        //showProgress(false);

        okButton = (Button)  rootView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MvpRxFileDialogPresenter)getPresenter()).onOkClick();
            }
        });

        cancelButton = (Button)  rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MvpRxFileDialogPresenter)getPresenter()).onCancelClick();
            }
        });

        return rootView;
    }

    private void initDiskSpinner()
    {
        final List<IDiskRepresenter> diskList = disks.getDiskList();
        Drawable diskIcons[]=new Drawable[diskList.size()];
        String diskName[] = new String[diskList.size()];
        for(int i=0;i<diskList.size();i++)
        {
            IDiskRepresenter disk = diskList.get(i);
            diskIcons[i] = new BitmapDrawable(getContext().getResources(), disk.getIcon());
            diskName[i] = disk.getName();
        }

        IconSpinnerAdapter adapter=new IconSpinnerAdapter(getContext(), diskName, diskIcons);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diskSpinner.setAdapter(adapter);
        diskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>=0 && position<diskList.size())
                    //зщы
                   ((MvpRxFileDialogPresenter)getPresenter()).onDiskChange(diskList.get(position));

            }

            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width =  WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    @Override
    public boolean onBackPressed()
    {
        ((MvpRxFileDialogPresenter) getPresenter()).onBackPressed();
        return true;
    }

    public void setPath(IFileSystemPath fileSystemPath, String wildCard)
    {
        int diskIndex=0;
        for(diskIndex = disks.getDiskList().size()-1; diskIndex>=0; diskIndex--) {
            IDiskRepresenter dr = disks.getDiskList().get(diskIndex);
            if(dr.getScheme().equals(fileSystemPath.getCurrentDisk().getScheme()))
                break;
        };
        if(diskIndex!=diskSpinner.getSelectedItemPosition())
            diskSpinner.setSelection(diskIndex);

        pathTextView.setText(fileSystemPath.getPath()+wildCard);
    }
    public void setDiskContainer(DiskContainer disks)
    {

        this.disks = disks;
        initDiskSpinner();
        /*diskImageView.setImageBitmap(disk.getIcon());
        diskTextView.setText(disk.getName());*/

    }

    public void scrollToItem(String fileName)
    {
        int pos=fileListView.getItemPosition(fileName);
        int p=fileListView.getVerticalScrollbarPosition();
        int f=fileListView.getFirstVisiblePosition();
        if(pos >=0 )
            fileListView.//setVerticalScrollbarPosition(pos);
            //smoothScrollToPosition(pos);
        setSelection(pos);
    }
    public void showProgress(boolean show)
    {
        if(show) progress.setVisibility(View.VISIBLE);
        else progress.setVisibility(View.GONE);
    }
    boolean FileNameEditVisible=true;
    public void showFileNameEdit(boolean show)
    {
        FileNameEditVisible = show;
        if(FileNameEditVisible) fileNameLayout.setVisibility(View.VISIBLE);
        else fileNameLayout.setVisibility(View.GONE);
    }
    public void setFileList(List<IDiskIO.ResourceInfo> fileList)
    {
        diskSpinner.setVisibility(View.VISIBLE);
        pathTextView.setVisibility(View.VISIBLE);
        if(FileNameEditVisible)
            fileNameLayout.setVisibility(View.VISIBLE);
        else
            fileNameLayout.setVisibility(View.GONE);
        fileListView.setFileList(fileList);
    }

    public void setDiskList(List<IDiskRepresenter> disks)
    {
        diskSpinner.setVisibility(View.GONE);
        pathTextView.setVisibility(View.GONE);
        fileNameLayout.setVisibility(View.GONE);
        fileListView.setDiskList(disks);
    }

    public String getEditText()
    {
        return fileNameEdit.getText().toString();
    }
    public void setEditText(String text)
    {
        fileNameEdit.setText(text);
    }
    public void showMessage(String msg)
    {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item, IDiskIO.ResourceInfo resource) {
        int itemId=item.getItemId();

        /*if(itemId==R.id.itemcreatefile) {
            ((MvpRxFileDialogPresenter) getPresenter()).onCreateFileClick();
            return true;
        }
        else */
        if(itemId==R.id.itemcreatedirectory) {
            ((MvpRxFileDialogPresenter) getPresenter()).onCreateDirClick();
            return true;
        }
        else if(itemId==R.id.itemdelete)
        {
            ((MvpRxFileDialogPresenter) getPresenter()).onDeleteFileClick(resource);
            return true;
        }
        else if(itemId==R.id.itemrename)
        {

            ((MvpRxFileDialogPresenter) getPresenter()).onRenameClick(resource);
            return true;
        }

        return false;
    }
}
