package de._125m125.trelloMail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.mail.MessagingException;

public class TrelloMail {
    private static MailManager         receiver;
    private static MailManager         sender;

    private static String              targetMail;
    private static final AtomicBoolean stop = new AtomicBoolean(false);
    private static Thread              th;

    public static void main(final String[] args) throws MessagingException, IOException {
        final TrelloMailConfig cfg = new TrelloMailConfig("config.properties");
        TrelloMail.targetMail = cfg.getProperty("targetMail", true);
        final long delay = Long.parseLong(cfg.getPropertyOrDefault("delay", "600000"));
        TrelloMail.receiver = new MailManager(cfg.getProperty("recServer", true), cfg.getProperty("recUser", true),
                cfg.getProperty("recPass", true), cfg.getProperty("recPort", true));
        TrelloMail.sender = new MailManager(cfg.getProperty("sendServer", true), cfg.getProperty("sendUser", true),
                cfg.getProperty("sendPass", true), cfg.getProperty("sendPort", true));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                TrelloMail.stop.set(true);
                TrelloMail.th.interrupt();
                System.out.println("awaiting termination...");
                try {
                    TrelloMail.th.join();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("terminated");
            }
        });

        TrelloMail.th = new Thread(() -> {
            while (true) {
                readAndSend();
                if (TrelloMail.stop.get()) {
                    break;
                }
                try {
                    Thread.sleep(delay);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                if (TrelloMail.stop.get()) {
                    break;
                }
            }
        });
        TrelloMail.th.start();

    }

    private static void readAndSend() {
        Date d = null;
        final File f = new File("lastCheck.txt");
        if (f.exists()) {
            try (BufferedReader in = new BufferedReader(new FileReader(f))) {
                d = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss").parse(in.readLine());
            } catch (final FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (final IOException e1) {
                e1.printStackTrace();
            } catch (final ParseException e1) {
                e1.printStackTrace();
            }
        }
        if (d == null) {
            d = new Date();
        }
        final List<Todo> receive = TrelloMail.receiver.receive(d);
        Date latest = d;
        for (final Todo t : receive) {
            if (t.getDate() != null) {
                latest = t.getDate();
            }
            System.out.println(latest);
            TrelloMail.sender.send(t, TrelloMail.targetMail);
        }
        try (BufferedWriter out = new BufferedWriter(new FileWriter(f))) {
            out.write(new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss").format(latest));
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
    }
}
