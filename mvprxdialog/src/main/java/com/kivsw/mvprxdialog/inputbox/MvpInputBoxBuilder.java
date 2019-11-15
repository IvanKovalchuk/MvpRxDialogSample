package com.kivsw.mvprxdialog.inputbox;

import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.View;

/**
 * Builder for MvpInputBoxPresenter class
 */

public class MvpInputBoxBuilder {
    public static MvpInputBoxBuilder newInstance()
    {
        return new MvpInputBoxBuilder();
    };

    private int iconRes= View.NO_ID;
    private String title=null, msg=null, InputValue=null;
    private int inputType = InputType.TYPE_CLASS_TEXT;
    private  MvpInputBoxPresenter.TestValue testValue=null;

    public MvpInputBoxBuilder setIcon(int iconRes)
    {
        this.iconRes = iconRes;
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
            MvpInputBoxPresenter.createDialog( fragmentManager, iconRes, title, msg, InputValue, inputType, testValue);
    }

}
