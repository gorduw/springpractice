package com.testing.springpractice.util.csv;

import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PortfolioCsvUtil {

    private PortfolioCsvUtil() {
    }

    private static final int ASSET_COLUMNS_LIMIT = 5;


    //Better for big files
    public static Path writePortfolioToCsvServerSide(final List<PortfolioEntity> portfolios) {
        try {
            // Create a temporary file with a specified prefix and suffix.
            Path tempFile = Files.createTempFile("portfolio_", ".csv");

            // Use try-with-resources to ensure proper resource management.
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                         .builder()
                         .setHeader("ID", "Name", "Time Range", "Risk Profile", "Advisor Name", "Asset1", "Asset2", "Asset3", "Asset4", "Asset5")
                         .build())) {

                for (PortfolioEntity portfolio : portfolios) {
                    List<AssetHoldingEntity> assets = portfolio.getAssets();
                    Object[] asset = new Object[ASSET_COLUMNS_LIMIT];

                    // Fill asset array with the asset codes or empty strings if fewer assets are available.
                    for (int i = 0; i < ASSET_COLUMNS_LIMIT; i++) {
                        asset[i] = (i < assets.size()) ? assets.get(i).getCode() : "";
                    }

                    csvPrinter.printRecord(
                            portfolio.getId(),
                            portfolio.getName(),
                            portfolio.getTimeRange().name(),
                            portfolio.getRiskProfile().name(),
                            portfolio.getAdvisorEntity().getName(),
                            asset[0], asset[1], asset[2], asset[3], asset[4]
                    );
                }

                csvPrinter.flush();
            }

            return tempFile.toAbsolutePath();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return null;
    }

    //Good for small files (after calculation etc)
    public static void writePortfoliosToCsv(final HttpServletResponse response, final List<PortfolioEntity> portfolios) {
        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT
                .builder()
                .setHeader("ID", "Name", "Time Range", "Risk Profile", "Advisor name", "Asset1", "Asset2", "Asset3", "Asset4", "Asset5")
                .build())) {

            for (PortfolioEntity portfolio : portfolios) {
                List<AssetHoldingEntity> assets = portfolio.getAssets();
                Object[] asset = new Object[ASSET_COLUMNS_LIMIT];
                for (int i = 0; i < ASSET_COLUMNS_LIMIT; i++) {
                    asset[i] = (i < assets.size()) ? assets.get(i).getCode() : "";
                }

                csvPrinter.printRecord(
                        portfolio.getId(),
                        portfolio.getName(),
                        portfolio.getTimeRange().name(),
                        portfolio.getRiskProfile().name(),
                        portfolio.getAdvisorEntity().getName(),
                        asset[0], asset[1], asset[2], asset[3], asset[4]
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "CSV Generation Error");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
