# Nexus Ship Logistics API 🚀

Nexus Ship Logistics API is an enterprise-grade supply chain and fleet logistics backend system engineered with **Spring Boot 3** and **PostgreSQL/PostGIS**. It is designed to manage multi-tenant shipping operations, automated trip dispatching, geospatial routing, and precise vehicle capacity constraints.

Instead of following generic e-commerce CRUD patterns, this project focuses heavily on complex data modeling, relational integrity, spatial tracking, and algorithmic resource optimization.

---

## 🏗️ Core Features & Business Logic

### 1. Advanced Automated Trip Orchestration Engine
* **Dynamic Capacity Matching:** An algorithmic clustering approach that groups shipments based on matching spatial markers (Governorate and City/District).
* **Physical Constraint Enforcement:** Automatically filters and loads shipments into a vehicle only if they fit within the vehicle's remaining strict capacity thresholds (`availableWeight` and `availableVolume`).
* **Volumetric Math Protection:** Features embedded guardrails preventing double-allocation and mitigating floating-point precision leaks during transit cancellation or shipment returns.

### 2. Geospatial Processing (PostGIS & JTS Integration)
* **Real Spatial Analytics:** Utilizes **Java Topology Suite (JTS)** and **PostGIS** extensions instead of flat desktop coordinates. 
* **Location Geometry:** Pickup and destination points are handled using true spatial coordinates (`geometry(Point, 4326)`) to lay the groundwork for real-world distance and routing matrices.

### 3. Comprehensive Shipment Lifecycle & State Machine
Tracks the precise chronological flow of goods across multiple status states:
`PENDING` ➡️ `ASSIGNED` ➡️ `OUT_FOR_DELIVERY` ➡️ `ARRIVED_AT_HUB` ➡️ `DELIVERED` / `RETURNED_TO_SENDER`.

### 4. Strict Enterprise Data Validation & Sanitization
Every request DTO maps exactly to strict Egyptian governmental and regional structural constraints via Jakarta Validation:
* **National ID & Licensing:** Driver licenses and National IDs are strictly validated to be exactly `14` digits of pure numeric data.
* **License Plate Formats:** Vehicle plates strictly enforce the Egyptian structural format (3 Arabic letters followed by 3 or 4 digits) using robust regular expressions.

---

## 📊 Database Architecture & Domain Model

The relational schema is highly normalized to enforce data security and domain boundaries:

* **Polymorphic Identity Layer:** Employs the **JPA Joined Inheritance Strategy** (`InheritanceType.JOINED`) across the user domain. A unified `users` table handles fundamental credentials, which seamlessly branches into specialized sub-tables: `admin_user`, `driver`, and `sender`.
* **Auditable State Tracking:** A dedicated `shipment_history` table maintains a continuous append-only log of every transition, binding the exact timestamp and logging which internal actor (Admin or Driver) authorized the modification.
* **Soft Deletion & Isolation:** Uses Hibernate's `@SQLRestriction` to safely handle records, guaranteeing that soft-deleted entities are isolated cleanly from active business operations.

---

## 🛠️ Technology Stack

* **Framework:** Spring Boot 3.x (Spring Data JPA, Spring Web)
* **Database:** PostgreSQL (15+) + PostGIS Spatial Extension
* **Geospatial Library:** org.locationtech.jts (Java Topology Suite)
* **Boilerplate Reduction:** Project Lombok
* **Validation:** Jakarta Validation API (Hibernate Validator)

---

## 🔌 Core API Endpoints Preview

### Trip Orchestration (`/api/v1/trips`)
* `POST ?status=PENDING` - Triggers the Inbound Hub Collection algorithm to aggregate shipments from a city and generate a trip sheet.
* `POST ?status=ARRIVED_AT_HUB` - Triggers the Outbound Delivery algorithm to dispatch sorting-hub goods to destination vectors.
* `PUT /{id}/start` - Transitions the trip to active status and updates the fleet tracking state.
* `PUT /{id}/end-pickup` - Completes a pickup run, unloading freight safely into the sorting hub.
* `PUT /{id}/end-delivery` - Ends a delivery leg, transitioning all payloads to the final destination.
* `PUT /{id}/cancel` - Drops a trip before transit begins, resetting payload states and restoring fleet capacity immediately.

---


2. **Database Setup:** Create a database named `nexus_ship` and execute:
   ```sql
   CREATE EXTENSION IF NOT EXISTS postgis;
