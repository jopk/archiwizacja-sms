package com.jok.archwizacja_sms;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Compress {

    private static final int BUFFER = 1024;
    private String ZIPFILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + "compressedsms.zip";
    private String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";


    public Compress() {}

    private void deleteFiles(){
        File folder = new File(FILEPATH);
        if (folder.exists()){
            if(folder.isDirectory()){
                String[] files = folder.list();
                for(int i=0;i<files.length;i++){
                    if(files[i].endsWith(".xml")){
                        new File(folder,files[i]).delete();
                    }
                }
            }
        }
    }

    public String[] writeFiles(String[] data, int amount, String tag, Context ctx) {
        //File dir = ctx.getFilesDir();
        String[] files = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            try {
                int tmp = i + amount;
                files[i] = tag + tmp + ".xml";
                FileOutputStream fos = ctx.openFileOutput(files[i],Context.MODE_PRIVATE);
                fos.write(data[i].getBytes());
                fos.close();
                /*File file = new File(dir, files[i]);
                PrintWriter pw = new PrintWriter(file);
                pw.write(data[i]);
                pw.flush();
                pw.close();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return files;
    }



    public String[] writeFilesExternal(String[] data, int amount, String tag) {
        String[] files = new String[data.length];
        File dir = new File(FILEPATH);
        if (!dir.exists())
            dir.mkdir();
        PrintWriter pw;
        try {
            for(int i = 0; i < data.length; i++) {
                FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";
                int tmp = i + amount;
                files[i] = tag + tmp + ".xml";
                File file = new File(FILEPATH + files[i]);
                pw = new PrintWriter(file);
                pw.write(data[i]);
                pw.flush();
                pw.close();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    private final String TAG = "Compress";

    public String[] readFiles(Context ctx) {
       /* File path = ctx.getFilesDir();
        File fileslist[] = path.listFiles();
        String[] files = new String[fileslist.length];  */
        File dir = ctx.getDir("files", Context.MODE_PRIVATE);
        File[] fileslist = dir.listFiles();
        Log.e(TAG, ""+fileslist.length);
        String[] files= new String[fileslist.length];
        byte[] buffer = new byte[1024];
        for (int i=0;i<fileslist.length;i++) {
            try {
                String name = fileslist[i].getName();
                Log.e(TAG, name);
                if (name.endsWith(".xml")) {
                    String file = fileslist[i].getName();
                    FileInputStream fis = ctx.openFileInput(file);
                    int ch;
                    StringBuilder sb = new StringBuilder();
                    while((ch = fis.read())!=-1){
                        sb.append((char) ch);
                    }
                    files[i] = sb.toString();
                    fis.close();
                    /*
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine())!=null){
                        sb.append(line);
                    }
                    files[i] = sb.toString();*/

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    public String[] readFilesExternal() {
        File fileslist[] = new File(FILEPATH).listFiles();
        String[] files = new String[fileslist.length];

        try {
            for (int i = 0; i < fileslist.length; i++) {
                String name = fileslist[i].getName();
                if (name.endsWith(".xml")) {
                    String file = FILEPATH + name;
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine())!=null){
                        sb.append(line);
                    }
                    files[i] = sb.toString();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //deleteFiles();
        return files;
    }

    public void zip(String[] files) {
        File filepath = new File(FILEPATH);
        File compFile = new File(ZIPFILE);

        if (!filepath.exists())
            filepath.mkdir();

        if(compFile.exists()) {
            unzip();
            files = filepath.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".xml");
                }
            });
        }
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(ZIPFILE);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];
            for(String file : files) {
                file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + file;
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //deleteFiles();
    }


    public void unzip(){
        InputStream fis;
        ZipInputStream zis;
        try {
            String filename;
            fis = new FileInputStream(ZIPFILE);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry zipEntry;
            byte[] data = new byte[BUFFER];
            int count;

            while ((zipEntry = zis.getNextEntry()) != null) {
                filename = zipEntry.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (zipEntry.isDirectory()) {
                    File fmd = new File(FILEPATH + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fos = new FileOutputStream(FILEPATH + filename);

                while ((count = zis.read(data)) != -1) {
                    fos.write(data, 0, count);
                }

                fos.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
     }
}
