package data;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
public class SimpleBookTableModel extends AbstractTableModel {
   private String[] columnNames = {"Title","Author","Price"};
   private ArrayList<SimpleBook> myList;
   public SimpleBookTableModel(SimpleBookList bkList) {
      myList = bkList.getBooks();
   }
   public int getColumnCount() {
      return columnNames.length;
   }
   public int getRowCount() {
      int size;
      if (myList == null) {
         size = 0;
      }
      else {
         size = myList.size();
      }
      return size;
   }
   public Object getValueAt(int row, int col) {
      Object temp = null;
      if (col == 0) {
         temp = myList.get(row).getTitle();
      }
      else if (col == 1) {
         temp = myList.get(row).getAuthor();
      }
      else if (col == 2) {
         temp = new Double(myList.get(row).getPrice());
      }
      return temp;
   }
   // needed to show column names in JTable
   public String getColumnName(int col) {
      return columnNames[col];
   }
   public Class getColumnClass(int col) {
      if (col == 2) {
         return Double.class;
      }
      else {
         return String.class;
      }
   }
}