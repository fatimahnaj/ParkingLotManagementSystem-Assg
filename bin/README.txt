Parking Lot Management System - Group 7 [TC3L/T11L]

Overview
Simple Java-based parking lot management system with admin and customer UI, billing, and fines.

How to Run
1) Open a terminal in the project root:
	c:\Users\fatim\Desktop\ParkingLotManagementSystem-Assg
2) Compile (from project root):
	javac -d bin -cp lib/*;bin ui/MainFrame.java
3) Run:
	java -cp lib/*;bin ui.MainFrame

Interface guideline
Customer :
1)Click [register] to enter customer dashboard. 
2)Enter required vehicle plate number and type.
3)Click [park] to choose spot.
4)Click [exit] to exit your vehicle from the parking lot.

Admin :
1)Click [admin] and log in 
2)Enter admin credentials (can be find in database)
3)View reports on each tabs
4)Click refresh to reflect current changes in parking lot.

Directory Notes
- admin/: Admin database and repository classes.
- bin/: Compiled classes and generated artifacts.
- db/: Database schema and SQL setup.
- dto/: Data transfer objects for billing and fines.
- lib/: External libraries (if any).
- models/: Core domain models (parking, vehicles, tickets).
- service/: Business logic (billing, fines, persistence).
- test/: Demo/test entry points.
- ui/: Swing UI screens and main entry frame.
- util/: Helpers and initializers.
