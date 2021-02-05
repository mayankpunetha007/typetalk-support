package com.nulab.data.typetalk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulab.config.ApplicationConfig;
import com.nulab.data.dto.ChatDetails;
import com.nulab.data.dto.ExternalData;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.inner.Account;
import com.nulab.data.pojo.inner.Group;
import com.nulab.data.pojo.inner.Space;
import com.nulab.data.pojo.inner.Topic;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

/**
 * Service to access Typetalk ApI
 */
@Service("typeTalkService")
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
    private ApplicationConfig applicationConfig;

    @Autowired
    private SimpMessagingTemplate template;

    private static Account adminUserAccount = null;


    public TypeTalkService() {
    }

    public TypeTalkService(String typettalkClientId, String typettalkClientSecret) {
        applicationConfig = new ApplicationConfig();
        applicationConfig.setTypetalkClientId(typettalkClientId);
        applicationConfig.setTypetalkClientSecret(typettalkClientSecret);
    }

    /**
     * Helping function to convert incoming response body to JSON
     * @param is
     * @return
     */
    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }



    /**
     * Initialize a access_token
     * Refresh the token if already initialized
     */
    public void initToken() {
        try {
            if (!initialized) {
                client = HttpClientBuilder.create().build();
                List<NameValuePair> tokenParams = new ArrayList<>();
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
                adminUserAccount = fetchMyAccount();
                applicationConfig.setMyAccount(adminUserAccount);
            } else {
                client = HttpClientBuilder.create().build();
                List<NameValuePair> tokenParams = new ArrayList<>();
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
        } catch (IOException e) {
            logger.error("Are you sure you are using the right keys in application.properties?", e);
        }


    }

    /**
     * Get organisations for the user identified by given client_key access_key pair
     * @return
     * @throws IOException
     */
    public List<Space> getOrganisations() throws IOException {
        List<Space> allOrganisations = new ArrayList<>();
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


    /**
     * Get groups in the organisation the user identified by given client_key access_key pair
     * @param orgKey
     * @return
     * @throws IOException
     */
    public List<Group> getOrganisationGroups(String orgKey) throws IOException {
        List<Group> allGroupsInOrganisation = new ArrayList<>();
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

    /**
     * Get all memebrs inside a given organisation identified by the given space_key
     * @param orgKey
     * @return
     * @throws IOException
     */
    public List<Account> getOrganisationMembers(String orgKey) throws IOException {
        List<Account> allAccountsInOrganisation = new ArrayList<>();
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

    /**
     * Create a topic with the given topic subject
     * @param topicSubject
     * @return
     * @throws IOException
     */
    public Topic createTopic(String topicSubject) throws IOException {
        client = HttpClientBuilder.create().build();
        Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("name", topicSubject);
        tokenParams.put("spaceKey", applicationConfig.getTypetalkOrganisation());
        /*for(int i=0;i<applicationConfig.getTypetalkSupportAccountId().size();i++){
            tokenParams.put("addAccountIds["+i+"]", applicationConfig.getTypetalkSupportAccountId());
        }*/
        tokenParams.put("addAccountIds", applicationConfig.getTypetalkSupportAccountId());
        tokenParams.put("addGroupIds", applicationConfig.getTypetalkSupportGroups());
        HttpPost createTopicPost = new HttpPost("https://typetalk.in/api/v1/topics");
        createTopicPost.addHeader("Authorization", "Bearer " + accessToken);
        createTopicPost.setEntity(new StringEntity(objectMapper.writeValueAsString(tokenParams), ContentType.create("application/json")));
        HttpResponse response = client.execute(createTopicPost);
        InputStream inputStream = response.getEntity().getContent();
        String theString = convertStreamToString(inputStream);
        JSONObject obj = new JSONObject(theString);
        obj = obj.getJSONObject("topic");
        return objectMapper.readValue(obj.toString(), Topic.class);
    }

    /**
     * Post a message to the given topic
     * The message is posted as the user identified by the clien_key pass_key pair
     * Therfore make sure it is not a support account in applicatio.properties
     * Inorder for customer-support communication to take place
     * @param topic
     * @param message
     * @return
     * @throws IOException
     */
    public String postMessageToTopic(Topic topic, String message) throws IOException {
        client = HttpClientBuilder.create().build();
        List<NameValuePair> tokenParams = new ArrayList<>();
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

    /**
     * This method listens to type talk Streaming api and makes sure that
     * Any relavent data is but to external Data and streamed to clent
     * After being moved to chat detials
     */
    private void startLisTening() {
        new Thread(() -> {
            final String destUri = "wss://typetalk.in/api/v1/streaming";
            WebSocketClient ws = null;
            while (true) {
                try {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + accessToken);
                    ws = new WebSocketClient(URI.create(destUri));
                    ws.setHeaders(headers);
                    ws.connect();
                    while (isActive()) {
                        String result = ws.recv();
                        if (result.length() > 0) {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getString("type").equals("postMessage")) {
                                JSONObject data = obj.getJSONObject("data");
                                Topic topic = objectMapper.readValue(data.getJSONObject("topic").toString(), Topic.class);
                                Account account = objectMapper.readValue(data.getJSONObject("post").getJSONObject("account").toString(), Account.class);
                                String message = data.getJSONObject("post").getString("message");
                                addDataIfImp(topic, account, message);
                            }
                            Map<String, ExternalData> allMessages = fetchAndAddAllImpData();
                            for (Map.Entry<String, ExternalData> e : allMessages.entrySet()) {
                                logger.info("sendiing to /topic/" + e.getKey());
                                template.convertAndSend("/talk/"+e.getKey(), e.getValue());
                            }
                        }
                    }

                } catch (IOException t) {
                    logger.error(t.getMessage(), t);
                    try {
                        this.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startLisTening();
                } finally {
                    if (ws != null) {
                        try {
                            ws.close();
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                            ws = null;
                        }
                    }

                }

            }
        }).start();

    }

    /**
     * Add incoming streaming data to the External Data table
     * @param topic
     * @param account
     * @param message
     */
    private void addDataIfImp(Topic topic, Account account, String message) {


    }

    /**
     * Move data from external Data to Chat Data and return with client id and auth key url contructed
     * @return
     */
    private Map<String, ExternalData> fetchAndAddAllImpData() {
        Map<String, ExternalData> urlDataMapping = new HashMap<>();
        return urlDataMapping;
    }

    /**
     * Get account that will be used for customer based on client key and pass
     * Since client is God! :D and I could not figure out a way to get a
     * guest account key from the document we will post all meesages from the main account
     * Due to this problem there is alos a limitation that owner should never ever post on support groups
     * @return
     */
    private Account fetchMyAccount() throws IOException {
        client = HttpClientBuilder.create().build();
        List<NameValuePair> tokenParams = new ArrayList<>();
        HttpGet get = new HttpGet("https://typetalk.in/api/v1/profile");
        get.addHeader("Authorization", "Bearer " + accessToken);
        HttpResponse response = client.execute(get);
        InputStream inputStream = response.getEntity().getContent();
        String theString = convertStreamToString(inputStream);
        JSONObject object = new JSONObject(theString);
        return objectMapper.readValue(object.getJSONObject("account").toString(), Account.class);
    }

    /**
     * Is acces_token about to expire
     * @return
     */
    private boolean isActive() {
        return ((System.currentTimeMillis() - lastGrant) < expiry * 1000 - 120000);
    }
}
