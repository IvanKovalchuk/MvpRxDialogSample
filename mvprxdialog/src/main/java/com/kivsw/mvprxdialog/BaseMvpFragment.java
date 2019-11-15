package com.kivsw.mvprxdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * this is a base class for a fragment
 */
public abstract class BaseMvpFragment extends DialogFragment
implements Contract.IView
{

    TextView headerTextView;
    private ImageView headerIcon=null;

    public BaseMvpFragment() {
        setStyle(STYLE_NO_TITLE, getTheme());
    }

    protected final static String PRESENTER_ID="presenterId";
    private long presenterId=0;
    private Contract.IDialogPresenter presenter=null;


    protected Contract.IDialogPresenter  getPresenter()
    {
        return presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setShowsDialog();

        PresenterList pm= PresenterList.getInstance();
        presenterId = getArguments().getLong(PRESENTER_ID);
        if(presenterId<=0)
            throw new RuntimeException("PRESENTER_ID argument must be for this fragment " + this.getClass().getName());
        presenter = (Contract.IDialogPresenter) pm.getPresenter(presenterId);
        if(presenter==null) {
            Log.e("BaseMvpFragment", "Cannot find a presenter for this fragment " + this.getClass().getName());
            dismiss();
            //throw new RuntimeException("cannot find a presenter for this fragment " + this.getClass().getName());
        }

       /* if (savedInstanceState != null) {
            presenterId = savedInstanceState.getLong(PRESENTER_ID);

        } else {
            presenter = createPresenter();
            if(presenter!=null)
               presenterId = pm.addNewPresenter(presenter);
        }*/

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //return super.onCreateDialog(savedInstanceState);
        return  new Dialog(getActivity(), getTheme()){
            public void onBackPressed() {
                if(!BaseMvpFragment.this.onBackPressed())
                    super.onBackPressed();
                }
            };

    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(getPresenter()!=null)
             getPresenter().setUI(this);
        else
            dismiss();
    }
    @Override
    public void onStop()
    {
        super.onStop();
        if(getPresenter()!=null)
          getPresenter().removeUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }*/

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        if(getPresenter()!=null)
            getPresenter().onDismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {

        super.onCancel(dialog);
        getPresenter().onCancel();
    }

    protected final static String  TITLE_PARAM="TITLE_PARAM",
            ICON_ID ="ICON_ID";
    protected void setupTitle(View rootView)
    {
        headerIcon = (ImageView) rootView.findViewById(R.id.headerIcon);
        Bitmap icon=null;
        int iconResId = getArguments().getInt(ICON_ID);
        if(iconResId>0) {
            icon = BitmapFactory.decodeResource(getContext().getResources(), iconResId);
            headerIcon.setImageBitmap(icon);
        }
        if(icon==null)
            headerIcon.setVisibility(View.GONE);

        headerTextView =  (TextView)rootView.findViewById(R.id.headerText);
        headerTextView.setText(Html.fromHtml(getArguments().getString(TITLE_PARAM)));
    }

    /**
     * when user presses back button
     * @return
     */
    public boolean onBackPressed() {
        return false;
    }



}
