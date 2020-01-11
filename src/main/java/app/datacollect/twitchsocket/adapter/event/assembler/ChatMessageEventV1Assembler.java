package app.datacollect.twitchsocket.adapter.event.assembler;

import app.datacollect.twitchdata.feed.events.chatmessage.v1.ChatMessageEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.metadata.assembler.MetadataAssembler;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageEventV1Assembler {

  private final MetadataAssembler metadataAssembler;

  public ChatMessageEventV1Assembler(MetadataAssembler metadataAssembler) {
    this.metadataAssembler = metadataAssembler;
  }

  public ChatMessageEventV1 assemble(RawEvent rawEvent) {
    final String[] inputParts = rawEvent.getRawData().split(" ");
    final Map<String, String> metadata = metadataAssembler.assemble(inputParts[0]);
    final StringBuilder messageBuilder = new StringBuilder();
    for (int i = 4; i < inputParts.length; i++) {
      messageBuilder.append(inputParts[i]).append(" ");
    }
    return new ChatMessageEventV1(
        rawEvent.getId(),
        inputParts[1].split("!")[0].substring(1),
        metadata.get("display-name"),
        messageBuilder.toString().substring(1).trim(),
        inputParts[3].substring(1),
        Long.parseLong(metadata.get("user-id")),
        Long.parseLong(metadata.get("room-id")),
        metadata.get("mod").equals("1"),
        metadata.get("subscriber").equals("1"),
        metadata.get("turbo").equals("1"),
        rawEvent.getTime().iso8601());
  }
}
