package com.fc.myalarm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHelper {
    final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/notepad/" ;
    final static String TAG = FileHelper.class.getName();

    public static  String ReadFile( Context context,String fileName){
        String line = null;

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(path,fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }
        catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return line;
    }

    public static boolean saveToFile( String data,String fileName,boolean append){
        try {
           boolean b = new File(path).mkdir();
            if (b){
                Log.d(TAG, "Folder created");
            }else {
                Log.d(TAG, "Folder Not created");
            }
            File file = new File(path, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file,append);
            if (append){
                fileOutputStream.write(data.getBytes());
            }else {
                fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());
            }

            return true;
        }  catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return  false;

    }
    public static boolean deleteFile(String fileName){
        File file = new File(path, fileName);
        return file.delete();
    }

}
