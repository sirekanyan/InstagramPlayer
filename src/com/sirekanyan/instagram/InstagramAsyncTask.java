package com.sirekanyan.instagram;

import android.os.AsyncTask;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This AsyncTask gets URL like https://instagram.com/p/78EzUIH9Jh/ and returns
 * {@link MetaProperties} that contains a direct link to the media file
 * <p/>
 * User: sirekanyan, date: 10/17/15
 */
public class InstagramAsyncTask extends AsyncTask<String, Integer, MetaProperties> {

    private static Map<String, String> regexps = new HashMap<>();

    static {
        regexps.put(MetaProperties.TITLE, ".*?content=\"(.*?) on Instagram:.*?");
        regexps.put(MetaProperties.IMAGE, ".*?(https://.*?\\.jpg).*?");
        regexps.put(MetaProperties.VIDEO, ".*?(https://.*?\\.mp4).*?");
    }

    @Override
    protected MetaProperties doInBackground(String... params) {
        try {
            return InstagramAsyncTask.getMediaFile(params[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static MetaProperties getMediaFile(String pageUrl) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(pageUrl.replaceFirst("^http://", "https://"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            if (response != 200) {
                throw new UnsupportedOperationException("Cannot handle http code " + response);
            }
            is = conn.getInputStream();
            return parseMediaFile(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static MetaProperties parseMediaFile(InputStream stream) throws IOException {
        MetaProperties media = new MetaProperties();
        Scanner scanner = new Scanner(stream, "UTF-8");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("<meta")) {
                String typePatternString = ".*?property=\"(.*?)\".*?";
                Pattern typePattern = Pattern.compile(typePatternString);
                Matcher typeMatcher = typePattern.matcher(line);
                if (typeMatcher.matches()) {
                    String type = typeMatcher.group(1);
                    if (regexps.containsKey(type)) {
                        Pattern contentPattern = Pattern.compile(regexps.get(type));
                        Matcher contentMatcher = contentPattern.matcher(line);
                        if (contentMatcher.matches()) {
                            media.addMeta(type, contentMatcher.group(1));
                        }
                    }
                }
            }
        }
        scanner.close();
        return media;
    }
}
