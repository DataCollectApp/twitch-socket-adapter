package app.datacollect.twitchsocket.adapter.chatmessage.metadata.assembler;

import static org.junit.Assert.assertEquals;

import app.datacollect.twitchsocket.adapter.metadata.assembler.MetadataAssembler;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class MetadataAssemblerTest {

  private MetadataAssembler assembler;

  @Before
  public void setup() {
    assembler = new MetadataAssembler();
  }

  @Test
  public void assemble_whenGivenMetadataString_shouldReturnExpectedMap() {
    Map<String, String> result =
        assembler.assemble(
            "@badge-info=;badges=moderator/1,partner/1;color=#7C7CE1;display-name=Nightbot;emotes=;flags=0-24:;id=4d3f9a3e-4aa9-489b-b2c5-84deae613767;mod=1;room-id=135210465;subscriber=0;tmi-sent-ts=1573941187578;turbo=0;user-id=19264788;user-type=mod");

    assertEquals(12, result.size());

    assertEquals("moderator/1,partner/1", result.get("badges"));
    assertEquals("#7C7CE1", result.get("color"));
    assertEquals("Nightbot", result.get("display-name"));
    assertEquals("0-24:", result.get("flags"));
    assertEquals("4d3f9a3e-4aa9-489b-b2c5-84deae613767", result.get("id"));
    assertEquals("1", result.get("mod"));
    assertEquals("135210465", result.get("room-id"));
    assertEquals("0", result.get("subscriber"));
    assertEquals("1573941187578", result.get("tmi-sent-ts"));
    assertEquals("0", result.get("turbo"));
    assertEquals("19264788", result.get("user-id"));
    assertEquals("mod", result.get("user-type"));
  }
}
