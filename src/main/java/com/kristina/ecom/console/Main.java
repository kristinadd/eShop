package com.kristina.ecom.console;

import java.util.Scanner;
// for the user input ;; old projects student 
public class Main {

  private static MarketSpace marketSpace = MarketSpace.instance();
  private static Admin admin;
  private static Oms oms;
  private Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        admin = Admin.instance();
        oms = Oms.instance();
        Main m = new Main();
        int c;
        

        while (true) {
          m.menu();
          c = m.sc.nextInt();
    
          switch (c) {
            case 1:
              marketSpace.buy();
              break;
            case 2:
              marketSpace.getCart();
              break;
            case 3:
              marketSpace.sort("ID");
              break;
            case 4:
              marketSpace.sort("PRICE");
              break;
            case 5:
              admin.admin();
              break;
            case 6:
              oms.admin();
              break;
            case 7:
              System.exit(0);
            default:
              System.out.println("Invalid choice. Please try again.");
          }
        }
    }

    private void menu() {
      String[] items = {
        "Buy a computer",
        "See my shopping cart",
        "Sort by order ID (Descending order)",
        "Sort by order price (Descending order)",
        "Product Admin",
        "Order managment",
        "Quit"
      };

      System.out.println("Hi, what would you like to do?");
        for (int i = 0; i < items.length; i++)
          System.out.printf("%d: %s\n", i+1, items[i]);
    }
}
