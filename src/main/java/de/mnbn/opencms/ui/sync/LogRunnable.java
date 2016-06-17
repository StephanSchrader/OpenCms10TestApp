package de.mnbn.opencms.ui.sync;

import com.vaadin.ui.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by schrader on 17.06.16.
 */
public class LogRunnable implements Runnable {

    private static final long POLL_MS = 1000;

    private boolean running = false;

    private String logfile;

    private Label log;

    public LogRunnable(String logfile, Label log) {
        this.logfile = logfile;
        this.log = log;
    }

    public void run() {

        running = true;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(logfile));
            String line;
            while (running) {
                line = reader.readLine();
                if (line == null) {
                    // wait until there is more lines in the file
                    Thread.sleep(POLL_MS);
                } else {
                    // append to the log Label
                    //synchronized (MyApplication.this) {
                        log.setValue(log.getValue() + line + "<br />");
                    //}
                }
            }
        } catch (IOException e) {
            // TODO: handle me
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO: handle me
            e.printStackTrace();
        } finally {
            running = false;
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
        }

    }

}
