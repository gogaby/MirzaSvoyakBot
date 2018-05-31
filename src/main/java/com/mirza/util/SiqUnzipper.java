package com.mirza.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by yach0217 on 22.05.2018.
 */
@SuppressWarnings(value = "all")
@Component
public class SiqUnzipper {

    private final static String siqPackages = "siqPackages";

    public void unzip(String siqFile) throws IOException {
        URL url = new URL("https://api.telegram.org/file/bot587789428:AAGeFuJToFte62bw8Okz-TS_G0DoPMR2tZU" +
                "/" + siqFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection()
                .getInputStream()));
        File dir = new File(siqPackages + File.separator);
        if(!dir.exists())
            dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zis = new ZipInputStream(url.openConnection().getInputStream());
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(siqPackages + File.separator +siqFile + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}