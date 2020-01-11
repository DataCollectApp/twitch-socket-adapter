package app.datacollect.twitchsocket.adapter.config;

import app.datacollect.twitchsocket.adapter.twitchconnection.service.TwitchConnection;
import java.io.IOException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class BaseConfig implements SchedulingConfigurer {

  @Bean
  @ConfigurationProperties("twitch-socket-adapter.credentials")
  public TwitchCredentials twitchCredentials() {
    return new TwitchCredentials();
  }

  @Bean
  public TwitchConnection twitchConnection(TwitchCredentials twitchCredentials) throws IOException {
    TwitchConnection twitchConnection = new TwitchConnection(twitchCredentials);
    twitchConnection.login();
    return twitchConnection;
  }

  @Bean("chatMessageFeedProperties")
  @ConfigurationProperties("twitch-data-feed.publisher.chat-message")
  public FeedProperties chatMessageFeedProperties() {
    return new FeedProperties();
  }

  @Bean("connectionFeedProperties")
  @ConfigurationProperties("twitch-data-feed.publisher.connection")
  public FeedProperties connectionFeedProperties() {
    return new FeedProperties();
  }

  @Bean("hostFeedProperties")
  @ConfigurationProperties("twitch-data-feed.publisher.host")
  public FeedProperties hostFeedProperties() {
    return new FeedProperties();
  }

  @Bean("punishmentFeedProperties")
  @ConfigurationProperties("twitch-data-feed.publisher.punishment")
  public FeedProperties punishmentFeedProperties() {
    return new FeedProperties();
  }

  @Bean
  @ConfigurationProperties("twitch-user-search")
  public TwitchUserSearchProperties twitchUserSearchProperties() {
    return new TwitchUserSearchProperties();
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(10);
    taskScheduler.initialize();
    taskRegistrar.setTaskScheduler(taskScheduler);
  }
}
