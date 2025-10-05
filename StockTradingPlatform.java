import java.util.*;
import java.time.LocalDateTime;

public class StockTradingPlatform {

    // === Stock Class ===
    static class Stock {
        private String symbol;
        private String name;
        private double price;

        public Stock(String symbol, String name, double price) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
        }

        public String getSymbol() { return symbol; }
        public String getName() { return name; }
        public double getPrice() { return price; }

        public void setPrice(double price) { this.price = price; }

        @Override
        public String toString() {
            return symbol + " - " + name + ": $" + String.format("%.2f", price);
        }
    }

    // === Transaction Class ===
    static class Transaction {
        public enum Type { BUY, SELL }

        private Stock stock;
        private int quantity;
        private Type type;
        private LocalDateTime timestamp;

        public Transaction(Stock stock, int quantity, Type type) {
            this.stock = stock;
            this.quantity = quantity;
            this.type = type;
            this.timestamp = LocalDateTime.now();
        }

        public Stock getStock() { return stock; }
        public int getQuantity() { return quantity; }
        public Type getType() { return type; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return type + " " + quantity + " shares of " + stock.getSymbol() + " at $" + stock.getPrice() + " on " + timestamp;
        }
    }

    // === Portfolio Class ===
    static class Portfolio {
        private Map<String, Integer> holdings = new HashMap<>();
        private List<Transaction> transactions = new ArrayList<>();

        public void buyStock(Stock stock, int quantity) {
            holdings.put(stock.getSymbol(), holdings.getOrDefault(stock.getSymbol(), 0) + quantity);
            transactions.add(new Transaction(stock, quantity, Transaction.Type.BUY));
        }

        public void sellStock(Stock stock, int quantity) {
            int owned = holdings.getOrDefault(stock.getSymbol(), 0);
            if (quantity > owned) {
                System.out.println(" Not enough shares to sell.");
                return;
            }
            holdings.put(stock.getSymbol(), owned - quantity);
            transactions.add(new Transaction(stock, quantity, Transaction.Type.SELL));
        }

        public void viewHoldings(Map<String, Stock> market) {
            System.out.println("\n📊 Your Holdings:");
            if (holdings.isEmpty()) {
                System.out.println("None.");
                return;
            }
            for (String symbol : holdings.keySet()) {
                int qty = holdings.get(symbol);
                Stock stock = market.get(symbol);
                double value = qty * stock.getPrice();
                System.out.printf("%s: %d shares (Value: $%.2f)%n", symbol, qty, value);
            }
        }

        public void viewTransactions() {
            System.out.println("\n🧾 Transaction History:");
            if (transactions.isEmpty()) {
                System.out.println("No transactions.");
                return;
            }
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }

        public double getTotalValue(Map<String, Stock> market) {
            double total = 0;
            for (String symbol : holdings.keySet()) {
                int qty = holdings.get(symbol);
                double price = market.get(symbol).getPrice();
                total += qty * price;
            }
            return total;
        }
    }

    // === User Class ===
    static class User {
        private String username;
        private Portfolio portfolio;

        public User(String username) {
            this.username = username;
            this.portfolio = new Portfolio();
        }

        public String getUsername() { return username; }
        public Portfolio getPortfolio() { return portfolio; }
    }

    // === Main Application Logic ===
    private static Map<String, Stock> stockMarket = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        populateStockMarket();

        System.out.print(" Enter your username: ");
        String username = scanner.nextLine();
        User user = new User(username);

        boolean running = true;
        while (running) {
            displayMenu();
            String input = scanner.nextLine();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(" Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1 -> displayMarket();
                case 2 -> buyStock(user);
                case 3 -> sellStock(user);
                case 4 -> user.getPortfolio().viewHoldings(stockMarket);
                case 5 -> user.getPortfolio().viewTransactions();
                case 6 -> System.out.printf(" Portfolio Value: $%.2f%n", user.getPortfolio().getTotalValue(stockMarket));
                case 0 -> running = false;
                default -> System.out.println(" Invalid choice. Try again.");
            }
        }

        System.out.println(" Goodbye, " + user.getUsername() + "! Thanks for trading.");
    }

    private static void displayMenu() {
        System.out.println("\n Stock Trading Platform");
        System.out.println("1. View Market");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Portfolio");
        System.out.println("5. View Transactions");
        System.out.println("6. View Portfolio Value");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private static void populateStockMarket() {
        stockMarket.put("AAPL", new Stock("AAPL", "Apple Inc.", 180.12));
        stockMarket.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 2750.65));
        stockMarket.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 3400.25));
        stockMarket.put("TSLA", new Stock("TSLA", "Tesla Inc.", 850.50));
    }

    private static void displayMarket() {
        System.out.println("\n Current Market:");
        for (Stock stock : stockMarket.values()) {
            System.out.println(stock);
        }
    }

    private static void buyStock(User user) {
        System.out.print("Enter stock symbol to BUY: ");
        String symbol = scanner.nextLine().toUpperCase();
        Stock stock = stockMarket.get(symbol);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        System.out.print("Enter quantity to buy: ");
        int qty = readInt();
        if (qty <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        user.getPortfolio().buyStock(stock, qty);
        System.out.println("Bought " + qty + " shares of " + symbol);
    }

    private static void sellStock(User user) {
        System.out.print("Enter stock symbol to SELL: ");
        String symbol = scanner.nextLine().toUpperCase();
        Stock stock = stockMarket.get(symbol);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        System.out.print("Enter quantity to sell: ");
        int qty = readInt();
        if (qty <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        user.getPortfolio().sellStock(stock, qty);
    }

    private static int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
