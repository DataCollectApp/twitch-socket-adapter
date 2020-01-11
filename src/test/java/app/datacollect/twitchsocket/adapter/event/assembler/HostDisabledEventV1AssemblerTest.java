package app.datacollect.twitchsocket.adapter.event.assembler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.host.disabled.v1.HostDisabledEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class HostDisabledEventV1AssemblerTest {

  private HostDisabledEventV1Assembler assembler;
  private UUID id;
  private UTCDateTime time;
  private RawEvent rawEventMock;

  @Before
  public void setup() {
    assembler = new HostDisabledEventV1Assembler();

    id = UUID.randomUUID();
    time = UTCDateTime.now();

    rawEventMock = mock(RawEvent.class);
    when(rawEventMock.getId()).thenReturn(id);
    when(rawEventMock.getTime()).thenReturn(time);
  }

  @Test
  public void assemble_whenGivenInput_shouldReturnExpectedObject() {
    when(rawEventMock.getRawData()).thenReturn(":tmi.twitch.tv HOSTTARGET #kamolis :- 0");

    final HostDisabledEventV1 event = assembler.assemble(rawEventMock);

    assertEquals(id, event.getId());
    assertEquals("kamolis", event.getChannel());
    assertEquals(time.iso8601(), event.getTime());
  }
}
