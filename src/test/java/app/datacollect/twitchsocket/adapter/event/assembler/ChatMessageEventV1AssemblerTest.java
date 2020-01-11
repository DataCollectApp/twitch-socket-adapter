package app.datacollect.twitchsocket.adapter.event.assembler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.datacollect.time.UTCDateTime;
import app.datacollect.twitchdata.feed.events.chatmessage.v1.ChatMessageEventV1;
import app.datacollect.twitchsocket.adapter.event.domain.RawEvent;
import app.datacollect.twitchsocket.adapter.metadata.assembler.MetadataAssembler;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class ChatMessageEventV1AssemblerTest {

  private ChatMessageEventV1Assembler assembler;
  private UUID id;
  private UTCDateTime time;
  private RawEvent rawEventMock;

  @Before
  public void setup() {
    assembler = new ChatMessageEventV1Assembler(new MetadataAssembler());

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
            "@badge-info=;badges=;color=;display-name=julie39123;emotes=;flags=;id=277866c7-b4d7-4ad8-8689-77d86d5608a2;mod=0;room-id=111659941;subscriber=0;tmi-sent-ts=1577447967132;turbo=0;user-id=464410434;user-type= :julie39123!julie39123@julie39123.tmi.twitch.tv PRIVMSG #victoto96 :@fickzy_ Nais");

    final ChatMessageEventV1 event = assembler.assemble(rawEventMock);

    assertEquals(id, event.getId());
    assertEquals("julie39123", event.getUsername());
    assertEquals("@fickzy_ Nais", event.getMessage());
    assertEquals("victoto96", event.getChannel());
    assertEquals(464410434, event.getUserId());
    assertEquals(111659941, event.getRoomId());
    assertEquals(time.iso8601(), event.getTime());
  }
}
