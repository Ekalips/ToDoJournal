package com.example.ekalips.vitya;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ekalips on 6/12/16.
 */

public class EditContentsAsyncTask extends AsyncTask<DriveFile, Void, Boolean> {
    Activity context;
    public EditContentsAsyncTask(Activity context) {
        this.context = context;
    }


    @Override
    protected Boolean doInBackground(DriveFile... params) {
        DriveFile file = params[0];
        DriveApi.DriveContentsResult driveContentsResult = file.open(
                ((MainActivity)context).mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
        if (!driveContentsResult.getStatus().isSuccess()) {
            return false;
        }
        DriveContents driveContents = driveContentsResult.getDriveContents();
        OutputStream outputStream = driveContents.getOutputStream();
        File file2 = new File(Environment.getExternalStorageDirectory(), "SECRETDOCUMENTS");
        copyFileToOutputStream(outputStream,file2);


        com.google.android.gms.common.api.Status status =
                driveContents.commit( ((MainActivity)context).mGoogleApiClient, null).await();
        return status.getStatus().isSuccess();
    }

    public void copyFileToOutputStream( OutputStream out, File file ) {
        try {
            InputStream inputStream = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            Log.d("Lelele","Error while editing contents");
            return;
        }
        Log.d("Lelele","Successfully edited contents");
    }
}
