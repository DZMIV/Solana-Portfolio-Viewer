package com.github.dzmiv;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ScraperRunnable:
 *  Runs wallet scraping and token metadata fetching in a background thread,
 *  then updates the UI via callbacks.
 */

public class ScraperRunnable implements Runnable {
    private final String walletAddress;
    private final WalletScraper scraper;
    private final Consumer<WalletData> onSuccess;
    private final Consumer<Exception> onError;

    public ScraperRunnable(String walletAddress, WalletScraper scraper, Consumer<WalletData> onSuccess, Consumer<Exception> onError) {
        this.walletAddress = walletAddress;
        this.scraper = scraper;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    public void run() {
    	 try {
    	        WalletScraper.HoldingsResponse holdings = scraper.getWalletData(walletAddress);
    	        double solPrice = scraper.getSolPrice();

    	        List<WalletMetrics> walletMetricsList = new ArrayList<>();

    	        int maxTokens = 30; // Only display data for 30 tokens max.
    	        int count = 0;

    	        for (WalletScraper.Token token : holdings.getTokens()) {
    	            if (count >= maxTokens) break;

    	            WalletScraper.TokenMetadata meta = null;
    	            try {
    	                meta = scraper.getTokenMetadata(token.getMint());
    	            } catch (Exception e) {}   	      

    	            walletMetricsList.add(new WalletMetrics(token, meta));
    	            count++;
    	        }

    	        WalletData walletData = new WalletData(holdings.getSolBalance(), solPrice, walletMetricsList);

    	        Platform.runLater(() -> onSuccess.accept(walletData));

    	    } catch (Exception e) {
    	        Platform.runLater(() -> onError.accept(e));
    	    }
    }

    public static class WalletData {
        private final double solBalance;
        private final double solPrice;
        private final List<WalletMetrics> tokens;

        public WalletData(double solBalance, double solPrice, List<WalletMetrics> tokens) {
            this.solBalance = solBalance;
            this.solPrice = solPrice;
            this.tokens = tokens;
        }

        public double getSolBalance() { return solBalance; }
        public double getSolPrice() { return solPrice; }
        public List<WalletMetrics> getTokens() { return tokens; }
    }
}
