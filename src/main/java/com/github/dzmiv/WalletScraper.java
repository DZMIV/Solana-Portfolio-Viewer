package com.github.dzmiv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WalletScraper {
	
	//Paste your Helius API key here
	public static final String API_KEY = "";
	
	public static final String HELIUS_API = "https://api.helius.xyz/v0";
	private final HttpClient client = HttpClient.newBuilder().build();
	private final ObjectMapper mapper = new ObjectMapper();
	
	
	// Fetches wallet balances for a given Solana wallet address.
	public HoldingsResponse getWalletData(String walletAddress) throws Exception {
		if (API_KEY == null || API_KEY.isEmpty()) {
	        throw new IllegalStateException("API_KEY is not set.");
	    }
		
        String url = HELIUS_API + "/addresses/" + walletAddress + "/balances?api-key=" + API_KEY;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new Exception("HTTP error: " + response.statusCode());
        }
        
        // Parse JSON into objects 
        HoldingsResponse holdingsResponse = mapper.readValue(response.body(), HoldingsResponse.class);
       
        return holdingsResponse;
    }
	
	// Fetches the current Solana price in USD from Binance.
	public double getSolPrice() throws Exception {
	    String url = "https://api.binance.com/api/v3/ticker/price?symbol=SOLUSDT";
	    HttpRequest request = HttpRequest.newBuilder()
	            .GET()
	            .uri(URI.create(url))
	            .build();
	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	    if (response.statusCode() != 200) {
	        throw new Exception("HTTP error: " + response.statusCode());
	    }
	    
	    // Parse JSON to extract the price
	    JsonNode node = mapper.readTree(response.body());
	    String strPrice = node.get("price").asText();
	    double solPrice = Double.parseDouble(strPrice);
	    return solPrice;
	}
	
	// Fetches metadata for a given token mint address.
	public TokenMetadata getTokenMetadata(String mintAddress) throws Exception {
	    String url = "https://lite-api.jup.ag/tokens/v2/search?query=" + mintAddress;

	    HttpRequest request = HttpRequest.newBuilder()
	        .GET()
	        .uri(URI.create(url))
	        .build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	    if (response.statusCode() != 200) {
	        throw new Exception("HTTP error: " + response.statusCode());
	    }
	    
	    // Parse JSON array of token metadata
	    TokenMetadata[] results = mapper.readValue(response.body(), TokenMetadata[].class);

	    if (results.length > 0) {
	        return results[0];
	    }

	    return null; 
	}
	
	// Converts holdings response into a list of WalletMetrics objects.
	public List<WalletMetrics> getAssetInfo(HoldingsResponse holdings) throws Exception {
	    List<WalletMetrics> results = new ArrayList<>();
	    int maxTokens = 10;
	    int count = 0;
	    
	    for (Token token : holdings.getTokens()) {
	        if (count >= maxTokens) break;

	        TokenMetadata meta = null;
	        try {
	            meta = getTokenMetadata(token.getMint());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        // Fallback if metadata is null.
	        String name = (meta != null && meta.getName() != null) ? meta.getName() : "Unknown";
	        String logoUrl = (meta != null && meta.getLogoUrl() != null) ? meta.getLogoUrl() : "Unknown";
	    
	        // Create WalletMetrics objects for the controller.
	        results.add(new WalletMetrics(token.getMint(), name, logoUrl, token.getAmountHeld()));
	        count++;
	    }

	    return results;
	}
	
	// POJO CLASSES: Data containers for JSON mapping.
	public static class HoldingsResponse {
        private long nativeBalance;
        private List<Token> tokens;

        public long getNativeBalance() {
            return nativeBalance;
        }

        public void setNativeBalance(long nativeBalance) {
            this.nativeBalance = nativeBalance;
        }

        public List<Token> getTokens() {
            return tokens;
        }

        public void setTokens(List<Token> tokens) {
            this.tokens = tokens;
        }

        public double getSolBalance() {
            return nativeBalance / 1_000_000_000.0; 
        }
    }

    public static class Token {
        private String tokenAccount;
        private String mint;
        private long amount;
        private int decimals;

        public String getTokenAccount() {
            return tokenAccount;
        }

        public void setTokenAccount(String tokenAccount) {
            this.tokenAccount = tokenAccount;
        }

        public String getMint() {
            return mint;
        }

        public void setMint(String mint) {
            this.mint = mint;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public int getDecimals() {
            return decimals;
        }

        public void setDecimals(int decimals) {
            this.decimals = decimals;
        }

        public double getAmountHeld() {
            return amount / Math.pow(10, decimals);
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenMetadata {
        private String name;
        private String icon;
       
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
        @JsonProperty("icon")
        public String getLogoUrl() {
            return icon;
        }
        
        @JsonProperty("icon")
        public void setLogoUrl(String icon) {
            this.icon = icon;
        }
    }
}