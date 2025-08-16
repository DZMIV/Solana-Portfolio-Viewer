package com.github.dzmiv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller {
	@FXML
	Label errorLabel;
	@FXML
    private Label solValueLabel;
	@FXML
	private Label solBalanceLabel;
	@FXML
	private PieChart holdingsPieChart;
	@FXML
    private TableView<WalletMetrics> holdingsTable;
    @FXML
    private TableColumn<WalletMetrics, String> mintColumn;
    @FXML
    private TableColumn<WalletMetrics, String> nameColumn;
    @FXML
    private TableColumn<WalletMetrics, String> logoColumn;
    @FXML
    private TableColumn<WalletMetrics, Double> amountColumn;
    @FXML
    private TextField walletAddressField;
    
	WalletScraper scraper = new WalletScraper();
		
	public void initialize() {
	    mintColumn.setCellValueFactory(new PropertyValueFactory<>("mint"));
	    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
	    amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
	    logoColumn.setCellValueFactory(new PropertyValueFactory<>("assetLogo"));
	    
	    // Custom cell factory to display token images.
	    logoColumn.setCellFactory(col -> new TableCell<WalletMetrics, String>() {
	        private final ImageView imageView = new ImageView();
	        {
	            imageView.setFitHeight(24);
	            imageView.setFitWidth(24);
	        }

	        @Override
	        protected void updateItem(String logoUrl, boolean empty) {
	            super.updateItem(logoUrl, empty);
	            if (empty || logoUrl == null || logoUrl.isEmpty()) {
	                setGraphic(null);
	            } else {
	                imageView.setImage(new Image(logoUrl, true));
	                setGraphic(imageView);
	            }
	        }
	    });

	    // Trigger data load when the user presses Enter.
	    walletAddressField.setOnAction(event -> handleStart());
	}
	
	// Called when user enters a wallet address
	private void handleStart() {
	    String walletAddress = walletAddressField.getText().trim();
	    if (!walletAddress.isEmpty()) {
	        loadData(walletAddress);
	    } else {
	        errorLabel.setText("Please enter a wallet address.");
	    }
	}
	
	// Load wallet data asynchronously
	public void loadData(String walletAddress) {
		System.out.println("loadData called with wallet: " + walletAddress);
	    solBalanceLabel.setText("Loading...");
	    errorLabel.setText("");
	    
	    // Runnable to fetch wallet data in background
	    ScraperRunnable task = new ScraperRunnable(
	        walletAddress,
	        scraper,
	        walletData -> {  
	            try {
	            	// Update UI labels and table with retrieved data
	            	solBalanceLabel.setText("SOL Balance: " + String.format("%,d", Math.round(walletData.getSolBalance())));
	            	solValueLabel.setText("Value: ($" + String.format("%,d", Math.round(walletData.getSolBalance() * walletData.getSolPrice())) + " USD)");
	            	
	                holdingsTable.getItems().setAll(walletData.getTokens());
	                holdingsChart(holdingsPieChart);
	                
	            } catch (Exception e) {
	                solBalanceLabel.setText("Error");
	                errorLabel.setText("Failed to update UI: " + e.getMessage());
	                e.printStackTrace(); 
	            }
	        },
	        e -> { // Handle fetch errors
	            solBalanceLabel.setText("Error");
	            errorLabel.setText("Failed to fetch wallet data: " + e.getMessage());
	        }
	    );
	    
	    // Run task on a background thread
	    Thread thread = new Thread(task);
	    thread.setDaemon(true);
	    thread.start();
	    System.out.println("Thread started");
	    
	}
	
	// Populate the pie chart with top token holdings
	public void holdingsChart(PieChart pieChart) {
		ObservableList<WalletMetrics> allHoldings = holdingsTable.getItems();

	    List<WalletMetrics> holdingsList = new ArrayList<>(allHoldings);
	    
	    Collections.sort(holdingsList, new AssetCompare());
	    
	    // Take top 10 holdings
	    List<WalletMetrics> topHoldings = new ArrayList<>();
	    int count = 0;
	    for (WalletMetrics wm : holdingsList) {
	        if (count >= 10) break;
	        topHoldings.add(wm);
	        count++;
	    }
	    
	    // Convert to PieChart.Data
	    ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
	    for (WalletMetrics wm : topHoldings) {
	        pieData.add(new PieChart.Data(wm.getName(), wm.getAmount()));
	    }

	    holdingsPieChart.setData(pieData);
	}
}