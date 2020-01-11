package app.datacollect.twitchsocket.adapter.service;

import app.datacollect.twitchsocket.adapter.event.service.EventService;
import app.datacollect.twitchsocket.adapter.twitchconnection.service.TwitchConnection;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CollectorService {

  private static final Logger logger = LoggerFactory.getLogger(CollectorService.class);

  private final TwitchConnection twitchConnection;
  private final EventService eventService;

  public CollectorService(TwitchConnection twitchConnection, EventService eventService) {
    this.twitchConnection = twitchConnection;
    this.eventService = eventService;
  }

  @Scheduled(fixedDelay = 1)
  public void listen() {
    final String input = twitchConnection.read();
    logInput(input);
    final String[] inputParts = input.split(" ");
    if (input.equals("PING :tmi.twitch.tv")) {
      twitchConnection.send("PONG :tmi.twitch.tv");
    } else if (input.equals(":tmi.twitch.tv CAP * ACK :twitch.tv/membership")) {
      twitchConnection.send("CAP REQ :twitch.tv/tags");
    } else if (input.equals(":tmi.twitch.tv CAP * ACK :twitch.tv/tags")) {
      twitchConnection.send("CAP REQ :twitch.tv/commands");
    } else if (input.equals(":tmi.twitch.tv CAP * ACK :twitch.tv/commands")) {
      logInfo("Registered capabilities");
    } else if (!shouldIgnore(inputParts[1])) {
      if (inputParts[1].equals("001")) {
        twitchConnection.getBotUser().setLoggedIn(true);
        logInfo("Bot user logged in as " + twitchConnection.getBotUser().getUsername());
        twitchConnection.send("CAP REQ :twitch.tv/membership");
      } else if (inputParts[1].equals("JOIN")) {
        eventService.saveUserJoinEvent(input);
      } else if (inputParts[1].equals("PART")) {
        eventService.saveUserLeaveEvent(input);
      } else if (inputParts[1].equalsIgnoreCase("HOSTTARGET")) {
        if (!input.contains(":-")) {
          eventService.saveHostEnabledEvent(input);
        } else {
          eventService.saveHostDisabledEvent(input);
        }
      } else if (inputParts[2].equals("PRIVMSG")) {
        eventService.saveChatMessageEvent(input);
      } else if (inputParts[2].equals("CLEARMSG")) {
        eventService.saveClearMessageEvent(input);
      } else if (inputParts[2].equals("CLEARCHAT")) {
        if (inputParts.length == 5) {
          eventService.saveClearChatEvent(input);
        } else if (inputParts.length == 4) {
          eventService.saveGlobalClearChatEvent(input);
        }
      }
    }
  }

  private void logInput(String input) {
    logger.debug("FROM TWITCH >> " + input);
  }

  private void logInfo(String message) {
    logger.info("[INFO] " + message);
  }

  private boolean shouldIgnore(String number) {
    return Arrays.asList("002", "003", "004", "375", "372", "376").contains(number);
  }
}
