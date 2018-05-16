package hu.bme.aut.digikaland.utility;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

public class DistanceCalculatorTest {

    @Test
    public void calculate() {
        double result = DistanceCalculator.calculate(47.4730, 19.0595, 47.4829, 19.0543);
        Assert.assertEquals(1168.0, result, 2);
    }
}