package app.datacollect.twitchsocket.adapter.activechannel.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;

public class CreateActiveChannelResource {

  private final List<Long> userIds;

  @JsonCreator
  public CreateActiveChannelResource(List<Long> userIds) {
    this.userIds = userIds;
  }

  public List<Long> getUserIds() {
    return userIds;
  }
}
