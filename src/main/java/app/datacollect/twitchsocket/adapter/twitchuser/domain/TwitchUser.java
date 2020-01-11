package app.datacollect.twitchsocket.adapter.twitchuser.domain;

import app.datacollect.time.UTCDateTime;
import org.json.JSONObject;

public class TwitchUser {

  private final long id;
  private final String username;
  private final String displayName;
  private final UTCDateTime discoveredTime;
  private final String discoveredChannel;

  public TwitchUser(
      long id,
      String username,
      String displayName,
      UTCDateTime discoveredTime,
      String discoveredChannel) {
    this.id = id;
    this.username = username;
    this.displayName = displayName;
    this.discoveredTime = discoveredTime;
    this.discoveredChannel = discoveredChannel;
  }

  public TwitchUser(JSONObject jsonObject) {
    this.id = jsonObject.getLong("id");
    this.username = jsonObject.getString("username");
    this.displayName = jsonObject.getString("displayName");
    this.discoveredTime = UTCDateTime.of(jsonObject.getString("discoveredTime"));
    this.discoveredChannel = jsonObject.getString("discoveredChannel");
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public UTCDateTime getDiscoveredTime() {
    return discoveredTime;
  }

  public String getDiscoveredChannel() {
    return discoveredChannel;
  }
}
