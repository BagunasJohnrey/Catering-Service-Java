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
import java.sql.Timestamp;
import java.util.Arrays;

/**
 *
 * @author nicoll mitch
 */
public final class LogIn extends javax.swing.JFrame {

        String adminUsername = "admin"; // Admin username
        String adminPassword = "admin"; // Admin password
        
    /**
     * Creates new form LogIn
     */
    public LogIn() {
        initComponents();
        Connect();
    }

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
    
abstract class Page{
    abstract void switchPage(); 
}

class AdminPage extends Page{
    @Override
    public void switchPage(){
        dispose();
        Admin admin = new Admin(); // Change this to your admin panel
        admin.setVisible(true);
    }
}
    
    
 private void userLogIn() {
    try {
        String username = user.getText();
        // Use getPassword() method
        char[] passwordChars = pass.getPassword();
        String password = new String(passwordChars);
        String accessType = (String) access.getSelectedItem();
        
        // Always clear password after use
        Arrays.fill(passwordChars, '0');
        
        
        if (password.length() > 20) {
            JOptionPane.showMessageDialog(this, "Password is too long!");
            return;
        }
        
        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty() || 
            accessType == null || accessType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input all fields");
            return;
        }

        // Check for admin login
        if (accessType.equals("Admin")) {
            if (username.equals(adminUsername) && password.equals(adminPassword)) {
                // Insert admin credentials into the database
                pst = con.prepareStatement("INSERT INTO login (username, password, access, loginDate) VALUES(?, ?, ?, ?)");
                pst.setString(1, adminUsername);
                pst.setString(2, adminPassword);
                pst.setString(3, "Admin");
                
                
                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                pst.setTimestamp(4, currentTimestamp);

                int k = pst.executeUpdate();

                if (k == 1) {
                    JOptionPane.showMessageDialog(this, "Admin Record Successfully Added!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add admin record");
                }
              
                
                AdminPage admin = new AdminPage();
                admin.switchPage();
                
                
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin Credentials!");
            }
            return; // Exit the method after handling admin login
        }

        // Check for user login
        if (accessType.equals("User")) {
            pst = con.prepareStatement("SELECT * FROM login WHERE username = ? AND password = ? AND access = ?");
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, accessType);
            rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                this.setVisible(false);
                Cateringv1 cateringWindow = new Cateringv1(); // Change this to your user panel
                cateringWindow.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }
            return;
        }

    } catch (SQLException ex) {
        Logger.getLogger(Cateringv1.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
    }
}

private void userSignUp(){
    
        String username = user.getText();
        // Use getPassword() method
        char[] passwordChars = pass.getPassword();
        String password = new String(passwordChars);
        
        // Always clear password after use
        Arrays.fill(passwordChars, '0');
        
         if (password.length() > 20) {
            JOptionPane.showMessageDialog(this, "Password is too long!");
            return;
        }
        
        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input all fields");
            return;
        }
        
        if (access.getSelectedItem().equals("Admin")) {
            if (!username.equals(adminUsername) || !password.equals(adminPassword)){
                JOptionPane.showMessageDialog(this, "Only User can Register");
            }
            return; // Exit the method after handling admin login
        }
        
    try {
        // Check if username already exists
        pst = con.prepareStatement("SELECT * FROM login WHERE username = ?");
        pst.setString(1, username);
        rs = pst.executeQuery();

        if (rs.next()) {
            JOptionPane.showMessageDialog(this, "Username already exists! Please choose another.");
            return;
        }

        // Insert new user into the database
        pst = con.prepareStatement("INSERT INTO login (username, password, access) VALUES(?, ?, ?)");
        pst.setString(1, username);
        pst.setString(2, password);
        pst.setString(3, "user"); // Set access type to user

        int k = pst.executeUpdate();

        if (k == 1) {
            JOptionPane.showMessageDialog(this, "User  Registered Successfully!");
            this.setVisible(false);
                Cateringv1 cateringWindow = new Cateringv1(); // Change this to your admin panel
                cateringWindow.setVisible(true);
                
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register user.");
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

        login = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        register = new necesario.MaterialButton();
        signIn = new necesario.MaterialButton();
        checkpass = new javax.swing.JCheckBox();
        pass = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        user = new javax.swing.JTextField();
        access = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rSMaterialButtonRectangle7 = new rojerusan.RSMaterialButtonRectangle();
        rSPanelImage7 = new rojerusan.RSPanelImage();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(810, 840));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        login.setBackground(new java.awt.Color(119, 82, 254));
        login.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        login.setMinimumSize(new java.awt.Dimension(760, 783));
        login.setPreferredSize(new java.awt.Dimension(760, 783));
        login.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(194, 217, 255));
        jLabel5.setText("Username :");
        login.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 330, 175, -1));

        register.setBackground(new java.awt.Color(142, 143, 250));
        register.setForeground(new java.awt.Color(255, 255, 255));
        register.setText("REgister");
        register.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerActionPerformed(evt);
            }
        });
        login.add(register, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 650, -1, -1));

        signIn.setBackground(new java.awt.Color(142, 143, 250));
        signIn.setForeground(new java.awt.Color(255, 255, 255));
        signIn.setText("SIgn In");
        signIn.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
        signIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signInActionPerformed(evt);
            }
        });
        login.add(signIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 650, -1, -1));

        checkpass.setFont(new java.awt.Font("Dubai", 0, 12)); // NOI18N
        checkpass.setForeground(new java.awt.Color(255, 255, 255));
        checkpass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hide.png"))); // NOI18N
        checkpass.setMaximumSize(new java.awt.Dimension(30, 30));
        checkpass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkpassActionPerformed(evt);
            }
        });
        login.add(checkpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 500, 50, 70));

        pass.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        pass.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        login.add(pass, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 512, 520, 50));

        jLabel1.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(194, 217, 255));
        jLabel1.setText("Access Your Flavorful Experience!");
        login.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 300, 340, -1));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Light", 0, 70)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(194, 217, 255));
        jLabel2.setText("|");
        login.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 610, 20, -1));

        user.setFont(new java.awt.Font("Dubai", 0, 24)); // NOI18N
        user.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        user.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userActionPerformed(evt);
            }
        });
        login.add(user, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 400, 520, 49));

        access.setFont(new java.awt.Font("Dubai", 1, 18)); // NOI18N
        access.setForeground(new java.awt.Color(25, 4, 130));
        access.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "User", "Admin" }));
        access.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(25, 4, 130)));
        access.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accessActionPerformed(evt);
            }
        });
        login.add(access, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 770, 114, -1));

        jLabel4.setFont(new java.awt.Font("Dubai", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(194, 217, 255));
        jLabel4.setText("Password :");
        login.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 450, 175, -1));

        jLabel3.setFont(new java.awt.Font("Dubai", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(194, 217, 255));
        jLabel3.setText("Don’t have an account? Complete the forms above and click register!");
        login.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 590, 540, -1));

        rSMaterialButtonRectangle7.setBackground(new java.awt.Color(25, 4, 130));
        rSMaterialButtonRectangle7.setEnabled(false);
        login.add(rSMaterialButtonRectangle7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, 710, 460));

        rSPanelImage7.setImagen(new javax.swing.ImageIcon(getClass().getResource("/Black Gold Elegant Catering Logo (2).png"))); // NOI18N
        login.add(rSPanelImage7, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, -40, 410, 470));

        getContentPane().add(login, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -30, 810, 845));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userActionPerformed

    private void accessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accessActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_accessActionPerformed

    private void signInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signInActionPerformed
        userLogIn();
    }//GEN-LAST:event_signInActionPerformed

    private void registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerActionPerformed
       userSignUp();
    }//GEN-LAST:event_registerActionPerformed

    private void checkpassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkpassActionPerformed
        if (checkpass.isSelected()){
            pass.setEchoChar((char)0); // Show Password
        } else {
            pass.setEchoChar('*');  // Hide Password
        }
    }//GEN-LAST:event_checkpassActionPerformed

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
            java.util.logging.Logger.getLogger(LogIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LogIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LogIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LogIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LogIn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> access;
    private javax.swing.JCheckBox checkpass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel login;
    private javax.swing.JPasswordField pass;
    private rojerusan.RSMaterialButtonRectangle rSMaterialButtonRectangle7;
    private rojerusan.RSPanelImage rSPanelImage7;
    private necesario.MaterialButton register;
    private necesario.MaterialButton signIn;
    private javax.swing.JTextField user;
    // End of variables declaration//GEN-END:variables
}
