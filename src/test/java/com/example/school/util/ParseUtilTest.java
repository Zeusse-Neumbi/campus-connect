package com.example.school.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParseUtilTest {

    @Test
    void parseOptionalInt_ShouldReturnParsedValue_WhenValidStringIsProvided() {
        assertEquals(42, ParseUtil.parseOptionalInt("42", 0));
        assertEquals(-10, ParseUtil.parseOptionalInt("-10", 0));
    }

    @Test
    void parseOptionalInt_ShouldReturnDefaultValue_WhenStringIsNull() {
        assertEquals(5, ParseUtil.parseOptionalInt(null, 5));
    }

    @Test
    void parseOptionalInt_ShouldReturnDefaultValue_WhenStringIsEmptyOrBlank() {
        assertEquals(10, ParseUtil.parseOptionalInt("", 10));
        assertEquals(15, ParseUtil.parseOptionalInt("   ", 15));
    }

    @Test
    void parseOptionalInt_ShouldReturnDefaultValue_WhenStringIsInvalidNumber() {
        assertEquals(20, ParseUtil.parseOptionalInt("abc", 20));
        assertEquals(25, ParseUtil.parseOptionalInt("42.5", 25)); // Not an integer
    }
}
