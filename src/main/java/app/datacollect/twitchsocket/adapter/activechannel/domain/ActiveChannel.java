package app.datacollect.twitchsocket.adapter.activechannel.domain;

import app.datacollect.time.UTCDateTime;
import java.util.Optional;

public class ActiveChannel {

  private final long userId;
  private final String username;
  private final boolean joined;
  private final Optional<UTCDateTime> joinedTime;

  public ActiveChannel(
      long userId, String username, boolean joined, Optional<UTCDateTime> joinedTime) {
    this.userId = userId;
    this.username = username;
    this.joined = joined;
    this.joinedTime = joinedTime;
  }

  public long getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public boolean hasJoined() {
    return joined;
  }

  public Optional<UTCDateTime> getJoinedTime() {
    return joinedTime;
  }
}
