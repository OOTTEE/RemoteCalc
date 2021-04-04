package me.ote.polishcalc;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import me.ote.polishcalc.server.Server;
import org.jboss.logging.Logger;

import javax.inject.Inject;

@QuarkusMain
public class Main {

    public static void main(String... args) {
        Quarkus.run(PolishCalcMain.class, args);
    }

    public static class PolishCalcMain implements QuarkusApplication {
        @Inject
        Logger log;
        @Inject
        Server server;

        @Override
        public int run(String... args) throws Exception {
            log.info("MAIN START");
            server.start();
            Quarkus.waitForExit();
            log.info("MAIN STOP");
            return 0;
        }
    }

}
