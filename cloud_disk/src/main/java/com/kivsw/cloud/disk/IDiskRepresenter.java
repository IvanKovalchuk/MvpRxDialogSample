package com.kivsw.cloud.disk;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.kivsw.cloud.disk.IDiskIO;

/**
 * This interface represents a disk
 */

public interface IDiskRepresenter {
    IDiskIO getDiskIo();
    String getName();
    String getScheme();
    Bitmap getIcon();
}
