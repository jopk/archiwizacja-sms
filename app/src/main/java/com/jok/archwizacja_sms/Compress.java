package com.jok.archwizacja_sms;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Compress{
    private static final int buffer = 1024;
    public String[] files;
    public Compress(String[] files){
        this.files=files;
    }
    public void zip() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + "test1.txt";
        String fileName2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + "test2.txt";
        String zipFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + "compressedsms.zip";
        File filepath = new File(path);
        File zipfile = new File(zipFile);
        if (!filepath.exists()) filepath.mkdir();
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFile);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[buffer];
            for(int i=0;i<files.length;i++){
                files[i]=Environment.getExternalStorageDirectory().getAbsolutePath()+"/compress/"+files[i];
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, buffer);
                ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, buffer)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();

        } catch (Exception e) {
                e.printStackTrace();
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
