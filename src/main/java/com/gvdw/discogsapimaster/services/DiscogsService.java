package com.gvdw.discogsapimaster.services;

import com.gvdw.discogsapimaster.models.JpaOAuthConsumerToken;
import com.gvdw.discogsapimaster.models.Release;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;

import java.util.ArrayList;

/**
 * @author Gullian Van Der Walt
 * Created at 12:29 on May, 2021
 */
public interface DiscogsService {

    public final String APP_ID = "discogs";
    public final String REQUEST_TOKEN_URL = "https://api.discogs.com/oauth/request_token";
    public final String AUTHORIZATION_URL = "https://www.discogs.com/oauth/authorize";
    public final String ACCESS_TOKEN_URL = "https://api.discogs.com/oauth/access_token";
    public final String IDENTITY_CHECK_URL = "https://api.discogs.com/oauth/identity";
    public final String USER_SUBMISSIONS_URL = "https://api.discogs.com//users/gullian101/submissions";
    public final String USER_AGENT = "discogs-api-master/0.0.1 +http:/gvdw.com";
    public final String DISCOGS_SEARCH_URL = "https://api.discogs.com/database/search";
    public final String DISCOGS_USERS_URL = "https://api.discogs.com/users";

    OAuthConsumerToken fetchRequestToken(String callbackURL);

    OAuthConsumerToken fetchAccessToken(OAuthConsumerToken oAuthConsumerToken, String oAuthVerifier);

//    /**
//     * Returns a list of Releases matching the given search parameters.
//     *
//     * @param currTag
//     * @param accessToken
//     * @return
//     */
//    ArrayList<Release> getReleaseList(JpaOAuthConsumerToken accessToken);

    /**
     * Strips illegal characters from the query string and returns the corrected
     * version.
     *
     * @param uriString
     * @return
     */
    public static String stripIllegalQueryChars(String uriString) {
        int queryStartIndex = uriString.indexOf('?') + 1;
        String params = uriString.substring(queryStartIndex);
        return uriString.substring(0, queryStartIndex) + params.replace("?", "%3F").replace(";", "%3B");
    }

    /**
     * Get the Discogs username that the given access token relates to.
     * @param accessToken
     * @return
     */
    String getUserName(JpaOAuthConsumerToken accessToken);

    String getUserSubmissions(JpaOAuthConsumerToken accessToken);
}
