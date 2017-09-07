package com.kivsw.mvprxdialog.messagebox;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;

import com.kivsw.mvprxdialog.inputbox.MvpInputBoxBuilder;

/**
 * Builder for MvpMessageBoxPresenter class
 */

public class MvpMessageBoxBuilder {
    public static MvpMessageBoxBuilder newInstance()
    {
        return new MvpMessageBoxBuilder();
    };

    private Bitmap icon;
    private String title=null,  msg=null;
    private  boolean askDontShowAgain=false;
    private  String okTitle="",  cancelTitle=null,  exTitle=null;

    /**
     *
     * @param icon
     * @return
     */
    public MvpMessageBoxBuilder setIcon(Bitmap icon)
    {
        this.icon = icon;
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
                 icon,  title,  msg,
                 askDontShowAgain,
                 okTitle,  cancelTitle,  exTitle);
    }
}
