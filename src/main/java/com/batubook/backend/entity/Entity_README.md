# Entity README - Annotations in BatuBook Project

## 1️⃣ JPA and ORM Annotations

📌 **Purpose**: Define how entity classes behave as database tables.

- `@Entity` → Marks a class as a JPA entity.
- `@MappedSuperclass` → Defines a superclass that provides mapping information but is not an entity itself.
- `@Table(name = "...")` → Specifies the database table name.
- `@Id` → Marks the primary key field.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` → Automatically generates primary key values.
- `@Column(nullable = false, unique = true)` → Configures column constraints.
- `@JoinColumn(name = "user_id", nullable = false)` → Defines a foreign key column.

## 2️⃣ Relationship (Association) Annotations

📌 **Purpose**: Manage relationships between entities.

- `@OneToOne` → Defines a one-to-one relationship.
- `@OneToMany` → Defines a one-to-many relationship.
- `@ManyToOne` → Defines a many-to-one relationship.
- `@ManyToMany` → Defines a many-to-many relationship.
- `@Enumerated(EnumType.STRING)` → Stores enums as strings in the database.

## 3️⃣ Cascade and Data Management Annotations

📌 **Purpose**: Control how related data is managed.

- `cascade = CascadeType.PERSIST` → Saves related entities when the parent entity is saved.
- `cascade = CascadeType.MERGE` → Updates related entities when the parent is updated.
- `cascade = CascadeType.REMOVE` → Deletes related entities when the parent is deleted.
- `orphanRemoval = true` → Removes orphaned entities automatically.

## 4️⃣ Validation Annotations

📌 **Purpose**: Ensure that input data meets specific rules.

- `@NotNull` → Ensures a field is not null.
- `@NotBlank` → Ensures a string is not empty or only whitespace.
- `@Size(min, max)` → Limits the length of strings or collections.
- `@Pattern(regexp = "...")` → Enforces a regex format.
- `@Email` → Validates email format.
- `@Min(value = X)` → Ensures a number is at least X.
- `@Max(value = X)` → Ensures a number does not exceed X.
- `@DecimalMin(value = "X")` → Sets a minimum decimal value.
- `@Past` → Requires a past date.
- `@PastOrPresent` → Requires today or a past date.
- `@ValidAge` → Custom annotation to ensure a user is at least 18 years old.
- `@ValidBookInteraction` → Custom annotation to ensure a book cannot be liked unless it has been read.
- `@ValidRating` → Custom annotation to allow ratings only in predefined values (1, 1.5, 2, ..., 5).

## 5️⃣ Timestamp and Update Annotations

📌 **Purpose**: Manage creation and update timestamps.

- `@CreationTimestamp` → Automatically sets creation time.
- `@UpdateTimestamp` → Automatically sets the last update time.

## 6️⃣ Serialization and JSON Processing Annotations

📌 **Purpose**: Control JSON serialization behavior.

- `@JsonIgnore` → Excludes a field from JSON output.

## 7️⃣ Lombok Annotations

📌 **Purpose**: Reduce boilerplate code for getters, setters, and constructors.

- `@Getter` → Generates getters.
- `@Setter` → Generates setters.
- `@Data` → Includes `@Getter`, `@Setter`, `@ToString`, and `@EqualsAndHashCode`.
- `@EqualsAndHashCode(callSuper = true, exclude = {...})` → Generates `equals()` and `hashCode()` methods.
- `@Builder` → Implements the Builder pattern.
- `@NoArgsConstructor` → Generates a no-argument constructor.
- `@AllArgsConstructor` → Generates an all-argument constructor.

## 8️⃣ Custom Method Execution Annotations

📌 **Purpose**: Execute specific methods before database operations.

- `@PrePersist` → Runs before an entity is saved.
- `@PreUpdate` → Runs before an entity is updated.

## 9️⃣ Unique Constraints and Uniqueness Annotations

📌 **Purpose**: Ensure uniqueness in the database.

- `@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "book_id"})})` → Enforces unique constraints for a column combination.
- `@Column(unique = true)` → Ensures a column has unique values.

---

## Enum and Validation Classes Overview

### 1️⃣0️⃣ Enum Classes

- **`ActionType`**: Defines user actions (`REPOST`, `SAVE`).
- **`Gender`**: Supports `MALE`, `FEMALE`, and `UNDISCLOSED`, with logging and JSON transformations.
- **`Genre`**: Defines book genres like `NOVEL`, `SCIENCE_FICTION`, etc., with JSON support.
- **`MessageType`**: Specifies message types (`PERSONAL`, `BOOK`, `REVIEW`, `QUOTE`).
- **`Role`**: Defines user roles (`ADMIN`, `USER`), with logging and error handling.

### 1️⃣1️⃣ Validation Classes

- **`ValidAge`**: Ensures the user is at least 18 years old.
- **`ValidBookInteraction`**: Ensures a book cannot be liked unless read.
- **`ValidRating`**: Allows only predefined rating values (e.g., `1`, `1.5`, `2`, `2.5`, `3`, etc.).

📌 **Conclusion:**

### **Priority Order of Annotations:**
1️⃣ JPA & ORM → Defines database structure.
2️⃣ Relationship Annotations → Manages entity relationships.
3️⃣ Cascade Management → Controls related entity persistence.
4️⃣ Validation Annotations → Ensures input correctness.
5️⃣ Timestamps → Tracks data changes.
6️⃣ JSON Serialization → Manages API responses.
7️⃣ Lombok → Reduces boilerplate code.
8️⃣ Custom Execution Methods → Automates entity lifecycle actions.
9️⃣ Unique Constraints → Ensures database integrity.
