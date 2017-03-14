# TrelloMailExtractor
[![Build Status](https://travis-ci.org/125m125/TrelloMailExtractor.svg?branch=master)](https://travis-ci.org/125m125/TrelloMailExtractor)

The TrelloMailExtractor extracts trello cards from emails and sends them to your trello board.

## Installation
### Build from sources
- clone the repository
- run `mvn package`
- move `trelloMail-0.0.1-SNAPSHOT-jar-with-dependencies.jar` to your desired location (you can rename it)

### Configuration and Execution
- follow the steps for creating the trello email adress from http://help.trello.com/article/809-creating-cards-by-email
- create a file called `config.properties` in the same directory as the jar with the following content:
```
# configuration for the server from where the incoming mails should be retrieved (pop3s has to be supported)
recServer=<the url>
recPort=<the port>
recUser=<the username>
recPass=<the password>

# configuration for the server from where to send the cards to trello (smtp has to be supported)
sendServer=<the url>
sendPort=<the port>
sendUser=<the username>
sendPass=<the password>

targetMail=<the email adress for the board>
``` 
- run the jar with `java -jar <jarname>`

## Sending emails
When the TrelloMailExtractor is extracting new cards in an email, it uses certain keywords to start or end cards. These keywords have to be the only content in a row.
Labels and members can be added as described in http://help.trello.com/article/809-creating-cards-by-email .
### Start a new card
To start a new card, the following text can be used:
- todo
- \<todo\>
- todostart
- \<todostart\>

The next line after encountering one of these texts is used as the title for the card. Lines after the title are used for the content. When encountering an empty line or a line that contains one of the patterns above, the current card is terminated and a new card started.
### Ending cards
To end cards, the following text can be used:
- todoend
- \</todo\>
- \</todostart\>
- endtodo

After ending a card, new cards can be started by using one of the start-patterns again. Empty lines terminate one card and start a new card at the same time.
### Example email
```
Dear Jane and John,

For our project we should implement the following feature:
<todo>
title1
content1
</todo>

And please remember the following points for out meeting:
todo
title2 #meeting

title3 #meeting
content3
todoend

John, you still have my USB stick
todo
bring Richard's USB stick @John
todoend

Best regards,
Richard
```
## More configuration options
| name  | example       | default | explanation                                                        |
|-------|---------------|---------|--------------------------------------------------------------------|
| delay | delay=3600000 | 600000  | the number of milliseconds to wait before checking for new emails  |
