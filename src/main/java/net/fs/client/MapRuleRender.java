// Copyright (c) 2015 D1SM.net

package net.fs.client;

import net.miginfocom.swing.MigLayout;
import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MapRuleRender extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = -3260748459008436510L;

    private JLabel label_wan_address;
    private JLabel label2;

    {
        setOpaque(true);
        setLayout(new MigLayout("insets 8 10 0 0"));
        label_wan_address = new JLabel();
        add(label_wan_address, "width :500:,wrap");
        label_wan_address.setBackground(new Color(0f, 0f, 0f, 0f));
        label_wan_address.setOpaque(true);
        label2 = new JLabel();
        add(label2, "width :500:,wrap");
        label2.setBackground(new Color(0f, 0f, 0f, 0f));
        label2.setOpaque(true);
    }


    private void update(MapRule rule, JTable table, int row) {
        int rowHeight = 50;
        int h = table.getRowHeight(row);
        if (h != rowHeight) {
            table.setRowHeight(row, rowHeight);
        }
        label_wan_address.setText("名称: " + rule.name + "  加速端口: " + rule.dst_port);
        label2.setText("本地端口: " + rule.getListen_port());

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        JTable.DropLocation dropLocation = table.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsertRow()
                && !dropLocation.isInsertColumn()
                && dropLocation.getRow() == row
                && dropLocation.getColumn() == column) {

            isSelected = true;
        }
        if (isSelected) {
            setBackground(DefaultLookup.getColor(this, ui, "Table.dropCellBackground"));
        } else {
            setBackground(DefaultLookup.getColor(this, ui, "Table.alternateRowColor"));
        }
        MapRule rule = (MapRule) value;
        update(rule, table, row);
        return this;
    }

}
