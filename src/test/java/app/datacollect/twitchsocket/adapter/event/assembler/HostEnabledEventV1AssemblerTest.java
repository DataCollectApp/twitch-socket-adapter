package app.datacollect.twitchsocket.adapter.event.assembler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.host.enabled.v1.HostEnabledEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class HostEnabledEventV1AssemblerTest {

  private HostEnabledEventV1Assembler assembler;
  private UUID id;
  private UTCDateTime time;
  private RawEvent rawEventMock;

  @Before
  public void setup() {
    assembler = new HostEnabledEventV1Assembler();

    id = UUID.randomUUID();
    time = UTCDateTime.now();

    rawEventMock = mock(RawEvent.class);
    when(rawEventMock.getId()).thenReturn(id);
    when(rawEventMock.getTime()).thenReturn(time);
  }

  @Test
  public void assemble_whenGivenInput_shouldReturnExpectedObject() {
    when(rawEventMock.getRawData()).thenReturn(":tmi.twitch.tv HOSTTARGET #kamolis :policana 0");

    final HostEnabledEventV1 event = assembler.assemble(rawEventMock);

    assertEquals(id, event.getId());
    assertEquals("kamolis", event.getChannel());
    assertEquals("policana", event.getTargetChannel());
    assertEquals(time.iso8601(), event.getTime());
  }
}
