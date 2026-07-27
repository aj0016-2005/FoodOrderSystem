import javax.swing.*; 
import javax.swing.border.EmptyBorder; 
import javax.swing.border.TitledBorder; 
import java.awt.*; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.io.BufferedWriter; 
import java.io.File; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.util.HashMap; 
import java.util.Map; 
 
public class PizzaBillingSystemEnhanced extends JFrame { 
    // UI Components 
    private JList<String> pizzaList; 
    private JComboBox<String> sizeComboBox; 
    private JCheckBox[] toppingCheckBoxes; 
    private JTextArea orderSummaryArea; 
    private JLabel totalBillLabel; 
    private Map<String, Integer> selectedPizzas = new HashMap<>(); 
 
    // Pricing constants 
    private static final double[] PIZZA_PRICES = {750.0, 900.0, 600.0, 
1200.0}; 
    private static final String[] PIZZA_NAMES = { 
        "Margherita", "Pepperoni", "Veggie", "Meat Lovers" 
    }; 
    private static final double[] TOPPING_PRICES = {50.0, 30.0, 40.0, 
60.0}; 
    private static final String[] TOPPING_NAMES = { 
        "Extra Cheese", "Mushrooms", "Olives", "Pepperoni" 
    }; 
 
    public PizzaBillingSystemEnhanced() { 
        initializeUI(); 
    } 
 
    private void initializeUI() { 
        setTitle("Pizzeria Pro - Order Management"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(800, 700); 
        setLocationRelativeTo(null); 
 
        // Main panel with modern styling 
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)); 
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        mainPanel.setBackground(new Color(240, 240, 255)); 
 
        // Left panel for pizza and topping selection 
        JPanel selectionPanel = createSelectionPanel(); 
         
        // Right panel for order summary 
        JPanel summaryPanel = createSummaryPanel(); 
 
        mainPanel.add(selectionPanel, BorderLayout.WEST); 
        mainPanel.add(summaryPanel, BorderLayout.CENTER); 
 
        add(mainPanel); 
    } 
 
    private JPanel createSelectionPanel() { 
        JPanel selectionPanel = new JPanel(); 
        selectionPanel.setLayout(new BoxLayout(selectionPanel, 
BoxLayout.Y_AXIS)); 
        selectionPanel.setBackground(new Color(240, 240, 255)); 
        selectionPanel.setBorder(BorderFactory.createTitledBorder( 
            BorderFactory.createLineBorder(new Color(100, 120, 200), 2), 
            "Create Your Pizza",  
            TitledBorder.CENTER,  
            TitledBorder.TOP,  
            new Font("Arial", Font.BOLD, 16),  
            new Color(50, 70, 150) 
        )); 
 
        // Pizza Selection 
        JLabel pizzaLabel = new JLabel("Choose Pizza:"); 
        pizzaLabel.setFont(new Font("Arial", Font.BOLD, 14)); 
        pizzaList = new JList<>(createPizzaListModel()); 
        
pizzaList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
 ); 
        pizzaList.setVisibleRowCount(4); 
        JScrollPane pizzaScrollPane = new JScrollPane(pizzaList); 
        pizzaScrollPane.setPreferredSize(new Dimension(250, 120)); 
 
        // Size Selection 
        JLabel sizeLabel = new JLabel("Pizza Size:"); 
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 14)); 
        sizeComboBox = new JComboBox<>(new String[]{"Small (+₹0)", 
"Medium (+₹150)", "Large (+₹300)"}); 
 
        // Toppings Selection 
        JLabel toppingsLabel = new JLabel("Select Toppings:"); 
        toppingsLabel.setFont(new Font("Arial", Font.BOLD, 14)); 
        JPanel toppingsPanel = new JPanel(new GridLayout(0, 1)); 
        toppingsPanel.setBackground(new Color(240, 240, 255)); 
        toppingCheckBoxes = new JCheckBox[TOPPING_NAMES.length]; 
         
        for (int i = 0; i < TOPPING_NAMES.length; i++) { 
            toppingCheckBoxes[i] = new JCheckBox(TOPPING_NAMES[i] + " 
(+₹" + (int)TOPPING_PRICES[i] + ")"); 
            toppingsPanel.add(toppingCheckBoxes[i]); 
        } 
 
        // Add to Order Button 
        JButton addToOrderButton = new JButton("Add to Order"); 
        styleButton(addToOrderButton); 
        addToOrderButton.addActionListener(e -> addPizzaToOrder()); 
 
        // Arrange components 
        selectionPanel.add(pizzaLabel); 
        selectionPanel.add(pizzaScrollPane); 
        selectionPanel.add(Box.createVerticalStrut(10)); 
        selectionPanel.add(sizeLabel); 
        selectionPanel.add(sizeComboBox); 
        selectionPanel.add(Box.createVerticalStrut(10)); 
        selectionPanel.add(toppingsLabel); 
        selectionPanel.add(toppingsPanel); 
        selectionPanel.add(Box.createVerticalStrut(10)); 
        selectionPanel.add(addToOrderButton); 
 
        return selectionPanel; 
    } 
 
    private JPanel createSummaryPanel() { 
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10)); 
        summaryPanel.setBackground(new Color(240, 240, 255)); 
        summaryPanel.setBorder(BorderFactory.createTitledBorder( 
            BorderFactory.createLineBorder(new Color(100, 120, 200), 2), 
            "Order Summary",  
            TitledBorder.CENTER,  
            TitledBorder.TOP,  
            new Font("Arial", Font.BOLD, 16),  
            new Color(50, 70, 150) 
        )); 
 
        orderSummaryArea = new JTextArea(); 
        orderSummaryArea.setEditable(false); 
        orderSummaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        JScrollPane summaryScrollPane = new 
JScrollPane(orderSummaryArea); 
 
        totalBillLabel = new JLabel("Total Bill: ₹0", 
SwingConstants.CENTER); 
        totalBillLabel.setFont(new Font("Arial", Font.BOLD, 18)); 
        totalBillLabel.setForeground(new Color(0, 100, 0)); 
 
        JButton calculateBillButton = new JButton("Calculate Bill"); 
        styleButton(calculateBillButton); 
        calculateBillButton.addActionListener(e -> calculateBill()); 
 
        JButton clearOrderButton = new JButton("Clear Order"); 
        styleButton(clearOrderButton); 
        clearOrderButton.addActionListener(e -> clearOrder()); 
         
        // New Save to File button 
        JButton saveToFileButton = new JButton("Save Bill to File"); 
        styleButton(saveToFileButton); 
        saveToFileButton.setBackground(new Color(50, 150, 50)); 
        saveToFileButton.addActionListener(e -> saveBillToFile()); 
 
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0)); 
        buttonPanel.add(calculateBillButton); 
        buttonPanel.add(clearOrderButton); 
        buttonPanel.add(saveToFileButton); 
 
        summaryPanel.add(summaryScrollPane, BorderLayout.CENTER); 
        summaryPanel.add(totalBillLabel, BorderLayout.SOUTH); 
        summaryPanel.add(buttonPanel, BorderLayout.NORTH); 
 
        return summaryPanel; 
    } 
 
    private DefaultListModel<String> createPizzaListModel() { 
        DefaultListModel<String> model = new DefaultListModel<>(); 
        for (int i = 0; i < PIZZA_NAMES.length; i++) { 
            model.addElement(PIZZA_NAMES[i] + " - ₹" + 
(int)PIZZA_PRICES[i]); 
        } 
        return model; 
    } 
 
    private void styleButton(JButton button) { 
        button.setBackground(new Color(70, 130, 180)); 
        button.setForeground(Color.WHITE); 
        button.setFont(new Font("Arial", Font.BOLD, 14)); 
        button.setFocusPainted(false); 
    } 
 
    private void addPizzaToOrder() { 
        java.util.List<String> selectedPizzaItems = 
pizzaList.getSelectedValuesList(); 
        if (selectedPizzaItems.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Please select at least 
one pizza!"); 
            return; 
        } 
 
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, 
"Enter Quantity:")); 
         
        for (String pizzaItem : selectedPizzaItems) { 
            selectedPizzas.put(pizzaItem, quantity); 
        } 
 
        updateOrderSummary(); 
    } 
 
    private void updateOrderSummary() { 
        StringBuilder summary = new StringBuilder(); 
        summary.append("Current Order:\n"); 
        summary.append("--------------------\n"); 
        for (Map.Entry<String, Integer> entry : 
selectedPizzas.entrySet()) { 
            summary.append(entry.getKey()).append(" 
(x").append(entry.getValue()).append(")\n"); 
        } 
        orderSummaryArea.setText(summary.toString()); 
    } 
 
    private void calculateBill() { 
        double totalBill = 0.0; 
        StringBuilder detailedBill = new StringBuilder(); 
        detailedBill.append("Detailed Bill:\n"); 
        detailedBill.append("--------------------\n"); 
 
        for (Map.Entry<String, Integer> pizzaEntry : 
selectedPizzas.entrySet()) { 
            String pizzaName = pizzaEntry.getKey().split(" - ")[0]; 
            int quantity = pizzaEntry.getValue(); 
            double pizzaPrice = getPizzaPrice(pizzaName); 
            double sizePrice = getSizePrice(); 
            double toppingsPrice = calculateToppingsPrice(); 
 
            double subtotal = (pizzaPrice + sizePrice + toppingsPrice) * 
quantity; 
            totalBill += subtotal; 
 
            detailedBill.append(String.format("%s (x%d): ₹%.2f\n", 
pizzaName, quantity, subtotal)); 
        } 
 
        detailedBill.append("--------------------\n"); 
        detailedBill.append(String.format("Total Bill: ₹%.2f", 
totalBill)); 
 
        orderSummaryArea.setText(detailedBill.toString()); 
        totalBillLabel.setText(String.format("Total Bill: ₹%.2f", 
totalBill)); 
    } 
 
    private double getPizzaPrice(String pizzaName) { 
        for (int i = 0; i < PIZZA_NAMES.length; i++) { 
            if (PIZZA_NAMES[i].equals(pizzaName)) { 
                return PIZZA_PRICES[i]; 
            } 
        } 
        return 0; 
    } 
 
    private double getSizePrice() { 
        return new double[]{0, 150, 
300}[sizeComboBox.getSelectedIndex()]; 
    } 
 
    private double calculateToppingsPrice() { 
        double toppingsTotal = 0; 
        for (int i = 0; i < toppingCheckBoxes.length; i++) { 
            if (toppingCheckBoxes[i].isSelected()) { 
                toppingsTotal += TOPPING_PRICES[i]; 
            } 
        } 
        return toppingsTotal; 
    } 
 
    private void clearOrder() { 
        selectedPizzas.clear(); 
        orderSummaryArea.setText(""); 
        totalBillLabel.setText("Total Bill: ₹0"); 
        pizzaList.clearSelection(); 
        sizeComboBox.setSelectedIndex(0); 
        for (JCheckBox cb : toppingCheckBoxes) { 
            cb.setSelected(false); 
        } 
    } 
     
    // New method to save the bill to a text file 
    private void saveBillToFile() { 
        if (orderSummaryArea.getText().isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Please calculate the 
bill first!"); 
            return; 
        } 
         
        // Create a file chooser 
        JFileChooser fileChooser = new JFileChooser(); 
        fileChooser.setDialogTitle("Save Bill to File"); 
         
        // Set default file name with timestamp 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy
MM-dd_HH-mm-ss"); 
        String defaultFileName = "Pizza_Bill_" + 
LocalDateTime.now().format(formatter) + ".txt"; 
        fileChooser.setSelectedFile(new File(defaultFileName)); 
         
        // Show save dialog 
        int result = fileChooser.showSaveDialog(this); 
         
        if (result == JFileChooser.APPROVE_OPTION) { 
            File fileToSave = fileChooser.getSelectedFile(); 
             
            // Ensure the file has a .txt extension 
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) { 
                fileToSave = new File(fileToSave.getAbsolutePath() + 
".txt"); 
            } 
             
            try (BufferedWriter writer = new BufferedWriter(new 
FileWriter(fileToSave))) { 
                // Add header with timestamp 
                writer.write("===================================\n"); 
                writer.write("       PIZZERIA PRO - INVOICE      \n"); 
                writer.write("===================================\n"); 
                writer.write("Date: " + 
LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd 
HH:mm:ss")) + "\n\n"); 
                 
                // Write the bill content 
                writer.write(orderSummaryArea.getText()); 
                 
                // Add footer 
                
writer.write("\n\n===================================\n"); 
                writer.write("       THANK YOU FOR YOUR ORDER!    \n"); 
                writer.write("===================================\n"); 
                 
                writer.flush(); 
                JOptionPane.showMessageDialog(this, "Bill saved 
successfully to: " + fileToSave.getAbsolutePath()); 
            } catch (IOException ex) { 
                JOptionPane.showMessageDialog(this, "Error saving bill to 
file: " + ex.getMessage(),  
                                           "Error", 
JOptionPane.ERROR_MESSAGE); 
            } 
} 
} 
public static void main(String[] args) { 
SwingUtilities.invokeLater(() -> { 
new PizzaBillingSystemEnhanced().setVisible(true); 
}); 
} 
} 