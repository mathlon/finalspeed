// Copyright (c) 2015 D1SM.net

package net.fs.client;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

class AddMapFrame extends JDialog {

    private static final long serialVersionUID = -3248779355079724594L;

    ClientUI ui;

    private JTextField portTextField, text_port, nameTextField;

    AddMapFrame(final ClientUI ui, JFrame parent, final MapRule maprule_origin, final boolean edit) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        this.ui = ui;
        setTitle("增加映射");
        //setSize(size);
        if (edit) {
            setTitle("编辑映射");
        }

        JPanel panel = (JPanel) getContentPane();
        panel.setLayout(new MigLayout("alignx center,aligny center,insets 10 10 10 10"));


        JPanel p3 = new JPanel();
        panel.add(p3, "wrap");
        p3.setBorder(BorderFactory.createEtchedBorder());
        p3.setLayout(new MigLayout("inset 5 5 5 5"));

        p3.add(new JLabel("名称:"));
        nameTextField = new JTextField();
        p3.add(nameTextField, "width :100: ,wrap");

        p3.add(new JLabel("加速端口:"));
        portTextField = new JTextField("");
        p3.add(portTextField, "width :50:,wrap");
        portTextField.setToolTipText("需要加速的端口号");

        p3.add(new JLabel("本地端口:	"));
        text_port = new JTextField();
        p3.add(text_port, "width :50: ,wrap");

        JPanel p6 = new JPanel();
        panel.add(p6, "align center,wrap");
        p6.setLayout(new MigLayout("align center"));

        JButton button_ok = createButton("确定");
        p6.add(button_ok);
        button_ok.addActionListener(e -> {
            try {
                checkName(nameTextField.getText());
                checkPort(text_port.getText());
                checkPort(portTextField.getText());
                String name = nameTextField.getText();
                int listen_port = Integer.parseInt(text_port.getText());
                int dst_port = Integer.parseInt(portTextField.getText());
                MapRule mapRule_new = new MapRule();
                mapRule_new.setName(name);
                mapRule_new.listen_port = listen_port;
                mapRule_new.setDst_port(dst_port);
                if (!edit) {
                    ui.mapClient.portMapManager.addMapRule(mapRule_new);
                } else {
                    ui.mapClient.portMapManager.updateMapRule(maprule_origin, mapRule_new);
                }
                ui.loadMapRule();
                ui.select(mapRule_new.name);
                setVisible(false);
            } catch (Exception e1) {
                //e2.printStackTrace();
                JOptionPane.showMessageDialog(ui.mainFrame, e1.getMessage(), "消息", JOptionPane.WARNING_MESSAGE);
            }
        });

        p6.add(new JLabel(" "));

        JButton button_cancel = createButton("取消");
        p6.add(button_cancel);
        button_cancel.addActionListener(e -> setVisible(false));


        if (edit) {
            nameTextField.setText(maprule_origin.name);
            text_port.setText(maprule_origin.listen_port + "");
            portTextField.setText(maprule_origin.dst_port + "");
        }

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void checkName(String s) throws Exception {
        if (s.trim().equals("")) {
            throw new Exception("请输入名称");
        }
    }

    private void checkPort(String s) throws Exception {
        int port;
        try {
            port = Integer.parseInt(s);
        } catch (Exception e1) {
            throw new Exception("请输入正确端口号");
        }
        if (port < 1 | port > 256 * 256) {
            throw new Exception("请输入正确端口号");
        }
    }

    private JButton createButton(String name) {
        JButton button = new JButton(name);
        button.setMargin(new Insets(0, 5, 0, 5));
        button.setFocusPainted(false);
        return button;
    }


}
