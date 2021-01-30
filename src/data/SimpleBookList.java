/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.ArrayList;
import java.io.*; //File,FileReader,FileNotFoundException,BufferedReader,IOException
public class SimpleBookList {
   private ArrayList<SimpleBook> bookList;
   public SimpleBookList() {
      bookList = new ArrayList<SimpleBook>();
   }
   public void add(SimpleBook sb) {
      bookList.add(sb);
   }
   public ArrayList<SimpleBook> getBooks() {
      return bookList;
   }
   public void readFromCSV(String filename) {
      File file = new File(filename);
      FileReader reader = null;
      try {
         reader = new FileReader(file);
      }
      catch (FileNotFoundException e) {
         e.printStackTrace();
         System.exit(1);
      }
      BufferedReader infile = new BufferedReader(reader);
      String line = "";
      try {
         boolean done = false;
         while (!done) {
            line = infile.readLine();
            if (line == null) {
               done = true;
            }
            else {
               String[] tokens = line.trim().split(",");
               String title = tokens[0].trim();
               String author = tokens[1].trim();
               double price = Double.parseDouble(tokens[2].trim());
               SimpleBook sb = new SimpleBook(title,author,price);
               bookList.add(sb);
            }
         }
      }
      catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
   }
}