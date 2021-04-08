package me.ote.polishcalc.client;


import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@ApplicationScoped
public class StdinReader {
    @Inject
    Logger logger;

    private StdinReaderThread readerThread;

    void read(Consumer<String> lineConsumer) {
        this.readerThread = new StdinReaderThread(lineConsumer);
        Executors.newSingleThreadExecutor().execute(this.readerThread);
    }

    void close() {
        this.readerThread.stop();
    }

    public class StdinReaderThread implements Runnable {
        AtomicBoolean running = new AtomicBoolean(true);
        private Consumer<String> lineConsumer;

        public StdinReaderThread(Consumer<String> lineConsumer) {
            this.lineConsumer = lineConsumer;
        }

        @Override
        public void run() {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
                synchronized (this) {
                    while (running.get()) {
                        String line = bufferedReader.readLine().trim();
                        this.lineConsumer.accept(line);
                        Thread.sleep(50);
                    }
                }
            } catch (InterruptedException | IOException e) {
                StdinReader.this.logger.error("Exception on StdinReaderThread");
                Thread.currentThread().interrupt();
            }
        }

        public void stop() {
            running.set(false);
            synchronized (this) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
