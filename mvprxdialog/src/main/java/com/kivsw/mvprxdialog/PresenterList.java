package com.kivsw.mvprxdialog;

import java.util.HashMap;
import java.util.Map;

/**
 * this class holds all the presenters
 */

class PresenterList {

    private static PresenterList singletone=null;
    public static PresenterList getInstance()
    {
        if(singletone==null)
            singletone = new PresenterList();
        return singletone;
    };


    private long nextId;
    private Map<Long, Contract.IPresenter> map;
    protected PresenterList()
    {
        nextId=1;
        map = new HashMap<>();
    }

    protected long generateId()
    {
        return nextId++;
    }

    Contract.IPresenter getPresenter(long presenterId)
    {
        return map.get(presenterId);
    };

    long addNewPresenter(Contract.IDialogPresenter presenter)
    {
        long presenterId = generateId();
        //presenter.setPresenterId(presenterId);
        map.put(presenterId, presenter);
        return presenterId;
    }

    void deletePresenter(long presenterId)
    {
        map.remove(presenterId);
    }
}
