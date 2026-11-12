# Where each OOP concept is in this project (for the viva)

Open each file and look for the `// OOP:` comment next to the code.
Line numbers are approximate; the comments are easier to find than numbers.

| Concept | One-line meaning | Where to point |
|---|---|---|
| **Encapsulation** | Data is private; only guarded methods change it | `model/Bill.java` -> `addPayment()` · `model/SparePart.java` -> private `quantity` + `removeStock()` · `model/Person.java` |
| **Abstraction** | Define *what*, leave *how* to subclasses | `model/Person.java` (abstract `getRole()`) · `model/ServiceType.java` (abstract `getLabourCharge()`) · `ui/swing/BasePanel.java` |
| **Inheritance** | A child reuses its parent | `Customer`, `Mechanic` extend `Person` · `OilChange`, `Repair`, `FullService` extend `ServiceType` · every `*Panel` extends `BasePanel` |
| **Polymorphism** | Same call, different behaviour | `serviceType.calculateCharge()` -> 2500 for OilChange, hours x 3000 for Repair, 12000 for FullService. The caller (`ServiceRecord.getLabourCharge()`) never checks which type it has |
| **Exception handling** | Errors are expected events | `exception/` package · thrown in `SparePart.removeStock()` · caught in `BasePanel.run()` and `FileRepository` |
| **Collections & generics** | Type-safe containers | `Repository<T extends Identifiable>`, `FileRepository<T>`, `LinkedHashMap`, `List`, `Optional`, streams in the services |
| **File handling** | Reading/writing files | `FileRepository` (`ObjectOutputStream`/`ObjectInputStream` -> `data/*.dat`) · `BillingService.writeBillFile()` (`BufferedWriter` -> `bills/*.txt`) |
| **Interfaces** | Contracts a class promises to keep | `Identifiable`, `Billable`, `Repository<T>`, `Refreshable` |
| **Packages** | Organise code by responsibility | `model`, `repository`, `service`, `exception`, `util`, `ui`, `ui.swing` |

## 60-second demo you can do live
1. Register a customer, then a vehicle for them (inheritance: Customer is a Person).
2. Create a job of type "Oil Change", then another of type "Repair" (polymorphism: different charges).
3. Add more parts than are in stock (exception handling -> friendly dialog).
4. Complete the job and generate a bill (file handling -> open `bills/B001.txt`).
5. Close and reopen the app: data is still there (file persistence through the generic repository).

## Questions examiners often ask
- *Why is `Person` abstract?* A plain "Person" is never a real thing in this system; it is always a Customer or a Mechanic.
- *Why use an interface (`Identifiable`) instead of a base class?* Vehicles, bills and parts are not people; an interface lets unrelated classes share one capability.
- *What is polymorphism here?* `calculateCharge()` is called on a `ServiceType` variable, and Java picks the right subclass version at run time.
- *Why custom exceptions?* They carry business meaning ("insufficient stock") and let the UI show a clear message.
