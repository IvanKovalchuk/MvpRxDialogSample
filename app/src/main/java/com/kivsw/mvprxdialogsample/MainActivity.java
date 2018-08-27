package com.kivsw.mvprxdialogsample;

import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.kivsw.mvprxdialog.Contract;

public class MainActivity extends AppCompatActivity
        implements Contract.IView
{

    View rootView;

    protected MainActivityPresenter getPresenter()
    {
        return MainActivityPresenter.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rootView = findViewById(R.id.rootActivityView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button showDialogButton  = (Button) findViewById(R.id.showDialogButton);
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().showDialog();
            }
        });

        Button showInputBox=(Button) findViewById(R.id.showInputDialogButton);
        showInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().showInputBox();
            }
        });


        Button showFileOpenButton = (Button) findViewById(R.id.showFileOpenDialogButton);
        showFileOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().showFileOpen();
            }
        });

        Button showFileSaveDialogButton = (Button) findViewById(R.id.showFileSaveDialogButton);
        showFileSaveDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().showFileSave();
            }
        });

        Button showChooseDirDialogButton = (Button) findViewById(R.id.showChooseDirDialogButton);
        showChooseDirDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().showChooseDir();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getPresenter().setUI(this);

    }
    @Override
    protected void onStop()
    {
        super.onStop();
        getPresenter().setUI(null);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        int ds=getBundleSize(outState);

        if(ds>1042*1024)
            outState.clear();
    }


    public static int getBundleSize(Bundle b)
    {
        Parcel parcel = Parcel.obtain(); //new Parcel();
        b.writeToParcel(parcel, 0);
        int ds=parcel.dataSize();
        return ds;
    }

    public void showMessage(String message)
    {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
