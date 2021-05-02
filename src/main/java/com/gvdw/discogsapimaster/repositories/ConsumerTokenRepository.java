package com.gvdw.discogsapimaster.repositories;

import com.gvdw.discogsapimaster.models.JpaOAuthConsumerToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Gullian Van Der Walt
 * Created at 12:52 on May, 2021
 */
public interface ConsumerTokenRepository extends JpaRepository<JpaOAuthConsumerToken, String> {
}
