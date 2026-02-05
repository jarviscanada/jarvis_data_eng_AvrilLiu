# JavaScript & TypeScript Exercises

This repository contains a curated collection of **JavaScript and TypeScript practice exercises**, designed to build strong fundamentals in language features, data structures, problem-solving, and testing practices.

Each subfolder is a **self-contained mini-project**, with its own configuration, dependencies, and test setup. The exercises intentionally cover **different module systems (CommonJS vs ES Modules)** and **multiple test frameworks**, reflecting real-world JavaScript/TypeScript ecosystems.

---

## 📁 Repository Structure

Each folder below represents an independent exercise or example. You can navigate directly to any project using the links.

### 🔹 [doubly-linked-list](./doubly-linked-list)
TypeScript (**ES Modules**) implementation of a **doubly linked list** with an array-like interface.

Implemented methods:
- `push` – insert value at the back
- `pop` – remove value from the back and return it
- `shift` – remove value from the front and return it
- `unshift` – insert value at the front

**Tech stack**
- TypeScript (ESM)
- Vitest for testing

---

### 🔹 [sudoku](./sudoku)
TypeScript (**ES Modules**) implementation of a **Sudoku solver** using backtracking.  
The solver fills the board **in-place** while enforcing row, column, and 3×3 subgrid constraints.

**Highlights**
- Recursive backtracking
- Constraint validation
- In-place mutation (LeetCode-style API)

**Tech stack**
- TypeScript (ESM)
- Vitest for testing

---

### 🔹 [javascript-practice-one](./javascript-practice-one)
JavaScript (**CommonJS**) string-manipulation exercise that:
- Detects content type (HTML / CSS / TEXT)
- Extracts summary properties from input content

**Tech stack**
- JavaScript (CommonJS)
- Jest for testing

---

### 🔹 [typescript-conversion](./typescript-conversion)
TypeScript conversion of **javascript-practice-one**, focusing on:
- Static typing
- Type-safe refactoring
- Compatibility with existing CommonJS patterns

**Tech stack**
- TypeScript
- ts-jest for testing

---

### 🔹 [typescript-practice-one](./typescript-practice-one)
A collection of **15 TypeScript exercises** adapted from  
👉 https://typescript-exercises.github.io/

Focus areas include:
- Generics
- Advanced typing
- Interfaces and type inference

**Tech stack**
- TypeScript (ESM)
- Custom assertion library bundled with exercises

---

## ⚙️ Installation

### Prerequisites
- **Node.js** (recommended: LTS version)
- **npm** (bundled with Node.js)

Each exercise in this repository is an **independent project** with its own `package.json`.  
Dependencies should be installed **per exercise**, not at the repository root.

---

### Install Dependencies (Recommended Workflow)

1. Change into the exercise directory:
```bash
    cd JavaScript_TypeScript/<exercise-name>
```
2. Install dependencies:
```bash
    npm install
```
This ensures the correct test runner and development dependencies are available for that specific exercise.

Example: Install for Doubly Linked List
```bash
  cd JavaScript_TypeScript/doubly-linked-list
  npm install
```

## 🧪 Running Tests

All exercises in this repository include automated tests to validate correctness and expected behavior.

Because each subfolder is an **independent project**, tests must be run **from within the corresponding exercise directory**.

---

### Run Tests for an Exercise

1. Change into the exercise directory:
```bash
    cd JavaScript_TypeScript/<exercise-name>
```
2. Run the test suite:
```bash
    npm test
```

## 🎯 Learning Goals

This repository focuses on building **practical JavaScript and TypeScript fundamentals** through small, self-contained exercises.

Key areas of emphasis include:
- Core language features in JavaScript and TypeScript
- Data structures and algorithmic thinking (e.g. linked lists, backtracking)
- Working with different module systems (CommonJS vs ES Modules)
- Writing and running automated tests using industry-standard tools
- Understanding project structure, dependency management, and test isolation

Each exercise is designed to be minimal yet realistic, encouraging clarity, correctness, and maintainable code over premature optimization.
