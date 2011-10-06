package com.sj.freebase.index.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sj.freebase.index.data.FreebaseTopicResponse;
import com.sj.util.StringUtils;

public class SolrClient {

    public static Logger log = Logger.getLogger("SolrClient");


    public static void sendIndexDocuments(String jsonString, URL url) {
        InputStream ipStream = new ByteArrayInputStream(jsonString.getBytes());
        HttpURLConnection urlc = null;
        OutputStream out = null;

        try {
            urlc = (HttpURLConnection) url.openConnection();

            urlc.setDoInput(true);
            urlc.setDoOutput(true);
            urlc.setUseCaches(false);
            urlc.setAllowUserInteraction(false);
            urlc.setRequestProperty("Content-type", "application/json");
            urlc.setFixedLengthStreamingMode(jsonString.length());
            out = urlc.getOutputStream();

            if (out != null) {
                byte [] buf = new byte [1024];
                int read = 0;
                while ((read = ipStream.read(buf)) >= 0) {
                    if (null != out)
                        out.write(buf, 0, read);
                }
                if (null != out) {
                    out.flush();
                }

                buf = null;
            }
        } catch (MalformedURLException e) {
            log.debug("Failed to send request to the solr server, exception : " +
                e.toString());
        } catch (IOException e) {
            log.debug("Failed to send request to the solr server, exception : " +
                e.toString());
        } catch (Exception e) {
            log.debug("Failed to send request to the solr server, exception : " +
                e.toString());
        } finally {
            urlc.disconnect();
        }
    }


    public static List<FreebaseTopicResponse> queryTopics(String uri, Gson gson) {
        HttpURLConnection connection = null;
        BufferedReader rd = null;
        String line = null;

        URL url = null;

        String jsonString = "";
        List<FreebaseTopicResponse> responses = Collections.emptyList();
        try {
            url = new URL(uri);
            connection = null;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);

            connection.connect();
            InputStream ip = connection.getInputStream();
            if (ip == null) {
                ip = connection.getErrorStream();
            }

            rd = new BufferedReader(new InputStreamReader(ip));

            while ((line = rd.readLine()) != null) {
                jsonString = StringUtils.combine(jsonString, line);
            }

            JsonParser parser = new JsonParser();
            JsonElement jElement = parser.parse(jsonString);

            responses =
                gson.fromJson(jElement,
                    new TypeToken<List<FreebaseTopicResponse>>() {
                    }.getType());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        if (responses == null) {
            responses = Collections.emptyList();
        }

        return responses;
    }
}
