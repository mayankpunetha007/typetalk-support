package com.nulab.data.typetalk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.nulab.data.pojo.inner.Account;
import com.nulab.data.pojo.inner.Group;
import com.nulab.data.pojo.inner.Space;
import com.nulab.data.pojo.inner.Topic;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.aspectj.lang.annotation.Before;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by mayan on 11/20/2016.
 */
@Service("typeTalkService")
@ConfigurationProperties
public class TypeTalkService {

    private String typettalkClientId;

    private String typettalkClientSecret;

    private static boolean initialized = false;

    private String accessToken;

    private List<String> typettalkSupportGroups;

    private List<String> typettalkSupportAccountId;

    private String typettalkOrganisation;

    private ObjectMapper objectMapper = new ObjectMapper();

    private CloseableHttpClient client;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private long lastGrant;

    private long expiry;

    private String refreshToken;




    public TypeTalkService(String typettalkClientId, String typettalkClientSecret){
        this.typettalkClientId = typettalkClientId;
        this.typettalkClientSecret = typettalkClientSecret;
    }

    public TypeTalkService() {
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Before("com.nulab.data.typetalk.*")
    void reinitializeToken() throws IOException {
        if(System.currentTimeMillis() - lastGrant < 120){
            init();
        }
    }

    @PostConstruct
    public void init() throws IOException {
        if(!initialized) {
            client = HttpClientBuilder.create().build();
            List<NameValuePair> tokenParams = new ArrayList<NameValuePair>();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            tokenParams.add(new BasicNameValuePair("client_id", getTypettalkClientId()));
            tokenParams.add(new BasicNameValuePair("client_secret", getTypettalkClientSecret()));
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
            refreshToken  = obj.getString("refresh_token");
            expiry = obj.getLong("expires_in");
            lastGrant =  System.currentTimeMillis();
            initialized = true;
        }else{
            client = HttpClientBuilder.create().build();
            List<NameValuePair> tokenParams = new ArrayList<NameValuePair>();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            tokenParams.add(new BasicNameValuePair("client_id", getTypettalkClientId()));
            tokenParams.add(new BasicNameValuePair("client_secret", getTypettalkClientSecret()));
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
            refreshToken  = obj.getString("refresh_token");
            expiry = obj.getLong("expires_in");
            lastGrant =  System.currentTimeMillis();
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
        for(int i=0; i< jsonArray.length();i++){
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
        for(int i=0; i< jsonArray.length();i++){
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
        for(int i=0; i< jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);
            allAccountsInOrganisation.add(objectMapper.readValue(object.toString(), Account.class));
        }
        return allAccountsInOrganisation;
    }

    public Topic createTopic(String topicSubject) throws IOException {
        List<NameValuePair> tokenParams = new ArrayList<NameValuePair>();
        tokenParams.add(new BasicNameValuePair("name", topicSubject));
        tokenParams.add(new BasicNameValuePair("spaceKey", typettalkOrganisation));
        int i=0;
        if(typettalkSupportAccountId !=null)
        for(String s: typettalkSupportAccountId){
            tokenParams.add(new BasicNameValuePair(String.format("addAccountIds[%d] ", i), s));
            i++;
        }
        i=0;
        if(typettalkSupportGroups!=null)
        for(String s: typettalkSupportGroups){
            tokenParams.add(new BasicNameValuePair(String.format("addGroupIds[%d] ", i), s));
            i++;
        }
        HttpPost createTopicPost = new HttpPost("https://typetalk.in/api/v1/topics");
        createTopicPost.addHeader("Authorization", "Bearer " + accessToken);
        createTopicPost.setEntity(new UrlEncodedFormEntity(tokenParams));
        HttpResponse response = client.execute(createTopicPost);
        InputStream inputStream = response.getEntity().getContent();
        String theString = convertStreamToString(inputStream);
        JSONObject obj = new JSONObject(theString);
        obj = obj.getJSONObject("topic");
        return objectMapper.readValue(obj.toString(), Topic.class);
    }

    public boolean postMessageToTopic(Topic topic, String message) throws IOException {
        List<NameValuePair> tokenParams = new ArrayList<NameValuePair>();
        tokenParams.add(new BasicNameValuePair("message", message));
        HttpPost createTopicPost = new HttpPost(String.format("https://typetalk.in/api/v1/topics/%s", topic.getId()));
        createTopicPost.addHeader("Authorization", "Bearer " + accessToken);
        createTopicPost.setEntity(new UrlEncodedFormEntity(tokenParams));
        HttpResponse response = client.execute(createTopicPost);
        InputStream inputStream = response.getEntity().getContent();
        String theString = convertStreamToString(inputStream);
        try{
            JSONObject obj = new JSONObject(theString);
            return obj.getJSONObject("post").getString("message").length() > 0;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public void startLisTening() throws IOException {
            reinitializeToken();
            String destUri = "wss://typetalk.in/api/v1/streaming";
            WebSocketClient client = new WebSocketClient();
            SimpleEchoSocket socket = new SimpleEchoSocket();
            try {
                client.start();
                URI echoUri = new URI(destUri);
                ClientUpgradeRequest request = new ClientUpgradeRequest();
                request.setHeader("Authorization", "Bearer " + accessToken);
                client.connect(socket, echoUri, request);
                System.out.printf("Connecting to : %s%n", echoUri);

                // wait for closed socket connection.
                socket.awaitClose(5, TimeUnit.SECONDS);
            } catch (IOException t) {
                t.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    client.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    public String getTypettalkClientId() {
        return typettalkClientId;
    }

    public void setTypettalkClientId(String typettalkClientId) {
        this.typettalkClientId = typettalkClientId;
    }

    public String getTypettalkClientSecret() {
        return typettalkClientSecret;
    }

    public void setTypettalkClientSecret(String typettalkClientSecret) {
        this.typettalkClientSecret = typettalkClientSecret;
    }

    public List<String> getTypettalkSupportGroups() {
        return typettalkSupportGroups;
    }

    public void setTypettalkSupportGroups(String typettalkSupportGroups) {
        this.typettalkSupportGroups = Arrays.asList(typettalkSupportGroups.split(","));
    }

    public String getTypettalkOrganisation() {
        return typettalkOrganisation;
    }

    public void setTypettalkOrganisation(String typettalkOrganisation) {
        this.typettalkOrganisation = typettalkOrganisation;
    }

    public List<String> getTypettalkSupportAccountId() {
        return typettalkSupportAccountId;
    }

    public void setTypettalkSupportAccountId(String typettalkSupportAccountId) {
        this.typettalkSupportAccountId = Arrays.asList(typettalkSupportAccountId.split(","));
    }

    public static void main(String args[]) throws IOException {
        TypeTalkService typeTalkService = new TypeTalkService("", "");
        typeTalkService.init();
        typeTalkService.startLisTening();
    }
}
