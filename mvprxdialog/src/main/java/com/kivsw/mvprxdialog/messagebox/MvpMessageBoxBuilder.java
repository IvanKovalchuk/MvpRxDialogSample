package com.kivsw.mvprxdialog.messagebox;

import android.support.v4.app.FragmentManager;

/**
 * Builder for MvpMessageBoxPresenter class
 */

public class MvpMessageBoxBuilder {
    public static MvpMessageBoxBuilder newInstance()
    {
        return new MvpMessageBoxBuilder();
    };

    private int iconRes=0;
    private String title=null,  msg=null;
    private  boolean askDontShowAgain=false;
    private  String okTitle="",  cancelTitle=null,  exTitle=null;

    /**
     *
     * @param iconRes
     * @return
     */
    public MvpMessageBoxBuilder setIcon(int iconRes)
    {
        this.iconRes = iconRes;
        return this;
    }

    /**
     * controls Ok button
     * @return
     */
    public MvpMessageBoxBuilder showOkButton()
    {
        this.okTitle = "";
        return this;
    }
    public MvpMessageBoxBuilder hideOkButton()
    {
        this.okTitle = null;
        return this;
    }
    public MvpMessageBoxBuilder setOkButton(String text)
    {
        this.okTitle = text;
        return this;
    }
    public MvpMessageBoxBuilder setOkButton(CharSequence text)
    {
        return setOkButton(text.toString());
    }
    /**
     * controls Cancel button
     * @return
     */
    public MvpMessageBoxBuilder showCancelButton()
    {
        this.cancelTitle = "";
        return this;
    }
    public MvpMessageBoxBuilder hideCancelButton()
    {
        this.cancelTitle = null;
        return this;
    }
    public MvpMessageBoxBuilder setCancelButton(String text)
    {
        this.cancelTitle = text;
        return this;
    }
    public MvpMessageBoxBuilder setCancelButton(CharSequence text)
    {
        return setCancelButton(text.toString());
    }
    /**
     * controls Extra button
     * @return
     */
    public MvpMessageBoxBuilder showExtraButton()
    {
        this.exTitle = "";
        return this;
    }
    public MvpMessageBoxBuilder hideExtraButton()
    {
        this.exTitle = null;
        return this;
    }
    public MvpMessageBoxBuilder setExtraButton(String text)
    {
        this.exTitle = text;
        return this;
    }

    /**
     * sets message and the title
     */
    public MvpMessageBoxBuilder setText(String title, String msg)
    {
        this.title = title;
        this.msg = msg;
        return this;
    }
    public MvpMessageBoxBuilder setText(CharSequence title, CharSequence msg)
    {
        return setText(title.toString(), msg.toString());
    }
    public MvpMessageBoxBuilder setShowDontAskAgain(boolean show)
    {
        this.askDontShowAgain = show;
        return this;
    }

    /**
     *
     */
    public MvpMessageBoxPresenter build(FragmentManager fragmentManager) {
        return
        MvpMessageBoxPresenter.createDialog(fragmentManager,
                 iconRes,  title,  msg,
                 askDontShowAgain,
                 okTitle,  cancelTitle,  exTitle);
    }
}
