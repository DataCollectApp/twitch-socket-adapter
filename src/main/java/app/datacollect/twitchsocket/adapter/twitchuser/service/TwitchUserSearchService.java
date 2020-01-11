package app.datacollect.twitchsocket.adapter.twitchuser.service;

import app.datacollect.twitchsocket.adapter.config.TwitchUserSearchProperties;
import app.datacollect.twitchsocket.adapter.twitchuser.domain.TwitchUser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TwitchUserSearchService {

  private static final Logger logger = LoggerFactory.getLogger(TwitchUserSearchService.class);
  private final TwitchUserSearchProperties properties;

  public TwitchUserSearchService(TwitchUserSearchProperties properties) {
    this.properties = properties;
  }

  private static String basicAuth(String username, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
  }

  public Optional<TwitchUser> getTwitchUser(long userId) throws IllegalStateException {
    final HttpClient httpClient = HttpClient.newHttpClient();
    final URI uri = URI.create(properties.getUrl());
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofMillis(10000))
            .header("Authorization", basicAuth(properties.getUsername(), properties.getPassword()))
            .header("Content-Type", "application/json")
            .POST(
                BodyPublishers.ofString(
                    new JSONObject().put("userIds", List.of(userId)).toString()))
            .build();

    final HttpResponse<String> response;
    try {
      response = httpClient.send(request, BodyHandlers.ofString());
    } catch (InterruptedException | IOException ex) {
      logger.error("Encountered exception while trying get twitch user with id '{}'", userId, ex);
      throw new IllegalStateException(ex);
    }
    if (response.statusCode() == HttpStatus.OK.value()) {
      final JSONArray twitchUsers = new JSONArray(response.body());
      if (twitchUsers.length() == 1) {
        return Optional.of(new TwitchUser(twitchUsers.getJSONObject(0)));
      }
    }
    return Optional.empty();
  }

  public List<TwitchUser> getTwitchUsers(List<Long> userIds) throws IllegalStateException {
    final HttpClient httpClient = HttpClient.newHttpClient();
    final URI uri = URI.create(properties.getUrl());
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofMillis(10000))
            .header("Authorization", basicAuth(properties.getUsername(), properties.getPassword()))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(new JSONObject().put("userIds", userIds).toString()))
            .build();

    final HttpResponse<String> response;
    try {
      response = httpClient.send(request, BodyHandlers.ofString());
    } catch (InterruptedException | IOException ex) {
      logger.error(
          "Encountered exception while trying get twitch users with id '{}'",
          userIds.stream().map(Object::toString).collect(Collectors.joining(",")),
          ex);
      throw new IllegalStateException(ex);
    }
    if (response.statusCode() != HttpStatus.OK.value()) {
      throw new IllegalStateException(
          "Received unexpected status code "
              + response.statusCode()
              + " when fetching twitch users from "
              + properties.getUrl());
    }
    final List<TwitchUser> twitchUsers = new ArrayList<>();
    new JSONArray(response.body())
        .forEach(object -> twitchUsers.add(new TwitchUser((JSONObject) object)));
    return twitchUsers;
  }
}
