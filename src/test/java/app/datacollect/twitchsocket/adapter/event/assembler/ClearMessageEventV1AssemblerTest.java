package app.datacollect.twitchsocket.adapter.event.assembler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.clearmessage.v1.ClearMessageEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.metadata.assembler.MetadataAssembler;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class ClearMessageEventV1AssemblerTest {

  private ClearMessageEventV1Assembler assembler;
  private UUID id;
  private UTCDateTime time;
  private RawEvent rawEventMock;

  @Before
  public void setup() {
    assembler = new ClearMessageEventV1Assembler(new MetadataAssembler());

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
            "@login=jotttn;room-id=;target-msg-id=1add9d45-7cf0-4db1-baa7-6395e1fd2aa3;tmi-sent-ts=1577401241677 :tmi.twitch.tv CLEARMSG #missvestkant1 :mod test 44");

    final ClearMessageEventV1 event = assembler.assemble(rawEventMock);

    assertEquals(id, event.getId());
    assertEquals("jotttn", event.getTargetUsername());
    assertEquals("missvestkant1", event.getChannel());
    assertEquals("mod test 44", event.getMessage());
    assertEquals(time.iso8601(), event.getTime());
  }
}
