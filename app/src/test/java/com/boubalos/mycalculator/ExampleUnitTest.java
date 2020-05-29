package com.boubalos.mycalculator;

import com.boubalos.mycalculator.Utils.Utils;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {


        assertEquals(1.0,  Utils.Calculate("(1+9)*(4+5)"));
    }
}