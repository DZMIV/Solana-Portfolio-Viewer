package com.github.dzmiv;

import java.util.Comparator;

public class AssetCompare implements Comparator<WalletMetrics> {
	@Override
    public int compare(WalletMetrics a, WalletMetrics b) {
        return Double.compare(b.getAmount(), a.getAmount());
    }
}
