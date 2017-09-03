package com.kivsw.mvprxdialog;

import java.util.Hashtable;
import java.util.Map;

/**
 * this class holds all the presenters
 */

public class PresenterManager {

    private static  PresenterManager singletone=null;
    public static PresenterManager getInstance()
    {
        if(singletone==null)
            singletone = new PresenterManager();
        return singletone;
    };


    long nextId;
    Map<Long, Contract.IPresenter> map;
    public PresenterManager()
    {
        nextId=1;
        map = new Hashtable();
    }

    long generateId()
    {
        return nextId++;
    }

    Contract.IPresenter getPresenter(long presenterId)
    {
        return map.get(presenterId);
    };

    long addNewPresenter(Contract.IPresenter presenter)
    {
        long presenterId = generateId();
        presenter.setPresenterId(presenterId);
        map.put(presenterId, presenter);
        return presenterId;
    }

    void deletePresenter(long presenterId)
    {
        map.remove(presenterId);
    }
}
