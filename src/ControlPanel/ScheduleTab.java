package ControlPanel;

import Shared.Billboard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static ControlPanel.CustomFont.lightGray;

public class ScheduleTab {
    private JPanel pane;
    public ScheduleTab(JTabbedPane mainPane){
        this.pane = new JPanel();
        mainPane.addTab("Schedule", pane);
    }
}
