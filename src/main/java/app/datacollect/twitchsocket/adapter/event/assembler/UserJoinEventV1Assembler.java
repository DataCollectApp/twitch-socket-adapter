package app.datacollect.twitchsocket.adapter.event.assembler;

import app.datacollect.twitchdata.feed.events.userjoin.v1.UserJoinEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import org.springframework.stereotype.Component;

@Component
public class UserJoinEventV1Assembler {

  public UserJoinEventV1 assemble(RawEvent rawEvent) {
    final String[] inputParts = rawEvent.getRawData().split(" ");
    return new UserJoinEventV1(
        rawEvent.getId(),
        inputParts[0].split("!")[0].substring(1),
        inputParts[2].substring(1),
        rawEvent.getTime().iso8601());
  }
}
