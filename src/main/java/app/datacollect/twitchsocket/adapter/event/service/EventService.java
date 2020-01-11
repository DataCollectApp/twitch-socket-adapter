package app.datacollect.twitchsocket.adapter.event.service;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.event.repository.EventRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventService {

  private static final Logger logger = LoggerFactory.getLogger(EventService.class);

  private final EventRepository repository;

  public EventService(EventRepository repository) {
    this.repository = repository;
  }

  public void saveChatMessageEvent(String rawData) {
    final RawEvent rawEvent =
        new RawEvent(
            UUID.randomUUID(),
            EventType.CHAT_MESSAGE_SNAPSHOT,
            ObjectType.CHAT_MESSAGE,
            Version.V1,
            rawData,
            UTCDateTime.now());

    logger.debug("Saving chat message event with id '{}'", rawEvent.getId());
    repository.saveEvent(rawEvent);
    logger.info("Saved chat message event with id '{}'", rawEvent.getId());
  }

  public void saveUserJoinEvent(String rawData) {
    final RawEvent rawEvent =
        new RawEvent(
            UUID.randomUUID(),
            EventType.USER_JOIN_SNAPSHOT,
            ObjectType.USER_JOIN,
            Version.V1,
            rawData,
            UTCDateTime.now());

    logger.debug("Saving user join event with id '{}'", rawEvent.getId());
    repository.saveEvent(rawEvent);
    logger.info("Saved user join event with id '{}'", rawEvent.getId());
  }

  public void saveUserLeaveEvent(String rawData) {
    final RawEvent rawEvent =
        new RawEvent(
            UUID.randomUUID(),
            EventType.USER_LEAVE_SNAPSHOT,
            ObjectType.USER_LEAVE,
            Version.V1,
            rawData,
            UTCDateTime.now());

    logger.debug("Saving user leave event with id '{}'", rawEvent.getId());
    repository.saveEvent(rawEvent);
    logger.info("Saved user leave event with id '{}'", rawEvent.getId());
  }

  public void saveHostEnabledEvent(String rawData) {
    final RawEvent rawEvent =
        new RawEvent(
            UUID.randomUUID(),
            EventType.HOST_ENABLED,
            ObjectType.HOST,
            Version.V1,
            rawData,
            UTCDateTime.now());

    logger.debug("Saving host enabled event with id '{}'", rawEvent.getId());
    repository.saveEvent(rawEvent);
    logger.info("Saved host enabled event with id '{}'", rawEvent.getId());
  }

  public void saveHostDisabledEvent(String rawData) {
    final RawEvent rawEvent =
        new RawEvent(
            UUID.randomUUID(),
            EventType.HOST_DISABLED,
            ObjectType.HOST,
            Version.V1,
            rawData,
            UTCDateTime.now());

    logger.debug("Saving host disabled event with id '{}'", rawEvent.getId());
    repository.saveEvent(rawEvent);
    logger.info("Saved host disabled event with id '{}'", rawEvent.getId());
  }

  public void saveClearMessageEvent(String rawData) {
    final RawEvent rawEvent =
        new RawEvent(
            UUID.randomUUID(),
            EventType.CLEAR_MESSAGE_SNAPSHOT,
            ObjectType.CLEAR_MESSAGE,
            Version.V1,
            rawData,
            UTCDateTime.now());

    logger.debug("Saving clear message event with id '{}'", rawEvent.getId());
    repository.saveEvent(rawEvent);
    logger.info("Saved clear message event with id '{}'", rawEvent.getId());
  }

  public void saveClearChatEvent(String rawData) {
    final RawEvent rawEvent =
        new RawEvent(
            UUID.randomUUID(),
            EventType.CLEAR_CHAT_SNAPSHOT,
            ObjectType.CLEAR_CHAT,
            Version.V1,
            rawData,
            UTCDateTime.now());

    logger.debug("Saving clear chat event with id '{}'", rawEvent.getId());
    repository.saveEvent(rawEvent);
    logger.info("Saved clear chat event with id '{}'", rawEvent.getId());
  }

  public void saveGlobalClearChatEvent(String rawData) {
    final RawEvent rawEvent =
        new RawEvent(
            UUID.randomUUID(),
            EventType.GLOBAL_CLEAR_CHAT_SNAPSHOT,
            ObjectType.GLOBAL_CLEAR_CHAT,
            Version.V1,
            rawData,
            UTCDateTime.now());

    logger.debug("Saving global clear chat event with id '{}'", rawEvent.getId());
    repository.saveEvent(rawEvent);
    logger.info("Saved global clear chat event with id '{}'", rawEvent.getId());
  }

  public List<RawEvent> getChatMessageEvents(int limit) {
    return repository.getEvents(
        EventType.CHAT_MESSAGE_SNAPSHOT, ObjectType.CHAT_MESSAGE, Version.V1, limit);
  }

  public List<RawEvent> getConnectionEvents(int limit) {
    return repository.getEvents(
        List.of(EventType.USER_JOIN_SNAPSHOT, EventType.USER_LEAVE_SNAPSHOT),
        List.of(ObjectType.USER_JOIN, ObjectType.USER_LEAVE),
        Version.V1,
        limit);
  }

  public List<RawEvent> getHostEvents(int limit) {
    return repository.getEvents(
        List.of(EventType.HOST_ENABLED, EventType.HOST_DISABLED),
        ObjectType.HOST,
        Version.V1,
        limit);
  }

  public List<RawEvent> getPunishmentEvents(int limit) {
    return repository.getEvents(
        List.of(
            EventType.CLEAR_CHAT_SNAPSHOT,
            EventType.GLOBAL_CLEAR_CHAT_SNAPSHOT,
            EventType.CLEAR_MESSAGE_SNAPSHOT),
        List.of(ObjectType.CLEAR_CHAT, ObjectType.GLOBAL_CLEAR_CHAT, ObjectType.CLEAR_MESSAGE),
        Version.V1,
        limit);
  }

  public void deleteEvents(List<UUID> ids) {
    logger.debug("Deleting '{}' events", ids.size());
    final int deleted = repository.deleteEvents(ids);
    logger.info("Deleted '{}'/'{}' events", deleted, ids.size());
  }
}
