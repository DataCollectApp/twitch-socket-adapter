package app.datacollect.twitchsocket.adapter.event.assembler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.userleave.v1.UserLeaveEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class UserLeaveEventV1AssemblerTest {

  private UserLeaveEventV1Assembler assembler;
  private UUID id;
  private UTCDateTime time;
  private RawEvent rawEventMock;

  @Before
  public void setup() {
    assembler = new UserLeaveEventV1Assembler();

    id = UUID.randomUUID();
    time = UTCDateTime.now();

    rawEventMock = mock(RawEvent.class);
    when(rawEventMock.getId()).thenReturn(id);
    when(rawEventMock.getTime()).thenReturn(time);
  }

  @Test
  public void assemble_whenGivenInputParts_shouldReturnExpectedObject() {
    when(rawEventMock.getRawData())
        .thenReturn(":drrichard8!drrichard8@drrichard8.tmi.twitch.tv PART #victoto96");

    final UserLeaveEventV1 event = assembler.assemble(rawEventMock);

    assertEquals(id, event.getId());
    assertEquals("drrichard8", event.getUsername());
    assertEquals("victoto96", event.getChannel());
    assertEquals(time.iso8601(), event.getTime());
  }
}
