package com.testing.springpractice;


import com.testing.springpractice.util.csv.PortfolioCsvUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UtilTest {

    @Test
    public void testPortfolio() {
        var testResult = PortfolioCsvUtil.writePortfolioToCsvServerSide(List.of());
        Assertions.assertNotNull(testResult);
    }
}
