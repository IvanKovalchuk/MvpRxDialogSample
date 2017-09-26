package com.kivsw.mvprxfiledialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.mvprxdialog.BaseMvpFragment;
import com.kivsw.mvprxdialog.Contract;

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
    private TextView pathTextView, diskTextView;
    private EditText fileNameEdit;
    private FileListView fileListView;
    private ProgressBar progress;
    private Button okButton, cancelButton;
    private LinearLayout fileNameLayout, pathLayout;
    private LinearLayout diskLayout;
    private ImageView diskImageView;

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
        pathLayout = (LinearLayout) rootView.findViewById(R.id.pathLayout);

        diskLayout = (LinearLayout)  rootView.findViewById(R.id.diskLayout);
        diskImageView = (ImageView)    rootView.findViewById(R.id.diskImageView);
        diskTextView = (TextView)    rootView.findViewById(R.id.diskTextView);

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

    public void setPath(String path)
    {
        pathTextView.setText(path);
    }
    public void setDisk(IDiskRepresenter disk)
    {

        diskImageView.setImageBitmap(disk.getIcon());
        diskTextView.setText(disk.getName());

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
        pathLayout.setVisibility(View.VISIBLE);
        diskLayout.setVisibility(View.VISIBLE);
        if(FileNameEditVisible)
            fileNameLayout.setVisibility(View.VISIBLE);
        else
            fileNameLayout.setVisibility(View.GONE);
        fileListView.setFileList(fileList);
    }

    public void setDiskList(List<IDiskRepresenter> disks)
    {
        pathLayout.setVisibility(View.GONE);
        diskLayout.setVisibility(View.GONE);
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
