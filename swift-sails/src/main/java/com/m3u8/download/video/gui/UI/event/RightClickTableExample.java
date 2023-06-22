package com.m3u8.download.video.gui.UI.event;

/**
 * @author Small_Tsk
 * @create 2023-06-20
 **/
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RightClickTableExample extends JFrame {
    private JTable table;
    private JPopupMenu popupMenu;

    public RightClickTableExample() {
        initComponents();
    }

    private void initComponents() {
        // 创建表格
        table = new JTable();
        table.setModel(new DefaultTableModel(
                new Object[][]{
                        {"1", "John", "Doe"},
                        {"2", "Jane", "Smith"},
                        {"3", "Bob", "Johnson"}
                },
                new String[]{"ID", "First Name", "Last Name"}
        ));

        // 创建右键菜单
        popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> {
            // 获取选中的行
            int[] selectedRows = table.getSelectedRows();
            // 删除选中的行
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                model.removeRow(selectedRows[i]);
            }
        });
        popupMenu.add(deleteItem);

        // 添加鼠标事件监听器
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            private void showPopupMenu(MouseEvent e) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        // 将表格添加到窗口中
        getContentPane().add(new JScrollPane(table));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RightClickTableExample().setVisible(true);
        });
    }
}
