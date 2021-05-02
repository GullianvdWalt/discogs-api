package com.gvdw.discogsapimaster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.*;

/**
 * @author Gullian Van Der Walt
 * Created at 12:18 on May, 2021
 */
@Configuration
@EnableAsync
public class AppConfig {
    @Bean // Used to store a set of sessionIds that have sent 'cancel' requests.
    public Set<String> cancelTaskSessionIds() {
        return Collections.synchronizedSet(new HashSet<>());
    }

    @Bean // Used to store a map of running task progress for each sessionID.
    public Map<String, Integer> taskProgress() {
        return Collections.synchronizedMap(new HashMap<>());
    }
}
