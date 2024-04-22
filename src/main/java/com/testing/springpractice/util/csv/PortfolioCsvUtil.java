package com.testing.springpractice.util.csv;

import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PortfolioCsvUtil {

    public static void writePortfolioToCsvServerSide(String filePath, List<PortfolioEntity> portfolios) {
        try {
           Files.createFile(Path.of(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .builder()
                     .setHeader("ID", "Name", "Time Range", "Risk Profile", "Advisor name", "Asset1", "Asset2", "Asset3", "Asset4", "Asset5")
                     .build())) {
            for (PortfolioEntity portfolio : portfolios) {
                List<AssetHoldingEntity> assets = portfolio.getAssets();
                Object[] asset = new Object[5];
                for (int i = 0; i < 5; i++) {
                    if (i < assets.size()) {
                        asset[i] = assets.get(i).getCode();
                    } else {
                        asset[i] = "";
                    }
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
            e.printStackTrace();
        }

    }


    public static void writePortfoliosToCsv(HttpServletResponse response, List<PortfolioEntity> portfolios) {
        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT
                .builder()
                .setHeader("ID", "Name", "Time Range", "Risk Profile", "Advisor name", "Asset1", "Asset2", "Asset3", "Asset4", "Asset5")
                .build())) {
            for (PortfolioEntity portfolio : portfolios) {
                List<AssetHoldingEntity> assets = portfolio.getAssets();
                Object[] asset = new Object[5];
                for (int i = 0; i < 5; i++) {
                    if (i < assets.size()) {
                        asset[i] = assets.get(i).getCode();
                    } else {
                        asset[i] = "";
                    }
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
            e.printStackTrace();
        }
    }
}
