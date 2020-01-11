package app.datacollect.twitchsocket.adapter.event.assembler;

import app.datacollect.twitchdata.feed.events.clearmessage.v1.ClearMessageEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.metadata.assembler.MetadataAssembler;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ClearMessageEventV1Assembler {

  private final MetadataAssembler metadataAssembler;

  public ClearMessageEventV1Assembler(MetadataAssembler metadataAssembler) {
    this.metadataAssembler = metadataAssembler;
  }

  public ClearMessageEventV1 assemble(RawEvent rawEvent) {
    final String[] inputParts = rawEvent.getRawData().split(" ");
    final Map<String, String> metadata = metadataAssembler.assemble(inputParts[0]);
    final StringBuilder messageBuilder = new StringBuilder();
    for (int i = 4; i < inputParts.length; i++) {
      messageBuilder.append(inputParts[i]).append(" ");
    }
    return new ClearMessageEventV1(
        rawEvent.getId(),
        metadata.get("login"),
        inputParts[3].substring(1),
        messageBuilder.substring(1).trim(),
        rawEvent.getTime().iso8601());
  }
}
