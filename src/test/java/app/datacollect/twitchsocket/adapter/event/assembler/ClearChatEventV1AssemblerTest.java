package app.datacollect.twitchsocket.adapter.event.assembler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.clearchat.v1.ClearChatEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.metadata.assembler.MetadataAssembler;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class ClearChatEventV1AssemblerTest {

  private ClearChatEventV1Assembler assembler;
  private UUID id;
  private UTCDateTime time;
  private RawEvent rawEventMock;

  @Before
  public void setup() {
    assembler = new ClearChatEventV1Assembler(new MetadataAssembler());

    id = UUID.randomUUID();
    time = UTCDateTime.now();

    rawEventMock = mock(RawEvent.class);
    when(rawEventMock.getId()).thenReturn(id);
    when(rawEventMock.getTime()).thenReturn(time);
  }

  @Test
  public void assemble_whenGivenInput_shouldReturnExpectedObject() {
    when(rawEventMock.getRawData())
        .thenReturn(
            "@ban-duration=600;room-id=111659941;target-user-id=259360305;tmi-sent-ts=1577409383226 :tmi.twitch.tv CLEARCHAT #victoto96 :rflaten");

    final ClearChatEventV1 event = assembler.assemble(rawEventMock);

    assertEquals(id, event.getId());
    assertEquals("rflaten", event.getTargetUsername());
    assertEquals("259360305", event.getTargetUserId());
    assertEquals("victoto96", event.getChannel());
    assertEquals(111659941, event.getRoomId());
    assertEquals(600, event.getSeconds());
    assertEquals(time.iso8601(), event.getTime());
  }

  @Test
  public void assemble_whenGivenInputWithMissingBanDuration_shouldReturnExpectedObject() {
    when(rawEventMock.getRawData())
        .thenReturn(
            "@room-id=111659941;target-user-id=259360305;tmi-sent-ts=1577409383226 :tmi.twitch.tv CLEARCHAT #victoto96 :rflaten");

    final ClearChatEventV1 event = assembler.assemble(rawEventMock);

    assertEquals(id, event.getId());
    assertEquals("rflaten", event.getTargetUsername());
    assertEquals("259360305", event.getTargetUserId());
    assertEquals("victoto96", event.getChannel());
    assertEquals(111659941, event.getRoomId());
    assertEquals(-1, event.getSeconds());
    assertEquals(time.iso8601(), event.getTime());
  }
}
