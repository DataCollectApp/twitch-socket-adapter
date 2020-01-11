package app.datacollect.twitchsocket.adapter.activechannel.service;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchsocket.adapter.activechannel.domain.ActiveChannel;
import app.datacollect.twitchsocket.adapter.activechannel.repository.ActiveChannelRepository;
import app.datacollect.twitchsocket.adapter.twitchconnection.service.TwitchConnection;
import app.datacollect.twitchsocket.adapter.twitchuser.domain.TwitchUser;
import app.datacollect.twitchsocket.adapter.twitchuser.service.TwitchUserSearchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ActiveChannelService {

  private static final Logger logger = LoggerFactory.getLogger(ActiveChannelService.class);

  private final ActiveChannelRepository repository;
  private final TwitchConnection twitchConnection;
  private final TwitchUserSearchService twitchUserSearchService;

  public ActiveChannelService(
      ActiveChannelRepository repository,
      TwitchConnection twitchConnection,
      TwitchUserSearchService twitchUserSearchService) {
    this.repository = repository;
    this.twitchConnection = twitchConnection;
    this.twitchUserSearchService = twitchUserSearchService;
  }

  public void saveActiveChannels(List<Long> userIds) {
    logger.debug("Saving '{}' active channels", userIds.size());
    for (Long userId : userIds) {
      saveActiveChannel(userId);
    }
    logger.info("Saved '{}' active channels", userIds.size());
  }

  public void saveActiveChannel(long userId) {
    logger.debug("Saving active channel with user id '{}'", userId);
    final TwitchUser twitchUser =
        twitchUserSearchService
            .getTwitchUser(userId)
            .orElseThrow(
                () -> new IllegalStateException("Twitch user not found with user id " + userId));
    repository.saveActiveChannel(twitchUser.getId(), twitchUser.getUsername());
    logger.info(
        "Saved active channel with user id '{}' and username '{}'",
        twitchUser.getId(),
        twitchUser.getUsername());
  }

  public List<ActiveChannel> getActiveChannels() {
    return repository.getActiveChannels();
  }

  public List<ActiveChannel> refreshUsernames() {
    logger.debug("Refreshing usernames of active channels");
    final Map<Long, ActiveChannel> activeChannels =
        getActiveChannels().stream()
            .collect(Collectors.toMap(ActiveChannel::getUserId, Function.identity()));
    final List<TwitchUser> twitchUsers =
        twitchUserSearchService.getTwitchUsers(
            activeChannels.values().stream()
                .map(ActiveChannel::getUserId)
                .collect(Collectors.toList()));
    final List<ActiveChannel> updatedActiveChannels = new ArrayList<>();
    twitchUsers.forEach(
        twitchUser -> {
          final ActiveChannel activeChannel = activeChannels.get(twitchUser.getId());
          if (!twitchUser.getUsername().equals(activeChannel.getUsername())) {
            repository.updateActiveChannel(twitchUser.getId(), twitchUser.getUsername());
            twitchConnection.send("JOIN #" + twitchUser.getUsername());
            updatedActiveChannels.add(activeChannel);
          }
        });
    logger.info(
        "Refreshed usernames of '{}'/'{}' active channels",
        updatedActiveChannels.size(),
        activeChannels.size());
    return updatedActiveChannels;
  }

  @Scheduled(fixedDelay = 30000)
  public void processNewChannels() {
    if (!twitchConnection.getBotUser().isLoggedIn()) {
      logger.warn("User not logged in. Waiting 30 seconds to join channels.");
      return;
    }
    List<ActiveChannel> newActiveChannels = repository.getActiveChannels(false);
    newActiveChannels.forEach(
        newActiveChannel -> {
          twitchConnection.send("JOIN #" + newActiveChannel.getUsername());
          repository.updateActiveChannel(newActiveChannel.getUserId(), true, UTCDateTime.now());
        });
  }
}
