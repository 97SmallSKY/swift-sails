package com.m3u8.download.video.gui.utils.tool;

import com.m3u8.download.video.m3u8.uiEnum.TableColumnEnum;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 表格过滤监听
 *
 * @author Small_Tsk
 * @create 2023-06-20
 **/
public class FilterActionListener implements ActionListener {

    private String filterValue;

    private JTable table;

    public FilterActionListener(String filterValue, JTable table) {
        this.filterValue = filterValue;
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 创建并设置过滤器
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) table.getModel());
        if(null !=filterValue) {
            sorter.setRowFilter(RowFilter.regexFilter(filterValue, TableColumnEnum.STATUS.getColumnIndex()));
            // 设置表格的排序器
            table.setRowSorter(sorter);
        }else{
            sorter.setRowFilter(null);
            table.setRowSorter(sorter);
        }
        // 刷新表格的显示
        sorter.sort();
    }


}
