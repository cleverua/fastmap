package com.cleverua.fastmap.interfaces;

import com.cleverua.fastmap.tasks.GetMapDataTask;

/**
 * Created by: Alex Kulakovsky
 * Date: 4/9/13
 * Time: 11:55 AM
 * Email: akulakovsky@cleverua.com
 */
public interface IOnMapDataDownloaded {
    public void onMapDataDownloaded(GetMapDataTask.DataContainer container);
}