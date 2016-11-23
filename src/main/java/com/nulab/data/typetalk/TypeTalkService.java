package com.nulab.data.typetalk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.nulab.config.ApplicationConfig;
import com.nulab.data.pojo.inner.Account;
import com.nulab.data.pojo.inner.Group;
import com.nulab.data.pojo.inner.Space;
import com.nulab.data.pojo.inner.Topic;
import com.nulab.data.service.ApplicationService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.aspectj.lang.annotation.Before;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;

/**
 * Created by mayan on 11/20/2016.
 */
@Service("typeTalkService")
@ConfigurationProperties
public class TypeTalkService {



    private static boolean initialized = false;

    private String accessToken;

    private ObjectMapper objectMapper = new ObjectMapper();

    private CloseableHttpClient client;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private long lastGrant;

    private long expiry;

    private String refreshToken;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationConfig applicationConfig;


    public TypeTalkService() {
    }

    public TypeTalkService(String typettalkClientId, String typettalkClientSecret) {
        applicationConfig.setTypetalkClientId(typettalkClientId);
        applicationConfig.setTypetalkClientSecret(typettalkClientSecret);
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Before("com.nulab.data.typetalk.*")
    void reinitializeToken() throws IOException {
        if (isActive()) {
            init();
        }
    }

    @PostConstruct
    public void init(){
        try {
            if (!initialized) {
                client = HttpClientBuilder.create().build();
                List<NameValuePair> tokenParams = new ArrayList<NameValuePair>();
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                tokenParams.add(new BasicNameValuePair("client_id", applicationConfig.getTypetalkClientId()));
                tokenParams.add(new BasicNameValuePair("client_secret", applicationConfig.getTypetalkClientSecret()));
                tokenParams.add(new BasicNameValuePair("grant_type", "client_credentials"));
                tokenParams.add(new BasicNameValuePair("scope", "topic.read,topic.post,topic.write,my"));
                HttpPost tokenPost = new HttpPost("https://typetalk.in/oauth2/access_token");
                tokenPost.setEntity(new UrlEncodedFormEntity(tokenParams));
                HttpResponse response = client.execute(tokenPost);
                // Map<String, String> json = objectMapper.readValue(EntityUtils.toString(response.getEntity(), "UTF-8"), Map.class);
                // Use if Pojos implemented
                InputStream inputStream = response.getEntity().getContent();
                String theString = convertStreamToString(inputStream);
                logger.info(theString);
                JSONObject obj = new JSONObject(theString);
                accessToken = obj.getString("access_token");
                refreshToken = obj.getString("refresh_token");
                expiry = obj.getLong("expires_in");
                lastGrant = System.currentTimeMillis();
                initialized = true;
                startLisTening();
            } else {
                client = HttpClientBuilder.create().build();
                List<NameValuePair> tokenParams = new ArrayList<NameValuePair>();
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                tokenParams.add(new BasicNameValuePair("client_id", applicationConfig.getTypetalkClientId()));
                tokenParams.add(new BasicNameValuePair("client_secret", applicationConfig.getTypetalkClientSecret()));
                tokenParams.add(new BasicNameValuePair("grant_type", "refresh_token"));
                tokenParams.add(new BasicNameValuePair("refresh_token", refreshToken));
                HttpPost tokenPost = new HttpPost("https://typetalk.in/oauth2/access_token");
                tokenPost.setEntity(new UrlEncodedFormEntity(tokenParams));
                HttpResponse response = client.execute(tokenPost);
                InputStream inputStream = response.getEntity().getContent();
                String theString = convertStreamToString(inputStream);
                logger.info(theString);
                JSONObject obj = new JSONObject(theString);
                accessToken = obj.getString("access_token");
                refreshToken = obj.getString("refresh_token");
                expiry = obj.getLong("expires_in");
                lastGrant = System.currentTimeMillis();
            }
        }catch (IOException e){
            logger.error("Are you sure you are using the right keys in application.properties?", e);
        }


    }

    public List<Space> getOrganisations() throws IOException {
        List<Space> allOrganisations = new ArrayList<Space>();
        client = HttpClientBuilder.create().build();
        HttpGet createTopicPost = new HttpGet("https://typetalk.in/api/v1/spaces");
        createTopicPost.addHeader("Authorization", "Bearer " + accessToken);
        HttpResponse response = client.execute(createTopicPost);
        InputStream inputStream = response.getEntity().getContent();
        String outputString = convertStreamToString(inputStream);
        JSONObject jsonObject = new JSONObject(outputString);
        JSONArray jsonArray = jsonObject.getJSONArray("mySpaces");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            allOrganisations.add(objectMapper.readValue(object.get("space").toString(), Space.class));
        }
        return allOrganisations;
    }

    public List<Group> getOrganisationGroups(String orgKey) throws IOException {
        List<Group> allGroupsInOrganisation = new ArrayList<Group>();
        client = HttpClientBuilder.create().build();
        HttpGet createGet = new HttpGet(String.format("https://typetalk.in/api/v1/spaces/%s/members", orgKey));
        createGet.addHeader("Authorization", "Bearer " + accessToken);
        HttpResponse response = client.execute(createGet);
        InputStream inputStream = response.getEntity().getContent();
        String outputString = convertStreamToString(inputStream);
        JSONObject jsonObject = new JSONObject(outputString);
        JSONArray jsonArray = jsonObject.getJSONArray("groups");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i).getJSONObject("group");
            allGroupsInOrganisation.add(objectMapper.readValue(object.toString(), Group.class));
        }
        return allGroupsInOrganisation;
    }

    public List<Account> getOrganisationMembers(String orgKey) throws IOException {
        List<Account> allAccountsInOrganisation = new ArrayList<Account>();
        client = HttpClientBuilder.create().build();
        HttpGet createGet = new HttpGet(String.format("https://typetalk.in/api/v1/spaces/%s/members", orgKey));
        createGet.addHeader("Authorization", "Bearer " + accessToken);
        HttpResponse response = client.execute(createGet);
        InputStream inputStream = response.getEntity().getContent();
        String outputString = convertStreamToString(inputStream);
        JSONObject jsonObject = new JSONObject(outputString);
        JSONArray jsonArray = jsonObject.getJSONArray("accounts");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            allAccountsInOrganisation.add(objectMapper.readValue(object.toString(), Account.class));
        }
        return allAccountsInOrganisation;
    }

    public Topic createTopic(String topicSubject) throws IOException {
        Map<String, Object> tokenParams = new HashMap<String,Object>();
        tokenParams.put("name", topicSubject);
        tokenParams.put("spaceKey", applicationConfig.getTypetalkOrganisation());
        tokenParams.put("addAccountIds ", applicationConfig.getTypetalkSupportAccountId());
        tokenParams.put("addGroupIds ", applicationConfig.getTypetalkSupportGroups());
        HttpPost createTopicPost = new HttpPost("https://typetalk.in/api/v1/topics");
        createTopicPost.addHeader("Authorization", "Bearer " + accessToken);
        createTopicPost.setEntity(new StringEntity(objectMapper.writeValueAsString(tokenParams)));
        HttpResponse response = client.execute(createTopicPost);
        InputStream inputStream = response.getEntity().getContent();
        String theString = convertStreamToString(inputStream);
        JSONObject obj = new JSONObject(theString);
        obj = obj.getJSONObject("topic");
        return objectMapper.readValue(obj.toString(), Topic.class);
    }

    public String postMessageToTopic(Topic topic, String message) throws IOException {
        List<NameValuePair> tokenParams = new ArrayList<NameValuePair>();
        tokenParams.add(new BasicNameValuePair("message", message));
        HttpPost createTopicPost = new HttpPost(String.format("https://typetalk.in/api/v1/topics/%s", topic.getId()));
        createTopicPost.addHeader("Authorization", "Bearer " + accessToken);
        createTopicPost.setEntity(new UrlEncodedFormEntity(tokenParams));
        HttpResponse response = client.execute(createTopicPost);
        InputStream inputStream = response.getEntity().getContent();
        String theString = convertStreamToString(inputStream);
        JSONObject obj = new JSONObject(theString);
        return obj.getJSONObject("post").getString("message");
    }

    private void startLisTening() throws IOException {
        reinitializeToken();
        new Thread(() -> {
            final String destUri = "wss://typetalk.in/api/v1/streaming";
            try {
                while (true) {
                    reinitializeToken();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + accessToken);
                    WebSocketClient ws = new WebSocketClient(URI.create(destUri));
                    ws.setHeaders(headers);
                    ws.connect();
                    String result = ws.recv();
                    if (result.length() > 0) {
                        JSONObject obj = new JSONObject(result);
                        if (obj.getString("type").equals("postMessage")) {
                            JSONObject data = obj.getJSONObject("data");
                            Topic topic = objectMapper.readValue(data.getJSONObject("topic").toString(), Topic.class);
                            Account account = objectMapper.readValue(data.getJSONObject("post").getJSONObject("account").toString(), Account.class);
                            String message = data.getJSONObject("post").getString("message");
                            applicationService.addDataIfImp(topic, account, message);
                        }
                    }
                }
            } catch (IOException t) {
                t.printStackTrace();
            }

        }).start();

    }


    public boolean isActive() {
        return ((System.currentTimeMillis() - lastGrant) < 120);
    }
}
