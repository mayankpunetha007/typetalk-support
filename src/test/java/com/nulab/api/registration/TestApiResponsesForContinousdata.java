package com.nulab.api.registration;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by mayan on 11/27/2016.
 */
public class TestApiResponsesForContinousdata {
    public static void main(String[] args) throws IOException {
        for(int i=0;i<15;i++) {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            List<NameValuePair> tokenParams = new ArrayList<>();
            tokenParams.add(new BasicNameValuePair("message", "abc"));
            HttpPost createTopicPost = new HttpPost(String.format("https://typetalk.in/api/v1/topics/%s", 33876));
            createTopicPost.addHeader("Authorization", "Bearer " + "zoUu9xViKdGetlAVa9p5XEaNafctkkwotgf2uO2a4IzSqA6O8Po52raO029HcPKM");
            createTopicPost.setEntity(new UrlEncodedFormEntity(tokenParams));
            HttpResponse response = client.execute(createTopicPost);
            InputStream inputStream = response.getEntity().getContent();
            String theString = convertStreamToString(inputStream);
            JSONObject obj = new JSONObject(theString);
            System.out.print(obj.getJSONObject("post").getString("message"));
        }
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
