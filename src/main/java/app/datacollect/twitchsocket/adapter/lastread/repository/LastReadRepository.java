package app.datacollect.twitchsocket.adapter.lastread.repository;

import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LastReadRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public LastReadRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void saveLastReadId(String name, String id) {
    jdbcTemplate.update(
        "INSERT INTO last_read (name, id) VALUES (:name, :id)", Map.of("name", name, "id", id));
  }

  public void updateLastReadId(String name, String id) {
    jdbcTemplate.update(
        "UPDATE last_read SET id = :id WHERE name = :name", Map.of("id", id, "name", name));
  }

  public Optional<String> getLastReadId(String name) {
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              "SELECT id FROM last_read WHERE name = :name", Map.of("name", name), String.class));
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }
}
