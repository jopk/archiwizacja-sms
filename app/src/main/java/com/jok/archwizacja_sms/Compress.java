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
    private String ZIPFILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/" + "compressedsms.zip";
    private String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";


    public Compress() {}

    public String[] writeFiles(String[] smsData) {
        String[] files = new String[smsData.length];
        File dir = new File(FILEPATH);
        if (!dir.exists())
            dir.mkdir();
        PrintWriter pw;
        try {
            for(int i = 0; i < smsData.length; i++) {
                FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compress/";
                files[i] = "sms"+i+".xml";
                File file = new File(FILEPATH + files[i]);
                pw = new PrintWriter(file);
                pw.write(smsData[i]);
                pw.flush();
                pw.close();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    public String[] readFiles() {
        File fileslist[] = new File(FILEPATH).listFiles();
        String[] files = new String[fileslist.length];

        try {
            for (int i = 0; i < fileslist.length; i++) {
                String name = fileslist[i].getName();
                if (name.matches("sms[0-9]*[.]xml")) {
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
        return files;
    }

    public void zip(String[] files) {
        File filepath = new File(FILEPATH);
        File compFile = new File(ZIPFILE);

        if (!filepath.exists())
            filepath.mkdir();
        if(compFile.exists()){
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
        }
        else {
            appendZip();
        }
    }

    public void appendZip(){                //???????????????????
/*        boolean havePreviousData = false;
        Vector<ZipOutputStreamNew.XEntry> tempXentries = new Vector<>();
        long tempWritten = 0;
        int fileIter=0;
        try{
            for(String file:files) {
                //beginning of initial setup stuff
                BufferedInputStream origin = null;
                FileOutputStream dest = new FileOutputStream(ZIPFILE,true);
                ZipOutputStreamNew out = new ZipOutputStreamNew(new BufferedOutputStream(dest));
                byte data[] = new byte[BUFFER];
                if (havePreviousData) {
                    out.setWritten(tempWritten);
                    out.setXentries(tempXentries);
                }
                //end of initial setup stuff

                //beginning of for loop
                //Log.i("Compress", "Adding: " + files.get(i));
                FileInputStream fis = new FileInputStream(file);
                origin = new BufferedInputStream(fis, BUFFER);
                ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
                out.closeEntry();
                //end of for loop
                fileIter++;
                //beginning of finishing stuff
                if (fileIter == (files.length-1)) {
                    //it's the last record so we should finish it off
                    out.closeAndFinish();
                } else {
                    //close the file, but don't write the Central Directory
                    //first, back up where the zip file was...
                    tempWritten = out.getWritten();
                    tempXentries = out.getXentries();
                    havePreviousData = true;
                    //now close the file
                    out.close();
                }
                //end of finishing stuff
            }
            //zip succeeded
        }catch (Exception e){
            e.printStackTrace();
        }
        */
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
