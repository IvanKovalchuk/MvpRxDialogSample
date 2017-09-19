package com.kivsw.mvprxdialog.inputbox;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.text.InputType;

/**
 * Builder for MvpInputBoxPresenter class
 */

public class MvpInputBoxBuilder {
    public static MvpInputBoxBuilder newInstance()
    {
        return new MvpInputBoxBuilder();
    };

    private Bitmap icon=null;
    private String title=null, msg=null, InputValue=null;
    private int inputType = InputType.TYPE_CLASS_TEXT;
    private  MvpInputBoxPresenter.TestValue testValue=null;

    public MvpInputBoxBuilder setIcon(Bitmap icon)
    {
        this.icon = icon;
        return this;
    }

    public MvpInputBoxBuilder setText(String title, String msg)
    {
        this.title = title;
        this.msg = msg;
        return this;
    }
    public MvpInputBoxBuilder setText(CharSequence title, CharSequence msg)
    {
        return setText(title.toString(), msg.toString());
    }
    public MvpInputBoxBuilder setInitialValue(String value)
    {
        this.InputValue = value;
        return this;
    };

    public MvpInputBoxBuilder setInputType(int value)
    {
        this.inputType = value;
        return this;
    };

    public MvpInputBoxBuilder setTest(MvpInputBoxPresenter.TestValue testValue)
    {
        this.testValue = testValue;
        return this;
    };

    public MvpInputBoxPresenter build(FragmentManager fragmentManager)
    {
        return
            MvpInputBoxPresenter.createDialog( fragmentManager, icon, title, msg, InputValue, inputType, testValue);
    }

}
