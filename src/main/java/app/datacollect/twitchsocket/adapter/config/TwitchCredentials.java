package app.datacollect.twitchsocket.adapter.config;

public class TwitchCredentials {
  private String twitchHost;
  private int twitchPort;
  private String username;
  private String token;

  public String getTwitchHost() {
    return twitchHost;
  }

  public void setTwitchHost(String twitchHost) {
    this.twitchHost = twitchHost;
  }

  public int getTwitchPort() {
    return twitchPort;
  }

  public void setTwitchPort(int twitchPort) {
    this.twitchPort = twitchPort;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
