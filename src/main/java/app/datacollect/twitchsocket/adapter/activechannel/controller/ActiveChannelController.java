package app.datacollect.twitchsocket.adapter.activechannel.controller;

import app.datacollect.twitchsocket.adapter.activechannel.assembler.ActiveChannelResourceAssembler;
import app.datacollect.twitchsocket.adapter.activechannel.resource.ActiveChannelResource;
import app.datacollect.twitchsocket.adapter.activechannel.resource.CreateActiveChannelResource;
import app.datacollect.twitchsocket.adapter.activechannel.service.ActiveChannelService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("active-channel")
public class ActiveChannelController {

  private final ActiveChannelService service;
  private final ActiveChannelResourceAssembler resourceAssembler;

  public ActiveChannelController(
      ActiveChannelService service, ActiveChannelResourceAssembler resourceAssembler) {
    this.service = service;
    this.resourceAssembler = resourceAssembler;
  }

  @PostMapping
  public ResponseEntity<?> createActiveChannels(@RequestBody CreateActiveChannelResource resource) {
    service.saveActiveChannels(resource.getUserIds());
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<ActiveChannelResource>> getActiveChannels() {
    return ResponseEntity.ok(resourceAssembler.assemble(service.getActiveChannels()));
  }

  @PostMapping("refresh-usernames")
  public ResponseEntity<List<ActiveChannelResource>> refreshUsernames() {
    return ResponseEntity.ok(resourceAssembler.assemble(service.refreshUsernames()));
  }
}
