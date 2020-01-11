package app.datacollect.twitchsocket.adapter.event.assembler;

import app.datacollect.twitchdata.feed.events.globalclearchat.v1.GlobalClearChatEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.metadata.assembler.MetadataAssembler;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GlobalClearChatEventV1Assembler {

  private final MetadataAssembler metadataAssembler;

  public GlobalClearChatEventV1Assembler(MetadataAssembler metadataAssembler) {
    this.metadataAssembler = metadataAssembler;
  }

  public GlobalClearChatEventV1 assemble(RawEvent rawEvent) {
    final String[] inputParts = rawEvent.getRawData().split(" ");
    final Map<String, String> metadata = metadataAssembler.assemble(inputParts[0]);
    return new GlobalClearChatEventV1(
        rawEvent.getId(),
        inputParts[3].substring(1),
        Long.parseLong(metadata.get("room-id")),
        rawEvent.getTime().iso8601());
  }
}
