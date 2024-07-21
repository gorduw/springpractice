package com.testing.springpractice.junit;


import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.repository.enums.RiskProfileEnum;
import com.testing.springpractice.repository.enums.TimeRangeEnum;
import com.testing.springpractice.util.csv.PortfolioCsvUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UtilTest {


    @Mock
    private PortfolioCsvUtil portfolioCsvUtil;

    @Test
    void testWritePortfolioToCsvServerSide() {
        PortfolioEntity portfolio = createTestPortfolio();
        List<PortfolioEntity> portfolios = Collections.singletonList(portfolio);

        Path csvFile = PortfolioCsvUtil.writePortfolioToCsvServerSide(portfolios);
        assertNotNull(csvFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile.toFile()))) {
            String header = reader.readLine();
            assertEquals("ID,Name,Time Range,Risk Profile,Advisor Name,Asset1,Asset2,Asset3,Asset4,Asset5", header);

            String record = reader.readLine();
            assertNotNull(record);
            String[] values = record.split(",", -1);
            assertEquals("1", values[0]);
            assertEquals("Test Portfolio", values[1]);
            assertEquals("MORE_THAN_TEN", values[2]);
            assertEquals("L", values[3]);
            assertEquals("Test Advisor", values[4]);
            assertEquals("Asset1", values[5]);
            assertEquals("Asset2", values[6]);
            assertEquals("", values[7]);
            assertEquals("", values[8]);
            assertEquals("", values[9]);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        } finally {
            try {
                Files.deleteIfExists(csvFile);
            } catch (IOException e) {
                System.err.println("IOException: " + e);
            }

        }

    }

    @Test
    void testWritePortfolioToCsvServerSideIOException() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createTempFile(anyString(), anyString())).thenThrow(new IOException("Mocked IO Exception"));

            Path result = portfolioCsvUtil.writePortfolioToCsvServerSide(Collections.emptyList());

            // Assert that the result is null indicating that the method handled the IOException correctly (return null)
            assertNull(result);
        }
    }

    @Test
    void testWritePortfoliosToCsv() {
        PortfolioEntity portfolio = createTestPortfolio();
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("responseWriter.txt");
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFound: " + e);
        }
        try {
            when(response.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            System.err.println("IOException: " + e);

        }

        PortfolioCsvUtil.writePortfoliosToCsv(response, Collections.singletonList(portfolio));
        writer.flush();
        writer.close();

        try (BufferedReader reader = new BufferedReader(new FileReader("responseWriter.txt"))) {
            String header = reader.readLine();
            assertEquals("ID,Name,Time Range,Risk Profile,Advisor name,Asset1,Asset2,Asset3,Asset4,Asset5", header);

            String record = reader.readLine();
            assertNotNull(record);
            String[] values = record.split(",", -1);
            assertEquals("1", values[0]);
            assertEquals("Test Portfolio", values[1]);
            assertEquals("MORE_THAN_TEN", values[2]);
            assertEquals("L", values[3]);
            assertEquals("Test Advisor", values[4]);
            assertEquals("Asset1", values[5]);
            assertEquals("Asset2", values[6]);
            assertEquals("", values[7]);
            assertEquals("", values[8]);
            assertEquals("", values[9]);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        } finally {
            try {
                Files.deleteIfExists(Path.of("responseWriter.txt"));
            } catch (IOException e) {
                System.err.println("IOException: " + e);
            }
        }

    }

    @Test
    void testWritePortfoliosToCsvIOException() {
        PortfolioEntity portfolio = createTestPortfolio();
        HttpServletResponse response = mock(HttpServletResponse.class);

        try {
            when(response.getWriter()).thenThrow(new IOException("Mocked IO Exception"));
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        PortfolioCsvUtil.writePortfoliosToCsv(response, Collections.singletonList(portfolio));


        try {
            verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "CSV Generation Error");
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

    }

    private PortfolioEntity createTestPortfolio() {
        AdvisorEntity advisor = new AdvisorEntity();
        advisor.setName("Test Advisor");

        AssetHoldingEntity asset1 = new AssetHoldingEntity();
        asset1.setCode("Asset1");

        AssetHoldingEntity asset2 = new AssetHoldingEntity();
        asset2.setCode("Asset2");

        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);
        portfolio.setName("Test Portfolio");
        portfolio.setTimeRange(TimeRangeEnum.TimeRange.MORE_THAN_TEN);
        portfolio.setRiskProfile(RiskProfileEnum.RiskProfile.L);
        portfolio.setAdvisorEntity(advisor);
        portfolio.setAssets(Arrays.asList(asset1, asset2));

        return portfolio;
    }
}
