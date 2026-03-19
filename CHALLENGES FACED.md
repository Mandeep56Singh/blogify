**more dependency of one module to another**
Post module is highly dependent on category module and image upload module,
. Category module and Image Upload module is always communicating with blog post module.
So, a lot of coupling is here, and they belong to same bounded context.

To solve this, I thought of making them one module
**Blog Module**: it consists of posts, image upload, category.

**2. Managing relationship between blog and user(author)**
user has one to many relationship with blog post, blog post has many to one relationship with user.
now my user domain model is refering to blog post domain model, which is not possible, 
to solve this problem, I will be using some design pattern.
my goal is to make system less coupled.


## 3. Database State Leakage / Reducing Flakiness in Integration Tests
### The Challenge
Ensuring total test isolation while utilizing a high-performance, shared database environment (Testcontainers).

### Why it was a Challenge

- **Shared Global State:**  
  Using a "Singleton" Testcontainer pattern (starting the container once for the entire suite) meant all tests connected to the same PostgreSQL instance. Data persisted by one test (e.g., a registered email) remained in the DB, causing subsequent tests to fail with `UniqueConstraintViolation`.

- **The Transaction Trap:**  
  Standard `@Transactional` rollbacks were sometimes insufficient, especially if service logic used `Propagation.REQUIRES_NEW`, which commits data regardless of the test’s rollback status.

- **The Flakiness Factor:**  
  Hardcoded constants for unique fields (like `test@email.com`) made tests brittle and prone to failure if run in a different order or in parallel.

### Resolution

I implemented a multi-layered isolation strategy to create an "industrial-strength" test suite:

- **The "Reset Button" (Truncation):**  
  I added a `@BeforeEach` hook in the `BaseIntegrationTest` using `JdbcTemplate` to execute `TRUNCATE TABLE ... CASCADE`. This physically wipes the database state before every single test method.

- **Dynamic Data Pattern:**  
  I moved away from static constants for unique fields. By using a Factory Pattern to generate randomized data (e.g., UUID suffixes for emails), I ensured that even if a rollback fails, no two tests ever collide.

- **Singleton Test Container:**  
  I maintained the singleton container pattern to keep execution fast (avoiding the 10–15 second startup cost per class) while ensuring the internal database state is fresh for every test.