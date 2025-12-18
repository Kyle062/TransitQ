package database;

import models.Bus;
import models.Passenger;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        this.connection = DatabaseConnection.getConnection();
    }

    // ==================== BUS OPERATIONS ====================

    public List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        String query = "SELECT * FROM buses ORDER BY bus_id";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String busId = rs.getString("bus_id");
                int capacity = rs.getInt("capacity");
                int currentLoad = rs.getInt("current_load");

                Bus bus = new Bus(busId, capacity);
                bus.setCurrentLoad(currentLoad);
                bus.setActive(rs.getBoolean("is_active"));
                buses.add(bus);
            }
        } catch (SQLException e) {
            System.err.println("Error getting buses: " + e.getMessage());
        }
        return buses;
    }

    public Bus getBus(String busId) {
        String query = "SELECT * FROM buses WHERE bus_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, busId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int capacity = rs.getInt("capacity");
                int currentLoad = rs.getInt("current_load");

                Bus bus = new Bus(busId, capacity);
                bus.setCurrentLoad(currentLoad);
                bus.setActive(rs.getBoolean("is_active"));
                return bus;
            }
        } catch (SQLException e) {
            System.err.println("Error getting bus: " + e.getMessage());
        }
        return null;
    }

    public boolean updateBusLoad(String busId, int currentLoad) {
        String query = "UPDATE buses SET current_load = ? WHERE bus_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, currentLoad);
            pstmt.setString(2, busId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bus load: " + e.getMessage());
            return false;
        }
    }

    public boolean setActiveBus(String busId) {
        String resetQuery = "UPDATE buses SET is_active = FALSE";
        String activateQuery = "UPDATE buses SET is_active = TRUE WHERE bus_id = ?";

        try (Statement stmt = connection.createStatement();
                PreparedStatement pstmt = connection.prepareStatement(activateQuery)) {

            stmt.executeUpdate(resetQuery);
            pstmt.setString(1, busId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error setting active bus: " + e.getMessage());
            return false;
        }
    }

    public String getActiveBus() {
        String query = "SELECT bus_id FROM buses WHERE is_active = TRUE LIMIT 1";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getString("bus_id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting active bus: " + e.getMessage());
        }
        return null;
    }

    public boolean addBus(String busId, int capacity) {
        String query = "INSERT INTO buses (bus_id, capacity, current_load) VALUES (?, ?, 0)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, busId);
            pstmt.setInt(2, capacity);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding bus: " + e.getMessage());
            return false;
        }
    }

    // ==================== LINE MANAGEMENT METHODS ====================

    public List<Passenger> getVIPLine() {
        return getPassengersByLineAndStatus("VIP", "TicketArea");
    }

    public List<Passenger> getRegularLine() {
        return getPassengersByLineAndStatus("REGULAR", "TicketArea");
    }

    private List<Passenger> getPassengersByLineAndStatus(String lineType, String status) {
        List<Passenger> passengers = new ArrayList<>();
        String query = "SELECT * FROM passengers WHERE line_type = ? AND status = ? " +
                "ORDER BY position_in_line";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, lineType);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Passenger passenger = new Passenger(
                        rs.getInt("passenger_id"),
                        rs.getString("name"),
                        rs.getString("destination"),
                        rs.getString("ticket_type"),
                        rs.getString("payment_method"),
                        rs.getString("money_paid"),
                        rs.getBoolean("is_paid"),
                        rs.getString("status"),
                        rs.getString("line_type"),
                        rs.getInt("position_in_line"));
                passengers.add(passenger);
            }
        } catch (SQLException e) {
            System.err.println("Error getting " + lineType + " line: " + e.getMessage());
        }
        return passengers;
    }

    public List<Passenger> getPassengersByStatus(String status) {
        List<Passenger> passengers = new ArrayList<>();
        String query = "SELECT * FROM passengers WHERE status = ? ORDER BY passenger_id";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Passenger passenger = new Passenger(
                        rs.getInt("passenger_id"),
                        rs.getString("name"),
                        rs.getString("destination"),
                        rs.getString("ticket_type"),
                        rs.getString("payment_method"),
                        rs.getString("money_paid"),
                        rs.getBoolean("is_paid"),
                        rs.getString("status"),
                        rs.getString("line_type"),
                        rs.getInt("position_in_line"));
                passengers.add(passenger);
            }
        } catch (SQLException e) {
            System.err.println("Error getting passengers: " + e.getMessage());
        }
        return passengers;
    }

    public boolean addPassengerToLine(Passenger passenger) {
        String query = "INSERT INTO passengers (name, destination, ticket_type, payment_method, " +
                "money_paid, is_paid, status, line_type, position_in_line) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, passenger.getName());
            pstmt.setString(2, passenger.getDestination());
            pstmt.setString(3, passenger.getTicketType());
            pstmt.setString(4, passenger.getPaymentMethod());
            pstmt.setString(5, passenger.getMoneyPaid());
            pstmt.setBoolean(6, passenger.isPaid());
            pstmt.setString(7, "TicketArea");

            String lineType = passenger.isVIP() ? "VIP" : "REGULAR";
            pstmt.setString(8, lineType);

            int nextPosition = getNextPositionInLine(lineType);
            pstmt.setInt(9, nextPosition);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                passenger.setPassengerId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding passenger to line: " + e.getMessage());
        }
        return false;
    }

    private int getNextPositionInLine(String lineType) {
        String query = "SELECT MAX(position_in_line) as max_pos FROM passengers " +
                "WHERE line_type = ? AND status = 'TicketArea'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, lineType);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("max_pos") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting next position: " + e.getMessage());
        }
        return 1;
    }

    public Passenger getNextPassengerForProcessing() {
        // Priority: VIP line first
        List<Passenger> vipLine = getVIPLine();
        if (!vipLine.isEmpty()) {
            return vipLine.get(0);
        }

        // Then Regular line
        List<Passenger> regularLine = getRegularLine();
        if (!regularLine.isEmpty()) {
            return regularLine.get(0);
        }

        return null;
    }

    public boolean moveToAssignArea(int passengerId, String busId) {
        Passenger passenger = getPassengerById(passengerId);
        if (passenger == null)
            return false;

        // Remove from line first
        removeFromLine(passengerId);

        // Update to assign area
        String query = "UPDATE passengers SET status = 'AssignArea', assigned_bus = ? " +
                "WHERE passenger_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, busId);
            pstmt.setInt(2, passengerId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error moving to assign area: " + e.getMessage());
            return false;
        }
    }

    private void removeFromLine(int passengerId) {
        String query = "DELETE FROM passengers WHERE passenger_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, passengerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing from line: " + e.getMessage());
        }
    }

    public Passenger getPassengerById(int passengerId) {
        String query = "SELECT * FROM passengers WHERE passenger_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, passengerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Passenger(
                        rs.getInt("passenger_id"),
                        rs.getString("name"),
                        rs.getString("destination"),
                        rs.getString("ticket_type"),
                        rs.getString("payment_method"),
                        rs.getString("money_paid"),
                        rs.getBoolean("is_paid"),
                        rs.getString("status"),
                        rs.getString("line_type"),
                        rs.getInt("position_in_line"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting passenger: " + e.getMessage());
        }
        return null;
    }

    public List<Passenger> searchPassengerByName(String name) {
        List<Passenger> passengers = new ArrayList<>();
        String query = "SELECT * FROM passengers WHERE name LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Passenger passenger = new Passenger(
                        rs.getInt("passenger_id"),
                        rs.getString("name"),
                        rs.getString("destination"),
                        rs.getString("ticket_type"),
                        rs.getString("payment_method"),
                        rs.getString("money_paid"),
                        rs.getBoolean("is_paid"),
                        rs.getString("status"),
                        rs.getString("line_type"),
                        rs.getInt("position_in_line"));
                passengers.add(passenger);
            }
        } catch (SQLException e) {
            System.err.println("Error searching passenger by name: " + e.getMessage());
        }
        return passengers;
    }

    public boolean updatePassengerStatus(int passengerId, String status, String busId) {
        String query = "UPDATE passengers SET status = ?, assigned_bus = ? WHERE passenger_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setString(2, busId);
            pstmt.setInt(3, passengerId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating passenger status: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassenger(int passengerId, String name, String destination,
            String ticketType, String moneyPaid) {
        String query = "UPDATE passengers SET name = ?, destination = ?, ticket_type = ?, " +
                "money_paid = ? WHERE passenger_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, destination);
            pstmt.setString(3, ticketType);
            pstmt.setString(4, moneyPaid);
            pstmt.setInt(5, passengerId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating passenger: " + e.getMessage());
            return false;
        }
    }

    public boolean removePassenger(int passengerId) {
        String query = "DELETE FROM passengers WHERE passenger_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, passengerId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing passenger: " + e.getMessage());
            return false;
        }
    }

    // ==================== PAYMENT OPERATIONS ====================

    public boolean recordPayment(int passengerId, double amount, String ticketType, String status) {
        String query = "INSERT INTO payments (passenger_id, amount, ticket_type, verification_status) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, passengerId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, ticketType);
            pstmt.setString(4, status);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error recording payment: " + e.getMessage());
            return false;
        }
    }

    public double getTotalCashCollected() {
        String query = "SELECT SUM(amount) as total FROM payments";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total cash: " + e.getMessage());
        }
        return 0.0;
    }

    public Map<String, Integer> getTicketSales() {
        Map<String, Integer> sales = new HashMap<>();
        String query = "SELECT ticket_type, COUNT(*) as count FROM payments GROUP BY ticket_type";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                sales.put(rs.getString("ticket_type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting ticket sales: " + e.getMessage());
        }
        return sales;
    }

    // ==================== REPORTING ====================

    public Map<String, Integer> getPassengerStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT status, COUNT(*) as count FROM passengers GROUP BY status";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting passenger statistics: " + e.getMessage());
        }
        return stats;
    }

    public List<String> getRecentDepartures(int limit) {
        List<String> departures = new ArrayList<>();
        String query = "SELECT bus_id, passengers_count, departure_time FROM departures " +
                "ORDER BY departure_time DESC LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                departures.add(rs.getString("bus_id") + " - " +
                        rs.getInt("passengers_count") + " passengers - " +
                        rs.getTimestamp("departure_time"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting departures: " + e.getMessage());
        }
        return departures;
    }

    public boolean recordDeparture(String busId, int passengerCount) {
        String query = "INSERT INTO departures (bus_id, passengers_count) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, busId);
            pstmt.setInt(2, passengerCount);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error recording departure: " + e.getMessage());
            return false;
        }
    }

    // ==================== LOGGING ====================

    public void logOperation(String message, String type) {
        String query = "INSERT INTO system_logs (log_message, log_type) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, message);
            pstmt.setString(2, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging operation: " + e.getMessage());
        }
    }
}