package me.ote.polishcalc;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
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
        RemotePolishCalculator remotePolishCalculator;

        @Override
        public int run(String... args) throws Exception {
            log.info("MAIN START");
            remotePolishCalculator.run();
            Quarkus.waitForExit();
            log.info("MAIN STOP");
            return 0;
        }
    }

}
