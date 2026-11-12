# Vehicle Service Centre Management System
IIC 1153 – Object Oriented Programming – Group Assignment (University of Sri Jayewardenepura)

A desktop (Java Swing) application with an optional console mode, Java 17+, for a vehicle service centre. It replaces manual records for
customers, vehicles, service jobs, mechanics, spare parts, billing and appointments.

## Problem (from the startup interview)
Manual customer/vehicle records, hard-to-find service history, overlapping appointments, untracked
mechanic jobs, unmonitored parts stock, slow billing, and no easy service-history lookup.
> Replace the placeholder above with the real notes from YOUR interview (company name, who you met, date).

## Build & run
    javac -d out $(find src -name "*.java")      # Windows: dir /s /b src\*.java > files.txt && javac -d out @files.txt
    java -cp out vsc.Main              # desktop GUI
    java -cp out vsc.Main --console    # text menu version
In the GUI use File > Load demo data to try it quickly. Data is saved to `./data`, printable bills to `./bills`.

## Package structure
| Package | Purpose |
|---|---|
| `vsc.model` | Entities, interfaces (`Identifiable`, `Billable`), abstract classes, enums |
| `vsc.repository` | Generic `Repository<T>` interface + `FileRepository<T>` (file persistence) |
| `vsc.service` | Business logic, one service per module |
| `vsc.exception` | Custom checked exceptions |
| `vsc.util` | `Validator` |
| `vsc.ui.swing` | Desktop GUI: dashboard + one tab per module (`BasePanel` is the abstract parent of every tab) |
| `vsc.ui` | `ConsoleApp` text interface (kept as `--console`) |

## OOP checklist (for the viva)
| Requirement | Where |
|---|---|
| Encapsulation | private fields + getters/setters in all model classes; `Bill.addPayment` guards its own state |
| Abstraction | abstract `Person`, abstract `ServiceType` |
| Inheritance | `Customer`, `Mechanic` extend `Person`; `OilChange`, `Repair`, `FullService` extend `ServiceType` |
| Polymorphism | `ServiceType.calculateCharge()` overridden per type; `Person.getRole()`; `Billable` on `Bill` and `ServiceType` |
| Exception handling | `VscException` hierarchy, try-with-resources, `ConsoleApp.safely()` catches and reports |
| Collections & generics | `Repository<T extends Identifiable>`, `LinkedHashMap`, `List`, `Optional`, streams |
| File handling | object serialization in `FileRepository`; text bills via `BufferedWriter` in `BillingService` |
| Interfaces & packages | `Identifiable`, `Billable`, `Repository<T>`; 6 packages |

## Module map (assignment requirements)
Customer (register/update/search/history) · Vehicle (register, link to owner) · Service (create, complaint,
types, history) · Mechanic (register, assign, track status, view work) · Spare parts (add, restock, record use,
low-stock warning) · Billing (labour + parts, bill, payments, outstanding) · Appointments (book, upcoming,
reschedule/cancel, availability with 3 service bays).

## Git / GitHub workflow (marks for VCS)
- `main` = stable. Each member works on a feature branch: `feature/customer`, `feature/vehicle`, `feature/service-mechanic`, `feature/parts-billing`, `feature/appointments-ui`.
- Commit small and often, with messages like `Add duplicate phone check to CustomerService`.
- Merge through Pull Requests with at least one teammate review; keep the PR history visible.
- Suggested split (5 members): 1 Customer+Vehicle · 2 Service+Mechanic · 3 Parts+Billing · 4 Appointments+Validator · 5 Repository+UI+Docs/slides.
