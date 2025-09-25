# JSON to CSV Transformation Algorithm

This document explains the **JSON → CSV transformation algorithm** in simple terms, designed for non-technical stakeholders.

---

## 🧩 What does the algorithm do?
The algorithm takes **complex JSON data** (a digital format that stores information in a nested structure) and transforms it into a **CSV table** (rows and columns, like an Excel sheet).

👉 In other words, it turns **complicated, tree-like data** into something that looks like a **spreadsheet**, making it easier to read, analyze, and share.

---

## 🔑 Why is this important?
- **JSON** is often used by apps, websites, and databases to store information.  
- But JSON is **hard to read** when it has layers inside layers (e.g., people with multiple addresses, projects with many tasks).  
- Many stakeholders prefer **tables (CSV/Excel)** because they can quickly filter, sort, and analyze.  
- This algorithm bridges that gap: it **flattens the data** into a structured table.

---

## ⚙️ How does it work?
The algorithm looks at each part of the JSON and decides:

1. **Objects (like folders with labeled data):**  
   → Flatten them by keeping their labels and attaching them as “columns.”  
   ```json
   { "name": "Alice", "city": "Monterrey" }
   ```
   **Becomes →**  

   | name  | city       |
   |-------|------------|
   | Alice | Monterrey  |

---

2. **Arrays (lists of items):**  
   → Each item becomes a **new row in the table**.  
   ```json
   { "hobbies": ["reading", "cycling"] }
   ```
   **Becomes →**  

   | hobbies |
   |---------|
   | reading |
   | cycling |

---

3. **Scalars (simple values like numbers, words, or yes/no):**  
   → Just written directly into the table.  
   ```json
   { "age": 25 }
   ```
   **Becomes →**  

   | age |
   |-----|
   | 25  |

---

4. **Nulls (empty values):**  
   → Left blank in the table.

---

## 📊 Use Cases & Scenarios

### Case 1: Simple Objects (no arrays)
Input:
```json
{ "id": 1, "name": "Alice" }
```
Output:

| id | name  |
|----|-------|
| 1  | Alice |

---

### Case 2: Objects with Arrays of Simple Values
Input:
```json
{ "id": 2, "hobbies": ["reading", "cycling", "gaming"] }
```
Output:

| id | hobbies |
|----|---------|
| 2  | reading |
|    | cycling |
|    | gaming  |

---

### Case 3: Objects with Arrays of Objects
Input:
```json
{ 
  "id": 3,
  "projects": [
    { "title": "Bridge", "status": "ongoing" },
    { "title": "Park", "status": "completed" }
  ]
}
```
Output:

| id | projects__title | projects__status |
|----|-----------------|------------------|
| 3  | Bridge          | ongoing          |
|    | Park            | completed        |

---

### Case 4: Mix of Scalars, Arrays, and Objects
Input:
```json
{
  "name": "Carlos",
  "skills": ["Python", "Java"],
  "contact": { "email": "carlos@mail.com", "phone": "123456" }
}
```
Output:

| name   | skills | contact__email     | contact__phone |
|--------|--------|--------------------|----------------|
| Carlos | Python | carlos@mail.com    | 123456         |
|        | Java   |                    |                |

---

## ✅ Key Takeaways
- **Objects → Columns** (like attributes of a person or project).  
- **Arrays → Multiple Rows** (like lists of hobbies or projects).  
- **Scalars → Simple Values** (just numbers or words in the right place).  
- **Nulls → Empty Cells** (blank in the table).  

This ensures **all data, no matter how complex, becomes a clean table** stakeholders can open in Excel or share with others.

---
