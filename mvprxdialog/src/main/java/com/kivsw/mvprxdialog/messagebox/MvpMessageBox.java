package com.kivsw.mvprxdialog.messagebox;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kivsw.mvprxdialog.BaseMvpFragment;
import com.kivsw.mvprxdialog.R;

import io.reactivex.annotations.Nullable;


/**
 * A simple {@link Fragment} subclass.
 * do not create an instance of this fragment, use MvpMessageBoxPresenter instead
 */
public class MvpMessageBox extends BaseMvpFragment {

    private boolean doDismiss; // allow to dismiss dialog after any button was pressed
    private Button okBtn= null, cancelBtn= null, extraBtn= null;
    private TextView messageTextView, headerTextView;
    private CheckBox checkBoxDontShowAgain=null;



    public MvpMessageBox() {
        // Required empty public constructor
    }

    private final static String
                                MESSAGE_PARAM="MESSAGE_PARAM",
                                DONT_SHOW_AGAIN="DONT_SHOW_AGAIN",
                                OK_TITLE_PARAM="OK_TITLE_PARAM",
                                CANCEL_BTN_PARAM="CANCEL_BTN_PARAM",
                                EXTRA_BTN_PARAM="EXTRA_BTN_PARAM";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.


     /** creates a message dialog instance, that may hold all 3 buttons.
     *  @param  okTitle,
     *  @param  cancelTitle,
     *  @param exTitle These parameters are button's title:
     *      null value means that a button is invisible
     *      "" value means that button is visible and has its default title.
     *      another values entitle appropriate button
     *
     *  @param askDontShowAgain enables "Don't show again" checkBox
     */
    // TODO: Rename and change types and number of parameters
    public static MvpMessageBox newInstance(long presenterId, int iconResId, String title, String msg, boolean askDontShowAgain, @Nullable String okTitle, @Nullable String cancelTitle, @Nullable String exTitle)
    {
        MvpMessageBox fragment = new MvpMessageBox();
        Bundle args = new Bundle();

        args.putLong(PRESENTER_ID, presenterId);
        args.putInt(ICON_PARAM,iconResId);
        args.putString(MESSAGE_PARAM,msg);
        args.putString(TITLE_PARAM,title);
        args.putBoolean(DONT_SHOW_AGAIN,askDontShowAgain);

        args.putString(OK_TITLE_PARAM,okTitle);
        args.putString(CANCEL_BTN_PARAM,cancelTitle);
        args.putString(EXTRA_BTN_PARAM,exTitle);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.mvp_messagebox, container, false);

        okBtn= (Button)rootView.findViewById(R.id.dlButtonOk);
        cancelBtn=(Button)rootView.findViewById(R.id.dlButtonCancel);
        extraBtn=(Button)rootView.findViewById(R.id.dlButtonExtra);

        okBtn.setOnClickListener(onClickListener);
        cancelBtn.setOnClickListener(onClickListener);
        extraBtn.setOnClickListener(onClickListener);

        messageTextView = (TextView)rootView.findViewById(R.id.dlMessageTextView);
        messageTextView.setText(Html.fromHtml(getArguments().getString(MESSAGE_PARAM)));

        setupTitle(rootView);


        @Nullable String okTitle=getArguments().getString(OK_TITLE_PARAM);
        @Nullable String cancelTitle=getArguments().getString(CANCEL_BTN_PARAM);
        @Nullable String extraTitle=getArguments().getString(EXTRA_BTN_PARAM);

        if(okTitle!=null)
        {
            if(okTitle.length()==0) okTitle=getText(android.R.string.ok).toString();
            okBtn.setText(okTitle);
            okBtn.setVisibility(View.VISIBLE);
        }
        else okBtn.setVisibility(View.GONE);

        if(cancelTitle!=null)
        {
            if(cancelTitle.length()==0) cancelTitle=getText(android.R.string.cancel).toString();
            cancelBtn.setText(cancelTitle);
            cancelBtn.setVisibility(View.VISIBLE);
        }
        else cancelBtn.setVisibility(View.GONE);

        if(extraTitle!=null)
        {
            if(extraTitle.length()==0) extraTitle=getText(android.R.string.unknownName).toString();
            extraBtn.setText(extraTitle);
            extraBtn.setVisibility(View.VISIBLE);
        }
        else extraBtn.setVisibility(View.GONE);

        checkBoxDontShowAgain = (CheckBox)rootView.findViewById(R.id.checkBoxDontShowAgain);
        if(!getArguments().getBoolean(DONT_SHOW_AGAIN))
            checkBoxDontShowAgain.setVisibility(View.GONE);
        else checkBoxDontShowAgain.setVisibility(View.VISIBLE);

        return rootView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            int viewId = v.getId();
            MvpMessageBoxPresenter presenter = (MvpMessageBoxPresenter)getPresenter();

            if (viewId == R.id.dlButtonOk)           presenter.onPress(MvpMessageBoxPresenter.OK_BUTTON);
             else if (viewId == R.id.dlButtonCancel) presenter.onPress(MvpMessageBoxPresenter.CANCEL_BUTTON);
             else if (viewId == R.id.dlButtonExtra)  presenter.onPress(MvpMessageBoxPresenter.EXTRA_BUTTON);
        }
    };



}
