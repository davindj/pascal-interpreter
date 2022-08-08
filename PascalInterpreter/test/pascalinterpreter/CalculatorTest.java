/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package pascalinterpreter;

import java.util.ArrayList;
import java.util.Dictionary;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import org.junit.*;
import java.util.Hashtable;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Personal
 */
public class CalculatorTest {
    Calculator calc;
    
    public CalculatorTest() {
    
    }

    /**
     * Test of result method, of class Calculator.
     */
    @Test
    public void testGenerateFixedInfix1() {        
        calc = new Calculator(new Hashtable<>());
        Assert.assertArrayEquals(new String[]{"1","+","2","*","(","6","-","2",")","$"}, calc.generateFixedInfix("1+2*(6-2)").toArray());
    }
    @Test
    public void testGenerateFixedInfix2() {        
        calc = new Calculator(new Hashtable<>());
        Assert.assertArrayEquals(new String[]{"-1","*","2","$"}, calc.generateFixedInfix("-2").toArray());
    }
    @Test
    public void testGenerateFixedInfix3() {
        calc = new Calculator(new Hashtable<>());
        Assert.assertArrayEquals(new String[]{"-1","*","2","+","3","$"}, calc.generateFixedInfix("-2+3").toArray());
    }
    @Test
    public void testGenerateFixedInfix4() {
        Dictionary<String, Object> var = new Hashtable<>();
        calc = new Calculator(var);
        var.put("a", "2");
        Assert.assertArrayEquals(new String[]{"2","+","2","$"}, calc.generateFixedInfix("a+a").toArray());
    }
    @Test
    public void testGenerateFixedInfix5() {
        Dictionary<String, Object> var = new Hashtable<>();
        calc = new Calculator(var);
        var.put("n", "1");
        var.put("na", "2");
        var.put("nani", "3");
        Assert.assertArrayEquals(new String[]{"3","+","2","+","1","$"}, calc.generateFixedInfix("nani+na+n").toArray());
    }
}
