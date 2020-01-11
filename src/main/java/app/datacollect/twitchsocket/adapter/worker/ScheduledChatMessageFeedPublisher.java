package app.datacollect.twitchsocket.adapter.worker;

import app.datacollect.twitchdata.feed.events.EventData;
import app.datacollect.twitchsocket.adapter.config.FeedProperties;
import app.datacollect.twitchsocket.adapter.event.assembler.ChatMessageEventV1Assembler;
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
public class ScheduledChatMessageFeedPublisher {

  private final HttpClient httpClient;
  private final FeedProperties feedProperties;
  private final EventService eventService;
  private final EventPublisherService eventPublisherService;
  private final ChatMessageEventV1Assembler chatMessageEventV1Assembler;

  public ScheduledChatMessageFeedPublisher(
      @Qualifier("chatMessageFeedProperties") FeedProperties feedProperties,
      EventService eventService,
      EventPublisherService eventPublisherService,
      ChatMessageEventV1Assembler chatMessageEventV1Assembler) {
    this.httpClient = HttpClient.newHttpClient();
    this.feedProperties = feedProperties;
    this.eventService = eventService;
    this.eventPublisherService = eventPublisherService;
    this.chatMessageEventV1Assembler = chatMessageEventV1Assembler;
  }

  @Scheduled(fixedDelay = 5000)
  public void process() {
    processBatch();
  }

  @Transactional
  public void processBatch() {
    final List<RawEvent> rawEvents =
        eventService.getChatMessageEvents(feedProperties.getBatchSize());
    if (rawEvents.isEmpty()) {
      return;
    }
    final List<UUID> eventIds = new ArrayList<>();
    final List<EventData> chatMessageEvents =
        rawEvents.stream()
            .map(
                rawEvent -> {
                  eventIds.add(rawEvent.getId());
                  return chatMessageEventV1Assembler.assemble(rawEvent);
                })
            .collect(Collectors.toList());

    final List<UUID> publishedIds =
        eventPublisherService.publishEvents(httpClient, feedProperties, chatMessageEvents);
    if (publishedIds.size() == eventIds.size()) {
      eventService.deleteEvents(eventIds);
    }
  }
}
