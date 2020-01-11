package app.datacollect.twitchsocket.adapter.worker;

import app.datacollect.twitchdata.feed.events.EventData;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import app.datacollect.twitchsocket.adapter.config.FeedProperties;
import app.datacollect.twitchsocket.adapter.event.assembler.HostDisabledEventV1Assembler;
import app.datacollect.twitchsocket.adapter.event.assembler.HostEnabledEventV1Assembler;
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
public class ScheduledHostFeedPublisher {

  private final HttpClient httpClient;
  private final FeedProperties feedProperties;
  private final EventService eventService;
  private final EventPublisherService eventPublisherService;
  private final HostEnabledEventV1Assembler hostEnabledEventV1Assembler;
  private final HostDisabledEventV1Assembler hostDisabledEventV1Assembler;

  public ScheduledHostFeedPublisher(
      @Qualifier("hostFeedProperties") FeedProperties feedProperties,
      EventService eventService,
      EventPublisherService eventPublisherService,
      HostEnabledEventV1Assembler hostEnabledEventV1Assembler,
      HostDisabledEventV1Assembler hostDisabledEventV1Assembler) {
    this.httpClient = HttpClient.newHttpClient();
    this.feedProperties = feedProperties;
    this.eventService = eventService;
    this.eventPublisherService = eventPublisherService;
    this.hostEnabledEventV1Assembler = hostEnabledEventV1Assembler;
    this.hostDisabledEventV1Assembler = hostDisabledEventV1Assembler;
  }

  @Scheduled(fixedDelay = 5000)
  public void process() {
    processBatch();
  }

  @Transactional
  public void processBatch() {
    final List<RawEvent> rawEvents = eventService.getHostEvents(feedProperties.getBatchSize());
    if (rawEvents.isEmpty()) {
      return;
    }
    final List<UUID> eventIds = new ArrayList<>();
    final List<EventData> hostEvents =
        rawEvents.stream()
            .map(
                rawEvent -> {
                  if (rawEvent.getEventType() == EventType.HOST_ENABLED
                      && rawEvent.getObjectType() == ObjectType.HOST
                      && rawEvent.getVersion() == Version.V1) {
                    eventIds.add(rawEvent.getId());
                    return hostEnabledEventV1Assembler.assemble(rawEvent);
                  } else if (rawEvent.getEventType() == EventType.HOST_DISABLED) {
                    eventIds.add(rawEvent.getId());
                    return hostDisabledEventV1Assembler.assemble(rawEvent);
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
        eventPublisherService.publishEvents(httpClient, feedProperties, hostEvents);
    if (publishedIds.size() == eventIds.size()) {
      eventService.deleteEvents(eventIds);
    }
  }
}
