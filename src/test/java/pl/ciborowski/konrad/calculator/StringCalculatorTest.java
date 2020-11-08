package pl.ciborowski.konrad.calculator;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static pl.ciborowski.konrad.calculator.StringCalculator.NEGATIVE_NUMBER_ERROR_MESSAGE;

public class StringCalculatorTest {
    
    private StringCalculator calculator;
    
    @BeforeEach
    public void setUp() {
        calculator = new StringCalculator();
    }
    
    @Test
    public void testValidCasesForCommaAsDelimiter() {      
        assertEquals(0, calculator.add(""));
        assertEquals(5, calculator.add("5"));
        assertEquals(7, calculator.add("4,3"));
        assertEquals(30, calculator.add("5,10,15"));
        assertEquals(13, calculator.add("2\n1555,11")); // New line us ised as the first delimiter
    }

    @Test
    public void testValidCasesForVariousDelimiters() {
        assertEquals(5, calculator.add("//[***]\\n5"));
        assertEquals(7, calculator.add("//[&&]\\n4&&3"));
        assertEquals(30, calculator.add("//[uue]\\n5uue10uue15"));
        assertEquals(13, calculator.add("//[%@]\\n2\n1001%@11")); // New line us ised as the first delimiter
        assertEquals(1013, calculator.add("//[%@]\\n2\n1000%@11")); // New line us ised as the first delimiter
    }
    
    @Test
    public void testValidCasesForMultipleDelimiters() {
        assertEquals(5, calculator.add("//[**][ff]\\n5"));
        
        assertEquals(7, calculator.add("//[&&][**]\\n4**3"));
        assertEquals(30, calculator.add("//[uue][--]\\n5uue10--15"));
        assertEquals(23, calculator.add("//[TR][ZZ§][%@]\\n2TR1555ZZ§11\n10%@0")); // New line us ised as the first delimiter
    }

    @Test
    public void testCornerCases() {
        assertEquals(22, calculator.add("//[***][*]\\n5*7***10")); // One delimeter is a subset of another        
        assertThrows(IllegalArgumentException.class, () -> calculator.add("//[***][**]\\n5****6")); // First *** is applied which results is an error
    }

    @Test
    public void testForExceptionsForNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calculator.add("5,-3,10,-5"));   
        assertEquals(NEGATIVE_NUMBER_ERROR_MESSAGE + "-3,-5", exception.getMessage());
    }
        
    @Test
    public void testForExceptionsForNumbersAsDelimiters() {
        assertThrows(IllegalArgumentException.class, () -> calculator.add("//[b4f]\\n"));   
    }

    @Test
    public void testForExceptionsForIncorrectFormat() {
        assertThrows(IllegalArgumentException.class, () -> calculator.add("//[b4f]")); // no delimeter terminator
        assertThrows(IllegalArgumentException.class, () -> calculator.add("4,,4")); // no number between delimiters 
        assertThrows(IllegalArgumentException.class, () -> calculator.add("5,4,e4"));  // Illegal number format
    }
}
