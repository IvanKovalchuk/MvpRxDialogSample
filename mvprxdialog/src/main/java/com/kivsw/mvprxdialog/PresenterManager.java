package com.kivsw.mvprxdialog;

import java.util.HashMap;
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
        map = new HashMap<>();
    }

    protected long generateId()
    {
        return nextId++;
    }

    public Contract.IPresenter getPresenter(long presenterId)
    {
        return map.get(presenterId);
    };

    public long addNewPresenter(Contract.IPresenter presenter)
    {
        long presenterId = generateId();
        presenter.setPresenterId(presenterId);
        map.put(presenterId, presenter);
        return presenterId;
    }

    public void deletePresenter(long presenterId)
    {
        map.remove(presenterId);
    }
}
