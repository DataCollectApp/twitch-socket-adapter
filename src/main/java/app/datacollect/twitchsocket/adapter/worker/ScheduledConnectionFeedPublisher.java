package app.datacollect.twitchsocket.adapter.worker;

import app.datacollect.twitchdata.feed.events.EventData;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import app.datacollect.twitchsocket.adapter.config.FeedProperties;
import app.datacollect.twitchsocket.adapter.event.assembler.UserJoinEventV1Assembler;
import app.datacollect.twitchsocket.adapter.event.assembler.UserLeaveEventV1Assembler;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.event.service.EventService;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ScheduledConnectionFeedPublisher {

  private final HttpClient httpClient;
  private final FeedProperties feedProperties;
  private final EventService eventService;
  private final EventPublisherService eventPublisherService;
  private final UserJoinEventV1Assembler userJoinEventV1Assembler;
  private final UserLeaveEventV1Assembler userLeaveEventV1Assembler;

  public ScheduledConnectionFeedPublisher(
      @Qualifier("connectionFeedProperties") FeedProperties feedProperties,
      EventService eventService,
      EventPublisherService eventPublisherService,
      UserJoinEventV1Assembler userJoinEventV1Assembler,
      UserLeaveEventV1Assembler userLeaveEventV1Assembler) {
    this.httpClient = HttpClient.newHttpClient();
    this.feedProperties = feedProperties;
    this.eventService = eventService;
    this.eventPublisherService = eventPublisherService;
    this.userJoinEventV1Assembler = userJoinEventV1Assembler;
    this.userLeaveEventV1Assembler = userLeaveEventV1Assembler;
  }

  @Scheduled(fixedDelay = 5000)
  public void process() {
    processBatch();
  }

  @Transactional
  public void processBatch() {
    final List<RawEvent> rawEvents =
        eventService.getConnectionEvents(feedProperties.getBatchSize());
    if (rawEvents.isEmpty()) {
      return;
    }
    final List<UUID> eventIds = new ArrayList<>();
    final List<EventData> connectionEvents =
        rawEvents.stream()
            .map(
                rawEvent -> {
                  if (rawEvent.getEventType() == EventType.USER_JOIN_SNAPSHOT
                      && rawEvent.getObjectType() == ObjectType.USER_JOIN
                      && rawEvent.getVersion() == Version.V1) {
                    eventIds.add(rawEvent.getId());
                    return userJoinEventV1Assembler.assemble(rawEvent);
                  } else if (rawEvent.getEventType() == EventType.USER_LEAVE_SNAPSHOT
                      && rawEvent.getObjectType() == ObjectType.USER_LEAVE
                      && rawEvent.getVersion() == Version.V1) {
                    eventIds.add(rawEvent.getId());
                    return userLeaveEventV1Assembler.assemble(rawEvent);
                  } else {
                    throw new IllegalStateException(
                        "Invalid event with type "
                            + rawEvent.getEventType()
                            + " object type "
                            + rawEvent.getObjectType());
                  }
                })
            .collect(Collectors.toList());

    final List<UUID> publishedIds =
        eventPublisherService.publishEvents(httpClient, feedProperties, connectionEvents);
    if (publishedIds.size() == eventIds.size()) {
      eventService.deleteEvents(eventIds);
    }
  }
}
