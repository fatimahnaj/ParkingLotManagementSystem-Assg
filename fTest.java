import java.time.LocalDateTime;
import java.util.Scanner;
import models.vehicle.*;

public class fTest {
    public static void main(String[] args) {

        //scanner - to get input
        Scanner sc = new Scanner(System.in);
        
        while (true) {
        System.out.println("\n==== Parking Lot Management System ====");

        //available option
        System.out.println("1) Park Vehicle (Entry)");
        System.out.println("2) Exit Vehicle");
        System.out.println("0) Logout / Quit");

            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            try {
                //logout system
                if (choice.equals("0")) {
                    System.out.println("Goodbye!");
                    break;
                }

                //park vehicle
                else if (choice.equals("1")) {
                    System.out.println("SELECTED: Park vehicle");

                    //get vehicle type
                    System.out.println("Choose vehicle type:");
                    System.out.println("[1] Motorcycle");
                    System.out.println("[2] Car");
                    System.out.println("[3] SUV");
                    System.out.println("[4] Handicapped");
                    String vehicleTypeValue = sc.nextLine().trim();

                    //get plate number
                    System.out.print("Enter plate number :");
                    String plateNumValue = sc.nextLine().trim();

                    //call object based on vehicle type. pass in vehicle type and plate number as it is required for the constructor
                    Vehicle v1;
                    switch(vehicleTypeValue) {
                        case "1":
                            v1 = new Motorcycle(plateNumValue, "Motorcycle");
                            break;
                        case "2":
                            v1 = new Car(plateNumValue, "Car");
                            break;
                        case "3":
                            v1 = new SUV(plateNumValue, "SUV");
                            break;
                        case "4":
                            v1 = new Handicapped(plateNumValue, "Handicapped");
                            break;
                        default:
                            System.err.println("Kau pilih apa nyah.");
                            v1 = null;
                            break;
                    }

                    //print data of the vehicle if entry is succeed
                    if (v1 != null) {
                        //Entry time is counted only once customer selected parking spot
                        v1.setEntryTime(LocalDateTime.now());
                        //Ticket newTicket = new Ticket(v1, null)
                        System.out.println(v1);
                    }


                    break;
                }

                //exit vehicle
                else if (choice.equals("2")) {
                    System.out.println("SELECTED: Exit vehicle");
                    break;
                }
                else {
                    System.out.println("Invalid choice / Access denied.");
                    break;
                }

            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        sc.close();
    }
}