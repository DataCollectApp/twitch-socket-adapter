package app.datacollect.twitchsocket.adapter.event.assembler;

import app.datacollect.twitchdata.feed.events.host.disabled.v1.HostDisabledEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import org.springframework.stereotype.Component;

@Component
public class HostDisabledEventV1Assembler {

  public HostDisabledEventV1 assemble(RawEvent rawEvent) {
    final String[] inputParts = rawEvent.getRawData().split(" ");
    return new HostDisabledEventV1(
        rawEvent.getId(), inputParts[2].substring(1), rawEvent.getTime().iso8601());
  }
}
