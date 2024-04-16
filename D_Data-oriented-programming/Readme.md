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

Then, once this refactoring is done, you will see how adding the support for multileg flights can be done very easily, using sealed types and pattern matching.

## Step 0: Exploring the Application

You can launch the application as it is by running the `Main` class. As you can see, this class creates four flights: Paris Atlanta, Amsterdam New-York, London Miami, and Francfort Washington. It then decides to follow the prices of these four flights. If you follow the code that is being executed, you will see that these flights will see their price change every 500ms. Then two of these flights are displayed in the GUI (the console): London Miami and Francfort Washington.

Running this code show you the flights that are created, then monitored, and then displayed. Don't forget to stop the running of this application, because if you don't it will run forever.

## Step 1: Fixing the Dependencies

The first step consists in fixing the dependency problems this application suffers. What we want is that the technical modules (database, price service, GUI) depend on the business module, and not the opposite. For that, you are going to create interfaces and records, and place them in the right modules of the current application.

### Fixing the relation between the Business module and the GUI

To prevent the business module to depend on the GUI, you need to create an interface in the Business module (`D_Flight-business-process`) of your application. This interface has only one method. Calling it displays a flight on the GUI. The GUI thus becomes a client of your business process.

#### Adding Interfaces and Records

You can create the following interface in the `D_Flight-business-process` module. You can put it in a `service` package for instance.

```java
public interface FlightGUIService {
    void displayFlight(Flight flight);
}
```

And because you should not need to depend on the database anymore, you can also create the following records `Flight` and `City`. You can put them in a `model` package for instance.

```java
public record Flight(City from, City to) {}
```

```java
public record City(String name) {}
```

Be careful when doing this refactoring: your `FlightGUIService` should depend on your records, not on the classes defined in the `A_Database` module.

#### Refactoring the D_Flight-business-process Module

Now you can refactor the `FlightMonitoring` class. Remove the static field `FlightGUI flightGUIService` and replace it by a private non-static field `FlightGUIService flightGUIService`. Do not initialize it for the moment.

This class is not pulling its own dependency anymore, which is one less problem. You need to make the `launchDisplay()` method non-static, because it now needs to read an instance field.

The `FlightMonitoring.getInstance()` method does not compile anymore, which is fine because you will remove it very soon. In the meantime you can just remove the faulty method that calls `launchDisplay()`.

Your problem now is that you have two different classes named `Flight`: one that is coming from the `A_Database` module, and another one that is the record you just created. Let us manually change the imports of this class to make sure that you are importing the `Flight` record you just created, and not the other one.

The `FlightMonitoring.followFlight()` method is not compiling anymore, but you can fix it by replacing the `Flight flight` declaration by a `var flight` declaration. Type inference for the win!

The last problem you need to fix is in the `FlightMonitoring.monitorFlight()` method. The problem here has to be fixed with an adapter. You get an instance of the `Flight` class defined in the `A_Database` module, to an instance of your `Flight` record.

The code of this method should now look like the following:

```java
public void monitorFlight(IDFlight idFlight) {
    var flight = dbService.fetchFlight(idFlight);
    // Adaptation
    City from = new City(flight.from().name());
    City to = new City(flight.to().name());
    Flight newFlight = new Flight(from, to);
    monitoredFlights.put(idFlight, newFlight);
}
```

At some point you will be able to get rid of this adaptation.

#### Refactoring the C_Graphical-user-interface Code

Now you need to refactor the other side of this dependency: the `C_Graphical-user-interface` module.

The `FlightGUI` class should implement the `FlightGUIService` interface you defined in the `D_Flight-business-process` module. For that, you need to declare that the `C_Graphical-user-interface` module depends on the `D_Flight-business-process` module. So you need to open the POM files of both modules:

1. `D_Flight-business-process` module POM: remove the dependency to the `C_Graphical-user-interface` module.
2. And in the `C_Graphical-user-interface` module POM:  add a dependency to the `D_Flight-business-process` module.

If your refresh your Maven configuration in your IDE, you should now be able to import the `FlightGUIService` interface in your  `FlightGUI` class. Be careful in this class, because the `Flight` object that this method receives as a parameter is now the record you defined in the `D_Flight-business-process`, not the one in the `D_Flight-business-process` module.

You can also delete the ugly `FlightGUI.getInstance()` static method, that is not called anymore. Good riddance.

#### Fixing the Price Property of Flight

How can you fix the fact that your `Flight` record does not have a price? Your problem here is that the price changes, and that a record is non-modifiable, so you cannot simply add a component to this record, because you will not be able to change it.

The solution to this problem will come when you have refactored the relation between the `D_Flight-business-process` module and the `B_Price-monitoring` module. In the meantime, you can just stub it by creating an empty `price()` method on your `Flight` record. We will come to it again later.

### Fixing the relation between the Business module and the Price Monitoring Module

Let us use the same technique to fix the relation between the `D_Flight-business-process` module and the `B_Price-monitoring` module.

#### Refactoring the D_Flight-business-process Module

The `FlightConsumer` interface is defined in the `B_Price-monitoring` module, thus imposing a dependency in the wrong direction. Let us make it so that the `D_Flight-business-process` module defines this interface contract, and while we are at it, also defines the objects that are moved between these two modules.

1. Move the `FlightConsumer` interface to the `D_Flight-business-process` module, for instance in a `service` package.
2. Make it consume an instance of `Price`, a record you create in the `D_Flight-business-process` module, for instance in a `model` package.

The interface should now look like this:

```java
public interface FlightConsumer {
    void updateFlight(Price price);
}
```

You also need to create an interface to model what this `B_Price-monitoring` module is doing. Following what you did with the GUI, this interface needs to be defined on the `D_Flight-business-process` module and implemented in the `B_Price-monitoring` module.

This interface can be the following, you can put it in a `service` package of the `D_Flight-business-process` module.

```java
public interface PriceMonitoringService {
    void followPrice(FlightID flightID, FlightConsumer consumer);
}
```

It needs a `FlightID` object, that you can create as a record in your `D_Flight-business-process` module, in the `model` package for instance.

```java
public record FlightID(String id) {}
```

You now need to fix the `FlightMonitoring` class, following the same principles as previously.

1. Make the `priceMonitoringService` non-static, and remove the ugly call to `FlightPriceMonitoringService.getInstance()`. We will fix the initialization of this field later.
2. Remove the call to `priceMonitoringService.updatePrices()`. It is made in a method that will be removed anyway.
3. You need to fix the import on the `FlightID` class

At this point, your `D_Flight-business-process` module should not have anymore dependency on the `B_Price-monitoring` module. This module, on the contrary, needs to depend on the `D_Flight-business-process` module, where the interface it should implement is found, as well as the definition of the consumer it should call.

So you need to open the POM files of both modules:

1. In the `D_Flight-business-process` module POM: remove the dependency to the `B_Price-monitoring` module.
2. And in the `B_Price-monitoring` module POM:  add a dependency to the `D_Flight-business-process` module.

Do not forget to refresh your Maven configuration in your IDE.

#### Refactoring the B_Price-monitoring Module

You can see now that the class `FlightPriceMonitoringService` is not compiling anymore, which is expected. All you need to do is to fix the imports: it should only depend on the records of the `D_Flight-business-process` module.

You can now delete the classes `FlighID` and `FlightPrice` of this module, as they are not used anymore.

You can also delete the ugly `FlightPriceMonitoringService.getInstance()` static method, that is always a very pleasant thing to do. It is not called anymore, so nobody will notice.

### Fixing the Dependency between Business module and the Database Module

The last wrong relation you need to fix is the relation between the `D_Flight-business-process` module and the `A_Database` module.

You are going to follow the same principle, that is to create an interface in the `D_Flight-business-process` module and implement it in the `A_Database` module.

#### Refactoring the D_Flight-business-process Module

You can create this interface in the `service` package of the `D_Flight-business-process` module, and use it in the `FlightMonitoring` class.

This interface can look like this one. `Flight` and `FlightID` are the records you defined in the `D_Flight-business-process` module.

```java
public interface DBService {
    Flight fetchFlight(FlightID flightID);
}
```

Following this, you can get rid of your last call to a `getInstance()` in the `FlightMonitoring` class.

Several fixes should be made in the `FlightMonitoring.followFlight()` method.
1. Make it take a `FLightID` instead of an `IDFlight`. Now you do not need your adapter code anymore.
2. You will also need to create an empty `updatePrice()` method in your `Flight` record, just to make the compiler happy. We will fix this method later.
3. Also, make this `updatePrice()` method take an instance of the `Price` record ot the `D_Flight-business-process` module, instead of the `A_Database` one. It will make the `flightConsumer` easier to write, because you do not need any adaptation there  neither.

Some more fixes are needed in the `FlightMonitoring.monitorFlight()` method.
1. Make it take a `FLightID` instead of an `IDFlight`.
2. Fix the `monitoredFlights` registry. Its keys should now be your record `FLightID`.

Your `FlightMonitoring` class should not depend on any class from the `A_Database` module anymore. You can check that in its imports.

Now you can invert the dependency between the two modules `D_Flight-business-process` and `A_Database`.
1. In the `D_Flight-business-process` module POM: remove the dependency to the `A_Database` module.
2. And in the `A_Database` module POM: add a dependency to the `D_Flight-business-process` module.

If you refresh your Maven configuration in your IDE, your `E_Main` module should not compile anymore, which is OK for now.

#### Refactoring the A_Database Module

The same will apply to the `A_Database` module, that should become a client of the `D_Flight-business-process` module.

Your `FlightDBService` class should implement the `DBService` interface from your `D_Flight-business-process` module.

Also, this class should now depend on the model object your `D_Flight-business-process` module is providing. That includes `City`, `Flight` and `IDFlight`. It is perfectly OK for this module to use its own object model, since in a real application it would probably map it to some kind of database. But it is its responsibility to  adapt its object model to the requirements of your business modules.

An example of such an adaptation is the implementation of the `FlightDBService.fetchFlight()` method. This method takes an instance of `FlightID` as a parameter, which an object sent by the `D_Flight-business-process` module. But it needs a primary key to access the right flight, that is of type `IDFlight`, defined in this `A_Database` module. So an adaptation should be done here, between these two instances. Doing this adaptation is the responsibility of the `A_Database` module.

### Fixing the Main Module

Fixing the `E_Main` module consists in two things.

First, the `Main` class does not compile anymore. You should now create instances of `FlightID` instead of `IDFlight` to make the code compile.

Second, you should get rid of this ugly call to `FlightMonitoring.getInstance()`. All the instantiations of the interfaces you created should be done there. Once you have these instances, you should send them to the `FlightMonitoring` class constructor. You will to add dependencies to this module. This module has a dependency to all the other modules of this application.

In the end, creating your `FlightMonitoring` instance should look like this.

```java
DBService dbService =
        new FlightDBService();
FlightGUIService guiService =
        new FlightGUI();
PriceMonitoringService monitoringService =
        new FlightPriceMonitoringService();
var flightMonitoring = 
        new FlightMonitoring(
                dbService, 
                guiService, 
                monitoringService);
```

You do not need these `getInstance()` methods anymore, because you are now controlling the instantiations of your service classes in this class. You are providing the implementations of the services that your objects need by injecting them through their constructor. These classes do not have to fetch these implementations themselves.

You do not need any smart implementation of the Singleton pattern, because you are the only one who is calling these constructors, and you are doing it only once.

The constructor of your `FlightMonitoring` class should look like this.

```java
public class FlightMonitoring {
    private final DBService dbService;
    private final PriceMonitoringService priceMonitoringService;
    private final FlightGUIService flightGUIService;

    public FlightMonitoring(
            DBService dbService,
            FlightGUIService guiService,
            PriceMonitoringService monitoringService) {
        this.dbService = dbService;
        flightGUIService = guiService;
        priceMonitoringService = monitoringService;
    }
}
```

The last thing you need to do to have a working application is to launch the two services that update the prices in the background, and that displays automatically the prices you chose to follow. You need to call the two corresponding methods from your main method:

```java
monitoringService.updatePrices();
flightMonitoring.launchDisplay();
```

At this point the `updatePrices()` may not be declared on the `PriceMonitoringService` interface. So you may need to add it now.

### Fixing the Prices

One thing was left aside: the updating of the prices.

At this point, if your followed the instructions, you should have a `Flight.updatePrice(Price price)` method with an empty implementation. You cannot make a record modifiable. So from here you have two solutions: either you make is a regular class, or use a registry. Let us make a registry here We are choosing this solution because having unmodifiable classes make your code simpler to maintain.

In that case, your `Flight` record could look like the following.

```java
public record Flight(City from, City to) {

    public Flight {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }

    private static Map<Flight, Price> pricePerFlight =
            new ConcurrentHashMap<>();

    public Price price() {
        return pricePerFlight.get(this);
    }

    public void updatePrice(Price price) {
        pricePerFlight.put(this, price);
    }
}
```

## Step 2: Launching the Application Again

At this point, you should be able to launch your application properly again. All your dependencies has been fixed. Your application is organized around your Business module, all the other modules are isolated behind interfaces.

## Step 3: Adding The Support For Multileg Flights

A new business requirement has to be implemented: instead of supporting simple flights between cities, you need to support multileg flights.

In an Object Oriented context, you would probably refactor your object model with an `AbstractFlight` class, and extensions to support that. It is doable but would not work very well in our case, because the real object model of this application is hidden in the `A_Database` module, no other module knows about it, and we certainly do not want anybody to depend on this module. All we have are transport objects, implemented with record, that carry their state, but no behavior.

In the Data Oriented Programming, supporting this new requirement is actually easy, with minimal refactoring.

First, you are going to update the records that are carrying the state your transport from one module to the other. And second, you are going to add the behavior on the new records, where you need it.

### Creating Sealed Interfaces

There are two types you need to update:
- `FlightID`, that should become `SimpleFlightID` and `MultilegFlightID`,
- and `Flight`, that should become `SimpleFlight` and `MultilegFlight`.

In this context `Flight` and `FlightID` become two sealed interfaces, with two permitted implementations each.

Here is the code for the `Flight` hierarchy.

```java
public sealed interface Flight
        permits SimpleFlight, MulitlegFlight {
}
```

```java
public record SimpleFlight(SimpleFlightID id, City from, City to) 
        implements Flight {

    public SimpleFlight {
        Objects.requireNonNull(id);
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }
}
```

```java
public record MulitlegFlight(MulitlegFlightID id, City from, City via, City to) 
        implements Flight {

    public MultilegFlight {
        Objects.requireNonNull(id);
        Objects.requireNonNull(from);
        Objects.requireNonNull(via);
        Objects.requireNonNull(to);
    }
}
```

You need to adapt this pattern of code to the `FlightID` hierarchy.

Doing that breaks many things in your application, but it's not actually too bad, because each fix is very simple, and clearly localized within your code.

### Fixing the FlightGUI Class

The `FlightGUI.displayFlight()` method takes a `Flight` as a parameter. The Data Oriented Programming approach consists in switching over the different concrete types of `Flight`. All of them are known at compile time, thanks to the fact that `Flight` is a sealed interface.

This method then becomes the following:

```java
public void displayFlight(Flight flight) {
    switch(flight) {
        case SimpleFlight(_, City from, City to) -> System.out.println(
                "Flight from " + from.name() + " to " + to.name() +
                ": price is now " + SimpleFlight.price(flight));
        case MultilegFlight(_, City from, City via, City to) -> System.out.println(
                "Flight from " + from.name() + " to " + to.name() + " via " + via.name() +
                ": price is now " + MultilegFlight.price(flight));
    };
}
```

Note that we added the ID of the flight to both implementations `SimpleFlight` and `MultilegFlight`. But it turns out that you do not need this ID in this processing. So you can use the unnamed pattern to avoid creating an unneeded variable. In that case it does not make much difference. But you need to be aware that the accessor of this record component is called to initialize this value. If you have some expensive defensive copy in this accessor, then not calling it may make a difference.

Several points are worth noting.
1. The deconstruction allows you to have a simpler code for the printing of the information.
2. Because you used a sealed interface, this `switch` does not need any `default` branch. Actually you should not put any. If you do, and add more implementations of `Flight` in the future, then the compiler will not be able to tell you that you forgot a case.
2. All the different ways of printing your different objects are at the same place in your application, making its writing and maintenance easier.
3. For now, you can only deconstruct records from their components. So the code to get the price is not very nice. This should be fixed in the future.

You can also notice that we added a static method to `SimpleFlight` and `MultilegFlight`: `price()`, that takes the `flight` as a parameter. These two methods are built on the same pattern of code. They simply store the price of the flight in a registry. Here is the method for the `SimpleFlight` record. These two registries are not polymorphic, they only store the prices of the corresponding type of flight. They come with an `updatePrice()` method, that will be used in the following.

```java
private static final Map<SimpleFlightID, Price> pricePerFlight
        = new ConcurrentHashMap<>();

public static Price price(SimpleFlight flight) {
    return pricePerFlight.get(flight.id());
}

public static void updatePrice(SimpleFlightID id, Price price) {
    pricePerFlight.put(id, price);
}
```


### Fixing the FlightDBService Class

Fixing this class follows the exact same pattern as the previous one. The code of the `fetchFlight()` method can look like the following.

```java
public Flight fetchFlight(FlightID id) {
    
    return flights.computeIfAbsent(
            id,
            _ -> switch (id) {
                case SimpleFlightID(String from, String to) ->
                        new SimpleFlight((SimpleFlightID)id, 
                                cities.get(from), 
                                cities.get(to));
                case MultilegFlightID(String from, String via, String to) ->
                        new MultilegFlight((MultilegFlightID)id, 
                                cities.get(from), 
                                cities.get(via), 
                                cities.get(to));
            });
}
```

Note that the primary keys of the simple flights and the ones of the multileg flights are naturally bound to the corresponding implementations by the use of this `switch` statement. Again, no polymorphic calls here, all the types used are the exact types.


### Fixing the FlightMonitoring Class

Fixing the `FlightConsumer` implementation can be made by creating a factory class `Flights` to carry the `switch` to the correct registry. Here you do not need any deconstruction, you can use the ID directly.

```java
public class Flights {

    public static void updatePrice(FlightID flightID, Price price) {
        switch (flightID) {
            case SimpleFlightID id -> SimpleFlight.updatePrice(id, price);
            case MultilegFlightID id -> MultilegFlight.updatePrice(id, price);
        }
    }
}
```

Now that you can fix the `FlightConsumer` interface and its implementation.

The `FlightConsumer` interface uses the `Flights` class which is a factory class. So you need to pass the `updateFlight()` method a way to find the flight this new price will be applied to. In that case we pass the ID of the flight.

```java
public interface FlightConsumer {
    void updateFlight(FlightID flightID, Price price);
}
```

You also need to update the `updatePrices()` method in the `FlightPriceMonitoringService` implementation, since the `flightConsumer` now takes the `flightID` parameter. Fortunately, this method works with a registry that binds each consumer to its corresponding id.

So the declaration of the task that update all the prices may look like the following.

```java
Runnable task = () -> {
    for (var entry : registry.entrySet()) {
        var flightId = entry.getKey();
        var flightConsumer = entry.getValue();
        flightConsumer.updateFlight(flightId, new Price(random.nextInt(80, 120)));
    }
};
```

### Fixing the Main Class

The last element you need to update is your main class. Since `FlightID` is now an interface, you need to change this code. You can just replace your primary keys creation by the following:

```java
var f1 = new SimpleFlightID("Pa", "At");
var f2 = new SimpleFlightID("Am", "NY");
var f3 = new SimpleFlightID("Lo", "Mi");
var f4 = new SimpleFlightID("Fr", "Wa");
```

You can also make these flights multileg flights:

```java
var f1 = new MultilegFlightID("Pa", "Am", "At");
var f2 = new MultilegFlightID("Am", "Pa", "NY");
var f3 = new MultilegFlightID("Lo", "Fr", "Mi");
var f4 = new MultilegFlightID("Fr", "Pa", "Wa");
```


## Step 4: Launching the Application Again

From this point, you can launch your application again, and it should work in the same way as previously. 