package app.datacollect.twitchsocket.adapter.metadata.assembler;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MetadataAssembler {

  public Map<String, String> assemble(String metadata) {
    return Arrays.stream(metadata.substring(1).split(";"))
        .map(part -> part.split("="))
        .filter(keyValue -> keyValue.length > 1)
        .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
  }
}
