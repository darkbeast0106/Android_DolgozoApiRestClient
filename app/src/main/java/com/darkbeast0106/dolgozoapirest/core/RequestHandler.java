package com.darkbeast0106.dolgozoapirest.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public final class RequestHandler {
    private RequestHandler(){

    }

    public static Response getRequest(String url) throws IOException {
        HttpURLConnection conn = setupConnection(url);

        int responseCode = conn.getResponseCode();

        InputStream stream = null;
        if (responseCode<400){
            stream = conn.getInputStream();
        } else {
            stream = conn.getErrorStream();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            sb.append(s).append(System.lineSeparator());
        }
        return new Response(responseCode, sb.toString().trim());
    }

    public static Response postRequestURLEncoded(String url, HashMap<String, String> params) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(params));
        writer.flush();
        writer.close();
        os.close();
        int responseCode = conn.getResponseCode();

        InputStream stream = null;
        if (responseCode<400){
            stream = conn.getInputStream();
        } else {
            stream = conn.getErrorStream();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            sb.append(s).append(System.lineSeparator());
        }
        return new Response(responseCode, sb.toString().trim());
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static Response postRequestJSON(String url, String params) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(params);
        writer.flush();
        writer.close();
        os.close();
        int responseCode = conn.getResponseCode();

        InputStream stream = null;
        if (responseCode<400){
            stream = conn.getInputStream();
        } else {
            stream = conn.getErrorStream();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            sb.append(s).append(System.lineSeparator());
        }
        return new Response(responseCode, sb.toString().trim());
    }

    public static Response putRequestJSON(String url, String params) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(params);
        writer.flush();
        writer.close();
        os.close();
        int responseCode = conn.getResponseCode();

        InputStream stream = null;
        if (responseCode<400){
            stream = conn.getInputStream();
        } else {
            stream = conn.getErrorStream();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            sb.append(s).append(System.lineSeparator());
        }
        return new Response(responseCode, sb.toString().trim());
    }


    public static Response deleteRequest(String url) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestMethod("DELETE");

        int responseCode = conn.getResponseCode();

        InputStream stream = null;
        if (responseCode<400){
            stream = conn.getInputStream();
        } else {
            stream = conn.getErrorStream();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            sb.append(s).append(System.lineSeparator());
        }
        return new Response(responseCode, sb.toString().trim());
    }

    private static HttpURLConnection setupConnection(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        return conn;
    }
}
