package app.datacollect.twitchsocket.adapter.worker;

import app.datacollect.twitchdata.feed.events.EventData;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import app.datacollect.twitchsocket.adapter.config.FeedProperties;
import app.datacollect.twitchsocket.adapter.event.assembler.ClearChatEventV1Assembler;
import app.datacollect.twitchsocket.adapter.event.assembler.ClearMessageEventV1Assembler;
import app.datacollect.twitchsocket.adapter.event.assembler.GlobalClearChatEventV1Assembler;
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
public class ScheduledPunishmentFeedPublisher {

  private final HttpClient httpClient;
  private final FeedProperties feedProperties;
  private final EventService eventService;
  private final EventPublisherService eventPublisherService;
  private final ClearMessageEventV1Assembler clearMessageEventV1Assembler;
  private final ClearChatEventV1Assembler clearChatEventV1Assembler;
  private final GlobalClearChatEventV1Assembler globalClearChatEventV1Assembler;

  public ScheduledPunishmentFeedPublisher(
      @Qualifier("punishmentFeedProperties") FeedProperties feedProperties,
      EventService eventService,
      EventPublisherService eventPublisherService,
      ClearMessageEventV1Assembler clearMessageEventV1Assembler,
      ClearChatEventV1Assembler clearChatEventV1Assembler,
      GlobalClearChatEventV1Assembler globalClearChatEventV1Assembler) {
    this.clearMessageEventV1Assembler = clearMessageEventV1Assembler;
    this.clearChatEventV1Assembler = clearChatEventV1Assembler;
    this.globalClearChatEventV1Assembler = globalClearChatEventV1Assembler;
    this.httpClient = HttpClient.newHttpClient();
    this.feedProperties = feedProperties;
    this.eventService = eventService;
    this.eventPublisherService = eventPublisherService;
  }

  @Scheduled(fixedDelay = 5000)
  public void process() {
    processBatch();
  }

  @Transactional
  public void processBatch() {
    final List<RawEvent> rawEvents =
        eventService.getPunishmentEvents(feedProperties.getBatchSize());
    if (rawEvents.isEmpty()) {
      return;
    }
    final List<UUID> eventIds = new ArrayList<>();
    final List<EventData> punishmentEvents =
        rawEvents.stream()
            .map(
                rawEvent -> {
                  if (rawEvent.getEventType() == EventType.CLEAR_MESSAGE_SNAPSHOT
                      && rawEvent.getObjectType() == ObjectType.CLEAR_MESSAGE
                      && rawEvent.getVersion() == Version.V1) {
                    eventIds.add(rawEvent.getId());
                    return clearMessageEventV1Assembler.assemble(rawEvent);
                  } else if (rawEvent.getEventType() == EventType.CLEAR_CHAT_SNAPSHOT
                      && rawEvent.getObjectType() == ObjectType.CLEAR_CHAT
                      && rawEvent.getVersion() == Version.V1) {
                    eventIds.add(rawEvent.getId());
                    return clearChatEventV1Assembler.assemble(rawEvent);
                  } else if (rawEvent.getEventType() == EventType.GLOBAL_CLEAR_CHAT_SNAPSHOT
                      && rawEvent.getObjectType() == ObjectType.GLOBAL_CLEAR_CHAT
                      && rawEvent.getVersion() == Version.V1) {
                    eventIds.add(rawEvent.getId());
                    return globalClearChatEventV1Assembler.assemble(rawEvent);
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
        eventPublisherService.publishEvents(httpClient, feedProperties, punishmentEvents);
    if (publishedIds.size() == eventIds.size()) {
      eventService.deleteEvents(eventIds);
    }
  }
}
