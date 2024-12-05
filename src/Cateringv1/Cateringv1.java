/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Cateringv1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.raven.datechooser.SelectedDate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Statement;
import java.util.Map;


/**
 *
 * @author Johnrey
 */
public final class Cateringv1 extends javax.swing.JFrame {
    
    Menu menu = new Menu();
     
    public Cateringv1() {
           initComponents();
           Connect();
        
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

 public boolean insert(String fullname, String address, String phone, String email) {
        try {
            // Validate inputs
            if (fullname.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please input all fields");
                return false; // Return false if any fields are empty
            }

            // Validate phone number
            if (phone.length() > 11) {
                JOptionPane.showMessageDialog(this, "Phone number is too long!");
                return false; // Return false if phone number is too long
            }
            if (!phone.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Phone number must contain only digits.");
                return false; // Return false if phone number contains non-digits
            }

            // Prepare and execute insert statement
            pst = con.prepareStatement("INSERT INTO clientstable(FullName, Address, phoneNumber, customerEmail) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, fullname);
            pst.setString(2, address);
            pst.setString(3, phone);
            pst.setString(4, email);

            int k = pst.executeUpdate();

            if (k == 1) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        currentClientID = generatedKeys.getInt(1); // Get the generated ClientID
                    }
                }
                JOptionPane.showMessageDialog(this, "Record Successfully Added!");
                tabbedPane1.setSelectedIndex(2); // Change tab index only on success
                return true; // Return true if record added successfully
            } else {
                JOptionPane.showMessageDialog(this, "Record Failed to Save");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            return false; // Return false in case of an exception
        }
        return false; // Return false if the insertion was not successful
    }
     
public boolean insertEvent(String dateInput, String timeInput, String eventType, String guestInput, String eventVenue, String placeType, String formattedTime) {
        try {
            // Validate inputs
            if (dateInput == null || dateInput.isEmpty() ||
                timeInput == null || timeInput.isEmpty() ||
                eventType == null || eventType.isEmpty() ||
                guestInput == null || guestInput.isEmpty() ||
                placeType == null || placeType.isEmpty()) {

                JOptionPane.showMessageDialog(null, "Please input all fields");
                return false;
            }

            // Convert date input to SQL Date
            java.sql.Date sqlDate = java.sql.Date.valueOf(dateInput);

            // Parse time input
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
                Date date = inputFormat.parse(timeInput);
                formattedTime = outputFormat.format(date);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid time format. Please use HH:MM AM/PM.");
                return false;
            }

            // Convert formatted time to SQL Time
            java.sql.Time sqlTime = java.sql.Time.valueOf(formattedTime);

            // Check if the event date is already booked (ignoring time)
            String checkQuery = "SELECT COUNT(*) FROM eventstable WHERE EventDate = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setDate(1, sqlDate);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "This date is already booked.");
                    return false;
                }
            }

            // Parse guest count
            int guestCount;
            try {
                guestCount = Integer.parseInt(guestInput);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Guest count must be a valid number.");
                return false;
            }

            // Prepare and execute insert statement
            pst = con.prepareStatement("INSERT INTO eventstable(ClientID, EventDate, EventTime, EventType, GuessCount, EventVenue, place) VALUES(?, ?, ?, ?, ?, ?, ?)");
            pst.setInt(1, currentClientID); // Assuming you want to associate it with the current client
            pst.setDate(2, sqlDate);
            pst.setTime(3, sqlTime);
            pst.setString(4, eventType);
            pst.setInt(5, guestCount);
            pst.setString(6, eventVenue);
            pst.setString(7, placeType);

            int k = pst.executeUpdate();

            return k == 1; // Return true if record added successfully, otherwise false

        } catch (SQLException ex) {
            Logger.getLogger(Cateringv1.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use YYYY-MM-DD.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + ex.getMessage());
        }
        return false; // Return false in case of any exceptions
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
 
public boolean menuInsert(String service, String packageType, String mealType, String dietary) {
    try {
        String mainCourse = "";
        String appetizer = "";
        String dessert = "";
        String drinks = "";

        // Check if the package type is a custom package
        if (packageType.equals("Custom Package")) {
            // Prompt user for custom menu input
            mainCourse = JOptionPane.showInputDialog("Enter Main Course:");
            appetizer = JOptionPane.showInputDialog("Enter Appetizer:");
            dessert = JOptionPane.showInputDialog("Enter Dessert:");
            drinks = JOptionPane.showInputDialog("Enter Drinks:");

            // Validate custom input
            if (mainCourse == null || mainCourse.isEmpty() ||
                appetizer == null || appetizer.isEmpty() ||
                dessert == null || dessert.isEmpty() ||
                drinks == null || drinks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide all custom menu details");
                return false;
            }
        } else {
            // Retrieve package details from the Menu class for predefined packages
            Map<String, String> packageDetails = menu.getPackageDetails(packageType);

            // Check if package details are valid
            if (packageDetails == null) {
                JOptionPane.showMessageDialog(this, "Invalid package type.");
                return false;
            }

            // Get details from the package
            mainCourse = packageDetails.get("Main Course");
            appetizer = packageDetails.get("Appetizer");
            dessert = packageDetails.get("Dessert");
            drinks = packageDetails.get("Drinks");
        }

        // Verify that all required fields are filled
        if (service == null || service.isEmpty() ||
            packageType == null || packageType.isEmpty() ||
            mealType == null || mealType.isEmpty() || dietary == null || dietary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input all fields");
            return false;
        }

        // Prepare SQL query
        String sql = "INSERT INTO menutable(ClientID, serviceType, packageType, mealType, dietaryRestriction, mainCourse, appetizer, dessert, drinks) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Prepare statement
        pst = con.prepareStatement(sql);
        pst.setInt(1, currentClientID);
        pst.setString(2, service);
        pst.setString(3, packageType);
        pst.setString(4, mealType);
        pst.setString(5, dietary);
        pst.setString(6, mainCourse);
        pst.setString(7, appetizer);
        pst.setString(8, dessert);
        pst.setString(9, drinks);

        // Execute update
        int k = pst.executeUpdate();
        
        return k == 1;
        
    } catch (SQLException ex) {
        Logger.getLogger(Cateringv1.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
    }
        return false;
}

public void ordertInsert(String motif, String table, String tableSeat, String cloth, String backDrop, String chair, String items, String Suggestion) {
    try { 
           if (motif == null || motif.isEmpty() ||
              table == null || table.isEmpty() ||
              tableSeat == null || tableSeat.isEmpty() ||
              cloth == null || cloth.isEmpty() || 
              backDrop == null || backDrop.isEmpty() ||
              chair == null || chair.isEmpty() ||
              items == null || items.isEmpty() ||
              Suggestion == null || Suggestion.isEmpty()) {
    
            JOptionPane.showMessageDialog(this, "Please input all fields");
            return;
            
            
        }
            pst = con.prepareStatement("INSERT INTO orderstable(ClientID, theme, tables, tableSeater, tableCloth, backdrop, chairs, items, suggestion) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pst.setInt(1, currentClientID); // Assuming you want to associate it with the current client
            pst.setString(2, motif);
            pst.setString(3, table);
            pst.setString(4, tableSeat);
            pst.setString(5, cloth);
            pst.setString(6, backDrop);
            pst.setString(7, chair);
            pst.setString(8, items);
            pst.setString(9, Suggestion);
           
            int k = pst.executeUpdate();
           
            if(k==1){
                JOptionPane.showMessageDialog(this, "Record Successfully Added!");
               // tabbedPane1.setSelectedIndex(3);
            }
            else{
                JOptionPane.showMessageDialog(this, "Record Failed to Save");
            }
           
        } catch (SQLException ex) {
            Logger.getLogger(Cateringv1.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
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

        tabbedPane1 = new javax.swing.JTabbedPane();
        LandingPage = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        createReservation = new necesario.MaterialButton();
        rSPanelImage7 = new rojerusan.RSPanelImage();
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
        showPackages = new necesario.MaterialButton();
        proceedOrder = new necesario.MaterialButton();
        clearMenu = new necesario.MaterialButton();
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
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea1 = new javax.swing.JTextArea();
        confirmOrder = new necesario.MaterialButton();
        receipt = new necesario.MaterialButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        printReceipt = new necesario.MaterialButton();
        payment = new necesario.MaterialButton();
        rSMaterialButtonRectangle1 = new rojerusan.RSMaterialButtonRectangle();
        jLabel30 = new javax.swing.JLabel();
        Payment = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        rSPanelImage5 = new rojerusan.RSPanelImage();
        rSPanelImage1 = new rojerusan.RSPanelImage();
        rSMaterialButtonRectangle8 = new rojerusan.RSMaterialButtonRectangle();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        rSButtonHover1 = new rojerusan.RSButtonHover();
        rSMaterialButtonRectangle9 = new rojerusan.RSMaterialButtonRectangle();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Evelyn Catering Services");
        setMinimumSize(new java.awt.Dimension(810, 815));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        LandingPage.setBackground(new java.awt.Color(119, 82, 254));
        LandingPage.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Dubai", 1, 39)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Evelyn's Catering Services");
        LandingPage.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 520, 442, 77));

        jLabel12.setFont(new java.awt.Font("Dubai", 0, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(194, 217, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Welcome to our catering app! We’re here to make your event deliciously memorable.");
        jLabel12.setFocusable(false);
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel12.setName(""); // NOI18N
        LandingPage.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 610, 700, 77));

        jLabel13.setFont(new java.awt.Font("Dubai", 0, 20)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(194, 217, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Let’s get started on your perfect menu!");
        jLabel13.setFocusable(false);
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel13.setName(""); // NOI18N
        LandingPage.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 650, 670, 77));

        createReservation.setBackground(new java.awt.Color(142, 143, 250));
        createReservation.setBorder(new javax.swing.border.MatteBorder(null));
        createReservation.setForeground(new java.awt.Color(255, 255, 255));
        createReservation.setText("Create Reservation");
        createReservation.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        createReservation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createReservationActionPerformed(evt);
            }
        });
        LandingPage.add(createReservation, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 740, 220, -1));

        rSPanelImage7.setImagen(new javax.swing.ImageIcon(getClass().getResource("/Black Gold Elegant Catering Logo (2).png"))); // NOI18N
        LandingPage.add(rSPanelImage7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, -10, 620, 670));

        tabbedPane1.addTab("landingPage", LandingPage);

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
        Bg.add(tfName, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, 596, 73));

        tfAddress.setColumns(50);
        tfAddress.setFont(new java.awt.Font("Dubai", 0, 30)); // NOI18N
        tfAddress.setForeground(new java.awt.Color(25, 4, 130));
        tfAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfAddress.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        Bg.add(tfAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 360, 596, 73));

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
        Bg.add(tfPhoneNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 490, 596, 73));

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
        Bg.add(tfEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 620, 596, 73));

        jLabel9.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(194, 217, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Full Name :");
        Bg.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 180, -1, 50));

        jLabel2.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Evenlyn's Catering Services");
        Bg.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 30, 442, 77));

        jLabel3.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(194, 217, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Contact Information");
        Bg.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 130, -1, 50));

        jLabel4.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(194, 217, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Address :");
        Bg.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 320, -1, 38));

        jLabel5.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(194, 217, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Email : ");
        Bg.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 570, 191, 50));

        jLabel6.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(194, 217, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Phone Number :");
        Bg.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 450, 283, 38));

        clearInfo.setBackground(new java.awt.Color(142, 143, 250));
        clearInfo.setForeground(new java.awt.Color(255, 255, 255));
        clearInfo.setText("Clear Input");
        clearInfo.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        clearInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearInfoActionPerformed(evt);
            }
        });
        Bg.add(clearInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 760, -1, -1));

        proceedEvent.setBackground(new java.awt.Color(142, 143, 250));
        proceedEvent.setForeground(new java.awt.Color(255, 255, 255));
        proceedEvent.setText("Proceed to Event Details");
        proceedEvent.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        proceedEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedEventActionPerformed(evt);
            }
        });
        Bg.add(proceedEvent, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 760, 240, -1));

        rSMaterialButtonRectangle7.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle7.setEnabled(false);
        Bg.add(rSMaterialButtonRectangle7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 710, 620));

        tabbedPane1.addTab("signIn", Bg);

        Bg2.setBackground(new java.awt.Color(119, 82, 254));
        Bg2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setBackground(new java.awt.Color(0, 0, 0));
        jLabel7.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(194, 217, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Guest Count :");
        Bg2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 660, 150, 80));

        jLabel8.setBackground(new java.awt.Color(0, 0, 0));
        jLabel8.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Event Details");
        Bg2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 220, 110));

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
        Bg2.add(tfeventType, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 530, 220, 40));

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
        Bg2.add(tfDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 360, 260, 40));

        jLabel15.setBackground(new java.awt.Color(0, 0, 0));
        jLabel15.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(194, 217, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Event Type : ");
        Bg2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 470, 150, 77));

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
        Bg2.add(tfTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 410, 260, 30));

        tfCount.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tfCount.setForeground(new java.awt.Color(25, 4, 130));
        tfCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfCount.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tfCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfCountActionPerformed(evt);
            }
        });
        Bg2.add(tfCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 680, 110, 40));

        EventType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        EventType.setForeground(new java.awt.Color(25, 4, 130));
        EventType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Wedding Reception", "Birthday Party", "Baptism", "Debut", "Family Gathering" }));
        EventType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        EventType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EventTypeActionPerformed(evt);
            }
        });
        Bg2.add(EventType, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 530, 210, 40));

        jLabel14.setBackground(new java.awt.Color(0, 0, 0));
        jLabel14.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(194, 217, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Event Date and Time ");
        Bg2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, 210, 40));

        jLabel17.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(194, 217, 255));
        jLabel17.setText("Type of Venue :");
        Bg2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 680, -1, -1));

        EventVenue.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        EventVenue.setForeground(new java.awt.Color(25, 4, 130));
        EventVenue.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Indoor", "Outdoor" }));
        EventVenue.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        EventVenue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EventVenueActionPerformed(evt);
            }
        });
        Bg2.add(EventVenue, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 680, 180, 40));

        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(194, 217, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Event Venue : ");
        Bg2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 570, 150, 60));

        placeType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        placeType.setForeground(new java.awt.Color(25, 4, 130));
        placeType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Malabanan Resort", "Eastern Star Resort", "Victoria Resort", "MyPlace Resort", "Aurora Resort" }));
        placeType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        placeType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeTypeActionPerformed(evt);
            }
        });
        Bg2.add(placeType, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 620, 210, 40));

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
        Bg2.add(tfPlace, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 620, 220, 40));

        placeCustom.setBackground(new java.awt.Color(142, 143, 250));
        placeCustom.setForeground(new java.awt.Color(255, 255, 255));
        placeCustom.setText("Add Custom Venue");
        placeCustom.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        placeCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeCustomActionPerformed(evt);
            }
        });
        Bg2.add(placeCustom, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 620, 170, -1));

        dateChooser1.setForeground(new java.awt.Color(25, 4, 130));
        dateChooser1.setDateFormat("yyyy-MM-dd");
        dateChooser1.setTextRefernce(tfDate);
        Bg2.add(dateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 260, 200));

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
        Bg2.add(timePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 150, -1, 290));

        eventCustom.setBackground(new java.awt.Color(142, 143, 250));
        eventCustom.setForeground(new java.awt.Color(255, 255, 255));
        eventCustom.setText("Add Custom Event");
        eventCustom.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        eventCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventCustomActionPerformed(evt);
            }
        });
        Bg2.add(eventCustom, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 530, 170, -1));

        proceedMenu.setBackground(new java.awt.Color(142, 143, 250));
        proceedMenu.setForeground(new java.awt.Color(255, 255, 255));
        proceedMenu.setText("Proceed to Menu");
        proceedMenu.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        proceedMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedMenuActionPerformed(evt);
            }
        });
        Bg2.add(proceedMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 770, -1, -1));

        clearEvent.setBackground(new java.awt.Color(142, 143, 250));
        clearEvent.setForeground(new java.awt.Color(255, 255, 255));
        clearEvent.setText("Clear Input");
        clearEvent.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        clearEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearEventActionPerformed(evt);
            }
        });
        Bg2.add(clearEvent, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 770, -1, -1));

        rSMaterialButtonRectangle5.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle5.setEnabled(false);
        rSMaterialButtonRectangle5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonRectangle5ActionPerformed(evt);
            }
        });
        Bg2.add(rSMaterialButtonRectangle5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 680, 390));

        rSMaterialButtonRectangle6.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle6.setEnabled(false);
        rSMaterialButtonRectangle6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonRectangle6ActionPerformed(evt);
            }
        });
        Bg2.add(rSMaterialButtonRectangle6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 480, 680, 260));

        rSPanelImage2.setImagen(new javax.swing.ImageIcon(getClass().getResource("/event.png"))); // NOI18N
        Bg2.add(rSPanelImage2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 40, 40));

        tabbedPane1.addTab("event", Bg2);

        Bg3.setBackground(new java.awt.Color(119, 82, 254));
        Bg3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(0, 0, 0));
        jLabel18.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Menu Details");
        Bg3.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 210, 80));

        packageType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        packageType.setForeground(new java.awt.Color(25, 4, 130));
        packageType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Package 1", "Package 2", "Package 3", "Custom Package" }));
        packageType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        packageType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packageTypeActionPerformed(evt);
            }
        });
        Bg3.add(packageType, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 330, 600, 65));

        jLabel19.setBackground(new java.awt.Color(0, 0, 0));
        jLabel19.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(194, 217, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("You can select from our preset packages or have your menu fully customized!");
        Bg3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 270, 590, 77));

        jLabel20.setBackground(new java.awt.Color(0, 0, 0));
        jLabel20.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(194, 217, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Dietary Restriction : ");
        Bg3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 590, 220, 77));

        serviceType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        serviceType.setForeground(new java.awt.Color(25, 4, 130));
        serviceType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Buffet", "Ala Carte", "Pass Around" }));
        serviceType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        serviceType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceTypeActionPerformed(evt);
            }
        });
        Bg3.add(serviceType, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, 450, 65));

        jLabel22.setBackground(new java.awt.Color(0, 0, 0));
        jLabel22.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(194, 217, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Service Type : ");
        Bg3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 150, 60));

        mealType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        mealType.setForeground(new java.awt.Color(25, 4, 130));
        mealType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Breakfast", "Lunch", "Dinner", "Dessert" }));
        mealType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        mealType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mealTypeActionPerformed(evt);
            }
        });
        Bg3.add(mealType, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 510, 460, 65));

        jLabel23.setBackground(new java.awt.Color(0, 0, 0));
        jLabel23.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(194, 217, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Meal Type : ");
        Bg3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 500, 150, 77));

        dietaryRestriction.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        dietaryRestriction.setForeground(new java.awt.Color(25, 4, 130));
        dietaryRestriction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Gluten Free", "Nut Allergy", "Shrimp" }));
        dietaryRestriction.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        dietaryRestriction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dietaryRestrictionActionPerformed(evt);
            }
        });
        Bg3.add(dietaryRestriction, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 600, 390, 65));

        jLabel24.setBackground(new java.awt.Color(0, 0, 0));
        jLabel24.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(194, 217, 255));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Food Beverages :");
        Bg3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 442, 77));

        showPackages.setBackground(new java.awt.Color(142, 143, 250));
        showPackages.setForeground(new java.awt.Color(255, 255, 255));
        showPackages.setText("Show Packages");
        showPackages.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        showPackages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPackagesActionPerformed(evt);
            }
        });
        Bg3.add(showPackages, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 410, -1, 30));

        proceedOrder.setBackground(new java.awt.Color(142, 143, 250));
        proceedOrder.setForeground(new java.awt.Color(255, 255, 255));
        proceedOrder.setText("Proceed to Order");
        proceedOrder.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        proceedOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedOrderActionPerformed(evt);
            }
        });
        Bg3.add(proceedOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 730, -1, -1));

        clearMenu.setBackground(new java.awt.Color(142, 143, 250));
        clearMenu.setForeground(new java.awt.Color(255, 255, 255));
        clearMenu.setText("Clear Input     ");
        clearMenu.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        clearMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMenuActionPerformed(evt);
            }
        });
        Bg3.add(clearMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 730, -1, -1));

        rSMaterialButtonRectangle2.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle2.setEnabled(false);
        rSMaterialButtonRectangle2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonRectangle2ActionPerformed(evt);
            }
        });
        Bg3.add(rSMaterialButtonRectangle2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 480, 740, 220));

        rSMaterialButtonRectangle3.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle3.setEnabled(false);
        Bg3.add(rSMaterialButtonRectangle3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 740, 120));

        rSMaterialButtonRectangle4.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle4.setEnabled(false);
        Bg3.add(rSMaterialButtonRectangle4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 740, 220));

        rSPanelImage3.setImagen(new javax.swing.ImageIcon(getClass().getResource("/menu.png"))); // NOI18N
        Bg3.add(rSPanelImage3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 40, 40));

        tabbedPane1.addTab("menu", Bg3);

        Bg4.setBackground(new java.awt.Color(119, 82, 254));
        Bg4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rSPanelImage4.setImagen(new javax.swing.ImageIcon(getClass().getResource("/item.png"))); // NOI18N
        Bg4.add(rSPanelImage4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 40, 40));

        tableSeater.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tableSeater.setForeground(new java.awt.Color(25, 4, 130));
        tableSeater.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "4 Seater", "8 Seater", "10 Seater", "12 Seater" }));
        tableSeater.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tableSeater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableSeaterActionPerformed(evt);
            }
        });
        Bg4.add(tableSeater, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 240, 170, 60));

        suggestion.setColumns(20);
        suggestion.setForeground(new java.awt.Color(25, 4, 130));
        suggestion.setRows(5);
        suggestion.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        jScrollPane1.setViewportView(suggestion);

        Bg4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 630, 400, 70));

        jLabel25.setBackground(new java.awt.Color(0, 0, 0));
        jLabel25.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Set-up and Items needed");
        Bg4.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 390, 80));

        jLabel26.setBackground(new java.awt.Color(0, 0, 0));
        jLabel26.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(194, 217, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Backdrop");
        Bg4.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 300, 220, 77));

        jLabel27.setBackground(new java.awt.Color(0, 0, 0));
        jLabel27.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(194, 217, 255));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("If there’s no suggestion, write \"None.\"");
        Bg4.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 680, 290, 77));

        backdrop.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        backdrop.setForeground(new java.awt.Color(25, 4, 130));
        backdrop.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "White", "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet", "Pink", "Black" }));
        backdrop.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        backdrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backdropActionPerformed(evt);
            }
        });
        Bg4.add(backdrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 360, 170, 60));

        jLabel28.setBackground(new java.awt.Color(0, 0, 0));
        jLabel28.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(194, 217, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("Tables and Chairs :");
        Bg4.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 220, 50));

        jLabel29.setBackground(new java.awt.Color(0, 0, 0));
        jLabel29.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(194, 217, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("Tables");
        Bg4.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 220, -1));

        tableType.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tableType.setForeground(new java.awt.Color(25, 4, 130));
        tableType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Round", "Square", "Rectangular" }));
        tableType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tableType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableTypeActionPerformed(evt);
            }
        });
        Bg4.add(tableType, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, 170, 60));

        items.setBackground(new java.awt.Color(0, 0, 0));
        items.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        items.setForeground(new java.awt.Color(194, 217, 255));
        items.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        items.setText("Add More :");
        Bg4.add(items, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 430, 200, 40));

        jLabel31.setBackground(new java.awt.Color(0, 0, 0));
        jLabel31.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(194, 217, 255));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Table Cloth");
        Bg4.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, 120, 77));

        tableCloth.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tableCloth.setForeground(new java.awt.Color(25, 4, 130));
        tableCloth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "White", "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet", "Pink", "Black" }));
        tableCloth.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        tableCloth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableClothActionPerformed(evt);
            }
        });
        Bg4.add(tableCloth, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 360, 170, 60));

        jLabel32.setBackground(new java.awt.Color(0, 0, 0));
        jLabel32.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(194, 217, 255));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Chairs");
        Bg4.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 430, 70, 40));

        chairs.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        chairs.setForeground(new java.awt.Color(25, 4, 130));
        chairs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiffany Chairs", "Monoblock Chairs" }));
        chairs.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        chairs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chairsActionPerformed(evt);
            }
        });
        Bg4.add(chairs, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 470, 170, 60));

        cups.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        cups.setForeground(new java.awt.Color(194, 217, 255));
        cups.setText("Cups");
        cups.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cupsActionPerformed(evt);
            }
        });
        Bg4.add(cups, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 490, -1, -1));

        napkins.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        napkins.setForeground(new java.awt.Color(194, 217, 255));
        napkins.setText("Napkins");
        napkins.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        Bg4.add(napkins, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 460, -1, -1));

        utensils.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        utensils.setForeground(new java.awt.Color(194, 217, 255));
        utensils.setText("Utensils");
        utensils.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        utensils.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                utensilsActionPerformed(evt);
            }
        });
        Bg4.add(utensils, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 490, -1, -1));

        plates.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        plates.setForeground(new java.awt.Color(194, 217, 255));
        plates.setText("Plates");
        plates.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        plates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                platesActionPerformed(evt);
            }
        });
        Bg4.add(plates, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 460, -1, -1));

        jLabel33.setBackground(new java.awt.Color(0, 0, 0));
        jLabel33.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(194, 217, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("Theme or Motif :");
        Bg4.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 170, 77));

        tfMotif.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        tfMotif.setForeground(new java.awt.Color(25, 4, 130));
        tfMotif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfMotif.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tfMotif.setCaretColor(new java.awt.Color(25, 4, 130));
        Bg4.add(tfMotif, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, 240, 50));

        textArea1.setEditable(false);
        textArea1.setBackground(new java.awt.Color(255, 255, 255));
        textArea1.setColumns(20);
        textArea1.setFont(new java.awt.Font("Dubai", 0, 13)); // NOI18N
        textArea1.setForeground(new java.awt.Color(25, 4, 130));
        textArea1.setRows(5);
        textArea1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(25, 4, 130), 1, true));
        textArea1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        textArea1.setEnabled(false);
        jScrollPane2.setViewportView(textArea1);

        Bg4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 80, 270, 610));

        confirmOrder.setBackground(new java.awt.Color(142, 143, 250));
        confirmOrder.setForeground(new java.awt.Color(255, 255, 255));
        confirmOrder.setText("Confirm Order");
        confirmOrder.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        confirmOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmOrderActionPerformed(evt);
            }
        });
        Bg4.add(confirmOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 750, 190, -1));

        receipt.setBackground(new java.awt.Color(142, 143, 250));
        receipt.setForeground(new java.awt.Color(255, 255, 255));
        receipt.setText("Generate Order Receipt");
        receipt.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        receipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiptActionPerformed(evt);
            }
        });
        Bg4.add(receipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 750, 220, -1));

        jPanel1.setBackground(new java.awt.Color(25, 4, 130));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setBackground(new java.awt.Color(0, 0, 0));
        jLabel21.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Order Details :");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 250, 77));

        printReceipt.setBackground(new java.awt.Color(142, 143, 250));
        printReceipt.setForeground(new java.awt.Color(255, 255, 255));
        printReceipt.setText("Print Receipt");
        printReceipt.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        printReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printReceiptActionPerformed(evt);
            }
        });
        jPanel1.add(printReceipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 710, 270, -1));

        payment.setBackground(new java.awt.Color(142, 143, 250));
        payment.setForeground(new java.awt.Color(255, 255, 255));
        payment.setText("PROCEED TO PAYMENT");
        payment.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        payment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentActionPerformed(evt);
            }
        });
        jPanel1.add(payment, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 760, 270, 40));

        Bg4.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 0, 330, 850));

        rSMaterialButtonRectangle1.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle1.setEnabled(false);
        Bg4.add(rSMaterialButtonRectangle1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 460, 410));

        jLabel30.setBackground(new java.awt.Color(0, 0, 0));
        jLabel30.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(194, 217, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Suggestion or Request :");
        Bg4.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 560, 290, 77));

        tabbedPane1.addTab("Order", Bg4);

        Payment.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(119, 82, 254));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel35.setBackground(new java.awt.Color(0, 0, 0));
        jLabel35.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(194, 217, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Paymaya");
        jPanel3.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 140, 170, 77));

        jLabel34.setBackground(new java.awt.Color(0, 0, 0));
        jLabel34.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("Payment Method");
        jPanel3.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 280, 80));

        jLabel36.setBackground(new java.awt.Color(0, 0, 0));
        jLabel36.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(194, 217, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel36.setText("Gcash");
        jPanel3.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 130, 170, 100));

        rSPanelImage5.setImagen(new javax.swing.ImageIcon(getClass().getResource("/8.png"))); // NOI18N
        jPanel3.add(rSPanelImage5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 410, 400));

        rSPanelImage1.setImagen(new javax.swing.ImageIcon(getClass().getResource("/7.png"))); // NOI18N
        rSPanelImage1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(rSPanelImage1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 200, 390, 400));

        rSMaterialButtonRectangle8.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle8.setEnabled(false);
        jPanel3.add(rSMaterialButtonRectangle8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 730, 490));

        jLabel38.setBackground(new java.awt.Color(0, 0, 0));
        jLabel38.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(194, 217, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel38.setText("Make a partial payment of 1,500 Pesos fo you reservation fee!");
        jPanel3.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 620, 77));

        jLabel39.setBackground(new java.awt.Color(0, 0, 0));
        jLabel39.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(194, 217, 255));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel39.setText("Contact Information :  09978378016 Globe / 09366272916 TM");
        jPanel3.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 690, 660, 77));

        jLabel40.setBackground(new java.awt.Color(0, 0, 0));
        jLabel40.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(194, 217, 255));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("The staff will contact you for further details!");
        jPanel3.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 600, 440, 77));

        jLabel41.setBackground(new java.awt.Color(0, 0, 0));
        jLabel41.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("Evelyn's Catering Services ");
        jPanel3.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 660, 440, 77));

        rSButtonHover1.setBackground(new java.awt.Color(142, 143, 250));
        rSButtonHover1.setText("THANK YOU!!");
        rSButtonHover1.setColorHover(new java.awt.Color(25, 4, 130));
        rSButtonHover1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSButtonHover1ActionPerformed(evt);
            }
        });
        jPanel3.add(rSButtonHover1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 770, 240, -1));

        rSMaterialButtonRectangle9.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle9.setEnabled(false);
        jPanel3.add(rSMaterialButtonRectangle9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 730, 90));

        Payment.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 810, 850));

        tabbedPane1.addTab("Payment", Payment);

        getContentPane().add(tabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -40, 810, 880));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tfEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfEmailActionPerformed

    private void tfPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPhoneNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPhoneNumberActionPerformed

    private void tfNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNameActionPerformed

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

    private void timePicker1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_timePicker1PropertyChange
        String timeSelected = timePicker1.getSelectedTime();
        tfTime.setText(timeSelected);                             
    }//GEN-LAST:event_timePicker1PropertyChange

    private void timePicker1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_timePicker1MouseClicked
     
    }//GEN-LAST:event_timePicker1MouseClicked

    private void timePicker1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_timePicker1MouseExited
        String timeSelected = timePicker1.getSelectedTime();
        tfTime.setText(timeSelected);
    }//GEN-LAST:event_timePicker1MouseExited

    private void createReservationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createReservationActionPerformed
        tabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_createReservationActionPerformed

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
        
        boolean success = insert(fullname, address, phone, email);
        if (success) {
            System.out.println("Success!");
        }
    }//GEN-LAST:event_proceedEventActionPerformed

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

    private void proceedMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedMenuActionPerformed
        SelectedDate date = dateChooser1.getSelectedDate();
        String dateSelected = tfDate.getSelectedText();
        
        String time = timePicker1.getSelectedTime();
        tfTime.setText(time);
        
        
        // Gather event details from text fields
        String formattedTime = null;
        String dateInput = tfDate.getText().trim();
        String timeInput = tfTime.getText().trim();
        String eventType = (String) EventType.getSelectedItem(); // Assuming EventType is a JComboBox
        String guestInput = tfCount.getText().trim();
        String eventVenue = (String) EventVenue.getSelectedItem(); // Assuming EventVenue is a JComboBox
        String PlaceType = (String) placeType.getSelectedItem(); // Assuming placeType is a JComboBox
        boolean isSuccess = insertEvent(dateInput, timeInput, eventType, guestInput, eventVenue, PlaceType, formattedTime);
        if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Record Successfully Added!");
                tabbedPane1.setSelectedIndex(3);
            } else {
                JOptionPane.showMessageDialog(this, "Record Failed to Save");
            }
    }//GEN-LAST:event_proceedMenuActionPerformed

    private void showPackagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPackagesActionPerformed
        Menu showmenu = new Menu();
       showmenu.show();
    }//GEN-LAST:event_showPackagesActionPerformed

    private void proceedOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedOrderActionPerformed
        String service = (String) serviceType.getSelectedItem();
        String PackageType = (String) packageType.getSelectedItem();
        String MealType = (String) mealType.getSelectedItem();
        String dietary = (String) dietaryRestriction.getSelectedItem();
        
        boolean isSuccess = menuInsert(service, PackageType, MealType, dietary);
        if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Record Successfully Added!");
                tabbedPane1.setSelectedIndex(4);
            } else {
                JOptionPane.showMessageDialog(this, "Record Failed to Save");
            }
    }//GEN-LAST:event_proceedOrderActionPerformed

    private void clearMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMenuActionPerformed
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
    }//GEN-LAST:event_clearMenuActionPerformed

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

    // Call the ordertInsert method with the gathered parameters
    ordertInsert(motif, table, tableSeat, cloth, backDrop, chair, items, Suggestion);
    }//GEN-LAST:event_confirmOrderActionPerformed

    private void receiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiptActionPerformed

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
        "                 Order Number:   " + currentClientID + "          \n" +
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
        textArea1.setText(receipt);
    }//GEN-LAST:event_receiptActionPerformed

    private void printReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printReceiptActionPerformed
            try {
           textArea1.print();
            } catch (Exception e) {
            }   
    }//GEN-LAST:event_printReceiptActionPerformed

    private void rSMaterialButtonRectangle2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonRectangle2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonRectangle2ActionPerformed

    private void rSMaterialButtonRectangle6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonRectangle6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonRectangle6ActionPerformed

    private void eventCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventCustomActionPerformed
        eventCustom.addActionListener(e -> addEventType());
    }//GEN-LAST:event_eventCustomActionPerformed

    private void rSMaterialButtonRectangle5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonRectangle5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonRectangle5ActionPerformed

    private void placeTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeTypeActionPerformed

    private void tfPlaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPlaceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPlaceActionPerformed

    private void placeCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeCustomActionPerformed
       placeCustom.addActionListener(e -> addCustomPlace());
    }//GEN-LAST:event_placeCustomActionPerformed

    private void paymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentActionPerformed
        tabbedPane1.setSelectedIndex(5);
    }//GEN-LAST:event_paymentActionPerformed

    private void rSButtonHover1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSButtonHover1ActionPerformed
       int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Exit the application
        }
    }//GEN-LAST:event_rSButtonHover1ActionPerformed

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
            java.util.logging.Logger.getLogger(Cateringv1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cateringv1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cateringv1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cateringv1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              new Cateringv1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Bg;
    private javax.swing.JPanel Bg2;
    private javax.swing.JPanel Bg3;
    private javax.swing.JPanel Bg4;
    private javax.swing.JComboBox<String> EventType;
    private javax.swing.JComboBox<String> EventVenue;
    private javax.swing.JPanel LandingPage;
    private javax.swing.JPanel Payment;
    private javax.swing.JComboBox<String> backdrop;
    private javax.swing.JComboBox<String> chairs;
    private necesario.MaterialButton clearEvent;
    private necesario.MaterialButton clearInfo;
    private necesario.MaterialButton clearMenu;
    private necesario.MaterialButton confirmOrder;
    private necesario.MaterialButton createReservation;
    private javax.swing.JCheckBox cups;
    private com.raven.datechooser.DateChooser dateChooser1;
    private javax.swing.JComboBox<String> dietaryRestriction;
    private necesario.MaterialButton eventCustom;
    private javax.swing.JLabel items;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox<String> mealType;
    private javax.swing.JCheckBox napkins;
    private javax.swing.JComboBox<String> packageType;
    private necesario.MaterialButton payment;
    private necesario.MaterialButton placeCustom;
    private javax.swing.JComboBox<String> placeType;
    private javax.swing.JCheckBox plates;
    private necesario.MaterialButton printReceipt;
    private necesario.MaterialButton proceedEvent;
    private necesario.MaterialButton proceedMenu;
    private necesario.MaterialButton proceedOrder;
    private rojerusan.RSButtonHover rSButtonHover1;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle1;
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
    private rojerusan.RSPanelImage rSPanelImage5;
    private rojerusan.RSPanelImage rSPanelImage7;
    private necesario.MaterialButton receipt;
    private javax.swing.JComboBox<String> serviceType;
    private necesario.MaterialButton showPackages;
    private javax.swing.JTextArea suggestion;
    private javax.swing.JTabbedPane tabbedPane1;
    private javax.swing.JComboBox<String> tableCloth;
    private javax.swing.JComboBox<String> tableSeater;
    private javax.swing.JComboBox<String> tableType;
    private javax.swing.JTextArea textArea1;
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
    private javax.swing.JCheckBox utensils;
    // End of variables declaration//GEN-END:variables
}
