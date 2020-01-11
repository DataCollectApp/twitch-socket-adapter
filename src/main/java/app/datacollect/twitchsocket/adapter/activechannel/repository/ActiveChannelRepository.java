package app.datacollect.twitchsocket.adapter.activechannel.repository;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchsocket.adapter.activechannel.domain.ActiveChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ActiveChannelRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public ActiveChannelRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void saveActiveChannel(long userId, String username) {
    jdbcTemplate.update(
        "INSERT INTO active_channel(user_id, username) VALUES (:user_id, :username)",
        Map.of("user_id", userId, "username", username));
  }

  public List<ActiveChannel> getActiveChannels() {
    return jdbcTemplate.query(
        "SELECT user_id, username, joined, joined_time FROM active_channel", this::mapRow);
  }

  public List<ActiveChannel> getActiveChannels(boolean joined) {
    return jdbcTemplate.query(
        "SELECT user_id, username, joined, joined_time FROM active_channel WHERE joined = :joined LIMIT 100",
        Map.of("joined", joined),
        this::mapRow);
  }

  public void updateActiveChannel(long userId, String username) {
    jdbcTemplate.update(
        "UPDATE active_channel SET username = :username WHERE user_id = :user_id",
        Map.of("username", username, "user_id", userId));
  }

  public void updateActiveChannel(long userId, boolean joined, UTCDateTime joinedTime) {
    jdbcTemplate.update(
        "UPDATE active_channel SET joined = :joined, joined_time = :joined_time WHERE user_id = :user_id",
        Map.of("joined", joined, "joined_time", joinedTime.iso8601(), "user_id", userId));
  }

  private ActiveChannel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return new ActiveChannel(
        resultSet.getLong("user_id"),
        resultSet.getString("username"),
        resultSet.getBoolean("joined"),
        UTCDateTime.ofNullable(resultSet.getString("joined_time")));
  }
}
