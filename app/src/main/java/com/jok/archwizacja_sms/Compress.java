package com.jok.archwizacja_sms;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Compress {

    private static final int BUFFER = 1024;
    public String[] files;

    public Compress(String[] files){
        this.files=files;
    }

    public void writeFiles(String[] smsData) {
        files = new String[smsData.length];
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";
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

    public void zip() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";
        String zipFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + "compressedsms.zip";
        File filepath = new File(path);
        if (!filepath.exists())
            filepath.mkdir();
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
        /*else {
            try {
                ZipFile war = new ZipFile(zipFile);
                ZipOutputStream append = new ZipOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + "append.zip"));

                // first, copy contents from existing war
                Enumeration<? extends ZipEntry> entries = war.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry e = entries.nextElement();
                    append.putNextEntry(e);
                    if (!e.isDirectory()) {
                        copy(war.getInputStream(e), append);
                    }
                    append.closeEntry();
                }


                // now append some extra content
                ZipEntry e = new ZipEntry(fileName2);
                append.putNextEntry(e);
                append.write("42\n".getBytes());
                append.closeEntry();

                // close
                war.close();
                append.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }*/
    }
}
