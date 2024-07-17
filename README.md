Just execute `MessagingStreamsIT`.
It shows messages are correctly deserialized when Json fields match DTO fields.

It seems Jackson property `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES` is now enabled by default.