package me.ote.polishcalc.calculator;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class CalculatorServiceTest {
    @Inject
    Logger logger;
    @Inject
    CalculatorService calculatorService;

    @Test
    public void operation1() throws BadFormatOperationException {
        String operationStr = "1 2 +";
        Integer result = operation(operationStr);
        logger.info(String.format("%s: %d", operationStr, result));
        Assertions.assertEquals(3, result);
    }

    @Test
    public void operation2() throws BadFormatOperationException {
        String operationStr = "1 2 3 * +";
        Integer result = operation(operationStr);
        logger.info(String.format("%s: %d", operationStr, result));
        Assertions.assertEquals(7, result);
    }

    @Test
    public void operation3() throws BadFormatOperationException {
        String operationStr = "2 3 * 1 +";
        Integer result = operation(operationStr);
        logger.info(String.format("%s: %d", operationStr, result));
        Assertions.assertEquals(7, result);
    }

    @Test
    public void operation4() throws BadFormatOperationException {
        String operationStr = "2 3 * 1 + 2 -";
        Integer result = operation(operationStr);
        logger.info(String.format("%s: %d", operationStr, result));
        Assertions.assertEquals(5, result);
    }


    @Test
    public void operation5() throws BadFormatOperationException {
        String operationStr = "20 5 / 2 1 + * 2 -";
        Integer result = operation(operationStr);
        logger.info(String.format("%s: %d", operationStr, result));
        Assertions.assertEquals(10, result);
    }

    @Test
    public void badOperation1() throws BadFormatOperationException {
        Assertions.assertThrows(BadFormatOperationException.class, () -> {
            String operationStr = "20 -";
            operation(operationStr);
        });
    }

    @Test
    public void badOperation2() throws BadFormatOperationException {
        Assertions.assertThrows(BadFormatOperationException.class, () -> {
            String operationStr = "20 1";
            operation(operationStr);
        });
    }

    @Test
    public void badOperation3() throws BadFormatOperationException {
        Assertions.assertThrows(BadFormatOperationException.class, () -> {
            String operationStr = "20 1 2 -";
            operation(operationStr);
        });
    }

    @Test
    public void badOperation4() throws BadFormatOperationException {
        Assertions.assertThrows(BadFormatOperationException.class, () -> {
            String operationStr = "20 - -";
            operation(operationStr);
        });
    }

    @Test
    public void badOperation5() throws BadFormatOperationException {
        Assertions.assertThrows(BadFormatOperationException.class, () -> {
            String operationStr = "1 + 2 - 3 + 4 / 5";
            operation(operationStr);
        });
    }

    @Test
    public void badOperation6() throws BadFormatOperationException {
        Assertions.assertThrows(BadFormatOperationException.class, () -> {
            String operationStr = "1 2 3 4 5 + - *";
            operation(operationStr);
        });
    }

    @Test
    public void badOperation7() throws BadFormatOperationException {
        Assertions.assertThrows(BadFormatOperationException.class, () -> {
            String operationStr = "1 + - *";
            operation(operationStr);
        });
    }

    private Integer operation(String operationStr) throws BadFormatOperationException {
        Operation operation = OperationFactory.createFromChain(operationStr);
        return calculatorService.calculate(operation);
    }
}
