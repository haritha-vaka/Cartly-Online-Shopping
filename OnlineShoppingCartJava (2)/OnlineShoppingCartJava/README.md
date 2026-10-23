# Cartly Online Shopping Cart — Java + DSA

## Requirements
- JDK 17 or newer
- Apache Maven
- VS Code with Extension Pack for Java (recommended)

## Run it
1. Extract the ZIP file.
2. Open the `OnlineShoppingCartJava` folder in VS Code.
3. In VS Code, choose **Terminal → New Terminal**.
4. Check Java is installed: `java -version`
5. Run the project: `mvn spring-boot:run`
6. Open **http://localhost:8080** in Chrome or Edge.

If `mvn` is not recognized, install Apache Maven and add its `bin` folder to PATH, then restart VS Code.

## DSA requirements
See `DSA_EXPLANATION.md` and the comments in `OnlineShoppingCartApplication.java`.
- Linked List: custom cart node structure
- Hashing: `HashMap` product lookup
- Sorting: custom Merge Sort by price
- Searching: Linear Search by text

The checkout is only a demo. There is no real payment processing, and the in-memory cart resets when the server restarts.

## Easiest Windows launch
After installing JDK 17+ and Maven, double-click `run.bat`. It starts the Spring Boot backend in a terminal and opens `http://localhost:8080` in your default browser. If Chrome is not your default browser, copy that address into Chrome.
