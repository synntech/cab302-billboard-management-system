package ControlPanel;
import Shared.Billboard;
import Shared.Message;
import Shared.BillboardToImage;
import Viewer.GenerateBillboardFromXML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static ControlPanel.CustomFont.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Flow;

public class BillboardTab{
    private JTable table;
    private ArrayList<Billboard> billboards;
    private JPanel pane;
    private Client client;
    private String token;
    private String username;
    private JLabel preview;
    private int selected;
    private JPanel information;
    private ArrayList<Integer> permissions;


    public BillboardTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client, String token, String username){
        this.client = client;
        this.username = username;
        this.token = token;
        this.pane = new JPanel();
        this.selected = -1;
        this.permissions = permissions;
        pane.setLayout(new GridBagLayout());
        setupBillboardsTable();
        updateTable();
        setupDetails();
        mainPane.addTab("Billboard", pane);
    }

    public void updateTable(){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        this.billboards = (ArrayList<Billboard>) client.sendMessage(new Message(token).requestBillboards()).getData();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (Billboard billboard : billboards) {
            model.addRow(new Object[]{
                    "<html><b>" + billboard.getName() + "</b><br>ID: " + billboard.getBillboardID() + "<br>Created by: " + billboard.getCreatorName() + "</html>"});
        }
    }

    public void setupBillboardsTable() {
        DefaultTableModel model = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Billboard");
        this.table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        table.setRowHeight(90);
        table.setTableHeader(null);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(250,0));
        pane.add(scrollPane, GUI.generateGBC(0,1,1,1,0,1,GridBagConstraints.VERTICAL, 0, GridBagConstraints.NORTHWEST));
    }
    public void setupDetails() {
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton exportButton = new JButton("Export");
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        exportButton.setEnabled(false);
        JPanel TopButtons = new JPanel(new GridLayout(1,5,5,5));
        JButton createButton = new JButton("Create");
        createButton.setEnabled(false);
        createButton.addActionListener(e -> {
            Billboard created = new BillboardOptions(username).newBillboard();
            if(created != null) {
                Message request = client.sendMessage(new Message(token).createBillboard(created));
                GUI.ServerDialogue(request.getCommunicationID(),"Create billboard successful.");
                updateTable();
                information.removeAll();
                preview.setIcon(null);
                information.add(new JLabel("Select a billboard."));
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                exportButton.setEnabled(false);
                pane.validate();
                pane.repaint();
            }
        });
        TopButtons.add(createButton);

        JButton importButton = new JButton("Import");
        importButton.setEnabled(false);
        importButton.addActionListener(e -> {
            fileSelection();
            updateTable();
            information.removeAll();
            preview.setIcon(null);
            information.add(new JLabel("Select a billboard."));
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            exportButton.setEnabled(false);
            pane.validate();
            pane.repaint();
        });
        TopButtons.add(importButton);

        if (permissions.get(0).equals(1)) {
            createButton.setEnabled(true);
            importButton.setEnabled(true);
        }

        TopButtons.add(editButton);
        TopButtons.add(deleteButton);
        TopButtons.add(exportButton);
        pane.add(TopButtons, GUI.generateGBC(0,0,2,1,0,0,0, 5, GridBagConstraints.WEST));
        this.preview = new JLabel("");
        GridBagConstraints previewGBC = GUI.generateGBC(2,0,1,2,0,0,GridBagConstraints.BOTH,0,GridBagConstraints.NORTHWEST);
        previewGBC.insets = new Insets(0,0,0,18);
        pane.add(preview, previewGBC);
        this.information = new JPanel();
        information.setLayout(new GridBagLayout());
        information.add(new JLabel("Choose a billboard."));
        pane.add(information, GUI.generateGBC(1,1,1,1,1,1,GridBagConstraints.HORIZONTAL,18,GridBagConstraints.NORTHWEST));



        ListSelectionModel rowSelected = table.getSelectionModel();
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){
                this.selected = rowSelected.getMinSelectionIndex();
                if (billboards.get(selected).getCreatorName().equals(username) || permissions.get(1).equals(1)) {
                    if ( billboards.get(selected).getScheduled() == 0 || permissions.get(1).equals(1)) {
                        editButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                    }
                    exportButton.setEnabled(true);
                }
                else {
                    editButton.setEnabled(false);
                    exportButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }

                this.preview.setIcon(new BillboardToImage(billboards.get(selected),pane.getWidth()-600,(int)((pane.getWidth()-600)/1.77)).toImageIcon()); ;
                information.removeAll();
                JLabel title = new JLabel("<html><h1>" + billboards.get(selected).getName() +"</h1><html>");
                title.setPreferredSize(new Dimension(280,30));
                information.add(title, GUI.generateGBC(0,0,1,1,1,1,0,2,GridBagConstraints.WEST));
                JLabel author = new JLabel("<html><h3> Created by: " + billboards.get(selected).getCreatorName() +"</h3><html>");
                author.setPreferredSize(new Dimension(280,20));
                information.add(author, GUI.generateGBC(0,1,1,1,1,1,0,5,GridBagConstraints.WEST));
                JTextField field = new JTextField();
                field.setText(billboards.get(selected).getImageUrl());
                field.setPreferredSize(new Dimension(280,20));
                field.setEditable(false);
                JTextField field2 = new JTextField();
                field2.setText(billboards.get(selected).getMessageText());
                field2.setPreferredSize(new Dimension(280,20));
                field2.setEditable(false);
                JTextField field3 = new JTextField();
                field3.setText(billboards.get(selected).getInformationText());
                field3.setPreferredSize(new Dimension(280,20));
                field3.setEditable(false);
                JTextField field4 = new JTextField();
                field4.setText(billboards.get(selected).getBackgroundColour());
                field4.setPreferredSize(new Dimension(280,20));
                field4.setEditable(false);
                JTextField field5 = new JTextField();
                field5.setText(billboards.get(selected).getMessageTextColour());
                field5.setPreferredSize(new Dimension(280,20));
                field5.setEditable(false);
                JTextField field6 = new JTextField();
                field6.setText(billboards.get(selected).getInformationTextColour());
                field6.setPreferredSize(new Dimension(280,20));
                field6.setEditable(false);
                information.add(new JLabel("URL/Data:"), GUI.generateGBC(0,2,1,1,1,1,0,2,GridBagConstraints.WEST));
                information.add(field, GUI.generateGBC(0,3,1,1,1,1,0,5,GridBagConstraints.WEST));
                information.add(new JLabel("Message:"), GUI.generateGBC(0,4,1,1,1,1,0,2,GridBagConstraints.WEST));
                information.add(field2, GUI.generateGBC(0,5,1,1,1,1,0,5,GridBagConstraints.WEST));
                information.add(new JLabel("Information:"),GUI.generateGBC(0,6,1,1,1,1,0,2,GridBagConstraints.WEST));
                information.add(field3, GUI.generateGBC(0,7,1,1,1,1,0,5,GridBagConstraints.WEST));
                information.add(new JLabel("Background Colour:"), GUI.generateGBC(0,8,1,1,1,1,0,2,GridBagConstraints.WEST));
                information.add(field4, GUI.generateGBC(0,9,1,1,1,1,0,5,GridBagConstraints.WEST));
                information.add(new JLabel("Message Text Colour:"), GUI.generateGBC(0,10,1,1,1,1,0,2,GridBagConstraints.WEST));
                information.add(field5, GUI.generateGBC(0,11,1,1,1,1,0,5,GridBagConstraints.WEST));
                information.add(new JLabel("Information Text Colour:"), GUI.generateGBC(0,12,1,1,1,1,0,2,GridBagConstraints.WEST));
                information.add(field6, GUI.generateGBC(0,13,1,1,1,1,0,5,GridBagConstraints.WEST));
                pane.validate();
                pane.repaint();
            }
        });

        pane.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent evt) {
                if(selected >-1) {
                    preview.setIcon(new BillboardToImage(billboards.get(selected),pane.getWidth()-600,(int)((pane.getWidth()-600)/1.77)).toImageIcon());
                    pane.validate();
                    pane.repaint();
                }
            }
        });

        editButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")){
                selected = rowSelected.getMinSelectionIndex();
                Billboard created = new BillboardOptions(username).editBillboard(billboards.get(selected));
                if(created != null) {
                    Message request = client.sendMessage(new Message(token).updateBillboard(created));
                    GUI.ServerDialogue(request.getCommunicationID(),"Edit billboard successful.");
                    updateTable();
                    information.removeAll();
                    preview.setIcon(null);
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    exportButton.setEnabled(false);
                    information.add(new JLabel("Choose a billboard."));
                    pane.validate();
                    pane.repaint();
                }
            }
            else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });

        deleteButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")){
                int selected = rowSelected.getMinSelectionIndex();
                Billboard delete = billboards.get(selected);
                if(delete != null) {
                    Message request = client.sendMessage(new Message(token).deleteBillboard(delete));
                    GUI.ServerDialogue(request.getCommunicationID(),"Delete billboard successful.");
                    updateTable();
                    information.removeAll();
                    preview.setIcon(null);
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    exportButton.setEnabled(false);
                    information.add(new JLabel("Select a user to view permissions."));
                    pane.validate();
                    pane.repaint();
                }
            }
            else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });

        exportButton.addActionListener(e -> {
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            f.showSaveDialog(null);
            System.out.println(f.getSelectedFile());
        });



    }


    private void fileSelection() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.xml", "xml"));
        int result = fileChooser.showOpenDialog(pane);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Billboard selected = GenerateBillboardFromXML.XMLToBillboard(selectedFile, selectedFile.getName(), username);
            Billboard preview = new BillboardOptions(username).editBillboard(selected);
            if (preview != null) {
                Message request = client.sendMessage(new Message(token).createBillboard(preview));
                GUI.ServerDialogue(request.getCommunicationID(),"Create billboard successful.");
            }
            updateTable();
        }

    }

}