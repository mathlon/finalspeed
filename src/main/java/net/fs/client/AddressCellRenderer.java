package net.fs.client;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class AddressCellRenderer implements ListCellRenderer {

    private JPanel panel = null;

    private JLabel addressLabel;

    private Color color_normal = new Color(255, 255, 255);

    private Color color_selected = new Color(210, 233, 255);

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (panel == null) {
            init();
        }
        updateData(value, isSelected);
        return panel;
    }

    private void init() {
        panel = new JPanel();
        panel.setLayout(new MigLayout("insets 0 5 0 0", "[grow,fill]rel[right]", "[]0[]"));
        panel.setOpaque(true);
        panel.setBackground(color_normal);
        addressLabel = new JLabel("");
        panel.add(addressLabel, "");
        addressLabel.setOpaque(false);

        JButton button_remove = new JButton("x");
        //panel.add(button_remove,"align right");
        button_remove.setOpaque(false);
        button_remove.setContentAreaFilled(false);
        button_remove.setBorderPainted(false);
        button_remove.setMargin(new Insets(0, 10, 0, 10));
        button_remove.addActionListener(System.out::println);

    }

    private void updateData(Object value, boolean isSelected) {
        addressLabel.setText(value.toString());
        if (isSelected) {
            panel.setBackground(color_selected);
        } else {
            panel.setBackground(color_normal);
        }
    }

}
