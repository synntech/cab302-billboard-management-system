package ControlPanel;
import Shared.Billboard;
import static ControlPanel.CustomFont.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class BillboardTab{
    public static JPanel SetupBillboardsPane() {
        JPanel panel = new JPanel();                                                           //first tab
        panel.setBorder(BorderFactory.createEmptyBorder(30,20,15,20));
        panel.setLayout(new GridLayout(2,1));
        panel.setBackground(lightGray);
        return panel;
    }

    public static JTable SetupBillboardsTable(JPanel pane, ArrayList<Billboard> billboards) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Billboard");
        model.addColumn("Author");
        model.addColumn("Image URL");
        model.addColumn("Message Text");
        model.addColumn("Message Colour");
        model.addColumn("Background Colour");
        model.addColumn("Info Text");
        model.addColumn("Info Colour");
        JTable table = new JTable(model);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);
        setTableFeatures(table);                            //set table font, layout, size, colour etc.
        pane.add(new JScrollPane(table));                   //add table to pane - 1st row out of 2 in the grid layout.
        //------------------------------------Table Created --------------------------------------------------------//
        JLabel bottomGrid = new JLabel();
        bottomGrid.setLayout(new GridLayout(2,3,10, 0));       //2nd row of pane gridLayout contains a label with 2 rows 3 cols

        JButton previewButton = new JButton("\uD83D\uDCA9Preview Billboard");                    //button to be placed at grid space (0,1)
        JButton createButton = new JButton("Create New");                        //button to be placed at grid space (2,1)
        JButton editButton = new JButton();                  //button to be placed at grid space (3,1)

        setButtonLook(previewButton);
        setButtonLook(createButton);
        setButtonLook(editButton);

        JLabel selectedRow = new JLabel();
        selectedRow.setFont(tableContentsF);

        ListSelectionModel rowSelected = table.getSelectionModel();             //setup list selection model to listen for a selection of the table
        rowSelected.addListSelectionListener(e -> {
            if (!rowSelected.isSelectionEmpty()){
                int selected = rowSelected.getMinSelectionIndex();
                selectedRow.setText("       Row "+selected+" Selected - '" + billboards.get(selected).getName() + "'");               //change label text to display selected row.
                editButton.setText("Edit Billboard");
            }
        });

        bottomGrid.add(selectedRow);        //add label showing which row is selected
        bottomGrid.add(new JLabel());       //add 2 blank labels in grid locations (1,0) and (2,0) - room to replace in future
        bottomGrid.add(new JLabel());

        previewButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Not yet implemented."));
        editButton.addActionListener(e -> {
            if (!Objects.equals(editButton.getText(), "")){
                JOptionPane.showMessageDialog(null, "Not yet implemented.");
            }
            else JOptionPane.showMessageDialog(null, "Please select a billboard first.");
        });

        bottomGrid.add(previewButton);                 //place button 1 at (0,1)
        bottomGrid.add(editButton);                   //place button 2 at (1,1)
        bottomGrid.add(createButton);                  //place button 2 at (2,1)
        pane.add(bottomGrid);

        return table;
    }


    public static void setButtonLook(JButton b){
        b.setBorder(BorderFactory.createLineBorder(Color.black, 3));        //set button border, font, colours etc.
        b.setFont(buttons);
        b.setForeground(softBlue);
    }
    public static void setTableFeatures(JTable table){
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(0,74,127));
        //table.setSelectionForeground(Color.black);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.getColumnModel().getColumn(0).setMaxWidth(35);    //set column 0 to max 35 wide (doesn't need to be big)
        table.setIntercellSpacing(new Dimension(10, 20));
        table.setFont(tableContentsF);                                      //table contents font (16px Comic sans)
        table.getTableHeader().setBackground(softBlue);                     //set table header colour
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setFont(new Font("Comic Sans", Font.ITALIC, 18));
        table.setDefaultEditor(Object.class, null);
    }

    public static void updateTable(JTable table, ArrayList<Billboard> billboards){
        Integer i = 0;
        table.repaint(); //redraw table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (Billboard billboard : billboards) {
            model.addRow(new Object[]{
                    i.toString(),
                    billboard.getName(),
                    "TBA",
                    billboard.getPictureLink(),
                    billboard.getMessageText(),
                    billboard.getMessageTextColour(),
                    billboard.getBackgroundColour(),
                    billboard.getInformationText(),
                    billboard.getBackgroundColour()});
            i++;
        }
    }
}