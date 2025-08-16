package com.github.dzmiv;

/**
 * WalletMetrics:
 * responsible for holding data for a single token in the wallet.
 */

public class WalletMetrics {
    private String mint;
    private String name;
    private String assetLogo;
    private double amount;

    public WalletMetrics(String mint, String name, String assetLogo, double amount) {
        this.mint = mint;
        this.name = name;
        this.assetLogo = assetLogo;
        this.amount = amount;
    }

    public WalletMetrics(WalletScraper.Token token, WalletScraper.TokenMetadata meta) {
        if (meta != null) {
            this.name = meta.getName();
        } else {
            this.name = "Unknown Token";
        }
        this.amount = token.getAmountHeld();
        this.mint = token.getMint();
        this.assetLogo = meta.getLogoUrl();
    }

    public String getMint() {
        return mint;
    }

    public String getName() {
        return name;
    }
    
    public double getAmount() {
        return amount;
    }

	public String getAssetLogo() {
		return assetLogo;
	}
}