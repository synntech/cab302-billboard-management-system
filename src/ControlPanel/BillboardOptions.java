package ControlPanel;

import Shared.Billboard;
import Shared.BillboardToImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.Scanner;

public class BillboardOptions {
    private Billboard billboard = new Billboard();
    private JTextField billboardName = new JTextField();
    private JTextField imgSRC = new JTextField();
    private JTextField messageText = new JTextField();
    private JColorChooser messageColourPicker = new JColorChooser();
    private JColorChooser backgroundColourPicker = new JColorChooser();
    private JTextField infoText = new JTextField();
    private JColorChooser infoColourPicker = new JColorChooser();
    private String username;
    private JLabel preview = new JLabel("");
    private JPanel myPanel = new JPanel();
    private String blank;

    /**
     * Setup BillboardOptions object. Required to create or edit billboard.
     * @param username username of user editing/creating billboard.
     */
    public BillboardOptions(String username) {
        this.username = username;
        billboard.setName("");
        billboard.setMessageText("");
        billboard.setBillboardID(0);
        billboard.setBackgroundColour("");
        billboard.setImageUrl("");

        try {
            Scanner fileScanner = new Scanner(new File("externalResources/blank.txt"));
            blank = fileScanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        billboard.setInformationText("");
        billboard.setInformationTextColour("");
        billboard.setMessageTextColour("");
        preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
        messageText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                billboard.setMessageText(messageText.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                billboard.setMessageText(messageText.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                billboard.setMessageText(messageText.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
        });
        infoText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                billboard.setInformationText(infoText.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                billboard.setInformationText(infoText.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                billboard.setInformationText(infoText.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
        });

        imgSRC.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                billboard.setImageUrl(imgSRC.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                billboard.setImageUrl(imgSRC.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                billboard.setImageUrl(imgSRC.getText());
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
        });

        infoColourPicker.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                billboard.setInformationTextColour(rgbToHex(infoColourPicker.getColor().getRGB()));
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
        });

        backgroundColourPicker.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                billboard.setBackgroundColour(rgbToHex(backgroundColourPicker.getColor().getRGB()));
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
        });

        messageColourPicker.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                billboard.setMessageTextColour(rgbToHex(messageColourPicker.getColor().getRGB()));
                billboard.setImageUrl(blank);
                preview.setIcon(new BillboardToImage(billboard, 352,240).toImageIcon());
                preview.repaint();
            }
        });



    }

    /**
     * Instantiates the billboard editor with new Billboard object
     * @return returns billboard object with edited fields.
     */
    public Billboard newBillboard() {
        return BillboardEditorGUI();
    }


    /**
     * Instantiates the billboard editor with existing billboard
     * @param billboard Billboard object used to obtain parameters to edit a billboard
     * @return returns edited billboard object with new fields.
     */

    public Billboard editBillboard(Billboard billboard) {
        this.billboard = billboard;
        billboardName.setText(billboard.getName());
        billboardName.setEditable(false);
        imgSRC.setText(billboard.getPictureLink());
        messageText.setText(billboard.getMessageText());
        messageColourPicker.setColor(Color.decode(billboard.getMessageTextColour()));
        backgroundColourPicker.setColor(Color.decode(billboard.getBackgroundColour()));
        infoText.setText(billboard.getInformationText());
        infoColourPicker.setColor(Color.decode(billboard.getInformationTextColour()));
        return BillboardEditorGUI();
    }

    private String rgbToHex(int rgb) {
        String messageColourHex = Integer.toHexString( rgb & 0xffffff);
        return '#' + messageColourHex;
    }

    private Billboard BillboardEditorGUI(){
        myPanel.setLayout(new GridBagLayout());
        myPanel.add(new JLabel("Billboard Name: "),GUI.generateGBC(0,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        billboardName.setPreferredSize(new Dimension(400,20));
        myPanel.add(billboardName, GUI.generateGBC(0,1,3,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Image Source: "), GUI.generateGBC(0,2,1,1,1,1,0,5,GridBagConstraints.WEST));
        imgSRC.setPreferredSize(new Dimension(400,20));
        myPanel.add(imgSRC, GUI.generateGBC(0,3,3,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Message Text: "), GUI.generateGBC(0,4,1,1,1,1,0,5,GridBagConstraints.WEST));
        messageText.setPreferredSize(new Dimension(400,20));
        myPanel.add(messageText, GUI.generateGBC(0,5,3,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(new JLabel("Info Text: "), GUI.generateGBC(0,6,1,1,1,1,0,5,GridBagConstraints.WEST));

        infoText.setPreferredSize(new Dimension(400,20));
        myPanel.add(infoText, GUI.generateGBC(0,7,3,1,1,1,0,5,GridBagConstraints.WEST));


        preview.setPreferredSize(new Dimension(352,240));
        myPanel.add(preview, GUI.generateGBC(2,0,1,11,1,1,0,5,GridBagConstraints.WEST));


        myPanel.add(new JLabel("Background Colour: "), GUI.generateGBC(1,0,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(backgroundColourPicker, GUI.generateGBC(1,1,1,8,1,1,0,5,GridBagConstraints.WEST));
        backgroundColourPicker.setPreferredSize(new Dimension(450, 280));
        myPanel.add(new JLabel("Message Colour: "), GUI.generateGBC(0,9,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(messageColourPicker, GUI.generateGBC(0,10,1,1,1,1,0,5,GridBagConstraints.WEST));
        messageColourPicker.setPreferredSize(new Dimension(450, 280));
        myPanel.add(new JLabel("Info Colour: "), GUI.generateGBC(1,9,1,1,1,1,0,5,GridBagConstraints.WEST));
        myPanel.add(infoColourPicker, GUI.generateGBC(1,10,1,7,1,1,0,5,GridBagConstraints.WEST));
        infoColourPicker.setPreferredSize(new Dimension(450, 280));


        //create dialogue window containing Billboard Options UI elements.
        String[] options = new String[2];
        options[0] = "Submit";
        options[1] = "Cancel";
        int result = JOptionPane.showOptionDialog(null, myPanel, "Billboard Editor", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

        if (result == 0) {
            billboard.setCreatorName(username);
            billboard.setName(billboardName.getText());
            //error check billboard before sending to server
            try {
                new BillboardToImage(billboard,480,360).toImageIcon();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Unable to generate billboard. Invalid Properties.", "Invalid Billboard Properties",JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return billboard;
        }

        if (result == 2) {
            return null;
        }

        return null;
    }
}
