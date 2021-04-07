package me.ote.polishcalc.client;

import io.quarkus.runtime.Quarkus;

public class Main {

    public static void main(String ... args) {
        Quarkus.run(PolishCalcClientMain.class, args);
    }

}
