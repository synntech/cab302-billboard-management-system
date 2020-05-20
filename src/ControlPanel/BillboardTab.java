package ControlPanel;
import Shared.Billboard;
import Shared.Message;
import Shared.BillboardToImage;
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


    public BillboardTab(JTabbedPane mainPane, ArrayList<Integer> permissions, Client client, String token, String username){
        this.client = client;
        this.username = username;
        this.token = token;
        this.billboards = billboards;
        this.pane = new JPanel();
        this.selected = -1;
        pane.setLayout(new GridBagLayout());
        setupBillboardsTable();
        setupDetails();
        updateTable();
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
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Billboard");
        this.table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        table.setRowHeight(90);
        table.setTableHeader(null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty =1;
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300,0));
        pane.add(scrollPane, gbc);
    }
    public void setupDetails() {
        Button createButton = new Button("Create");
        Button importButton = new Button("Import");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button exportButton = new Button("Export");
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        JPanel TopButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        createButton.addActionListener(e -> {
            Billboard created = BillboardOptions.BillboardEditor(username);
            if(created != null) {
                client.sendMessage(new Message(token).createBillboard(created));
                updateTable();
            }
        });

        importButton.addActionListener(e -> {
            try {
                fileSelection();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            }
        });

        TopButtons.add(createButton);
        TopButtons.add(importButton);
        TopButtons.add(editButton);
        TopButtons.add(deleteButton);
        TopButtons.add(exportButton);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pane.add(TopButtons,gbc);



        this.preview = new JLabel("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridheight= 2;
        gbc.fill = GridBagConstraints.BOTH;
        pane.add(preview, gbc);

        this.information = new JPanel();
        information.setLayout(new BoxLayout(information, BoxLayout.PAGE_AXIS));
        information.add(new JLabel("Choose a billboard."));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight= 2;
        gbc.insets = new Insets(12,12,18,18);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor= GridBagConstraints.NORTHWEST;
        pane.add(information, gbc);



        ListSelectionModel rowSelected = table.getSelectionModel();             //setup list selection model to listen for a selection of the table
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){
                this.selected = rowSelected.getMinSelectionIndex();

                editButton.setEnabled(true);
                exportButton.setEnabled(true);
                deleteButton.setEnabled(true);
                this.preview.setIcon(new BillboardToImage(billboards.get(selected),pane.getWidth()-600,(int)((pane.getWidth()-600)/1.77)).Generate()); ;
                this.preview.setText("");
                information.removeAll();
                JLabel title = new JLabel("<html><h1>" + billboards.get(selected).getName() +"</h1><html>");
                title.setPreferredSize(new Dimension(200,30));
                title.setAlignmentX( Component.LEFT_ALIGNMENT );
                information.add(title);
                information.add(new JLabel("<html><h3> Created by: " + billboards.get(selected).getCreatorName() +"</h3><html>"));
                JTextField field = new JTextField();
                field.setText(billboards.get(selected).getPictureLink());
                field.setPreferredSize(new Dimension(200,20));
                field.setEditable(false);
                field.setAlignmentX( Component.LEFT_ALIGNMENT );
                JTextField field2 = new JTextField();
                field2.setText(billboards.get(selected).getMessageText());
                field2.setEditable(false);
                field2.setAlignmentX( Component.LEFT_ALIGNMENT );
                JTextField field3 = new JTextField();
                field3.setText(billboards.get(selected).getInformationText());
                field3.setEditable(false);
                field3.setAlignmentX( Component.LEFT_ALIGNMENT );
                information.add(new JLabel("URL:"));
                information.add(field);
                information.add(Box.createVerticalStrut(10));
                information.add(new JLabel("Message:"));
                information.add(field2);
                information.add(Box.createVerticalStrut(10));
                information.add(new JLabel("Information:"));
                information.add(field3);
                information.add(Box.createVerticalStrut(10));
                pane.validate();
                pane.repaint();
            }
        });

        pane.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent evt) {
                if(selected >-1) {
                    preview.setIcon(new BillboardToImage(billboards.get(selected),pane.getWidth()-600,(int)((pane.getWidth()-600)/1.77)).Generate());
                    pane.validate();
                    pane.repaint();
                }
            }
        });

        editButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getLabel(), "")){
                int selected = rowSelected.getMinSelectionIndex();
                Billboard created = BillboardOptions.BillboardEditor(username, billboards.get(selected));
                if(created != null) {
                    client.sendMessage(new Message(token).updateBillboard(created));
                    updateTable();
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


    private void fileSelection() throws ParserConfigurationException, IOException, SAXException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.xml", "xml"));
        int result = fileChooser.showOpenDialog(pane);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("PogChamp");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(selectedFile);
            doc.getDocumentElement().normalize();
            System.out.println("Billboard: "  + doc.getElementsByTagName("billboard").item(0).getAttributes().getNamedItem("background").getNodeValue());
            System.out.println("Message Colour: "  + doc.getElementsByTagName("message").item(0).getAttributes().getNamedItem("colour").getNodeValue());
            System.out.println("Message : "  + doc.getElementsByTagName("message").item(0).getTextContent());
            System.out.println("Picture URL: "  + doc.getElementsByTagName("picture").item(0).getAttributes().getNamedItem("url").getNodeValue());
            System.out.println("Information Colour: "  + doc.getElementsByTagName("information").item(0).getAttributes().getNamedItem("colour").getNodeValue());
            System.out.println("Information : "  + doc.getElementsByTagName("information").item(0).getTextContent());
        }

    }

}