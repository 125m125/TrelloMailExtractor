package de._125m125.trelloMail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailManager {
    private final String server;

    private final String username;

    private final String password;

    private final String port;

    public MailManager(final String server, final String username, final String password, final String port) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.port = port;

    }

    public List<Todo> receive(final Date after) {
        final List<Todo> todos = new ArrayList<>();
        Folder emailFolder = null;
        Store store = null;
        try {
            final Properties properties = new Properties();

            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", this.server);
            properties.put("mail.pop3.port", this.port);
            properties.put("mail.pop3.starttls.enable", "true");
            final Session emailSession = Session.getDefaultInstance(properties);

            store = emailSession.getStore("pop3s");
            store.connect(this.server, this.username, this.password);

            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            final Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (final Message message : messages) {
                if (message.getSentDate().compareTo(after) > 0) {
                    todos.addAll(extractTodos(message, after));
                }
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        // close the store and folder objects
        if (emailFolder != null && emailFolder.isOpen()) {
            try {
                emailFolder.close(false);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        if (store != null) {
            try {
                store.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        return todos;
    }

    public boolean send(final Todo t, final String target) {
        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", this.server);
        props.put("mail.smtp.port", this.port);

        final Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailManager.this.username, MailManager.this.password);
            }
        });
        try {
            // Create a default MimeMessage object.
            final Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(this.username));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(target));

            // Set Subject: header field
            message.setSubject(t.getTitle());

            // Now set the actual message
            message.setText(t.getContent());

            // Send message
            Transport.send(message);
            System.out.println("send success");
            return true;

        } catch (final MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<Todo> extractTodos(final Part p, Date d) throws MessagingException, IOException {
        if (p instanceof Message) {
            d = ((Message) p).getSentDate();
        }
        final List<Todo> todos = new ArrayList<>();

        final Object content = p.getContent();
        if (p.isMimeType("multipart/*")) {
            final Multipart mp = (Multipart) content;
            final int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                todos.addAll(extractTodos(mp.getBodyPart(i), d));
            }
        } else if (p.isMimeType("message/rfc822")) {
            todos.addAll(extractTodos((Part) p.getContent(), d));
        } else if (p.isMimeType("text/plain")) {
            todos.addAll(extractTodos((String) content, d));
        } else {
            todos.addAll(extractTodos(content.toString(), d));
        }

        return todos;
    }

    public List<Todo> extractTodos(final String s, final Date d) {
        final List<Todo> todos = new ArrayList<>();
        final String[] parts = s.split("\\r?\\n|\\r");
        int mode = 0;
        String title = "";
        String content = "";
        System.out.println("#########newString");
        for (final String part : parts) {
            if (part.isEmpty()) {
                System.out.println(mode + " 0 " + part);
                if (mode > 0) {
                    if (!title.isEmpty()) {
                        todos.add(new Todo(title, content, d));
                    }
                    mode = 1;
                    title = "";
                    content = "";
                }
            } else if (part.toLowerCase().matches("<?todo(start)?:?\\W*")) {
                System.out.println(mode + " 1 " + part);
                if (!title.isEmpty()) {
                    todos.add(new Todo(title, content, d));
                }
                mode = 1;
                title = "";
                content = "";
            } else if (part.toLowerCase().matches("(ende?todo|todoende?|<\\/todo(start)?>)\\W*")) {
                System.out.println(mode + " 2 " + part);
                System.out.println(part);
                if (!title.isEmpty()) {
                    todos.add(new Todo(title, content, d));
                }
                mode = 0;
                title = "";
                content = "";
            } else {
                System.out.println(mode + " 3 " + part);
                if (mode == 2) {
                    content += part + "\n";
                } else if (mode == 1) {
                    title = part;
                    mode = 2;
                }
            }
        }
        if (mode == 2) {
            todos.add(new Todo(title, content, d));
        }
        return todos;
    }
}
