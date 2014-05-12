/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.airshipmanagergui;

import cz.muni.fi.pv168.airshipmanager.*;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Marek
 * @author Michal
 */
public class MyFrame extends javax.swing.JFrame {

    private DataSource dataSource;
    private AirshipManagerImpl airshipManager;
    private ContractManagerImpl contractManager;
    private Boolean update = false;
    private boolean updateC = false;
    private Long updateId;
    private Long updateIdC;

    /**
     * Creates new form MyFrame
     */
    public MyFrame() {
        initComponents();
        this.setLocationRelativeTo(null);
        new AllAirshipSwingWorker().execute();
        new AllContractsSwingWorker().execute();
    }

    /* Localization  */
    Locale en = Locale.US;
    Locale sk = Locale.forLanguageTag("sk-SK");
    Locale cz = Locale.forLanguageTag("cs-CZ");

    Locale local = Locale.getDefault();

    ResourceBundle bundle = ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui.localization");

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/settings").getString("url"));
        dataSource.setUsername(java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/settings").getString("user"));
        dataSource.setPassword(java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/settings").getString("password"));
        return dataSource;
    }

    private AllAirshipSwingWorker allAirshipSwingWorker;

    private class AllAirshipSwingWorker extends SwingWorker<List<Airship>, Void> {

        private List<Airship> airships;

        @Override
        protected List<Airship> doInBackground() throws Exception {
            dataSource = prepareDataSource();
            airshipManager = new AirshipManagerImpl();
            airshipManager.setDataSource(dataSource);
            airships = new ArrayList<>();
            airships = airshipManager.getAllAirships();
            return airships;
        }

        @Override
        protected void done() {

            AirshipsTableModel model = (AirshipsTableModel) jTable1.getModel();
            try {
                model.removeAllAirship();
                for (Airship airship : get()) {
                    model.addAirship(airship);
                }
            } catch (ExecutionException ex) {
                new AirshipManagerException("Exception thrown in doInBackground: " + ex.getCause() + "\n");
            } catch (InterruptedException ex) {
                new AirshipManagerException("Operation interrupted (this should never happen)", ex);
            }
        }
    }

    private AddAirshipSwingWorker addAirshipSwingWorker;

    private class AddAirshipSwingWorker extends SwingWorker<Airship, Void> {

        private String name;
        private int capacity;
        private BigDecimal pricePerDay;
        private Airship airship = new Airship();

        public void setName(String name) {
            this.name = name;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public void setPricePerDay(BigDecimal pricePerDay) {
            this.pricePerDay = pricePerDay;
        }

        public void setAirship(Airship airship) {
            this.airship = airship;
        }

        @Override
        protected Airship doInBackground() throws Exception {

            dataSource = prepareDataSource();
            airshipManager = new AirshipManagerImpl();
            airshipManager.setDataSource(dataSource);
            airship.setName(name);
            airship.setCapacity(capacity);
            airship.setPricePerDay(pricePerDay);
            if (update == false) {
                airshipManager.addAirship(airship);
            } else {
                airship.setId(updateId);
                airshipManager.editAirship(airship);
            }
            return airship;
        }

        @Override
        protected void done() {
            AirshipsTableModel airshipsTableModel = new AirshipsTableModel();
            try {
                if (update == false) {
                    airshipsTableModel.addAirship(get());
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(MyFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private getAirshipByIdSwingWorker getAirshipByIdSwingWorker;

    private class getAirshipByIdSwingWorker extends SwingWorker<Airship, Void> {

        private Long airshipId;
        private Airship airship;

        public void setAirshipId(Long airshipId) {
            this.airshipId = airshipId;
        }

        @Override
        protected Airship doInBackground() throws Exception {

            dataSource = prepareDataSource();
            airshipManager = new AirshipManagerImpl();
            airshipManager.setDataSource(dataSource);
            airship = airshipManager.getAirshipById(airshipId);
            return airship;
        }

        @Override
        protected void done() {
            AirshipsTableModel airshipTableModel = new AirshipsTableModel();
            try {
                AirshipsTableModel model = (AirshipsTableModel) jTable1.getModel();
                model.removeAllAirship();
                model.addAirship(get());
            } catch (ExecutionException ex) {
                new AirshipManagerException("Exception thrown in doInBackground: " + ex.getCause() + "\n");
            } catch (InterruptedException ex) {
                new AirshipManagerException("Operation interrupted (this should never happen)", ex);
            }
        }
    }

    private GetContractByIdSwingWorker getContractByIdSwingWorker;

    private class GetContractByIdSwingWorker extends SwingWorker<Contract, Void> {

        private Long contractId;
        private Contract contract;

        public void setContractId(Long customerId) {
            this.contractId = customerId;
        }

        @Override
        protected Contract doInBackground() throws Exception {

            dataSource = prepareDataSource();
            contractManager = new ContractManagerImpl();
            contractManager.setDataSource(dataSource);
            contract = contractManager.getContractById(contractId);
            return contract;
        }

        @Override
        protected void done() {
            ContractsTableModel contractTableModel = new ContractsTableModel();
            try {
                ContractsTableModel model = (ContractsTableModel) jTable2.getModel();
                model.removeAllContract();
                model.addContract(get());
            } catch (ExecutionException ex) {
                new ContractManagerException("Exception thrown in doInBackground: " + ex.getCause() + "\n");
            } catch (InterruptedException ex) {
                new ContractManagerException("Operation interrupted (this should never happen)", ex);
            }
        }
    }

    private AddContractSwingWorker addContractSwingWorker;

    private class AddContractSwingWorker extends SwingWorker<Contract, Void> {

        private Long id;
        private java.sql.Date startDate;
        private int length;
        private String nameOfClient;
        private Airship airship;
        private float discount;
        private PaymentMethod paymentMethod;
        private Contract contract;

        public void setId(Long id) {
            this.id = id;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public void setNameOfClient(String nameOfClient) {
            this.nameOfClient = nameOfClient;
        }

        public void setAirship(Airship airship) {
            this.airship = airship;
        }

        public void setDiscount(float discount) {
            this.discount = discount;
        }

        public void setPaymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public boolean isValid() {
            if (startDate == null) {
                return false;
            }
            if (airship == null) {
                return false;
            }
            if (length <= 0) {
                return false;
            }
            return true;
        }

        @Override
        protected Contract doInBackground() throws Exception {
            dataSource = prepareDataSource();
            contractManager = new ContractManagerImpl();
            contractManager.setDataSource(dataSource);
            contract.setStartDate(startDate);
            contract.setDiscount(discount);
            contract.setAirship(airship);
            contract.setPaymentMethod(paymentMethod);
            contract.setLength(length);
            contract.setNameOfClient(nameOfClient);
            
            if (update == false) {
                contractManager.addContract(contract);
            } else {
                Contract c = new Contract().setId(updateIdC);
                contractManager.editContract(c);
            }

            return contract;
        }

        @Override
        protected void done() {
            //CustomersTableModel customersTableModel = new CustomersTableModel();
            ContractsTableModel model = (ContractsTableModel) jTable2.getModel();
            try {
                model.addContract(get());
            } catch (ExecutionException ex) {
                new ContractManagerException("Exception thrown in doInBackground: " + ex.getCause() + "\n");
            } catch (InterruptedException ex) {
                new ContractManagerException("Operation interrupted (this should never happen)", ex);
            }
        }
    }

    private class AllContractsSwingWorker extends SwingWorker<List<Contract>, Void> {

        private List<Contract> contracts;

        @Override
        protected List<Contract> doInBackground() throws Exception {
            dataSource = prepareDataSource();
            contractManager = new ContractManagerImpl();
            contractManager.setDataSource(dataSource);
            contracts = new ArrayList<Contract>();
            contracts = contractManager.getAllContracts();
            return contracts;
        }

        @Override
        protected void done() {

            ContractsTableModel model = (ContractsTableModel) jTable2.getModel();
            try {
                model.removeAllContract();
                for (Contract contract : get()) {
                    model.addContract(contract);
                }
            } catch (ExecutionException ex) {
                new ContractManagerException("Exception thrown in doInBackground: " + ex.getCause() + "\n");
            } catch (InterruptedException ex) {
                new ContractManagerException("Operation interrupted (this should never happen)", ex);
            }
        }
    }

    private class FreeAirshipsSwingWorker extends SwingWorker<List<Airship>, Void> {

        private List<Airship> airships;

        @Override
        protected List<Airship> doInBackground() throws Exception {
            dataSource = prepareDataSource();
            airshipManager = new AirshipManagerImpl();
            airshipManager.setDataSource(dataSource);
            airships = new ArrayList<>();
            airships = airshipManager.getAllAirships();
            return airships;
        }

        @Override
        protected void done() {

            AirshipsTableModel model = (AirshipsTableModel) jTable1.getModel();
            try {
                model.removeAllAirship();
                for (Airship airship : get()) {
                    model.addAirship(airship);
                }
            } catch (ExecutionException ex) {
                new AirshipManagerException("Exception thrown in doInBackground: " + ex.getCause() + "\n");
            } catch (InterruptedException ex) {
                new AirshipManagerException("Operation interrupted (this should never happen)", ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AirshipManagerPUEntityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("AirshipManagerPU").createEntityManager();
        updateContractFrame = new javax.swing.JFrame();
        updateContractLabel = new javax.swing.JLabel();
        contractDateLabel = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        contractDateInput1 = new javax.swing.JTextPane();
        jScrollPane11 = new javax.swing.JScrollPane();
        contractLengthInput = new javax.swing.JTextPane();
        contractLengthLabel = new javax.swing.JLabel();
        contractClientLabel = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        contractClientInput = new javax.swing.JTextPane();
        jScrollPane12 = new javax.swing.JScrollPane();
        contractDiscountInput = new javax.swing.JTextPane();
        contractDiscountLabel = new javax.swing.JLabel();
        contractAirshipLabel = new javax.swing.JLabel();
        contractAirshipInput = new javax.swing.JComboBox();
        contractPaymentInput = new javax.swing.JComboBox();
        contractPaymentLabel = new javax.swing.JLabel();
        updateContractSaveButton = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        contractDateInput2 = new javax.swing.JTextPane();
        jScrollPane13 = new javax.swing.JScrollPane();
        contractDateInput3 = new javax.swing.JTextPane();
        updateAirshipFrame = new javax.swing.JFrame();
        updateAirshipLabel = new javax.swing.JLabel();
        airshipNameLabel = new javax.swing.JLabel();
        airshipCapacityLabel = new javax.swing.JLabel();
        airshipPriceLabel = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        airshipNameInput = new javax.swing.JTextPane();
        jScrollPane16 = new javax.swing.JScrollPane();
        airshipPriceInput = new javax.swing.JTextPane();
        jScrollPane17 = new javax.swing.JScrollPane();
        airshipCapacityInput = new javax.swing.JTextPane();
        updateAirshipSaveButton = new javax.swing.JButton();
        jTabbedPane = new javax.swing.JTabbedPane();
        airshipTab = new javax.swing.JPanel();
        airshipManagerLabel = new javax.swing.JLabel();
        addAirshiptButton = new javax.swing.JButton();
        editAirshipButton = new javax.swing.JButton();
        removeAirshipButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        contractTab = new javax.swing.JPanel();
        contractManagerLabel = new javax.swing.JLabel();
        editContractButton = new javax.swing.JButton();
        addContractButton = new javax.swing.JButton();
        removeContractButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        updateContractFrame.setAlwaysOnTop(true);
        updateContractFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        updateContractFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                updateContractFrameWindowOpened(evt);
            }
        });

        updateContractLabel.setText("Add / Edit Contract");

        contractDateLabel.setText("Start Date");

        jScrollPane9.setViewportView(contractDateInput1);

        jScrollPane11.setViewportView(contractLengthInput);

        contractLengthLabel.setText("Length");

        contractClientLabel.setText("Client Name");

        jScrollPane8.setViewportView(contractClientInput);

        jScrollPane12.setViewportView(contractDiscountInput);

        contractDiscountLabel.setText("Discount");

        contractAirshipLabel.setText("Airship");

        contractAirshipInput.setModel(new javax.swing.DefaultComboBoxModel(new String[0]));

        contractPaymentInput.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        contractPaymentInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contractPaymentInputActionPerformed(evt);
            }
        });

        contractPaymentLabel.setText("Payment method");

        updateContractSaveButton.setText("Save");
        updateContractSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateContractSaveButtonActionPerformed(evt);
            }
        });

        jScrollPane10.setViewportView(contractDateInput2);

        jScrollPane13.setViewportView(contractDateInput3);

        javax.swing.GroupLayout updateContractFrameLayout = new javax.swing.GroupLayout(updateContractFrame.getContentPane());
        updateContractFrame.getContentPane().setLayout(updateContractFrameLayout);
        updateContractFrameLayout.setHorizontalGroup(
            updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateContractFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(updateContractFrameLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(updateContractSaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(updateContractFrameLayout.createSequentialGroup()
                        .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contractAirshipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(contractPaymentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contractAirshipInput, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(contractPaymentInput, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(updateContractFrameLayout.createSequentialGroup()
                        .addComponent(contractDiscountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(contractDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateContractLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, updateContractFrameLayout.createSequentialGroup()
                            .addComponent(contractClientLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane8))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, updateContractFrameLayout.createSequentialGroup()
                            .addComponent(contractLengthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(updateContractFrameLayout.createSequentialGroup()
                                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        updateContractFrameLayout.setVerticalGroup(
            updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateContractFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateContractLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contractDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contractLengthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(updateContractFrameLayout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contractClientLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contractDiscountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contractAirshipInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contractAirshipLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updateContractFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contractPaymentInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contractPaymentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(updateContractSaveButton)
                .addGap(31, 31, 31))
        );

        /*
        if(contractManager == null){
            for(Contract c: contractManager.getAllContracts()){
                contractAirshipInput.addItem(c.getAirship().getName());
            }
        }
        */
        for(PaymentMethod p: PaymentMethod.values()){
            contractPaymentInput.addItem(p);
        }

        updateAirshipFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        updateAirshipLabel.setText("Add / Edit Airship");

        airshipNameLabel.setText("Name");

        airshipCapacityLabel.setText("Capacity");

        airshipPriceLabel.setText("Price");

        jScrollPane15.setViewportView(airshipNameInput);

        jScrollPane16.setViewportView(airshipPriceInput);

        jScrollPane17.setViewportView(airshipCapacityInput);

        updateAirshipSaveButton.setText("Save");
        updateAirshipSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateAirshipSaveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout updateAirshipFrameLayout = new javax.swing.GroupLayout(updateAirshipFrame.getContentPane());
        updateAirshipFrame.getContentPane().setLayout(updateAirshipFrameLayout);
        updateAirshipFrameLayout.setHorizontalGroup(
            updateAirshipFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateAirshipFrameLayout.createSequentialGroup()
                .addGroup(updateAirshipFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(updateAirshipFrameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(updateAirshipFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(updateAirshipFrameLayout.createSequentialGroup()
                                .addComponent(airshipPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(updateAirshipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(updateAirshipFrameLayout.createSequentialGroup()
                                .addComponent(airshipNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(updateAirshipFrameLayout.createSequentialGroup()
                                .addComponent(airshipCapacityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(updateAirshipFrameLayout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(updateAirshipSaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        updateAirshipFrameLayout.setVerticalGroup(
            updateAirshipFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateAirshipFrameLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(updateAirshipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updateAirshipFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(airshipNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updateAirshipFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(airshipCapacityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updateAirshipFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(airshipPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(updateAirshipSaveButton)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        airshipManagerLabel.setText(bundle.getString("airship_manager"));

        addAirshiptButton.setText(bundle.getString("add_airship"));
        addAirshiptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAirshiptButtonActionPerformed(evt);
            }
        });

        editAirshipButton.setText(bundle.getString("edit_airship"));
        editAirshipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editAirshipButtonActionPerformed(evt);
            }
        });

        removeAirshipButton.setText(bundle.getString("remove_airship"));
        removeAirshipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAirshipButtonActionPerformed(evt);
            }
        });

        jTable1.setModel(new AirshipsTableModel());
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout airshipTabLayout = new javax.swing.GroupLayout(airshipTab);
        airshipTab.setLayout(airshipTabLayout);
        airshipTabLayout.setHorizontalGroup(
            airshipTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(airshipTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(airshipTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(airshipManagerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(airshipTabLayout.createSequentialGroup()
                        .addComponent(addAirshiptButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editAirshipButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeAirshipButton))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        airshipTabLayout.setVerticalGroup(
            airshipTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(airshipTabLayout.createSequentialGroup()
                .addComponent(airshipManagerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(airshipTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addAirshiptButton)
                    .addComponent(editAirshipButton)
                    .addComponent(removeAirshipButton))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Airship managment", airshipTab);

        contractManagerLabel.setText(bundle.getString("contract_manager"));

        editContractButton.setText(bundle.getString("edit_contract"));
        editContractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editContractButtonActionPerformed(evt);
            }
        });

        addContractButton.setText(bundle.getString("add_contract"));
        addContractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addContractButtonActionPerformed(evt);
            }
        });

        removeContractButton.setText(bundle.getString("remove_contract"));
        removeContractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeContractButtonActionPerformed(evt);
            }
        });

        jTable2.setModel(new ContractsTableModel()
        );
        jScrollPane1.setViewportView(jTable2);

        javax.swing.GroupLayout contractTabLayout = new javax.swing.GroupLayout(contractTab);
        contractTab.setLayout(contractTabLayout);
        contractTabLayout.setHorizontalGroup(
            contractTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contractTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contractTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contractManagerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(contractTabLayout.createSequentialGroup()
                        .addComponent(addContractButton)
                        .addGap(18, 18, 18)
                        .addComponent(editContractButton)
                        .addGap(18, 18, 18)
                        .addComponent(removeContractButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        contractTabLayout.setVerticalGroup(
            contractTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contractTabLayout.createSequentialGroup()
                .addComponent(contractManagerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contractTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addContractButton)
                    .addComponent(editContractButton)
                    .addComponent(removeContractButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 43, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Contract managment", contractTab);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addAirshiptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAirshiptButtonActionPerformed
        updateAirshipFrame.setSize(400, 400);
        updateAirshipFrame.setLocationRelativeTo(null);
        update = false;
        airshipNameInput.setText("");
        airshipCapacityInput.setText("");
        airshipPriceInput.setText("");
        updateAirshipFrame.setVisible(true);
    }//GEN-LAST:event_addAirshiptButtonActionPerformed

    private void updateAirshipSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateAirshipSaveButtonActionPerformed
        boolean nameB = false;
        boolean capacityB = false;
        boolean pricePerDayB = false;

        String name = airshipNameInput.getText();
        String capacity = airshipCapacityInput.getText();
        String pricePerDay = airshipPriceInput.getText();

        int capacityI = 0;
        BigDecimal pricePerDayBigDecimal = BigDecimal.ZERO;

        String msgName = java.util.ResourceBundle.getBundle(
                    "cz.muni.fi.pv168.airshipmanagergui/localization").getString("msgName");
        String msgCapacity = java.util.ResourceBundle.getBundle(
                    "cz.muni.fi.pv168.airshipmanagergui/localization").getString("msgCapacity");
        String msgPricePerDay = java.util.ResourceBundle.getBundle(
                    "cz.muni.fi.pv168.airshipmanagergui/localization").getString("msgPricePerDay");

        if (name == null || "".equals(name)) {
            JOptionPane.showMessageDialog(this, name);
            airshipNameLabel.setForeground(Color.red);
            nameB = true;
        } else {
            nameB = false;
            airshipNameLabel.setForeground(Color.GREEN);
        }
        if ("".equals(capacity) || capacity == null) {
            JOptionPane.showMessageDialog(this, msgCapacity);
            capacityB = true;
            airshipCapacityLabel.setForeground(Color.red);
        } else {
            capacityI = Integer.parseInt(capacity);
            if (capacityI < 0) {
                JOptionPane.showMessageDialog(this, msgCapacity);
                airshipCapacityLabel.setForeground(Color.red);
                capacityB = true;
            } else {
                capacityB = false;
                airshipCapacityLabel.setForeground(Color.GREEN);
            }
        }
        if (pricePerDay == null || "".equals(pricePerDay)) {
            JOptionPane.showMessageDialog(this, msgPricePerDay);
            airshipPriceInput.setForeground(Color.red);
            pricePerDayB = true;
        } else {
            pricePerDayBigDecimal = new BigDecimal(pricePerDay);
            if (pricePerDayBigDecimal.signum() != 1) {
                JOptionPane.showMessageDialog(this, msgPricePerDay);
                airshipPriceLabel.setForeground(Color.red);
                pricePerDayB = true;
            } else {
                pricePerDayB = false;
                airshipPriceLabel.setForeground(Color.GREEN);
            }
        }
        if (!(nameB || capacityB || pricePerDayB)) {
            addAirshipSwingWorker = new MyFrame.AddAirshipSwingWorker();
            addAirshipSwingWorker.setCapacity(capacityI);
            addAirshipSwingWorker.setName(name);
            addAirshipSwingWorker.setPricePerDay(pricePerDayBigDecimal);
            addAirshipSwingWorker.execute();
            updateAirshipFrame.dispose();
            airshipNameLabel.setForeground(Color.black);
            airshipPriceLabel.setForeground(Color.black);
            airshipCapacityLabel.setForeground(Color.black);
        }
        new AllAirshipSwingWorker().execute();
    }//GEN-LAST:event_updateAirshipSaveButtonActionPerformed

    private void removeAirshipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAirshipButtonActionPerformed

        int selectedRow = jTable1.getSelectedRow();
        Object idValue = jTable1.getValueAt(selectedRow, 0);
        Airship airship = null;
        try {
            airship = airshipManager.getAirshipById((Long) idValue);
        } catch (SQLException ex) {
            Logger.getLogger(MyFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean remove = false;
        if (selectedRow == -1) {
            return;
        }

        String msg = java.util.ResourceBundle.getBundle(
                    "cz.muni.fi.pv168.airshipmanagergui/localization").getString("removeAirshipMsg");
        String title = java.util.ResourceBundle.getBundle(
                    "cz.muni.fi.pv168.airshipmanagergui/localization").getString("removeAirshipTitle");
        int popUp = JOptionPane.showConfirmDialog(this, msg, title,
                    JOptionPane.YES_NO_OPTION);

        remove = true;
        for (Airship a : airshipManager.getFreeAirships()) {
            if (a.equals(airship)) {
                remove = false;
                break;
            }
        }
        if (!remove) {
            if (popUp == JOptionPane.YES_OPTION) {
                airshipManager.removeAirship(airship);
            }
        } else {
            String msg1 = java.util.ResourceBundle.getBundle(
                        "cz.muni.fi.pv168.airshipmanagergui/localization").getString("removingAirship");
            JOptionPane.showMessageDialog(this, msg1);
        }
        new AllAirshipSwingWorker().execute();
    }//GEN-LAST:event_removeAirshipButtonActionPerformed

    private void editAirshipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAirshipButtonActionPerformed
        updateAirshipFrame.setSize(400, 400);
        updateAirshipFrame.setLocationRelativeTo(null);

        int selectedRow = jTable1.getSelectedRow();
        Object idValue = jTable1.getValueAt(selectedRow, 0);
        Airship airship = null;
        try {
            airship = airshipManager.getAirshipById((Long) idValue);
        } catch (SQLException ex) {
            Logger.getLogger(MyFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateId = airship.getId();
        airshipNameInput.setText(airship.getName());
        airshipCapacityInput.setText(airship.getPricePerDay() + "");
        airshipPriceInput.setText(airship.getPricePerDay().toString());
        updateAirshipFrame.setVisible(true);
        update = true;
    }//GEN-LAST:event_editAirshipButtonActionPerformed

    private void addContractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addContractButtonActionPerformed
        updateContractFrame.setSize(400, 400);
        updateContractFrame.setLocationRelativeTo(null);
        updateContractFrame.setVisible(true);
        
        updateC = false;

    }//GEN-LAST:event_addContractButtonActionPerformed

    private void editContractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editContractButtonActionPerformed
        updateContractFrame.setSize(400, 400);
        updateContractFrame.setLocationRelativeTo(null);

        int selectedRow = jTable2.getSelectedRow();
        System.out.println("Selected row: " + selectedRow);
        Object idValue = jTable2.getValueAt(selectedRow, 0);
        Contract c = null;
        try {
            c = contractManager.getContractById((Long) idValue);
        } catch (Exception ex) {
            Logger.getLogger(MyFrame.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

        if (c.getStartDate() != null) {
            contractDateInput3.setText(Integer.toString(c.getStartDate().getDay()));
            contractDateInput2.setText(Integer.toString(c.getStartDate().getMonth()));
            contractDateInput1.setText(Integer.toString(c.getStartDate().getYear()));
        }
        contractLengthInput.setText(((Integer) c.getLength()).toString());
        contractClientInput.setText(c.getNameOfClient());
        contractDiscountInput.setText(((Float) c.getDiscount()).toString());
        updateIdC = c.getId();
        updateC = true;
        /*
         contractAirshipInput.setSelectedIndex(1);
         contractPaymentInput.setSelectedIndex(1);
         */
        updateContractFrame.setVisible(true);
    }//GEN-LAST:event_editContractButtonActionPerformed

    private void removeContractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeContractButtonActionPerformed
        int selectedRow = jTable2.getSelectedRow();
        Object idValue = jTable2.getValueAt(selectedRow, 0);
        Contract c = null;
        try {
            c = contractManager.getContractById((Long) idValue);
        } catch (Exception ex) {
            Logger.getLogger(MyFrame.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        if (selectedRow == -1) {
            return;
        }

        String msg = java.util.ResourceBundle.getBundle(
                    "cz.muni.fi.pv168.airshipmanagergui/localization").getString("removeAirshipMsg");
        String title = java.util.ResourceBundle.getBundle(
                    "cz.muni.fi.pv168.airshipmanagergui/localization").getString("removeAirshipTitle");
        int popUp = JOptionPane.showConfirmDialog(this, msg, title,
                    JOptionPane.YES_NO_OPTION);

        if (popUp == JOptionPane.YES_OPTION) {
            contractManager.removeContract(c);
        }

        new AllAirshipSwingWorker().execute();
    }//GEN-LAST:event_removeContractButtonActionPerformed

    private void updateContractSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateContractSaveButtonActionPerformed
        java.sql.Date date = null;
        Float discount = null;
        Calendar inputCal;
        /*
         Date must be well formated
         */
        try {
            inputCal = new GregorianCalendar(Integer.parseInt(contractDateInput3.getText()),
                        Integer.parseInt(contractDateInput2.getText())-1, Integer.parseInt(contractDateInput1.getText()));
            date = new Date(inputCal.getTimeInMillis());
        } catch (NumberFormatException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

            String msg1 = java.util.ResourceBundle.getBundle(
                        "cz.muni.fi.pv168.airshipmanagergui/localization").getString("wrongDate");
            JOptionPane.showMessageDialog(this, msg1);
        }
        int length = Integer.parseInt(contractLengthInput.getText());
        String clientName = contractClientInput.getText();
        try {
            discount = Float.parseFloat(contractDiscountInput.getText());
        } catch (NumberFormatException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

            String msg1 = java.util.ResourceBundle.getBundle(
                        "cz.muni.fi.pv168.airshipmanagergui/localization").getString("wrongLength");
            JOptionPane.showMessageDialog(this, msg1);
        }

        Airship selectedAirship = (Airship) contractAirshipInput.getSelectedItem();
        PaymentMethod selectedPayment = (PaymentMethod) contractPaymentInput.getSelectedItem();
        
        //Debug:
        System.out.println("Input parse:");
        System.out.println(discount);
        System.out.println(length);
        System.out.println(clientName);
        System.out.println(date);
        System.out.println(selectedAirship);
        System.out.println(selectedPayment);
        
        addContractSwingWorker = new MyFrame.AddContractSwingWorker();
        addContractSwingWorker.setDiscount(discount);
        addContractSwingWorker.setLength(length);
        addContractSwingWorker.setNameOfClient(clientName);
        addContractSwingWorker.setStartDate(date);
        addContractSwingWorker.setAirship(selectedAirship);
        addContractSwingWorker.setPaymentMethod(selectedPayment);

        if (!addContractSwingWorker.isValid()) {
            String msg2 = java.util.ResourceBundle.getBundle(
                        "cz.muni.fi.pv168.airshipmanagergui/localization").getString("wrongAddContract");
            JOptionPane.showMessageDialog(this, msg2);
            return;
        }

        addContractSwingWorker.execute();
        updateContractFrame.dispose();

        new AllContractsSwingWorker().execute();
    }//GEN-LAST:event_updateContractSaveButtonActionPerformed

    private void contractPaymentInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contractPaymentInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contractPaymentInputActionPerformed

    private void updateContractFrameWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_updateContractFrameWindowOpened
        //Airship selection load:
        for (Airship a : airshipManager.getAllAirships()) {
            contractAirshipInput.addItem(a);
        }
    }//GEN-LAST:event_updateContractFrameWindowOpened

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
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.persistence.EntityManager AirshipManagerPUEntityManager;
    private javax.swing.JButton addAirshiptButton;
    private javax.swing.JButton addContractButton;
    private javax.swing.JTextPane airshipCapacityInput;
    private javax.swing.JLabel airshipCapacityLabel;
    private javax.swing.JLabel airshipManagerLabel;
    private javax.swing.JTextPane airshipNameInput;
    private javax.swing.JLabel airshipNameLabel;
    private javax.swing.JTextPane airshipPriceInput;
    private javax.swing.JLabel airshipPriceLabel;
    private javax.swing.JPanel airshipTab;
    private javax.swing.JComboBox contractAirshipInput;
    private javax.swing.JLabel contractAirshipLabel;
    private javax.swing.JTextPane contractClientInput;
    private javax.swing.JLabel contractClientLabel;
    private javax.swing.JTextPane contractDateInput1;
    private javax.swing.JTextPane contractDateInput2;
    private javax.swing.JTextPane contractDateInput3;
    private javax.swing.JLabel contractDateLabel;
    private javax.swing.JTextPane contractDiscountInput;
    private javax.swing.JLabel contractDiscountLabel;
    private javax.swing.JTextPane contractLengthInput;
    private javax.swing.JLabel contractLengthLabel;
    private javax.swing.JLabel contractManagerLabel;
    private javax.swing.JComboBox contractPaymentInput;
    private javax.swing.JLabel contractPaymentLabel;
    private javax.swing.JPanel contractTab;
    private javax.swing.JButton editAirshipButton;
    private javax.swing.JButton editContractButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JButton removeAirshipButton;
    private javax.swing.JButton removeContractButton;
    private javax.swing.JFrame updateAirshipFrame;
    private javax.swing.JLabel updateAirshipLabel;
    private javax.swing.JButton updateAirshipSaveButton;
    private javax.swing.JFrame updateContractFrame;
    private javax.swing.JLabel updateContractLabel;
    private javax.swing.JButton updateContractSaveButton;
    // End of variables declaration//GEN-END:variables
}
