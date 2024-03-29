package io.github.ericdriggs.reportcard.example;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@SuppressWarnings("ALL")
//Only used for test xml generation -- not promotional criteria
@Disabled
public class FaultExamplesTest {

    @Test
    void skipTest() {
        System.out.println("skipTest stdout");
        assumeFalse(true);
    }

    @Test
    void failTest() {
        System.out.println("failTest stdout");
        System.err.println("failTest stderr");
        fail("failure message");
    }

    @Test
    void assertFailTest() {
        System.out.println("assertFailTest stdout");
        System.err.println("assertFailTest stderr");
        assertEquals(1, 2);
    }

    @Test
    void exceptionTest() {
        System.out.println("exceptionTest stdout");
        System.err.println("exceptionTest stderr");
        throw new RuntimeException("I'm sorry, Dave.");
    }

    @Test
    void errorTest() {
        throw new OutOfMemoryError();
    }

}
