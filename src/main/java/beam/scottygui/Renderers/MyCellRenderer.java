/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Renderers;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author tjhasty
 */
public class MyCellRenderer extends JTextArea implements TableCellRenderer {

    public MyCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        this.setBorder(null);

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((String) value);//or something in value, like value.getNote()...
        setSize(table.getColumnModel().getColumn(column).getWidth(),
        getPreferredSize().height);
        if (table.getRowHeight(row) != getPreferredSize().height) {
            table.setRowHeight(row, getPreferredSize().height);
        }
        return this;
    }
}
