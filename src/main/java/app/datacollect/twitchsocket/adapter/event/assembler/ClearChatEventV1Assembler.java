package app.datacollect.twitchsocket.adapter.event.assembler;

import app.datacollect.twitchdata.feed.events.clearchat.v1.ClearChatEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.metadata.assembler.MetadataAssembler;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ClearChatEventV1Assembler {

  private final MetadataAssembler metadataAssembler;

  public ClearChatEventV1Assembler(MetadataAssembler metadataAssembler) {
    this.metadataAssembler = metadataAssembler;
  }

  public ClearChatEventV1 assemble(RawEvent rawEvent) {
    final String[] inputParts = rawEvent.getRawData().split(" ");
    final Map<String, String> metadata = metadataAssembler.assemble(inputParts[0]);
    return new ClearChatEventV1(
        rawEvent.getId(),
        inputParts[4].substring(1),
        metadata.get("target-user-id"),
        inputParts[3].substring(1),
        Long.parseLong(metadata.get("room-id")),
        Optional.ofNullable(metadata.get("ban-duration")).map(Long::parseLong).orElse(-1L),
        rawEvent.getTime().iso8601());
  }
}
