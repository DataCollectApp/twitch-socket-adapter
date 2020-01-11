package app.datacollect.twitchsocket.adapter.event.assembler;

import app.datacollect.twitchdata.feed.events.host.enabled.v1.HostEnabledEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import org.springframework.stereotype.Component;

@Component
public class HostEnabledEventV1Assembler {

  public HostEnabledEventV1 assemble(RawEvent rawEvent) {
    final String[] inputParts = rawEvent.getRawData().split(" ");
    return new HostEnabledEventV1(
        rawEvent.getId(),
        inputParts[2].substring(1),
        inputParts[3].substring(1),
        rawEvent.getTime().iso8601());
  }
}
