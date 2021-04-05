package me.ote.polishcalc.calculator;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ApplicationScoped
public class CalculatorService {

    public Integer calculate(Operation operation) throws BadFormatOperationException {

        return resolve(operation.getChain().iterator(),  new ArrayList<>());
    }

    private Integer resolve(Iterator<String> iterator, List<String> resolutionChain) throws BadFormatOperationException {
        if(iterator.hasNext()) {
            resolutionChain.add(iterator.next());
        } else if (resolutionChain.size() > 1) {
            throw new BadFormatOperationException();
        }
        if(resolutionChain.size() >= 3) {
            try {
                Integer resultado = calc(resolutionChain);
                resolutionChain.remove(resolutionChain.size()-3);
                resolutionChain.remove(resolutionChain.size()-2);
                resolutionChain.remove(resolutionChain.size()-1);
                resolutionChain.add(String.format("%d", resultado));
                return resolve(iterator, resolutionChain);
            } catch (IllegalArgumentException e) {
                return resolve(iterator, resolutionChain);
            }
        }
        if(iterator.hasNext()) {
            return resolve(iterator, resolutionChain);
        }
        if(resolutionChain.size() == 1) {
            return Integer.parseInt(resolutionChain.get(0));
        } else {
            throw new BadFormatOperationException();
        }

    }

    private Integer calc(List<String> chain) {
        String op1 = chain.get(chain.size()-3);
        String op2 = chain.get(chain.size()-2);
        String op3 = chain.get(chain.size()-1);
        if (op3.equals(Operators.ADD)) {
            return Integer.parseInt(op1) + Integer.parseInt(op2);
        } else if ( op3.equals(Operators.SUBS) ) {
            return Integer.parseInt(op1) - Integer.parseInt(op2);
        } else if ( op3.equals(Operators.MUL) ) {
            return Integer.parseInt(op1) * Integer.parseInt(op2);
        } else if ( op3.equals(Operators.DIV) ) {
            return Integer.parseInt(op1) / Integer.parseInt(op2);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
