package app.datacollect.twitchsocket.adapter.event.repository;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public EventRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void saveEvent(RawEvent rawEvent) {
    jdbcTemplate.update(
        "INSERT INTO event (id, event_type, object_type, version, time, raw_data) VALUES(:id, :event_type, :object_type, :version, :time, :raw_data)",
        Map.of(
            "id", rawEvent.getId(),
            "event_type", rawEvent.getEventType().name(),
            "object_type", rawEvent.getObjectType().name(),
            "version", rawEvent.getVersion().name(),
            "time", rawEvent.getTime().iso8601(),
            "raw_data", rawEvent.getRawData()));
  }

  public List<RawEvent> getEvents(
      EventType eventType, ObjectType objectType, Version version, int limit) {
    return jdbcTemplate.query(
        "SELECT id, event_type, object_type, version, time, raw_data "
            + "FROM event "
            + "WHERE event_type = :event_type AND object_type = :object_type AND version = :version "
            + "ORDER BY time LIMIT :limit",
        Map.of(
            "event_type",
            eventType.name(),
            "object_type",
            objectType.name(),
            "version",
            version.name(),
            "limit",
            limit),
        this::mapRow);
  }

  public List<RawEvent> getEvents(
      List<EventType> eventTypes, ObjectType objectType, Version version, int limit) {
    return jdbcTemplate.query(
        "SELECT id, event_type, object_type, version, time, raw_data "
            + "FROM event "
            + "WHERE event_type in(:event_types) AND object_type = :object_type AND version = :version "
            + "ORDER BY time LIMIT :limit",
        Map.of(
            "event_types",
            eventTypes.stream().map(Enum::name).collect(Collectors.toList()),
            "object_type",
            objectType.name(),
            "version",
            version.name(),
            "limit",
            limit),
        this::mapRow);
  }

  public List<RawEvent> getEvents(
      List<EventType> eventTypes, List<ObjectType> objectTypes, Version version, int limit) {
    return jdbcTemplate.query(
        "SELECT id, event_type, object_type, version, time, raw_data "
            + "FROM event "
            + "WHERE event_type in(:event_types) AND object_type in(:object_types) AND version = :version "
            + "ORDER BY time LIMIT :limit",
        Map.of(
            "event_types",
            eventTypes.stream().map(Enum::name).collect(Collectors.toList()),
            "object_types",
            objectTypes.stream().map(Enum::name).collect(Collectors.toList()),
            "version",
            version.name(),
            "limit",
            limit),
        this::mapRow);
  }

  public int deleteEvents(List<UUID> ids) {
    return jdbcTemplate.update("DELETE FROM event WHERE id IN (:ids)", Map.of("ids", ids));
  }

  private RawEvent mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return new RawEvent(
        UUID.fromString(resultSet.getString("id")),
        EventType.valueOf(resultSet.getString("event_type")),
        ObjectType.valueOf(resultSet.getString("object_type")),
        Version.valueOf(resultSet.getString("version")),
        resultSet.getString("raw_data"),
        UTCDateTime.of(resultSet.getString("time")));
  }
}
