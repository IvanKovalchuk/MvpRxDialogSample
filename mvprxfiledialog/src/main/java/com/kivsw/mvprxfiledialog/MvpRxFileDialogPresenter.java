package com.kivsw.mvprxfiledialog;

import com.kivsw.mvprxdialog.BaseMvpPresenter;
import com.kivsw.mvprxdialog.Contract;

/**
 * Created by ivan on 9/7/2017.
 */

public class MvpRxFileDialogPresenter extends BaseMvpPresenter {
    MvpRxFileDialog view=null;
    @Override
    public Contract.IView getUI() {
        return view;
    }

    @Override
    public void setUI(Contract.IView view) {
        this.view = (MvpRxFileDialog)view;
    }

    private MvpRxFileDialogPresenter()
    {

    };



}
