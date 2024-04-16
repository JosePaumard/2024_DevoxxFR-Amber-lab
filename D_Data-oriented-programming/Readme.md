Refactoring to Data Oriented Programming
========================================

## Introduction

This lab takes you through the refactoring of a legacy application using the Data Oriented Principles, so that you can see what you can take from these ideas to improve the quality of your code and your architectures.

You are working on an application that allows you to follow the price of a set of flights.

The principle of the application is the following:
- you declare that you want to follow the price of a flight between two cities
- the system then pushes the price of this flight when it changes
- when a price is changed, then it is displayed on a graphical user interface.

Of course all of this is made as simple as possible: there is no network connection, the database is just an in-memory hash table, and the GUI is simply the console.

## Presenting the Application

The application has been organised in the following modules:
- a database module (`A_Database`): you can fetch flights from this module
- a price monitoring module (`B_Price-monitoring`): you can ask this module to send you the price of given flight when it changes
- a graphical user interface (`C_Graphical-user-interface`): you can send informations to this module, so that it can display it
- a business module (`D_Flight-business-process`): it is the core of your application, this is where your business process is implemented
- and a main module (`E_Main`), that can run the application.

There are many flaws in this application, not only in the code, but also in the way this application is organized.
1. Every module depends on the database.
2. There are classes declared in the object model that are not used by your application. Maybe there are used in other parts of your applications, but your business modules still depends on them. 
3. Your main business process fetches its technical dependencies itself, using badly implemented singletons.
4. One of the ugly consequence is that it depends on your graphical user interface, meaning that everytime someone decides to change the color of a button, you will recompile your business rules.
5. Because the model classes are not compatible throughout your application, the business module needs to adapt some objects from a given class to instances of another class. For instance, the `FlightConsumer` interface, defined in the `B_Price-monitoring` module consumes a `FlightPrice` object. But the price received in the `FilghtMonitoring` class is an instance of the `Price` class defined in the `A_Database` module. So an adaptation is needed there, which is done in the `FlightMonitoring.followFlight()` method.
7. Because everything is so tightly entangled, adding the support for multileg flights is hard, and will be very costly. 
 
## Goal of the Refactoring

The goal of the refactoring is to organize the application in such a way that the dependencies are in the right direction. All your modules should depend on the central module of your application: the one that is implementing your business rules (`D_Flight-business-process` in the application). 

Then, you will see how adding the support for multileg flights can be done very easily, using sealed types and pattern matching. 

## Fixing the Dependencies