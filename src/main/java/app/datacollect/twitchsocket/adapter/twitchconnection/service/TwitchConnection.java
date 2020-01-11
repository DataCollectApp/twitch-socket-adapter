package app.datacollect.twitchsocket.adapter.twitchconnection.service;

import app.datacollect.twitchsocket.adapter.config.TwitchCredentials;
import app.datacollect.twitchsocket.adapter.user.domain.User;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchConnection {
  private static final Logger logger = LoggerFactory.getLogger(TwitchConnection.class);

  private final BufferedReader inputReader;
  private final DataOutputStream outputStream;
  private final User botUser;

  public TwitchConnection(TwitchCredentials credentials) throws IOException {
    this.botUser = new User(credentials.getUsername(), credentials.getToken());
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(credentials.getTwitchHost(), credentials.getTwitchPort()));

    this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.outputStream = new DataOutputStream(socket.getOutputStream());
  }

  public User getBotUser() {
    return botUser;
  }

  public void login() {
    if (botUser.isLoggedIn()) {
      logger.error("Failed to login: A user is already logged in");
      return;
    }
    send("PASS " + botUser.getToken());
    send("NICK " + botUser.getUsername());
  }

  public void send(String message) {
    try {
      outputStream.writeBytes(message + "\n");
      logOutput(message);
    } catch (IOException ex) {
      logger.error("Exception occurred while writing to socket", ex);
    }
  }

  public String read() {
    try {
      return inputReader.readLine();
    } catch (IOException ex) {
      logger.error("Exception occurred while reading from socket", ex);
      return null;
    }
  }

  private void logOutput(String output) {
    logger.info("TO TWITCH << " + output);
  }
}
