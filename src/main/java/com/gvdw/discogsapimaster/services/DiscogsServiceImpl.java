package com.gvdw.discogsapimaster.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.gvdw.discogsapimaster.models.JpaOAuthConsumerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.InMemoryProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Gullian Van Der Walt
 * Created at 12:29 on May, 2021
 */
@Service
@PropertySource("classpath:api.properties") // API keys are hidden in second properties file.
public class DiscogsServiceImpl implements DiscogsService{

    @Value("${discogs.api-key}")
    private String API_KEY;
    @Value("${discogs.api-secret}")
    private String API_SECRET;

//    @Value("${shazam2discogs.test-access-token}")
//    private String DEV_OAUTH_TOKEN;
//    @Value("${shazam2discogs.test-access-secret}")
//    private String DEV_OAUTH_SECRET;

    private BaseProtectedResourceDetails resource;
    private HashMap<String, String> extraHeaderParams;
    private CoreOAuthConsumerSupport consumerSupport;
    private InMemoryProtectedResourceDetailsService protectedResourceDetailsService;
    private HashMap<String, BaseProtectedResourceDetails> resourceDetailsStore;

    // Discogs allows up to 60 requests per minute. This is found to be a save value
    // to prevent errors.
    private static final RateLimiter rateLimiter = RateLimiter.create(0.75);

    // Header
    @PostConstruct
    private void init() {
        resource = new BaseProtectedResourceDetails();
        extraHeaderParams = new HashMap<>();
        consumerSupport = new CoreOAuthConsumerSupport();
        protectedResourceDetailsService = new InMemoryProtectedResourceDetailsService();
        resourceDetailsStore = new HashMap<String, BaseProtectedResourceDetails>();

        resource.setId(APP_ID);
        resource.setConsumerKey(API_KEY);
        resource.setSharedSecret(new SharedConsumerSecretImpl(API_SECRET));
        resource.setRequestTokenURL(REQUEST_TOKEN_URL);
        resource.setUserAuthorizationURL(AUTHORIZATION_URL);
        resource.setAccessTokenURL(ACCESS_TOKEN_URL);

        extraHeaderParams.put("User-Agent", USER_AGENT);
        resource.setAdditionalParameters(extraHeaderParams);

        resourceDetailsStore.put(APP_ID, resource);
        protectedResourceDetailsService.setResourceDetailsStore(resourceDetailsStore);
        consumerSupport.setProtectedResourceDetailsService(protectedResourceDetailsService);
    }

    @Override
    public OAuthConsumerToken fetchRequestToken(String callbackURL) {
        rateLimiter.acquire();
        OAuthConsumerToken token = consumerSupport.getUnauthorizedRequestToken(resource, callbackURL);
        if (token.getAdditionalParameters().get("oauth_callback_confirmed").equals("true")) {
            return token;
        } else {
            throw new OAuthRequestFailedException("Callback URL not confirmed by OAuth provider. ");
        }
    }
    @Override
    public OAuthConsumerToken fetchAccessToken(OAuthConsumerToken oAuthConsumerToken, String oAuthVerifier) {
        rateLimiter.acquire();
        OAuthConsumerToken token = consumerSupport.getAccessToken(resource, oAuthConsumerToken, oAuthVerifier);
        return token;
    }

    @Override
    public String getUserName(JpaOAuthConsumerToken accessToken) {
        rateLimiter.acquire();
        String username = "";
        try {
            InputStream inputStream = consumerSupport.readProtectedResource(new URL(IDENTITY_CHECK_URL),
                    accessToken.toOAuthConsumerToken(), "GET");
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            String json = s.hasNext() ? s.next() : "";
            s.close();

            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> map = mapper.readValue(json, new TypeReference<HashMap<String, String>>() {
            });
            username = map.get("username");

        } catch (OAuthRequestFailedException e) {
            // TODO Not authenticated.
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO URL parsing error (not likely)
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Could not map JSON properly (not likely)
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return username;
    }

    @Override
    public String getUserSubmissions(JpaOAuthConsumerToken accessToken) {
        rateLimiter.acquire();
        String submissions = "";
        String json = "";
        try {
            InputStream inputStream = consumerSupport.readProtectedResource(new URL(USER_SUBMISSIONS_URL),
                    accessToken.toOAuthConsumerToken(), "GET");
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            json = s.hasNext() ? s.next() : "";
            System.out.println(json);
            s.close();

            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> map = mapper.readValue(json, new TypeReference<HashMap<String, String>>() {
            });
            System.out.println(map);
//            submissions = map.get

        } catch (OAuthRequestFailedException e) {
            // TODO Not authenticated.
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO URL parsing error (not likely)
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Could not map JSON properly (not likely)
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return json;
    }

//    @Override
//    public JpaOAuthConsumerToken createTestAccessToken(String sessionId) {
//        JpaOAuthConsumerToken oauthToken = new JpaOAuthConsumerToken();
//        oauthToken.setHttpSessionId(sessionId);
//        oauthToken.setAdditionalParameters(new HashMap<String, String>());
//        oauthToken.setOauthResourceId(DiscogsService.APP_ID);
//        oauthToken.setAccessToken(true);
//        oauthToken.setOauthToken(DEV_OAUTH_TOKEN);
//        oauthToken.setOauthSecret(DEV_OAUTH_SECRET);
//        oauthToken.setUsername(getUserName(oauthToken));
//        return oauthToken;
//    }
}
