package app.datacollect.twitchsocket.adapter.activechannel.resource;

public class ActiveChannelResource {

  private final long userId;
  private final String username;
  private final boolean joined;
  private final String joinedTime;

  public ActiveChannelResource(long userId, String username, boolean joined, String joinedTime) {
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

  public String getJoinedTime() {
    return joinedTime;
  }
}
