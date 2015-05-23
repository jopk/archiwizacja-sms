package com.jok.archwizacja_sms;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Compress {

    private static final int BUFFER = 1024;
    public String[] files;
    private String zipFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + "compressedsms.zip";
    private String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";


    public Compress(String[] files){
        this.files=files;
    }

    public void writeFiles(String[] smsData) {
        files = new String[smsData.length];
        File dir = new File(filepath);
        if (!dir.exists())
            dir.mkdir();
        PrintWriter pw;
        try {
            for(int i = 0; i < smsData.length; i++) {
                filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";
                files[i] = "sms"+i+".xml";
                File file = new File(filepath+files[i]);
                pw = new PrintWriter(file);
                pw.write(smsData[i]);
                pw.flush();
                pw.close();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String[] filesToString(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress";
        String[] filesbody = new String [files.length];
        File filepath = new File(path);
        int fileIter = 0;
        if (!filepath.exists())
            filepath.mkdir();
        try{
            for(String file : files){
                file =Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + file;
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }
                filesbody[fileIter]=sb.toString();
                fileIter++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return filesbody;
    }

    public void zip() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";
        File filepath = new File(path);
        File compFile = new File(zipFile);
        if (!filepath.exists())
            filepath.mkdir();
        if(compFile.exists()){
            try {
                BufferedInputStream origin = null;
                ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));

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
        }
        else {
            appendZip();
        }
    }

    public void appendZip(){
        }
    public void unzip(){
        InputStream fis;
        ZipInputStream zis;
        try {
            String filename;
            fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry zipEntry;
            byte[] data = new byte[BUFFER];
            int count;

            while ((zipEntry = zis.getNextEntry()) != null) {
                filename = zipEntry.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (zipEntry.isDirectory()) {
                    File fmd = new File(filepath + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fos = new FileOutputStream(filepath + filename);

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
