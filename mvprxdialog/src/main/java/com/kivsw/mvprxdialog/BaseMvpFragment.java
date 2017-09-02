package com.kivsw.mvprxdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * this is a base class for a fragment
 */
public abstract class BaseMvpFragment extends DialogFragment
implements Contract.IView
{

    TextView headerTextView;
    public BaseMvpFragment() {
       // setStyle(STYLE_NO_TITLE, getTheme());
    }

    /**
     * creates a presenter for this fragment
     * if the fragment is not dialog, the method must return null and getPresenter() must be overridden
     * @return a created presenter
     */
    protected Contract.IPresenter createPresenter()
    {
        return null;
    };

    protected final static String PRESENTER_ID="presenterId";
    private long presenterId=0;
    private Contract.IPresenter presenter=null;


    protected Contract.IPresenter  getPresenter()
    {
        return presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setShowsDialog();

        PresenterManager pm=PresenterManager.getInstance();
        presenterId = getArguments().getLong(PRESENTER_ID);
        if(presenterId<=0)
            throw new RuntimeException("PRESENTER_ID argument must be for this fragment " + this.getClass().getName());
        presenter = pm.getPresenter(presenterId);
        if(presenter==null)
            throw new RuntimeException("cannot find a presenter for this fragment " + this.getClass().getName());

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
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        getPresenter().onDismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {

        super.onCancel(dialog);
        getPresenter().onCancel();
    }

    protected final static String  TITLE_PARAM="TITLE_PARAM";
    protected void setupTitle(View rootView)
    {
        headerTextView =  (TextView)rootView.findViewById(R.id.dlMessageTextView);
        headerTextView.setText(Html.fromHtml(getArguments().getString(TITLE_PARAM)));
    }



 /*   @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/


}
