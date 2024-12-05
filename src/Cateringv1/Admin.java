/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Cateringv1;

import com.raven.datechooser.SelectedDate;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author nicoll mitch
 */
public class Admin extends javax.swing.JFrame {

    Color medyoDark = new Color(119, 82, 254);
    Color medyoLight = new Color(142, 143, 250);
    Cateringv1 catering = new Cateringv1();
    
  
    /**
     * Creates new form Admin
     */
    public Admin() {
        initComponents();
        Connect();
        Menu menu = new Menu();
    }
    
    int currentClientID;
        Connection con;
        PreparedStatement pst;
        ResultSet rs;
        
public void Connect() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost/catering", "jarey", "jarey123");
    } catch (ClassNotFoundException ex) {
        Logger.getLogger(Cateringv1.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Database Driver not found!");
    } catch (SQLException ex) {
        Logger.getLogger(Cateringv1.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Failed to connect to the database!");
    }
}
private void updateSelectedRow() {
    int selectedRow = jTable1.getSelectedRow(); // Get the selected row index
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a row to update.");
        return; // No row selected, exit the method
    }

    // Retrieve the data name from the first column
    String dataName = (String) jTable1.getValueAt(selectedRow, 0);
    Object currentValueObj = jTable1.getValueAt(selectedRow, 1);
    String currentValue = (currentValueObj != null) ? currentValueObj.toString() : "";
    String newValue = JOptionPane.showInputDialog(this, "Edit " + dataName + ":", currentValue);

    // If the user cancels the dialog, newValue will be null
    if (newValue == null) {
        return; // Exit if no new value is provided
    }

    // Update the value in the table
    jTable1.setValueAt(newValue, selectedRow, 1); // Update the second column with the new value

    // Now, prepare to update the database
    try {
        int currentClientID = Integer.parseInt(clientIdField.getText()); // Assuming clientIdField is your input field
        String tableName = getTableName(dataName);
        String columnName = getColumnName(dataName);

        if (tableName == null || columnName == null) {
            JOptionPane.showMessageDialog(this, "No database field mapping for: " + dataName);
            return;
        }

        // Build the update query
        String updateQuery = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + getIdColumnName(tableName) + " = ?";
        pst = con.prepareStatement(updateQuery);
        setPreparedStatementValues(pst, columnName, newValue, currentClientID, tableName);

        // Execute the update
        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Entry updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No entry found with the specified ID.");
        }

        // Clean up
        pst.close();
    } catch (SQLException ex) {
        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Error updating entry: " + ex.getMessage());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid Client ID format. Please enter a valid number.");
    }
}

// Utility to get table name based on dataName
private String getTableName(String dataName) {
    switch (dataName) {
        case "Full Name":
        case "Contact":
        case "Address":
        case "Email":
            return "clientstable";
        case "Event Date":
        case "Event Time":
        case "Event Type":
        case "Guest Count":
        case "Event Venue":
        case "Place Type":
            return "eventstable";
        case "Service":
        case "Package Type":
        case "Meal Type":
        case "Dietary":
        case "Main Course":
        case "Appetizer":
        case "Dessert":
        case "Drinks":
            return "menutable";
        case "Motif":
        case "Table":
        case "Table Seat":
        case "Cloth":
        case "Back Drop":
        case "Chair":
        case "Items":
        case "Suggestion":
            return "orderstable";
        default:
            return null;
    }
}

// Utility to get column name based on dataName
private String getColumnName(String dataName) {
    switch (dataName) {
        // Client table fields
        case "Full Name": return "FullName";
        case "Contact": return "phoneNumber";
        case "Address": return "address";
        case "Email": return "customerEmail";

        // Event table fields
        case "Event Date": return "EventDate";
        case "Event Time": return "EventTime";
        case "Event Type": return "EventType";
        case "Guest Count": return "GuessCount";
        case "Event Venue": return "EventVenue";
        case "Place Type": return "place";

        // Menu table fields
        case "Service": return "service";
        case "Package Type": return "packageType";
        case "Meal Type": return "mealType";
        case "Dietary": return "dietaryRestriction";
        case "Main Course": return "mainCourse";
        case "Appetizer": return "appetizer";
        case "Dessert": return "dessert";
        case "Drinks": return "drinks";

        // Orders table fields
        case "Motif": return "motif";
        case "Table": return "tables";
        case "Table Seat": return "tableSeater";
        case "Cloth": return "tableCloth";
        case "Back Drop": return "backdrop";
        case "Chair": return "chairs";
        case "Items": return "items";
        case "Suggestion": return "suggestion";

        default: return null;
    }
}

// Utility to get the ID column name based on the table
private String getIdColumnName(String tableName) {
    switch (tableName) {
        case "clientstable": return "ClientID";
        case "eventstable": return "EventID";
        case "menutable": return "MenuID";
        case "orderstable": return "OrderID";
        default: return null;
    }
}

// Utility to set PreparedStatement values
private void setPreparedStatementValues(PreparedStatement pst, String columnName, String newValue, int currentClientID, String tableName) throws SQLException {
    if (columnName.equals("EventDate")) {
        pst.setDate(1, java.sql.Date.valueOf(newValue)); // Assuming newValue is in YYYY-MM-DD format
    } else if (columnName.equals("EventTime")) {
        pst.setTime(1, java.sql.Time.valueOf(newValue)); // Assuming newValue is in HH:MM:SS format
    } else if (columnName.equals("GuessCount")) {
        pst.setInt(1, Integer.parseInt(newValue)); // Assuming newValue is a number
    } else {
        pst.setString(1, newValue);
    }
    
    // Determine the correct ID based on the table
    if (tableName.equals("clientstable")) {
        pst.setInt(2, currentClientID);
    } else if (tableName.equals("eventstable")) {
        pst.setInt(2, getEventIDForClient(currentClientID));
    } else if (tableName.equals("menutable")) {
        pst.setInt(2, getMenuIDForClient(currentClientID));
    } else if (tableName.equals("orderstable")) {
        pst.setInt(2, getOrderIDForClient(currentClientID));
    }
}
// Method to retrieve EventID based on ClientID
private int getEventIDForClient(int clientID) throws SQLException {
    String query = "SELECT EventID FROM eventstable WHERE ClientID = ?";
    try (PreparedStatement pst = con.prepareStatement(query)) {
        pst.setInt(1, clientID);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt("EventID");
        } else {
            throw new SQLException("No EventID found for the given ClientID: " + clientID);
        }
    }
}

// Method to retrieve MenuID based on ClientID
private int getMenuIDForClient(int clientID) throws SQLException {
    String query = "SELECT MenuID FROM menutable WHERE ClientID = ?";
    try (PreparedStatement pst = con.prepareStatement(query)) {
        pst.setInt(1, clientID);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt("MenuID");
        } else {
            throw new SQLException("No MenuID found for the given ClientID: " + clientID);
        }
    }
}

// Method to retrieve OrderID based on ClientID
private int getOrderIDForClient(int clientID) throws SQLException {
    String query = "SELECT OrderID FROM orderstable WHERE ClientID = ?";
    try (PreparedStatement pst = con.prepareStatement(query)) {
        pst.setInt(1, clientID);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt("OrderID");
        } else {
            throw new SQLException("No OrderID found for the given ClientID: " + clientID);
        }
    }
}

private void deleteSelectedRow() {
    int selectedRow = showList.getSelectedRow(); // Get the selected row index
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a row to delete.");
        return; // No row selected, exit the method
    }

    // Get the ClientID of the selected row
    int clientId = (int) showList.getValueAt(selectedRow, 0); // Assuming ClientID is in the first column
    System.out.println("Attempting to delete ClientID: " + clientId); // Debug log

    // Confirm deletion
    int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this entry?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
    if (confirmation != JOptionPane.YES_OPTION) {
        return; // User cancelled the deletion
    }

    try {
        
        // Now, delete related records from orderstable
        String deleteOrdersQuery = "DELETE FROM orderstable WHERE ClientID = ?";
        pst = con.prepareStatement(deleteOrdersQuery);
        pst.setInt(1, clientId);
        int ordersDeleted = pst.executeUpdate(); // Delete related orders
        System.out.println("Deleted " + ordersDeleted + " related orders."); // Debug log
        
        // Then, delete related records from menutable
        String deleteMenuQuery = "DELETE FROM menutable WHERE ClientID = ?";
        pst = con.prepareStatement(deleteMenuQuery);
        pst.setInt(1, clientId);
        int menuDeleted = pst.executeUpdate(); // Delete related menu items
        System.out.println("Deleted " + menuDeleted + " related menu items."); // Debug log
        
        // Then, delete related records from eventstable
        String deleteEventsQuery = "DELETE FROM eventstable WHERE ClientID = ?";
        pst = con.prepareStatement(deleteEventsQuery);
        pst.setInt(1, clientId);
        int eventsDeleted = pst.executeUpdate(); // Delete related events
        System.out.println("Deleted " + eventsDeleted + " related events."); // Debug log

        // Finally, delete the client from clientstable
        String deleteClientQuery = "DELETE FROM clientstable WHERE ClientID = ?";
        pst = con.prepareStatement(deleteClientQuery);
        pst.setInt(1, clientId);
        
        // Execute the delete operation
        int rowsAffected = pst.executeUpdate();
        System.out.println("Rows affected by client deletion: " + rowsAffected); // Debug log

        if (rowsAffected > 0) {
            // Successfully deleted from the database
            DefaultTableModel model = (DefaultTableModel) showList.getModel();
            model.removeRow(selectedRow); // Remove the row from the JTable
            
            JOptionPane.showMessageDialog(this, "Entry deleted successfully.");
        } else {
            // No rows affected, possibly the ClientID does not exist
            JOptionPane.showMessageDialog(this, "No entry found with the specified ClientID.");
        }

        // Clean up
        pst.close();
    } catch (SQLException ex) {
        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Error deleting entry: " + ex.getMessage());
    }
}

 private void addEventType() {
    String newEventType = tfeventType.getText().trim(); // Get the text from the text field

    if (!newEventType.isEmpty()) { // Check if the input is not empty
        // Add the new event type to the combo box
        EventType.addItem(newEventType);
        tfeventType.setText(""); // Clear the text field after adding
        JOptionPane.showMessageDialog(this, "Event type added: " + newEventType);
    } else {
        JOptionPane.showMessageDialog(this, "Please enter a valid event type.");
    }
}
 
 private void addCustomPlace() {
    String newPlace = tfPlace.getText().trim(); // Get the text from the text field

    if (!newPlace.isEmpty()) { // Check if the input is not empty
        // Add the new event type to the combo box
        placeType.addItem(newPlace);
        tfPlace.setText(""); // Clear the text field after adding
        JOptionPane.showMessageDialog(this, "Custom Venue added: " + newPlace);
    } else {
        JOptionPane.showMessageDialog(this, "Please enter a valid Venue.");
    }
}

private void showOrders(){
        try {
        // Create a mapping of month names to month numbers
        Map<String, Integer> monthNameToNumber = new HashMap<>();
        monthNameToNumber.put("January", 1);
        monthNameToNumber.put("February", 2);
        monthNameToNumber.put("March", 3);
        monthNameToNumber.put("April", 4);
        monthNameToNumber.put("May", 5);
        monthNameToNumber.put("June", 6);
        monthNameToNumber.put("July", 7);
        monthNameToNumber.put("August", 8);
        monthNameToNumber.put("September", 9);
        monthNameToNumber.put("October", 10);
        monthNameToNumber.put("November", 11);
        monthNameToNumber.put("December", 12);

        // Get the selected month name from the JComboBox
        String monthName = (String) monthList.getSelectedItem();

        // Convert the month name to a number
        Integer selectedMonthNumber = monthNameToNumber.get(monthName);

        // Check if the selected month is valid
        if (selectedMonthNumber == null) {
            JOptionPane.showMessageDialog(this, "Invalid month selected.");
            return;
        }

        // Now you can use the selectedMonthNumber in your query
        String query = "SELECT c.ClientID, c.FullName, c.phoneNumber, e.EventDate, e.EventTime, e.place " +
                       "FROM clientstable c " +
                       "JOIN eventstable e ON c.ClientID = e.ClientID " +
                       "WHERE MONTH(e.EventDate) = ?";

        pst = con.prepareStatement(query);
        pst.setInt(1, selectedMonthNumber); // Use the converted month number

        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) showList.getModel();

        // Clear existing rows before adding new data
        model.setRowCount(0); // Clear previous data
        model.setColumnCount(0); // Clear previous columns (if you want to reset the columns as well)

        // Add columns only if they are not already added
        if (model.getColumnCount() == 0) {
            model.addColumn("Client Number");
            model.addColumn("Full Name");
            model.addColumn("Phone Number");
            model.addColumn("Event Date");
            model.addColumn("Event Time");
            model.addColumn("Place");
        }

        // Populate the model with data from the ResultSet
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("ClientID"));
            row.add(rs.getString("FullName"));
            row.add(rs.getString("phoneNumber"));
            row.add(rs.getDate("EventDate"));
            row.add(rs.getTime("EventTime"));
            row.add(rs.getString("place"));
            model.addRow(row);
        }

        monthOrder.setText("For the month of " + monthName);

        // Clean up
        rs.close();
        pst.close();
    } catch (SQLException ex) {
        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
    }  
}

private void showOrderInfo(){
        try {
    // Get the client ID from the input field
    String clientIdString = clientIdField.getText(); // Assuming clientIdField is your input field
    int currentClientID;

    // Validate the client ID input
    try {
        currentClientID = Integer.parseInt(clientIdString);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid client ID. Please enter a valid number.");
        return;
    }

    // Now you can use the currentClientID in your query
    String query = "SELECT c.FullName, c.phoneNumber, c.address, c.customerEmail, " +
                   "e.EventDate, e.EventTime, e.EventType, e.GuessCount, e.EventVenue, e.place, " +
                   "m.serviceType, m.packageType, m.mealType, m.dietaryRestriction, m.mainCourse, m.appetizer, m.dessert, m.drinks, " +
                   "o.theme, o.tables, o.tableSeater, o.tableCloth, o.backdrop, o.chairs, o.items, o.suggestion " +
                   "FROM clientstable c " +
                   "JOIN eventstable e ON c.ClientID = e.ClientID " + // Join with events table
                   "JOIN menutable m ON c.ClientID = m.ClientID " + // Join with menu table
                   "JOIN orderstable o ON c.ClientID = o.ClientID " + // Join with orders table
                   "WHERE c.ClientID = ?";

    pst = con.prepareStatement(query);
    pst.setInt(1, currentClientID); // Use the provided client ID

    ResultSet rs = pst.executeQuery();
    
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel(); // Assuming showList2 is your JTable
    
    // Clear existing rows before adding new data
    model.setRowCount(0); // Clear previous data

    // Add columns if they are not already added
    if (model.getColumnCount() == 0) {
        model.addColumn("Data Name");
        model.addColumn("Data Value");
    }
    
    // Check if the client exists
    if (rs.next()) {
        // Populate the model with data from the ResultSet
        Vector<Object> fullNameRow = new Vector<>();
        fullNameRow.add("Full Name");
        fullNameRow.add(rs.getString("FullName"));
        model.addRow(fullNameRow);
        
        Vector<Object> contactRow = new Vector<>();
        contactRow.add("Contact");
        contactRow.add(rs.getString("phoneNumber"));
        model.addRow(contactRow);
        
        Vector<Object> addressRow = new Vector<>();
        addressRow.add("Address");
        addressRow.add(rs.getString("address"));
        model.addRow(addressRow);
        
        Vector<Object> emailRow = new Vector<>();
        emailRow.add("Email");
        emailRow.add(rs.getString("customerEmail"));
        model.addRow(emailRow);

        Vector<Object> eventDateRow = new Vector<>();
        eventDateRow.add("Event Date");
        eventDateRow.add(rs.getDate("EventDate"));
        model.addRow(eventDateRow);
        
        Vector<Object> eventTimeRow = new Vector<>();
        eventTimeRow.add("Event Time");
        eventTimeRow.add(rs.getTime("EventTime"));
        model.addRow(eventTimeRow);
        
        Vector<Object> eventTypeRow = new Vector<>();
        eventTypeRow.add("Event Type");
        eventTypeRow.add(rs.getString("EventType"));
        model.addRow(eventTypeRow);
        
        Vector<Object> guestCountRow = new Vector<>();
        guestCountRow.add("Guest Count");
        guestCountRow.add(rs.getInt("GuessCount"));
        model.addRow(guestCountRow);
        
        Vector<Object> eventVenueRow = new Vector<>();
        eventVenueRow.add("Event Venue");
        eventVenueRow.add(rs.getString("EventVenue"));
        model.addRow(eventVenueRow);
        
        Vector<Object> placeTypeRow = new Vector<>();
        placeTypeRow.add("Place Type");
        placeTypeRow.add(rs.getString("place"));
        model.addRow(placeTypeRow);
        
        Vector<Object> serviceRow = new Vector<>();
        serviceRow.add("Service Type");
        serviceRow.add(rs.getString("serviceType"));
        model.addRow(serviceRow);
        
        Vector<Object> packageTypeRow = new Vector<>();
        packageTypeRow.add("Package Type");
        packageTypeRow.add(rs.getString("packageType"));
        model.addRow(packageTypeRow);

        Vector<Object> mealTypeRow = new Vector<>();
        mealTypeRow.add("Meal Type");
        mealTypeRow.add(rs.getString("mealType"));
        model.addRow(mealTypeRow);
        
        Vector<Object> dietaryRow = new Vector<>();
        dietaryRow.add("Dietary Restriction");
        dietaryRow.add(rs.getString("dietaryRestriction"));
        model.addRow(dietaryRow);
        
        Vector<Object> mainCourseRow = new Vector<>();
        mainCourseRow.add("Main Course");
        mainCourseRow.add(rs.getString("mainCourse"));
        model.addRow(mainCourseRow);
        
        Vector<Object> appetizerRow = new Vector<>();
        appetizerRow.add("Appetizer");
        appetizerRow.add(rs.getString("appetizer"));
        model.addRow(appetizerRow);
        
        Vector<Object> dessertRow = new Vector<>();
        dessertRow.add("Dessert");
        dessertRow.add(rs.getString("dessert"));
        model.addRow(dessertRow);
        
        Vector<Object> drinksRow = new Vector<>();
        drinksRow.add("Drinks");
        drinksRow.add(rs.getString("drinks"));
        model.addRow(drinksRow);
        
        Vector<Object> motifRow = new Vector<>();
        motifRow.add("Motif or Theme");
        motifRow.add(rs.getString("theme"));
        model.addRow(motifRow);
        
        Vector<Object> tableRow = new Vector<>();
        tableRow.add("Table");
        tableRow.add(rs.getString("tables"));
        model.addRow(tableRow);
        
        Vector<Object> tableSeatRow = new Vector<>();
        tableSeatRow.add("Table Seat");
        tableSeatRow.add(rs.getString("tableSeater"));
        model.addRow(tableSeatRow);
        
        Vector<Object> clothRow = new Vector<>();
        clothRow.add("Table Cloth");
        clothRow.add(rs.getString("tableCloth"));
        model.addRow(clothRow);
        
        Vector<Object> backDropRow = new Vector<>();
        backDropRow.add("Back Drop");
        backDropRow.add(rs.getString("backdrop"));
        model.addRow(backDropRow);
        
        Vector<Object> chairRow = new Vector<>();
        chairRow.add("Chair");
        chairRow.add(rs.getString("chairs"));
        model.addRow(chairRow);
        
        Vector<Object> itemsRow = new Vector<>();
        itemsRow.add("Items");
        itemsRow.add(rs.getString("items"));
        model.addRow(itemsRow);
        
        Vector<Object> suggestionRow = new Vector<>();
        suggestionRow.add("Suggestion");
        suggestionRow.add(rs.getString("suggestion"));
        model.addRow(suggestionRow);
        
        jTable1.setEnabled(true);
        
    } else {
        JOptionPane.showMessageDialog(this, "No client found with ID: " + currentClientID);
    }
    
    // Clean up
    rs.close();
    pst.close();
} catch (SQLException ex) {
    Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
    JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
}
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rSMaterialButtonCircleBeanInfo1 = new rojerusan.RSMaterialButtonCircleBeanInfo();
        backgroundAdmin = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        materialButton1 = new necesario.MaterialButton();
        materialButton2 = new necesario.MaterialButton();
        materialButton3 = new necesario.MaterialButton();
        materialButton4 = new necesario.MaterialButton();
        materialButton16 = new necesario.MaterialButton();
        rSPanelImage1 = new rojerusan.RSPanelImage();
        AdminPanel = new javax.swing.JPanel();
        adminpanel1 = new javax.swing.JPanel();
        rSPanelImage7 = new rojerusan.RSPanelImage();
        jLabel11 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        rSMaterialButtonRectangle11 = new rojerusan.RSMaterialButtonRectangle();
        adminpanel2 = new javax.swing.JPanel();
        tabbedPane1 = new javax.swing.JTabbedPane();
        Bg = new javax.swing.JPanel();
        tfName = new javax.swing.JTextField();
        tfAddress = new javax.swing.JTextField();
        tfPhoneNumber = new javax.swing.JTextField();
        tfEmail = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        clearInfo = new necesario.MaterialButton();
        proceedEvent = new necesario.MaterialButton();
        rSMaterialButtonRectangle7 = new rojerusan.RSMaterialButtonRectangle();
        Bg2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tfeventType = new javax.swing.JTextField();
        tfDate = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        tfTime = new javax.swing.JTextField();
        tfCount = new javax.swing.JTextField();
        EventType = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        EventVenue = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        placeType = new javax.swing.JComboBox<>();
        tfPlace = new javax.swing.JTextField();
        placeCustom = new necesario.MaterialButton();
        dateChooser1 = new com.raven.datechooser.DateChooser();
        timePicker1 = new com.raven.swing.TimePicker();
        eventCustom = new necesario.MaterialButton();
        proceedMenu = new necesario.MaterialButton();
        clearEvent = new necesario.MaterialButton();
        rSMaterialButtonRectangle5 = new rojerusan.RSMaterialButtonRectangle();
        rSMaterialButtonRectangle6 = new rojerusan.RSMaterialButtonRectangle();
        rSPanelImage2 = new rojerusan.RSPanelImage();
        Bg3 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        packageType = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        serviceType = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        mealType = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        dietaryRestriction = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        materialButton11 = new necesario.MaterialButton();
        proceedOrder = new necesario.MaterialButton();
        materialButton13 = new necesario.MaterialButton();
        rSMaterialButtonRectangle2 = new rojerusan.RSMaterialButtonRectangle();
        rSMaterialButtonRectangle3 = new rojerusan.RSMaterialButtonRectangle();
        rSMaterialButtonRectangle4 = new rojerusan.RSMaterialButtonRectangle();
        rSPanelImage3 = new rojerusan.RSPanelImage();
        Bg4 = new javax.swing.JPanel();
        rSPanelImage4 = new rojerusan.RSPanelImage();
        tableSeater = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        suggestion = new javax.swing.JTextArea();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        backdrop = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        tableType = new javax.swing.JComboBox<>();
        items = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        tableCloth = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        chairs = new javax.swing.JComboBox<>();
        cups = new javax.swing.JCheckBox();
        napkins = new javax.swing.JCheckBox();
        utensils = new javax.swing.JCheckBox();
        plates = new javax.swing.JCheckBox();
        jLabel33 = new javax.swing.JLabel();
        tfMotif = new javax.swing.JTextField();
        confirmOrder = new necesario.MaterialButton();
        orderReceipt = new necesario.MaterialButton();
        rSMaterialButtonRectangle1 = new rojerusan.RSMaterialButtonRectangle();
        jLabel30 = new javax.swing.JLabel();
        Bg5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        printReceipt = new necesario.MaterialButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        textArea2 = new javax.swing.JTextArea();
        rSMaterialButtonRectangle9 = new rojerusan.RSMaterialButtonRectangle();
        rSMaterialButtonRectangle10 = new rojerusan.RSMaterialButtonRectangle();
        bookAgain = new necesario.MaterialButton();
        adminpanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        showList = new javax.swing.JTable();
        showlistMonth = new necesario.MaterialButton();
        delete = new necesario.MaterialButton();
        monthOrder = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        monthList = new javax.swing.JComboBox<>();
        rSMaterialButtonRectangle8 = new rojerusan.RSMaterialButtonRectangle();
        adminpanel4 = new javax.swing.JPanel();
        showlistOrder = new necesario.MaterialButton();
        update = new necesario.MaterialButton();
        jLabel13 = new javax.swing.JLabel();
        clientIdField = new javax.swing.JTextField();
        showList3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(810, 845));
        setResizable(false);

        backgroundAdmin.setBackground(new java.awt.Color(119, 82, 254));
        backgroundAdmin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(25, 4, 130));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        materialButton1.setBackground(new java.awt.Color(142, 143, 250));
        materialButton1.setForeground(new java.awt.Color(255, 255, 255));
        materialButton1.setText("HOME");
        materialButton1.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        materialButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                materialButton1MouseClicked(evt);
            }
        });
        jPanel2.add(materialButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));

        materialButton2.setBackground(new java.awt.Color(142, 143, 250));
        materialButton2.setForeground(new java.awt.Color(255, 255, 255));
        materialButton2.setText("Manage");
        materialButton2.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        materialButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                materialButton2MouseClicked(evt);
            }
        });
        jPanel2.add(materialButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, -1));

        materialButton3.setBackground(new java.awt.Color(142, 143, 250));
        materialButton3.setForeground(new java.awt.Color(255, 255, 255));
        materialButton3.setText("SHOW LIST");
        materialButton3.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        materialButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                materialButton3MouseClicked(evt);
            }
        });
        materialButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                materialButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(materialButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, -1, -1));

        materialButton4.setBackground(new java.awt.Color(142, 143, 250));
        materialButton4.setForeground(new java.awt.Color(255, 255, 255));
        materialButton4.setText("EDIT DATA");
        materialButton4.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        materialButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                materialButton4MouseClicked(evt);
            }
        });
        materialButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                materialButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(materialButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 410, -1, -1));

        materialButton16.setBackground(new java.awt.Color(142, 143, 250));
        materialButton16.setForeground(new java.awt.Color(255, 255, 255));
        materialButton16.setText("EXIT");
        materialButton16.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        materialButton16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                materialButton16MouseClicked(evt);
            }
        });
        materialButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                materialButton16ActionPerformed(evt);
            }
        });
        jPanel2.add(materialButton16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 460, -1, 40));

        rSPanelImage1.setImagen(new javax.swing.ImageIcon(getClass().getResource("/Black Gold Elegant Catering Logo (5).png"))); // NOI18N

        javax.swing.GroupLayout rSPanelImage1Layout = new javax.swing.GroupLayout(rSPanelImage1);
        rSPanelImage1.setLayout(rSPanelImage1Layout);
        rSPanelImage1Layout.setHorizontalGroup(
            rSPanelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );
        rSPanelImage1Layout.setVerticalGroup(
            rSPanelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );

        jPanel2.add(rSPanelImage1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 210, 210));

        backgroundAdmin.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 230, 870));

        AdminPanel.setBackground(new java.awt.Color(119, 82, 254));
        AdminPanel.setLayout(new java.awt.CardLayout());

        adminpanel1.setBackground(new java.awt.Color(119, 82, 254));
        adminpanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rSPanelImage7.setImagen(new javax.swing.ImageIcon(getClass().getResource("/Black Gold Elegant Catering Logo (2).png"))); // NOI18N
        rSPanelImage7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Dubai", 1, 48)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("- Admin Panel -");
        rSPanelImage7.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 570, 570, 77));

        adminpanel1.add(rSPanelImage7, new org.netbeans.lib.awtextra.AbsoluteConstraints(-20, 20, 610, 670));

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Contact Number : 09266272916");
        adminpanel1.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 680, 570, 100));

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("FB Page : Evelyn's Catering Services");
        adminpanel1.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 650, 570, 80));

        rSMaterialButtonRectangle11.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle11.setEnabled(false);
        rSMaterialButtonRectangle11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonRectangle11ActionPerformed(evt);
            }
        });
        adminpanel1.add(rSMaterialButtonRectangle11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 570, 550, 210));

        AdminPanel.add(adminpanel1, "card2");

        adminpanel2.setBackground(new java.awt.Color(119, 82, 254));
        adminpanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Bg.setBackground(new java.awt.Color(119, 82, 254));
        Bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tfName.setColumns(50);
        tfName.setFont(new java.awt.Font("Dubai", 0, 30)); // NOI18N
        tfName.setForeground(new java.awt.Color(25, 4, 130));
        tfName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tfName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNameActionPerformed(evt);
            }
        });
        Bg.add(tfName, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 230, 410, 73));

        tfAddress.setColumns(50);
        tfAddress.setFont(new java.awt.Font("Dubai", 0, 30)); // NOI18N
        tfAddress.setForeground(new java.awt.Color(25, 4, 130));
        tfAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfAddress.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        Bg.add(tfAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 360, 410, 73));

        tfPhoneNumber.setColumns(50);
        tfPhoneNumber.setFont(new java.awt.Font("Dubai", 0, 30)); // NOI18N
        tfPhoneNumber.setForeground(new java.awt.Color(25, 4, 130));
        tfPhoneNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfPhoneNumber.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tfPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPhoneNumberActionPerformed(evt);
            }
        });
        Bg.add(tfPhoneNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 490, 410, 73));

        tfEmail.setColumns(50);
        tfEmail.setFont(new java.awt.Font("Dubai", 0, 30)); // NOI18N
        tfEmail.setForeground(new java.awt.Color(25, 4, 130));
        tfEmail.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEmail.setToolTipText("");
        tfEmail.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tfEmail.setCaretColor(new java.awt.Color(25, 4, 130));
        tfEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEmailActionPerformed(evt);
            }
        });
        Bg.add(tfEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 620, 410, 73));

        jLabel9.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(194, 217, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Full Name :");
        Bg.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 180, -1, 50));

        jLabel2.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Evenlyn's Catering Services");
        Bg.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 442, 77));

        jLabel3.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(194, 217, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Contact Information");
        Bg.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, -1, 50));

        jLabel4.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(194, 217, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Address :");
        Bg.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 320, -1, 38));

        jLabel5.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(194, 217, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Email : ");
        Bg.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 570, 191, 50));

        jLabel6.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(194, 217, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Phone Number :");
        Bg.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 450, 283, 38));

        clearInfo.setBackground(new java.awt.Color(142, 143, 250));
        clearInfo.setForeground(new java.awt.Color(255, 255, 255));
        clearInfo.setText("Clear Input");
        clearInfo.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        clearInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearInfoActionPerformed(evt);
            }
        });
        Bg.add(clearInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 760, -1, -1));

        proceedEvent.setBackground(new java.awt.Color(142, 143, 250));
        proceedEvent.setForeground(new java.awt.Color(255, 255, 255));
        proceedEvent.setText("Proceed to Event Details");
        proceedEvent.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        proceedEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedEventActionPerformed(evt);
            }
        });
        Bg.add(proceedEvent, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 760, 240, -1));

        rSMaterialButtonRectangle7.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle7.setEnabled(false);
        Bg.add(rSMaterialButtonRectangle7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 520, 620));

        tabbedPane1.addTab("signIn", Bg);

        Bg2.setBackground(new java.awt.Color(119, 82, 254));
        Bg2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setBackground(new java.awt.Color(0, 0, 0));
        jLabel7.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(194, 217, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Guest Count :");
        Bg2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 660, 140, 80));

        jLabel8.setBackground(new java.awt.Color(0, 0, 0));
        jLabel8.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Event Details");
        Bg2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 220, 110));

        tfeventType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tfeventType.setForeground(new java.awt.Color(25, 4, 130));
        tfeventType.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfeventType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tfeventType.setCaretColor(new java.awt.Color(25, 4, 130));
        tfeventType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfeventTypeActionPerformed(evt);
            }
        });
        Bg2.add(tfeventType, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 530, 170, 40));

        tfDate.setEditable(false);
        tfDate.setBackground(new java.awt.Color(255, 255, 255));
        tfDate.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tfDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDate.setToolTipText("");
        tfDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tfDate.setDisabledTextColor(new java.awt.Color(25, 4, 130));
        tfDate.setEnabled(false);
        tfDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDateActionPerformed(evt);
            }
        });
        Bg2.add(tfDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 360, 260, 40));

        jLabel15.setBackground(new java.awt.Color(0, 0, 0));
        jLabel15.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(194, 217, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Event Type : ");
        Bg2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 470, 140, 77));

        tfTime.setEditable(false);
        tfTime.setBackground(new java.awt.Color(255, 255, 255));
        tfTime.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tfTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfTime.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tfTime.setDisabledTextColor(new java.awt.Color(25, 4, 130));
        tfTime.setEnabled(false);
        tfTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTimeActionPerformed(evt);
            }
        });
        Bg2.add(tfTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 406, 260, -1));

        tfCount.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tfCount.setForeground(new java.awt.Color(25, 4, 130));
        tfCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfCount.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tfCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfCountActionPerformed(evt);
            }
        });
        Bg2.add(tfCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 680, 100, 40));

        EventType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        EventType.setForeground(new java.awt.Color(25, 4, 130));
        EventType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Wedding Reception", "Birthday Party", "Baptism", "Debut", "Family Gathering" }));
        EventType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        EventType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EventTypeActionPerformed(evt);
            }
        });
        Bg2.add(EventType, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 530, 200, 40));

        jLabel14.setBackground(new java.awt.Color(0, 0, 0));
        jLabel14.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(194, 217, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Event Date and Time ");
        Bg2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 100, 220, 40));

        jLabel17.setFont(new java.awt.Font("Dubai", 0, 20)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(194, 217, 255));
        jLabel17.setText("Type of Venue :");
        Bg2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 680, 140, -1));

        EventVenue.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        EventVenue.setForeground(new java.awt.Color(25, 4, 130));
        EventVenue.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Indoor", "Outdoor" }));
        EventVenue.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        EventVenue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EventVenueActionPerformed(evt);
            }
        });
        Bg2.add(EventVenue, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 680, 100, 40));

        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(194, 217, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Event Venue : ");
        Bg2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 570, -1, 60));

        placeType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        placeType.setForeground(new java.awt.Color(25, 4, 130));
        placeType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Malabanan Resort", "Eastern Star Resort", "Victoria Resort", "MyPlace Resort", "Aurora Resort" }));
        placeType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        placeType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeTypeActionPerformed(evt);
            }
        });
        Bg2.add(placeType, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 620, -1, 40));

        tfPlace.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tfPlace.setForeground(new java.awt.Color(25, 4, 130));
        tfPlace.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfPlace.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tfPlace.setCaretColor(new java.awt.Color(25, 4, 130));
        tfPlace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPlaceActionPerformed(evt);
            }
        });
        Bg2.add(tfPlace, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 620, 170, 40));

        placeCustom.setBackground(new java.awt.Color(142, 143, 250));
        placeCustom.setForeground(new java.awt.Color(255, 255, 255));
        placeCustom.setText("Add Custom Venue");
        placeCustom.setFont(new java.awt.Font("Roboto Medium", 1, 10)); // NOI18N
        placeCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeCustomActionPerformed(evt);
            }
        });
        Bg2.add(placeCustom, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 620, 110, -1));

        dateChooser1.setForeground(new java.awt.Color(25, 4, 130));
        dateChooser1.setDateFormat("yyyy-MM-dd");
        dateChooser1.setTextRefernce(tfDate);
        Bg2.add(dateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 150, 260, 200));

        timePicker1.setForeground(new java.awt.Color(25, 4, 130));
        timePicker1.set24hourMode(false);
        timePicker1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                timePicker1MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                timePicker1MouseExited(evt);
            }
        });
        timePicker1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                timePicker1PropertyChange(evt);
            }
        });
        Bg2.add(timePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(318, 150, 210, 290));

        eventCustom.setBackground(new java.awt.Color(142, 143, 250));
        eventCustom.setForeground(new java.awt.Color(255, 255, 255));
        eventCustom.setText("Add Custom Event");
        eventCustom.setFont(new java.awt.Font("Roboto Medium", 1, 10)); // NOI18N
        eventCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventCustomActionPerformed(evt);
            }
        });
        Bg2.add(eventCustom, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 530, 110, -1));

        proceedMenu.setBackground(new java.awt.Color(142, 143, 250));
        proceedMenu.setForeground(new java.awt.Color(255, 255, 255));
        proceedMenu.setText("Proceed to Menu");
        proceedMenu.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        proceedMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedMenuActionPerformed(evt);
            }
        });
        Bg2.add(proceedMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 760, -1, -1));

        clearEvent.setBackground(new java.awt.Color(142, 143, 250));
        clearEvent.setForeground(new java.awt.Color(255, 255, 255));
        clearEvent.setText("Clear Input");
        clearEvent.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        clearEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearEventActionPerformed(evt);
            }
        });
        Bg2.add(clearEvent, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 760, -1, -1));

        rSMaterialButtonRectangle5.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle5.setEnabled(false);
        rSMaterialButtonRectangle5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonRectangle5ActionPerformed(evt);
            }
        });
        Bg2.add(rSMaterialButtonRectangle5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 560, 390));

        rSMaterialButtonRectangle6.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle6.setEnabled(false);
        rSMaterialButtonRectangle6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonRectangle6ActionPerformed(evt);
            }
        });
        Bg2.add(rSMaterialButtonRectangle6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, 560, 260));

        rSPanelImage2.setImagen(new javax.swing.ImageIcon(getClass().getResource("/event.png"))); // NOI18N
        Bg2.add(rSPanelImage2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 40, 40));

        tabbedPane1.addTab("event", Bg2);

        Bg3.setBackground(new java.awt.Color(119, 82, 254));
        Bg3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(0, 0, 0));
        jLabel18.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Menu Details");
        Bg3.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 210, 80));

        packageType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        packageType.setForeground(new java.awt.Color(25, 4, 130));
        packageType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Package 1", "Package 2", "Package 3", "Custom Package" }));
        packageType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        packageType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packageTypeActionPerformed(evt);
            }
        });
        Bg3.add(packageType, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 330, 390, 65));

        jLabel19.setBackground(new java.awt.Color(0, 0, 0));
        jLabel19.setFont(new java.awt.Font("Dubai", 0, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(194, 217, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("You can select from our preset packages or have your menu fully customized!");
        Bg3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 270, 380, 77));

        jLabel20.setBackground(new java.awt.Color(0, 0, 0));
        jLabel20.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(194, 217, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Dietary Restriction : ");
        Bg3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 590, 210, 77));

        serviceType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        serviceType.setForeground(new java.awt.Color(25, 4, 130));
        serviceType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Buffet", "Ala Carte", "Pass Around" }));
        serviceType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        serviceType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceTypeActionPerformed(evt);
            }
        });
        Bg3.add(serviceType, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 130, 240, 65));

        jLabel22.setBackground(new java.awt.Color(0, 0, 0));
        jLabel22.setFont(new java.awt.Font("Dubai", 0, 20)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(194, 217, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Service Type : ");
        Bg3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, 130, 60));

        mealType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        mealType.setForeground(new java.awt.Color(25, 4, 130));
        mealType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Breakfast", "Lunch", "Dinner", "Dessert" }));
        mealType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        mealType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mealTypeActionPerformed(evt);
            }
        });
        Bg3.add(mealType, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 510, 250, 65));

        jLabel23.setBackground(new java.awt.Color(0, 0, 0));
        jLabel23.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(194, 217, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Meal Type : ");
        Bg3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 500, 140, 77));

        dietaryRestriction.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        dietaryRestriction.setForeground(new java.awt.Color(25, 4, 130));
        dietaryRestriction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Gluten Free", "Nut Allergy", "Shrimp" }));
        dietaryRestriction.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        dietaryRestriction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dietaryRestrictionActionPerformed(evt);
            }
        });
        Bg3.add(dietaryRestriction, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 600, 190, 65));

        jLabel24.setBackground(new java.awt.Color(0, 0, 0));
        jLabel24.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(194, 217, 255));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Food Beverages :");
        Bg3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 240, 230, 77));

        materialButton11.setBackground(new java.awt.Color(142, 143, 250));
        materialButton11.setForeground(new java.awt.Color(255, 255, 255));
        materialButton11.setText("Show Packages");
        materialButton11.setFont(new java.awt.Font("Roboto Medium", 1, 12)); // NOI18N
        materialButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                materialButton11ActionPerformed(evt);
            }
        });
        Bg3.add(materialButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 410, 130, 30));

        proceedOrder.setBackground(new java.awt.Color(142, 143, 250));
        proceedOrder.setForeground(new java.awt.Color(255, 255, 255));
        proceedOrder.setText("Proceed to Order");
        proceedOrder.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        proceedOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedOrderActionPerformed(evt);
            }
        });
        Bg3.add(proceedOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 730, -1, -1));

        materialButton13.setBackground(new java.awt.Color(142, 143, 250));
        materialButton13.setForeground(new java.awt.Color(255, 255, 255));
        materialButton13.setText("Clear Input");
        materialButton13.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        materialButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                materialButton13ActionPerformed(evt);
            }
        });
        Bg3.add(materialButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 730, -1, -1));

        rSMaterialButtonRectangle2.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle2.setEnabled(false);
        rSMaterialButtonRectangle2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonRectangle2ActionPerformed(evt);
            }
        });
        Bg3.add(rSMaterialButtonRectangle2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, 530, 220));

        rSMaterialButtonRectangle3.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle3.setEnabled(false);
        Bg3.add(rSMaterialButtonRectangle3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 530, 120));

        rSMaterialButtonRectangle4.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle4.setEnabled(false);
        Bg3.add(rSMaterialButtonRectangle4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 530, 220));

        rSPanelImage3.setImagen(new javax.swing.ImageIcon(getClass().getResource("/menu.png"))); // NOI18N
        Bg3.add(rSPanelImage3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 40, 40));

        tabbedPane1.addTab("menu", Bg3);

        Bg4.setBackground(new java.awt.Color(119, 82, 254));
        Bg4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rSPanelImage4.setImagen(new javax.swing.ImageIcon(getClass().getResource("/item.png"))); // NOI18N
        Bg4.add(rSPanelImage4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 40, 40));

        tableSeater.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tableSeater.setForeground(new java.awt.Color(25, 4, 130));
        tableSeater.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "4 Seater", "8 Seater", "10 Seater", "12 Seater" }));
        tableSeater.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tableSeater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableSeaterActionPerformed(evt);
            }
        });
        Bg4.add(tableSeater, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 240, 170, 60));

        suggestion.setColumns(20);
        suggestion.setForeground(new java.awt.Color(25, 4, 130));
        suggestion.setRows(5);
        suggestion.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        jScrollPane1.setViewportView(suggestion);

        Bg4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 620, 400, 70));

        jLabel25.setBackground(new java.awt.Color(0, 0, 0));
        jLabel25.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Set-up and Items needed");
        Bg4.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 390, 80));

        jLabel26.setBackground(new java.awt.Color(0, 0, 0));
        jLabel26.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(194, 217, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Backdrop");
        Bg4.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 300, 220, 77));

        jLabel27.setBackground(new java.awt.Color(0, 0, 0));
        jLabel27.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(194, 217, 255));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("If there’s no suggestion, write \"None.\"");
        Bg4.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 680, 290, 77));

        backdrop.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        backdrop.setForeground(new java.awt.Color(25, 4, 130));
        backdrop.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "White", "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet", "Pink", "Black" }));
        backdrop.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        backdrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backdropActionPerformed(evt);
            }
        });
        Bg4.add(backdrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 360, 170, 60));

        jLabel28.setBackground(new java.awt.Color(0, 0, 0));
        jLabel28.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(194, 217, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("Tables and Chairs :");
        Bg4.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, 220, 50));

        jLabel29.setBackground(new java.awt.Color(0, 0, 0));
        jLabel29.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(194, 217, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("Tables");
        Bg4.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 220, -1));

        tableType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tableType.setForeground(new java.awt.Color(25, 4, 130));
        tableType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Round", "Square", "Rectangular" }));
        tableType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tableType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableTypeActionPerformed(evt);
            }
        });
        Bg4.add(tableType, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 240, 170, 60));

        items.setBackground(new java.awt.Color(0, 0, 0));
        items.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        items.setForeground(new java.awt.Color(194, 217, 255));
        items.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        items.setText("Add More :");
        Bg4.add(items, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 430, 200, 40));

        jLabel31.setBackground(new java.awt.Color(0, 0, 0));
        jLabel31.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(194, 217, 255));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Table Cloth");
        Bg4.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 300, 120, 77));

        tableCloth.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tableCloth.setForeground(new java.awt.Color(25, 4, 130));
        tableCloth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "White", "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet", "Pink", "Black" }));
        tableCloth.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tableCloth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableClothActionPerformed(evt);
            }
        });
        Bg4.add(tableCloth, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 360, 170, 60));

        jLabel32.setBackground(new java.awt.Color(0, 0, 0));
        jLabel32.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(194, 217, 255));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Chairs");
        Bg4.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 430, 70, 40));

        chairs.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        chairs.setForeground(new java.awt.Color(25, 4, 130));
        chairs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiffany Chairs", "Monoblock Chairs" }));
        chairs.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        chairs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chairsActionPerformed(evt);
            }
        });
        Bg4.add(chairs, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 470, 170, 60));

        cups.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        cups.setForeground(new java.awt.Color(194, 217, 255));
        cups.setText("Cups");
        cups.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cupsActionPerformed(evt);
            }
        });
        Bg4.add(cups, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 490, -1, -1));

        napkins.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        napkins.setForeground(new java.awt.Color(194, 217, 255));
        napkins.setText("Napkins");
        napkins.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        Bg4.add(napkins, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 460, -1, -1));

        utensils.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        utensils.setForeground(new java.awt.Color(194, 217, 255));
        utensils.setText("Utensils");
        utensils.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        utensils.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                utensilsActionPerformed(evt);
            }
        });
        Bg4.add(utensils, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 490, -1, -1));

        plates.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        plates.setForeground(new java.awt.Color(194, 217, 255));
        plates.setText("Plates");
        plates.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        plates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                platesActionPerformed(evt);
            }
        });
        Bg4.add(plates, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 460, -1, -1));

        jLabel33.setBackground(new java.awt.Color(0, 0, 0));
        jLabel33.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(194, 217, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("Theme or Motif :");
        Bg4.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 90, 170, 60));

        tfMotif.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tfMotif.setForeground(new java.awt.Color(25, 4, 130));
        tfMotif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfMotif.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tfMotif.setCaretColor(new java.awt.Color(25, 4, 130));
        Bg4.add(tfMotif, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 100, 240, -1));

        confirmOrder.setBackground(new java.awt.Color(142, 143, 250));
        confirmOrder.setForeground(new java.awt.Color(255, 255, 255));
        confirmOrder.setText("Confirm Order");
        confirmOrder.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        confirmOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmOrderActionPerformed(evt);
            }
        });
        Bg4.add(confirmOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 750, 190, -1));

        orderReceipt.setBackground(new java.awt.Color(142, 143, 250));
        orderReceipt.setForeground(new java.awt.Color(255, 255, 255));
        orderReceipt.setText("Generate Order Receipt");
        orderReceipt.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        orderReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderReceiptActionPerformed(evt);
            }
        });
        Bg4.add(orderReceipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 750, 220, -1));

        rSMaterialButtonRectangle1.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle1.setEnabled(false);
        Bg4.add(rSMaterialButtonRectangle1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 460, 410));

        jLabel30.setBackground(new java.awt.Color(0, 0, 0));
        jLabel30.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(194, 217, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Suggestion or Request :");
        Bg4.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 560, 290, 77));

        tabbedPane1.addTab("Order", Bg4);

        jPanel4.setBackground(new java.awt.Color(119, 82, 254));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel34.setBackground(new java.awt.Color(0, 0, 0));
        jLabel34.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("Order Details :");
        jPanel4.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 250, 77));

        printReceipt.setBackground(new java.awt.Color(142, 143, 250));
        printReceipt.setForeground(new java.awt.Color(255, 255, 255));
        printReceipt.setText("Print Receipt");
        printReceipt.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        printReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printReceiptActionPerformed(evt);
            }
        });
        jPanel4.add(printReceipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 730, 270, 30));

        textArea2.setEditable(false);
        textArea2.setBackground(new java.awt.Color(255, 255, 255));
        textArea2.setColumns(20);
        textArea2.setFont(new java.awt.Font("Dubai", 0, 13)); // NOI18N
        textArea2.setForeground(new java.awt.Color(25, 4, 130));
        textArea2.setRows(5);
        textArea2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        textArea2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        textArea2.setEnabled(false);
        jScrollPane3.setViewportView(textArea2);

        jPanel4.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 270, 620));

        rSMaterialButtonRectangle9.setBackground(new java.awt.Color(25, 4, 130));
        jPanel4.add(rSMaterialButtonRectangle9, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, -10, 200, 890));

        rSMaterialButtonRectangle10.setBackground(new java.awt.Color(25, 4, 130));
        jPanel4.add(rSMaterialButtonRectangle10, new org.netbeans.lib.awtextra.AbsoluteConstraints(-20, -30, 130, 910));

        bookAgain.setBackground(new java.awt.Color(142, 143, 250));
        bookAgain.setForeground(new java.awt.Color(255, 255, 255));
        bookAgain.setText("Book again");
        bookAgain.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        bookAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookAgainActionPerformed(evt);
            }
        });
        jPanel4.add(bookAgain, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 770, 270, 30));

        javax.swing.GroupLayout Bg5Layout = new javax.swing.GroupLayout(Bg5);
        Bg5.setLayout(Bg5Layout);
        Bg5Layout.setHorizontalGroup(
            Bg5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Bg5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        Bg5Layout.setVerticalGroup(
            Bg5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabbedPane1.addTab("receipt", Bg5);

        adminpanel2.add(tabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -40, 610, 880));

        AdminPanel.add(adminpanel2, "card2");

        adminpanel3.setBackground(new java.awt.Color(119, 82, 254));
        adminpanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        showList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Client Number", "Full Name", "Phone Number", "Event Date", "Event Time", "Place"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(showList);

        adminpanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 540, 420));

        showlistMonth.setBackground(new java.awt.Color(142, 143, 250));
        showlistMonth.setForeground(new java.awt.Color(255, 255, 255));
        showlistMonth.setText("SHOW LIST");
        showlistMonth.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        showlistMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showlistMonthActionPerformed(evt);
            }
        });
        adminpanel3.add(showlistMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 680, -1, -1));

        delete.setBackground(new java.awt.Color(142, 143, 250));
        delete.setForeground(new java.awt.Color(255, 255, 255));
        delete.setText("DELETE");
        delete.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        adminpanel3.add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 680, -1, -1));

        monthOrder.setBackground(new java.awt.Color(255, 255, 255));
        monthOrder.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        monthOrder.setForeground(new java.awt.Color(255, 255, 255));
        monthOrder.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        monthOrder.setText("For the month of");
        adminpanel3.add(monthOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 442, 77));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Dubai", 1, 39)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Recent Orders ");
        adminpanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(-70, 20, 442, 77));

        monthList.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        monthList.setForeground(new java.awt.Color(25, 4, 130));
        monthList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        monthList.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        monthList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthListActionPerformed(evt);
            }
        });
        adminpanel3.add(monthList, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 120, 180, 40));

        rSMaterialButtonRectangle8.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle8.setEnabled(false);
        rSMaterialButtonRectangle8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonRectangle8ActionPerformed(evt);
            }
        });
        adminpanel3.add(rSMaterialButtonRectangle8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 560, 100));

        AdminPanel.add(adminpanel3, "card2");

        adminpanel4.setBackground(new java.awt.Color(119, 82, 254));
        adminpanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        showlistOrder.setBackground(new java.awt.Color(142, 143, 250));
        showlistOrder.setForeground(new java.awt.Color(255, 255, 255));
        showlistOrder.setText("SHOW LIST");
        showlistOrder.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        showlistOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showlistOrderActionPerformed(evt);
            }
        });
        adminpanel4.add(showlistOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 680, -1, -1));

        update.setBackground(new java.awt.Color(142, 143, 250));
        update.setForeground(new java.awt.Color(255, 255, 255));
        update.setText("Update");
        update.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateActionPerformed(evt);
            }
        });
        adminpanel4.add(update, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 680, -1, -1));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Dubai", 1, 39)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Edit Orders Data");
        adminpanel4.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(-50, 20, 442, 77));

        clientIdField.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        clientIdField.setForeground(new java.awt.Color(25, 4, 130));
        clientIdField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        clientIdField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        clientIdField.setCaretColor(new java.awt.Color(25, 4, 130));
        adminpanel4.add(clientIdField, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 120, 210, 60));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Data Name", "Data"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        showList3.setViewportView(jTable1);

        adminpanel4.add(showList3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 227, -1, 410));

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Client ID :");
        adminpanel4.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(-60, 120, 442, 70));

        AdminPanel.add(adminpanel4, "card2");

        backgroundAdmin.add(AdminPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, -10, 610, 860));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(backgroundAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 850, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void materialButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_materialButton3MouseClicked
       adminpanel3.setVisible(true);
       adminpanel1.setVisible(false);
       adminpanel2.setVisible(false);
       adminpanel4.setVisible(false);
       
       materialButton3.setBackground(medyoDark);
       materialButton1.setBackground(medyoLight);
       materialButton2.setBackground(medyoLight);
       materialButton4.setBackground(medyoLight);
       
       materialButton1.repaint();
       materialButton2.repaint();
       materialButton4.repaint();
       materialButton4.repaint();
    }//GEN-LAST:event_materialButton3MouseClicked

    private void materialButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_materialButton1MouseClicked
       adminpanel1.setVisible(true);
       adminpanel2.setVisible(false);
       adminpanel3.setVisible(false);
       adminpanel4.setVisible(false);
       
       materialButton1.setBackground(medyoDark);
       materialButton2.setBackground(medyoLight);
       materialButton3.setBackground(medyoLight);
       materialButton4.setBackground(medyoLight);
       
       materialButton1.repaint();
       materialButton2.repaint();
       materialButton4.repaint();
       materialButton4.repaint();
    }//GEN-LAST:event_materialButton1MouseClicked

    private void materialButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_materialButton2MouseClicked
       adminpanel2.setVisible(true);
       adminpanel1.setVisible(false);
       adminpanel3.setVisible(false);
       adminpanel4.setVisible(false);
       
       materialButton2.setBackground(medyoDark);
       materialButton1.setBackground(medyoLight);
       materialButton3.setBackground(medyoLight);
       materialButton4.setBackground(medyoLight);
       
       materialButton1.repaint();
       materialButton2.repaint();
       materialButton4.repaint();
       materialButton4.repaint();
    }//GEN-LAST:event_materialButton2MouseClicked

    private void materialButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_materialButton4MouseClicked
       adminpanel4.setVisible(true);
       adminpanel2.setVisible(false);
       adminpanel3.setVisible(false);
       adminpanel1.setVisible(false);
       
       materialButton4.setBackground(medyoDark);
       materialButton2.setBackground(medyoLight);
       materialButton3.setBackground(medyoLight);
       materialButton1.setBackground(medyoLight);
       
       materialButton1.repaint();
       materialButton2.repaint();
       materialButton4.repaint();
       materialButton4.repaint();
    }//GEN-LAST:event_materialButton4MouseClicked

    private void tfNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNameActionPerformed

    private void tfPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPhoneNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPhoneNumberActionPerformed

    private void tfEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfEmailActionPerformed

    private void clearInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearInfoActionPerformed
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to proceed?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            tfName.setText(null);
            tfPhoneNumber.setText(null);
            tfEmail.setText(null);
            tfAddress.setText(null);
        } else if (response == JOptionPane.NO_OPTION) {
            System.out.println("User chose: No");
        } else {
            System.out.println("User closed the dialog.");
        }
    }//GEN-LAST:event_clearInfoActionPerformed

    private void proceedEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedEventActionPerformed
    String fullname = tfName.getText().trim();
    String address = tfAddress.getText().trim();
    String phone = tfPhoneNumber.getText().trim();
    String email = tfEmail.getText().trim();
    
    boolean success = catering.insert(fullname, address, phone, email);
        if (success) {
            tabbedPane1.setSelectedIndex(1);
        }
    }//GEN-LAST:event_proceedEventActionPerformed

    private void tfeventTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfeventTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfeventTypeActionPerformed

    private void tfDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDateActionPerformed

    private void tfTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTimeActionPerformed

    private void tfCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfCountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfCountActionPerformed

    private void EventTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EventTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EventTypeActionPerformed

    private void EventVenueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EventVenueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EventVenueActionPerformed

    private void placeTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeTypeActionPerformed

    private void tfPlaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPlaceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPlaceActionPerformed

    private void placeCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeCustomActionPerformed
        placeCustom.addActionListener(e -> addCustomPlace());
    }//GEN-LAST:event_placeCustomActionPerformed

    private void timePicker1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_timePicker1MouseClicked

    }//GEN-LAST:event_timePicker1MouseClicked

    private void timePicker1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_timePicker1MouseExited
        String timeSelected = timePicker1.getSelectedTime();
        tfTime.setText(timeSelected);
    }//GEN-LAST:event_timePicker1MouseExited

    private void timePicker1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_timePicker1PropertyChange
        String timeSelected = timePicker1.getSelectedTime();
        tfTime.setText(timeSelected);
    }//GEN-LAST:event_timePicker1PropertyChange

    private void eventCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventCustomActionPerformed
        eventCustom.addActionListener(e -> addEventType());
    }//GEN-LAST:event_eventCustomActionPerformed

    private void proceedMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedMenuActionPerformed
        SelectedDate date = dateChooser1.getSelectedDate();
        String dateSelected = tfDate.getSelectedText();

        String time = timePicker1.getSelectedTime();
        tfTime.setText(time);

//        catering.insertEvent();
        String formattedTime = null;
        String dateInput = tfDate.getText().trim();
        String timeInput = tfTime.getText().trim();
        String eventType = (String) EventType.getSelectedItem(); // Assuming EventType is a JComboBox
        String guestInput = tfCount.getText().trim();
        String eventVenue = (String) EventVenue.getSelectedItem(); // Assuming EventVenue is a JComboBox
        String placeTypeInput = (String) placeType.getSelectedItem(); // Assuming placeType is a JComboBox

        // Call the insertEvent method from the Cateringv1 instance
        boolean isSuccess = catering.insertEvent(dateInput, timeInput, eventType, guestInput, eventVenue, placeTypeInput, formattedTime);
        if (isSuccess) {
        JOptionPane.showMessageDialog(this, "Record Successfully Added!");
        tabbedPane1.setSelectedIndex(2);
    } else {
        JOptionPane.showMessageDialog(this, "Record Failed to Save");
    }
    }//GEN-LAST:event_proceedMenuActionPerformed

    private void clearEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearEventActionPerformed
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to proceed?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            tfDate.setText(null);
            tfTime.setText(null);
            EventType.setSelectedIndex(0);
            tfCount.setText(null);
            EventVenue.setSelectedIndex(0);
        } else if (response == JOptionPane.NO_OPTION) {
            System.out.println("User chose: No");
        } else {
            System.out.println("User closed the dialog.");
        }
    }//GEN-LAST:event_clearEventActionPerformed

    private void rSMaterialButtonRectangle5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonRectangle5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonRectangle5ActionPerformed

    private void rSMaterialButtonRectangle6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonRectangle6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonRectangle6ActionPerformed

    private void packageTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packageTypeActionPerformed

    }//GEN-LAST:event_packageTypeActionPerformed

    private void serviceTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serviceTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_serviceTypeActionPerformed

    private void mealTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mealTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mealTypeActionPerformed

    private void dietaryRestrictionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dietaryRestrictionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dietaryRestrictionActionPerformed

    private void materialButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_materialButton11ActionPerformed
        Menu showmenu = new Menu();
        showmenu.show();
    }//GEN-LAST:event_materialButton11ActionPerformed

    private void proceedOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedOrderActionPerformed
        String service = (String) serviceType.getSelectedItem();
        String PackageType = (String) packageType.getSelectedItem();
        String MealType = (String) mealType.getSelectedItem();
        String dietary = (String) dietaryRestriction.getSelectedItem();
        
        boolean isSuccess = catering.menuInsert(service, PackageType, MealType, dietary);
        if (isSuccess) {
        JOptionPane.showMessageDialog(this, "Record Successfully Added!");
        tabbedPane1.setSelectedIndex(3);
    }
    }//GEN-LAST:event_proceedOrderActionPerformed

    private void materialButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_materialButton13ActionPerformed
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to proceed?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            serviceType.setSelectedIndex(0);
            packageType.setSelectedIndex(0);
            mealType.setSelectedIndex(0);
            dietaryRestriction.setSelectedIndex(0);
        } else if (response == JOptionPane.NO_OPTION) {
            System.out.println("User chose: No");
        } else {
            System.out.println("User closed the dialog.");
        }
    }//GEN-LAST:event_materialButton13ActionPerformed

    private void rSMaterialButtonRectangle2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonRectangle2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonRectangle2ActionPerformed

    private void tableSeaterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableSeaterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tableSeaterActionPerformed

    private void backdropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backdropActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_backdropActionPerformed

    private void tableTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tableTypeActionPerformed

    private void tableClothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableClothActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tableClothActionPerformed

    private void chairsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chairsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chairsActionPerformed

    private void cupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cupsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cupsActionPerformed

    private void utensilsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_utensilsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_utensilsActionPerformed

    private void platesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_platesActionPerformed

    private void confirmOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmOrderActionPerformed
        String motif = tfMotif.getText().trim();
        String table = (String) tableType.getSelectedItem();
        String tableSeat = (String) tableSeater.getSelectedItem();
        String cloth = (String) tableCloth.getSelectedItem();
        String backDrop = (String) backdrop.getSelectedItem();
        String chair = (String) chairs.getSelectedItem();

        // Collect selected items from checkboxes
        StringBuilder selectedItems = new StringBuilder();
        if (napkins.isSelected()) {
            selectedItems.append(napkins.getText()).append(", ");
        }
        if (utensils.isSelected()) {
            selectedItems.append(utensils.getText()).append(", ");
        }
        if (plates.isSelected()) {
            selectedItems.append(plates.getText()).append(", ");
        }
        if (cups.isSelected()) {
            selectedItems.append(cups.getText()).append(", ");
        }
        // Remove the last comma and space if any items were added
        if (selectedItems.length() > 0) {
            selectedItems.setLength(selectedItems.length() - 2); // Remove last ", "
        } else {
            selectedItems.append("None"); // If no items selected
        }

        String items = selectedItems.toString();
        String Suggestion = suggestion.getText().trim(); // Assuming suggestion is a JTextArea
        
        catering.ordertInsert(motif, table, tableSeat, cloth, backDrop, chair, items, Suggestion);
    }//GEN-LAST:event_confirmOrderActionPerformed

    private void orderReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderReceiptActionPerformed
        int clientID = currentClientID;
        tabbedPane1.setSelectedIndex(4);
        
        StringBuilder selectedItems = new StringBuilder();
    if (napkins.isSelected()) {
        selectedItems.append(napkins.getText()).append(", ");
    }
    if (utensils.isSelected()) {
        selectedItems.append(utensils.getText()).append(", ");
    }
    if (plates.isSelected()) {
        selectedItems.append(plates.getText()).append(", ");
    }
    if (cups.isSelected()) {
        selectedItems.append(cups.getText()).append(", ");
    }
    // Remove the last comma and space if any items were added
    if (selectedItems.length() > 0) {
        selectedItems.setLength(selectedItems.length() - 2); // Remove last ", "
    } else {
        selectedItems.append("None"); // If no items selected
    }

        String receipt = 
        "   ------------------------------------------------- \n" +
        "              EVELYN'S CATERING SERVICES      \n" +
        "   ------------------------------------------------- \n" +
        "                 Order Number:   " + clientID + "          \n" +
        "   Full Name:         " + tfName.getText() + "                          \n" +
        "   Contact Num:         " + tfPhoneNumber.getText() + "                          \n" +
        "   Date:         " + tfDate.getText() + "                                      \n" +
        "   Time:         " + tfTime.getText() + "                                      \n" +
        "   ------------------------------------------------- \n" +
        "   Event Description                                   \n" +
        "   ------------------------------------------------- \n" +
        "   Event Type:         " + EventType.getSelectedItem() + "                                   \n" +
        "   Event Venue:         " + placeType.getSelectedItem() + "                               \n" +
        "   Guest Count:         " + tfCount.getText() + "                                 \n" +
        "   Type of Venue:         " + EventVenue.getSelectedItem() + "                                \n" +
        "   ------------------------------------------------- \n" +
        "   Service Description                                   \n" +
        "   ------------------------------------------------- \n" +
        "   Service Type:         " + serviceType.getSelectedItem() + "                                   \n" +
        "   Food Beverage:         " + packageType.getSelectedItem() + "                               \n" +
        "   Meal Type:         " + mealType.getSelectedItem() + "                                         \n" +
        "   Dietary Restrictions:          " + dietaryRestriction.getSelectedItem() + "                                         \n" +
        "   ------------------------------------------------- \n" +
        "   Items Description                                         \n" +
        "   ------------------------------------------------- \n" +
        "   Theme:         " + tfMotif.getText() + "                                          \n" +
        "   Table and Type        : " + tableType.getSelectedItem() + "                              \n" +
        "   Table Cloth:         " + tableCloth.getSelectedItem() + "                                \n" +
        "   Backdrop:         " + backdrop.getSelectedItem() + "                                      \n" +
        "   Chairs:        " + chairs.getSelectedItem() + "                                        \n" +
        "   Additional Items:   " + selectedItems.toString() + "                     \n" +
        "   Suggestion or Request: \n   " + suggestion.getText() + "                     \n" +
        "   ------------------------------------------------- \n" +
        "   Downpayment:                               Php 1,500  \n" +
        "   ------------------------------------------------- \n" +
        "   TOTAL:                                         Php 1,500  \n" +
        "   ------------------------------------------------- \n" +
        "                Thanks for Choosing Us!  \n" +
        "   ------------------------------------------------- \n" +
        "   Please proceed to next page for Downpayment       \n" +
        "   ------------------------------------------------- "; 
        textArea2.setText(receipt);
    }//GEN-LAST:event_orderReceiptActionPerformed

    private void printReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printReceiptActionPerformed
        try {
           textArea2.print();
            } catch (Exception e) {
            }
    }//GEN-LAST:event_printReceiptActionPerformed

    private void showlistMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showlistMonthActionPerformed
   showOrders();
    }//GEN-LAST:event_showlistMonthActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
       deleteSelectedRow();
    }//GEN-LAST:event_deleteActionPerformed

    private void monthListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthListActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_monthListActionPerformed

    private void rSMaterialButtonRectangle8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonRectangle8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonRectangle8ActionPerformed

    private void showlistOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showlistOrderActionPerformed
        showOrderInfo();
    }//GEN-LAST:event_showlistOrderActionPerformed

    private void updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateActionPerformed
        updateSelectedRow();
    }//GEN-LAST:event_updateActionPerformed

    private void materialButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_materialButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_materialButton4ActionPerformed

    private void materialButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_materialButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_materialButton3ActionPerformed

    private void bookAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookAgainActionPerformed
        
            tfName.setText(null);
            tfPhoneNumber.setText(null);
            tfEmail.setText(null);
            tfAddress.setText(null);
            
            tfDate.setText(null);
            tfTime.setText(null);
            EventType.setSelectedIndex(0);
            tfCount.setText(null);
            EventVenue.setSelectedIndex(0);
            
            serviceType.setSelectedIndex(0);
            packageType.setSelectedIndex(0);
            mealType.setSelectedIndex(0);
            dietaryRestriction.setSelectedIndex(0);
            
            tfMotif.setText(null);
            tableType.setSelectedIndex(0);
            tableSeater.setSelectedIndex(0);
            tableCloth.setSelectedIndex(0);
            backdrop.setSelectedIndex(0);
            chairs.setSelectedIndex(0);
            suggestion.setText(null);
            napkins.setSelected(false);
            utensils.setSelected(false);
            plates.setSelected(false);
            cups.setSelected(false);
            
            
        tabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_bookAgainActionPerformed

    private void rSMaterialButtonRectangle11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonRectangle11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonRectangle11ActionPerformed

    private void materialButton16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_materialButton16MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_materialButton16MouseClicked

    private void materialButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_materialButton16ActionPerformed
      int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Exit the application
        }
    }//GEN-LAST:event_materialButton16ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AdminPanel;
    private javax.swing.JPanel Bg;
    private javax.swing.JPanel Bg2;
    private javax.swing.JPanel Bg3;
    private javax.swing.JPanel Bg4;
    private javax.swing.JPanel Bg5;
    private javax.swing.JComboBox<String> EventType;
    private javax.swing.JComboBox<String> EventVenue;
    private javax.swing.JPanel adminpanel1;
    private javax.swing.JPanel adminpanel2;
    private javax.swing.JPanel adminpanel3;
    private javax.swing.JPanel adminpanel4;
    private javax.swing.JComboBox<String> backdrop;
    private javax.swing.JPanel backgroundAdmin;
    private necesario.MaterialButton bookAgain;
    private javax.swing.JComboBox<String> chairs;
    private necesario.MaterialButton clearEvent;
    private necesario.MaterialButton clearInfo;
    private javax.swing.JTextField clientIdField;
    private necesario.MaterialButton confirmOrder;
    private javax.swing.JCheckBox cups;
    private com.raven.datechooser.DateChooser dateChooser1;
    private necesario.MaterialButton delete;
    private javax.swing.JComboBox<String> dietaryRestriction;
    private necesario.MaterialButton eventCustom;
    private javax.swing.JLabel items;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private necesario.MaterialButton materialButton1;
    private necesario.MaterialButton materialButton11;
    private necesario.MaterialButton materialButton13;
    private necesario.MaterialButton materialButton16;
    private necesario.MaterialButton materialButton2;
    private necesario.MaterialButton materialButton3;
    private necesario.MaterialButton materialButton4;
    private javax.swing.JComboBox<String> mealType;
    private javax.swing.JComboBox<String> monthList;
    private javax.swing.JLabel monthOrder;
    private javax.swing.JCheckBox napkins;
    private necesario.MaterialButton orderReceipt;
    private javax.swing.JComboBox<String> packageType;
    private necesario.MaterialButton placeCustom;
    private javax.swing.JComboBox<String> placeType;
    private javax.swing.JCheckBox plates;
    private necesario.MaterialButton printReceipt;
    private necesario.MaterialButton proceedEvent;
    private necesario.MaterialButton proceedMenu;
    private necesario.MaterialButton proceedOrder;
    private rojerusan.RSMaterialButtonCircleBeanInfo rSMaterialButtonCircleBeanInfo1;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle1;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle10;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle11;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle2;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle3;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle4;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle5;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle6;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle7;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle8;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle9;
    private rojerusan.RSPanelImage rSPanelImage1;
    private rojerusan.RSPanelImage rSPanelImage2;
    private rojerusan.RSPanelImage rSPanelImage3;
    private rojerusan.RSPanelImage rSPanelImage4;
    private rojerusan.RSPanelImage rSPanelImage7;
    private javax.swing.JComboBox<String> serviceType;
    private javax.swing.JTable showList;
    private javax.swing.JScrollPane showList3;
    private necesario.MaterialButton showlistMonth;
    private necesario.MaterialButton showlistOrder;
    private javax.swing.JTextArea suggestion;
    private javax.swing.JTabbedPane tabbedPane1;
    private javax.swing.JComboBox<String> tableCloth;
    private javax.swing.JComboBox<String> tableSeater;
    private javax.swing.JComboBox<String> tableType;
    private javax.swing.JTextArea textArea2;
    private javax.swing.JTextField tfAddress;
    private javax.swing.JTextField tfCount;
    private javax.swing.JTextField tfDate;
    private javax.swing.JTextField tfEmail;
    private javax.swing.JTextField tfMotif;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfPhoneNumber;
    private javax.swing.JTextField tfPlace;
    private javax.swing.JTextField tfTime;
    private javax.swing.JTextField tfeventType;
    private com.raven.swing.TimePicker timePicker1;
    private necesario.MaterialButton update;
    private javax.swing.JCheckBox utensils;
    // End of variables declaration//GEN-END:variables
}
