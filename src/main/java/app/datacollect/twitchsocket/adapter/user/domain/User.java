package app.datacollect.twitchsocket.adapter.user.domain;

public class User {
  private final String username;
  private final String token;
  private boolean loggedIn;

  public User(String username, String token) {
    this.username = username;
    this.token = token;
    this.loggedIn = false;
  }

  public String getUsername() {
    return username;
  }

  public String getToken() {
    return token;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }
}
