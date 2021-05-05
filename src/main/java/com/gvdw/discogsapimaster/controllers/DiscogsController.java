package com.gvdw.discogsapimaster.controllers;

import com.gvdw.discogsapimaster.models.JpaOAuthConsumerToken;
import com.gvdw.discogsapimaster.repositories.ConsumerTokenRepository;
import com.gvdw.discogsapimaster.services.DiscogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

/**
 * @author Gullian Van Der Walt
 * Created at 12:48 on May, 2021
 */
@Controller
public class DiscogsController {

    private static final String OAUTH_CALLBACK = "/oauthCallback";
    private static final Logger logger = LoggerFactory.getLogger(DiscogsController.class);

    // Constructor Injection
    private final DiscogsService discogsService;
    private final ConsumerTokenRepository tokenStore;
    @Autowired
    public DiscogsController(DiscogsService discogsService, ConsumerTokenRepository tokenStore) {
        this.discogsService = discogsService;
        this.tokenStore = tokenStore;
    }

    @GetMapping("/")
    public ModelAndView getHome() {

        return new ModelAndView("index");
    }

    @GetMapping("/login")
    public RedirectView login1(HttpSession session) {
        RedirectView rv = new RedirectView();
        String appBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        JpaOAuthConsumerToken testToken = tokenStore.findAll().get(0);
        OAuthConsumerToken requestToken = discogsService.fetchRequestToken(appBaseUrl + OAUTH_CALLBACK);
        JpaOAuthConsumerToken jpaToken = new JpaOAuthConsumerToken(session.getId(), requestToken);
        tokenStore.save(jpaToken);
                rv.setUrl(DiscogsService.AUTHORIZATION_URL + "?oauth_token=" + requestToken.getValue());
        return rv;
    }

    @GetMapping(OAUTH_CALLBACK)
    public ModelAndView oauthCallback(@RequestParam Map<String, String> requestParams, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        if (requestParams.containsKey("denied")) {
            mv.setViewName("index");
        } else if (requestParams.containsKey("oauth_token") && requestParams.containsKey("oauth_verifier")) {
            JpaOAuthConsumerToken requestToken = tokenStore.findById(session.getId()).orElseThrow();
            // TODO check if request token is already an access token (that means OAuth has been completed).
                        OAuthConsumerToken accessToken = discogsService.fetchAccessToken(requestToken.toOAuthConsumerToken(),
                    requestParams.get("oauth_verifier"));
            JpaOAuthConsumerToken jpaToken = new JpaOAuthConsumerToken(session.getId(), accessToken);
            jpaToken.setUsername(discogsService.getUserName(jpaToken));
            tokenStore.save(jpaToken);
            mv.addObject("username", jpaToken.getUsername());
            mv.setViewName("index");
        } else {
            logger.error("Session - " + session.getId() + ", unrecognised OAuth response from Discogs - \n"
                    + requestParams.toString());
            throw new IllegalArgumentException("Unrecognised OAuth response from Discogs");
        }
        return mv;
    }
    @GetMapping("/checkUserAuth")
    public ModelAndView checkUserAuth(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        String sessionId = session.getId();
        mv.addObject("isAuthenticated", authCheck());
        mv.setViewName("index");
        return mv;
    }
    @GetMapping("/getUserSubmissions")
    public ModelAndView getUserSubmissions(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        String sessionId = session.getId();
        if(authCheck()){
            JpaOAuthConsumerToken userToken = tokenStore.findAll().get(0);
            mv.addObject("submissions", discogsService.getUserSubmissions(userToken));
        }

        mv.setViewName("index");
        return mv;
    }

    @GetMapping("/getUserCollection")
    public ModelAndView getUserCollection(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        String sessionId = session.getId();
        if(authCheck()){
            JpaOAuthConsumerToken userToken = tokenStore.findAll().get(0);
            mv.addObject("collection", discogsService.getUserCollection(userToken));
        }
        mv.setViewName("index");
        return mv;
    }

    @GetMapping("/release")
    public ModelAndView getRelease(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        String sessionId = session.getId();
        if(authCheck()){
            JpaOAuthConsumerToken userToken = tokenStore.findAll().get(0);
            mv.addObject("release", discogsService.getRelease(userToken));
        }
        mv.setViewName("index");
        return mv;
    }

    /**
     * Check to see if a user is authenticated before we send requests.
     */
    private boolean authCheck() {
        Optional<JpaOAuthConsumerToken> userToken = Optional.ofNullable(tokenStore.findAll().get(0));
        if (userToken.isPresent()) {
            if (userToken.get().isAccessToken()) {
                return true;
            } else {
                throw new IllegalStateException(
                        "OAuth process not complete (it was started, but we have no access token)");
            }
        } else {
            throw new IllegalStateException("User not found in OAuth data store. ");
        }
    }
}
