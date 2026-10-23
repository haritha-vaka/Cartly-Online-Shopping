# DSA used in Cartly

- **Custom singly linked list:** cart is made of `CartNode` nodes. Each node stores a product ID, quantity and `next` reference. Add/search/update/remove are linked-list operations.
- **Hashing (`HashMap<Integer, Product>`):** product ID lookup is average O(1), used by cart API methods.
- **Merge Sort:** custom recursive merge sort sorts prices ascending or descending in O(n log n) time and O(n) extra space.
- **Linear Search:** product search checks each product's name, category and description in O(n) time.

Flow: Browser HTML/CSS/JavaScript → Spring Boot Java REST API → HashMap catalogue + linked-list cart → JSON response.
