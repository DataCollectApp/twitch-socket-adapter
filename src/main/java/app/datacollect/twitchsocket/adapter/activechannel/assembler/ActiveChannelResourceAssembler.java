package app.datacollect.twitchsocket.adapter.activechannel.assembler;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchsocket.adapter.activechannel.domain.ActiveChannel;
import app.datacollect.twitchsocket.adapter.activechannel.resource.ActiveChannelResource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ActiveChannelResourceAssembler {

  public ActiveChannelResource assemble(ActiveChannel activeChannel) {
    return new ActiveChannelResource(
        activeChannel.getUserId(),
        activeChannel.getUsername(),
        activeChannel.hasJoined(),
        activeChannel.getJoinedTime().map(UTCDateTime::iso8601).orElse(null));
  }

  public List<ActiveChannelResource> assemble(List<ActiveChannel> activeChannels) {
    return activeChannels.stream().map(this::assemble).collect(Collectors.toList());
  }
}
