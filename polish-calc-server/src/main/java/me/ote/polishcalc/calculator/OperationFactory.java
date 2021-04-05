package me.ote.polishcalc.calculator;

import java.util.Arrays;
import java.util.Objects;

public class OperationFactory {

    public static Operation createFromChain(String chain) {
        Objects.requireNonNull(chain);
        return new Operation(Arrays.asList(chain.split(" ")));
    }

}
