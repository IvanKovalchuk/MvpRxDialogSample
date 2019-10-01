package com.kivsw.cloud.disk.yandex;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.kivsw.cloud.disk.BaseDiskRepresenter;
import com.kivsw.cloud_disk.R;

/**
 * Created by ivan on 9/7/2017.
 */

public class YandexRepresenter extends BaseDiskRepresenter {
    public YandexRepresenter(Context context, String clientId, String deviceId, String deviceName, YandexDiskIo.FileListOrder fileListOrder)
    {
        super(new YandexDiskIo(context, clientId, deviceId, deviceName, fileListOrder), "yandexdisk", "Yandex.Disk",
                BitmapFactory.decodeResource(context.getResources(), R.drawable.yandexdisk));
    }
}
