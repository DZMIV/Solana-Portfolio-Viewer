# Solana Wallet Viewer

This is a Solana Portfolio Viewer built with JavaFX that allows users to view and track the balances and token holdings of any Solana wallet. The application fetches current JSON wallet data and displays it in a dynamic table and pie chart, making it easy to analyze your portfolio at a glance. The application is structured using Apache Maven, and the user interface was designed using Gluon's SceneBuilder.

Apache Maven is recommended to run the application, as it handles all dependencies automatically. Alternatively, you can manually add the required libraries (such as Jackson) to your project if not using Maven.

## Features
- Displays SOL balance and USD value for a given wallet.
- Lists top token holdings (with name, amount, and logo) in a TableView.
- Dynamic PieChart visualization of top assets.
- Fetches live token metadata from public APIs.
- Background threads ensure the UI remains responsive while fetching data.

## Getting Started

### Prerequisites
- Java 17 or later
- JavaFX SDK 24 (or compatible version)
- Helius account for API key insertion.
- Make sure JavaFX libraries are properly set up in your build environment.

### Running the Application
- Ensure you have pasted your Helius API key into the api key variable located in the WalletScraper Class.
  ```bash
   public static final String API_KEY = "PASTE KEY HERE";

#### Option 1: Through an IDE
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Solana-portfolio-explorer.git
   cd Solana-portfolio-explorer
   
2. Add the required dependencies manually:
   - JavaFX SDK 24
   - Jackson libraries (jackson-core, jackson-databind, jackson-annotations)
   
4. Open the project in your IDE (e.g., IntelliJ IDEA or Eclipse).
   
5. Run the Main class located in:
   ```bash
   src/main/java/com/github/dzmiv/Main.java
#### Option 2: With Maven
1. If you have Maven installed, you can run the application directly from the terminal:
   ```bash
   mvn clean javafx:run
## Notes
Work in Progress (WIP): This project is currently under active development. Some features may be subject to change. For example, The UI is still being refined for responsiveness and usability. Additional sorting, filtering, or portfolio analytics features may be added in future updates. Make sure to provide your own Helius API key as the default API_KEY is empty.
