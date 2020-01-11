package app.datacollect.twitchsocket.adapter.event.domain;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import java.util.UUID;

public class RawEvent {

  private final UUID id;
  private final EventType eventType;
  private final ObjectType objectType;
  private final Version version;
  private final String rawData;
  private final UTCDateTime time;

  public RawEvent(
      UUID id,
      EventType eventType,
      ObjectType objectType,
      Version version,
      String rawData,
      UTCDateTime time) {
    this.id = id;
    this.eventType = eventType;
    this.objectType = objectType;
    this.version = version;
    this.rawData = rawData;
    this.time = time;
  }

  public UUID getId() {
    return id;
  }

  public EventType getEventType() {
    return eventType;
  }

  public ObjectType getObjectType() {
    return objectType;
  }

  public Version getVersion() {
    return version;
  }

  public String getRawData() {
    return rawData;
  }

  public UTCDateTime getTime() {
    return time;
  }
}
