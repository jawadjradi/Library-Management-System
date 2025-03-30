import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";  // change as per your setup
    private static final String PASS = "29112004Leo!";  // change as per your setup
    String url = "jdbc:mysql://localhost:3306/library?useSSL=false&allowPublicKeyRetrieval=true";
    String user = "root";
    String password = "your_password";
    Connection conn = DriverManager.getConnection(url, user, password);

    public LibraryManagementSystem() throws SQLException {
    }

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Add Book");
                System.out.println("2. Update Book");
                System.out.println("3. Delete Book");
                System.out.println("4. View Books");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // consume newline

                if (choice == 1) {
                    addBook(conn, scanner);
                } else if (choice == 2) {
                    updateBook(conn, scanner);
                } else if (choice == 3) {
                    deleteBook(conn, scanner);
                } else if (choice == 4) {
                    viewBooks(conn);
                } else if (choice == 5) {
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean authenticateUser(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT password, role FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    String role = rs.getString("role");
                    if (storedPassword.equals(password)) {
                        System.out.println("Login successful. Role: " + role);
                        return true; // Proceed based on role
                    }
                }
            }
        }
        return false;  // Invalid credentials
    }
    private static void borrowBook(Connection conn, int bookId) throws SQLException {
        String query = "UPDATE books SET available = false WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            System.out.println("Book borrowed successfully.");
        }
    }

    private static void returnBook(Connection conn, int bookId) throws SQLException {
        String query = "UPDATE books SET available = true WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            System.out.println("Book returned successfully.");
        }
    }
    private static void calculateOverdueFine(Connection conn, int bookId) throws SQLException {
        String query = "SELECT due_date FROM books WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dueDate = rs.getDate("due_date");
                    Date currentDate = new Date(System.currentTimeMillis());
                    if (currentDate.after(dueDate)) {
                        long diff = currentDate.getTime() - dueDate.getTime();
                        long overdueDays = diff / (1000 * 60 * 60 * 24);
                        double fine = overdueDays * 1.0;  // $1 per day fine
                        System.out.println("Overdue fine: $" + fine);
                    }
                }
            }
        }
    }
    private static void searchBooks(Connection conn, String searchQuery) throws SQLException {
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR genre LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            String searchTerm = "%" + searchQuery + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Title: " + rs.getString("title"));
                    System.out.println("Author: " + rs.getString("author"));
                    System.out.println("Genre: " + rs.getString("genre"));
                    System.out.println("Available: " + rs.getBoolean("available"));
                }
            }
        }
    }
    private static void logTransaction(Connection conn, int userId, int bookId, String transactionType) throws SQLException {
        String query = "INSERT INTO transaction_history (user_id, book_id, transaction_type) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setString(3, transactionType);
            stmt.executeUpdate();
        }
    }

    public static void addBook(String title, String author, String genre, String isbn) {
        String query = "INSERT INTO books (title, author, genre, isbn) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, genre);
            stmt.setString(4, isbn);
            stmt.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void viewBooks() {
        String query = "SELECT * FROM books";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Title: " + rs.getString("title") +
                        ", Author: " + rs.getString("author") +
                        ", Genre: " + rs.getString("genre") +
                        ", ISBN: " + rs.getString("isbn"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter book author: ");
        String author = scanner.nextLine();
        System.out.print("Enter book genre: ");
        String genre = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        String query = "INSERT INTO books (title, author, genre, isbn) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, genre);
            stmt.setString(4, isbn);
            stmt.executeUpdate();
            System.out.println("Book added successfully.");
        }
    }

    private static void updateBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter book ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // consume newline
        System.out.print("Enter new book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new book author: ");
        String author = scanner.nextLine();
        System.out.print("Enter new book genre: ");
        String genre = scanner.nextLine();
        System.out.print("Enter new ISBN: ");
        String isbn = scanner.nextLine();

        String query = "UPDATE books SET title = ?, author = ?, genre = ?, isbn = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, genre);
            stmt.setString(4, isbn);
            stmt.setInt(5, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book updated successfully.");
            } else {
                System.out.println("Book not found.");
            }
        }
    }

    private static void deleteBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter book ID to delete: ");
        int id = scanner.nextInt();

        String query = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Book not found.");
            }
        }
    }

    private static void viewBooks(Connection conn) throws SQLException {
        String query = "SELECT * FROM books";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Genre: " + rs.getString("genre"));
                System.out.println("ISBN: " + rs.getString("isbn"));
                System.out.println("Available: " + rs.getBoolean("available"));
                System.out.println("-----------------------------");
            }
        }
    }
}
