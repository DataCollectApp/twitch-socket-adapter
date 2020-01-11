# twitch-socket-adapter
Application to collect data from Twitch

### Environment variables
The following properties are required to run the application:  

| Name  | Description |  
| ------------- | ------------- |  
| SPRING_SECURITY_USER_NAME  | Username for accessing endpoints  |  
| SPRING_SECURITY_USER_PASSWORD   | Password for accessing endpoints  |  
| SPRING_DATASOURCE_URL   | Database URL (Postgres  supported)  |  
| SPRING_DATASOURCE_USERNAME   | Database username  |  
| SPRING_DATASOURCE_PASSWORD   | Database password  |  
| TWITCH_SOCKET_ADAPTER_CREDENTIALS_TWITCH_HOST   | Host of Twitch socket server  |  
| TWITCH_SOCKET_ADAPTER_CREDENTIALS_TWITCH_PORT   | Port to Twitch socket server  |  
| TWITCH_SOCKET_ADAPTER_CREDENTIALS_USERNAME   | Twitch username for connecting to chat  |  
| TWITCH_SOCKET_ADAPTER_CREDENTIALS_TOKEN   | Token for authenticating the bot user  |
| TWITCH_USER_SEARCH_URL | URL to the twitch user search endpoint |
| TWITCH_USER_SEARCH_USERNAME | Username for the twitch user search endpoint |
| TWITCH_USER_SEARCH_PASSWORD | Password for the twitch user search endpoint |
| TWITCH_DATA_FEED_PUBLISHER_CHAT_MESSAGE_URL | URL to the endpoint for publishing chat message events |
| TWITCH_DATA_FEED_PUBLISHER_CHAT_MESSAGE_USERNAME | Username to the feed |
| TWITCH_DATA_FEED_PUBLISHER_CHAT_MESSAGE_PASSWORD | Password to the feed |
| TWITCH_DATA_FEED_PUBLISHER_CHAT_MESSAGE_BATCH_SIZE | Amount of events to publish at a time |
| TWITCH_DATA_FEED_PUBLISHER_CONNECTION_URL | URL to the endpoint for publishing connection events |
| TWITCH_DATA_FEED_PUBLISHER_CONNECTION_USERNAME | Username to the feed |
| TWITCH_DATA_FEED_PUBLISHER_CONNECTION_PASSWORD | Password to the feed |
| TWITCH_DATA_FEED_PUBLISHER_CONNECTION_BATCH_SIZE | Amount of events to publish at a time |
| TWITCH_DATA_FEED_PUBLISHER_HOST_URL | URL to the endpoint for publishing host events |
| TWITCH_DATA_FEED_PUBLISHER_HOST_USERNAME | Username to the feed |
| TWITCH_DATA_FEED_PUBLISHER_HOST_PASSWORD | Password to the feed |
| TWITCH_DATA_FEED_PUBLISHER_HOST_BATCH_SIZE | Amount of events to publish at a time |
| TWITCH_DATA_FEED_PUBLISHER_PUNISHMENT_URL | URL to the endpoint for publishing punishment events |
| TWITCH_DATA_FEED_PUBLISHER_PUNISHMENT_USERNAME | Username to the feed |
| TWITCH_DATA_FEED_PUBLISHER_PUNISHMENT_PASSWORD | Password to the feed |
| TWITCH_DATA_FEED_PUBLISHER_PUNISHMENT_BATCH_SIZE | Amount of events to publish at a time |

### Database migration
The application manages the database schema with Flyway.