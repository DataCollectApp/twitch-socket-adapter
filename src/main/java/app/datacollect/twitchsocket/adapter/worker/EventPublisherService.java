package app.datacollect.twitchsocket.adapter.worker;

import app.datacollect.twitchdata.feed.events.EventData;
import app.datacollect.twitchsocket.adapter.config.FeedProperties;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

  private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);

  private static String basicAuth(String username, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
  }

  public boolean publishEvent(
      HttpClient httpClient, FeedProperties feedProperties, EventData eventData, UUID eventId) {
    final JSONObject jsonObject =
        new JSONObject()
            .put("author", feedProperties.getAuthor())
            .put("eventType", eventData.getEventType().name())
            .put("objectType", eventData.getObjectType().name())
            .put("version", eventData.getVersion().name())
            .put("content", eventData.toJson().toString());
    final JSONArray data = new JSONArray();
    data.put(jsonObject);

    final URI uri = URI.create(feedProperties.getUrl());
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofMillis(10000))
            .header(
                "Authorization",
                basicAuth(feedProperties.getUsername(), feedProperties.getPassword()))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(data.toString()))
            .build();

    final HttpResponse<String> response;
    try {
      response = httpClient.send(request, BodyHandlers.ofString());
    } catch (InterruptedException | IOException ex) {
      logger.error("Encountered exception while trying to publish event with id '{}'", eventId, ex);
      return false;
    }

    if (!(response.statusCode() == HttpStatus.ACCEPTED.value())) {
      logger.error(
          "Received unexpected status code '{}' while trying to publish event with id '{}'",
          response.statusCode(),
          eventId);
      if (response.body() != null) {
        logger.info("Body of error message was '{}'", response.body());
      }
      return false;
    }
    logger.info(
        "Successfully published event with id '{}' to '{}' feed",
        eventId,
        feedProperties.getResource());
    return true;
  }

  public List<UUID> publishEvents(
      HttpClient httpClient, FeedProperties feedProperties, List<EventData> eventDataList) {
    final JSONArray data = new JSONArray();
    final List<JSONObject> objects =
        eventDataList.stream()
            .map(
                e ->
                    new JSONObject()
                        .put("author", feedProperties.getAuthor())
                        .put("eventType", e.getEventType().name())
                        .put("objectType", e.getObjectType().name())
                        .put("version", e.getVersion().name())
                        .put("content", e.toJson().toString()))
            .collect(Collectors.toList());
    objects.forEach(data::put);

    final URI uri = URI.create(feedProperties.getUrl());
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofMillis(10000))
            .header(
                "Authorization",
                basicAuth(feedProperties.getUsername(), feedProperties.getPassword()))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(data.toString()))
            .build();

    final HttpResponse<String> response;
    try {
      response = httpClient.send(request, BodyHandlers.ofString());
    } catch (InterruptedException | IOException ex) {
      logger.error("Encountered exception while trying to publish events", ex);
      return Collections.emptyList();
    }

    if (!(response.statusCode() == HttpStatus.ACCEPTED.value())) {
      logger.error(
          "Received unexpected status code '{}' while trying to publish events",
          response.statusCode());
      if (response.body() != null) {
        logger.info("Body of error message was '{}'", response.body());
      }
      return Collections.emptyList();
    }
    final JSONArray responseArray = new JSONArray(response.body());
    final List<UUID> publishedIds = new ArrayList<>();
    responseArray.forEach(publishedId -> publishedIds.add(UUID.fromString((String) publishedId)));
    logger.info(
        "Successfully published '{}' events to '{}' feed",
        eventDataList.size(),
        feedProperties.getResource());
    return publishedIds;
  }
}
