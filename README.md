[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Jd7ILUgB)

# 🌐 JobMatcher

> JobMatcher is a web application that connects users with companies through a system of likes, posts, and reviews. Each user can explore companies and discover opportunities. It is designed to boost business visibility and encourage direct engagement between users and organizations.

---

## 📘 Entities

The application manages the following main entities, which can be created, edited, viewed, and deleted:

- **User**
- **Company**
- **Post**
- **Review**

---

### 🔄 Key Relationships

- **User** ⇄ **Company** through 'likes' → N:M relationship  
- **User** → creates multiple **Posts** → 1:N relationship  
- **User** → writes multiple **Reviews** → 1:N relationship  
- **Post** → can have multiple **Reviews** → 1:N relationship

---

### 🔐 User Permissions

| User Type   | Permissions                                                                                         |
|-------------|-----------------------------------------------------------------------------------------------------|
| Basic User  | Can create, edit, and delete companies (if acting as admin), posts, reviews, and manage their profile. |

> *Note:* In this phase, the admin is simulated as a basic user with company creation privileges.

---

### 🖼️ Images

The following entities include associated images:

- **User**: Profile picture  
- **Post**: Main post image  

---

### 🗂️ Database Schema

![Database diagram](db-mysql/db-diagram.png)

This diagram illustrates the main entities of the application and their relationships, including a many-to-many association between users and companies through likes.


---

## 👥 Development Team

| Name                 | University Email                    | GitHub Username      |
|----------------------|-------------------------------------|----------------------|
| Daniel Martín Muñoz  | d.martinm.2023@alumnos.urjc.es      | @dmartinm2023        |
| Carlos Marrón Benito | c.marron.2023@alumnos.urjc.es       | @CarlosMarronBenito  |
| Hector Julián Alijas | h.julian.2023@alumnos.urjc.es       | @h-julian            |
| Álvaro Mota Lucena   | a.mota.2023@alumnos.urjc.es         | @4lvaro18            |

---

### 👤 Daniel Martín Muñoz

#### Tasks completed:
- [Short and clear bullet point describing a task]
- [Another one, preferably technical or relevant]
- [Can include frontend, backend, database, logic, etc.]

#### Top 5 commits:
1. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
2. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
3. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
4. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
5. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])

#### Top 5 files contributed to:
- `src/main/java/com/example/controller/[FileName].java`
- `src/main/java/com/example/service/[FileName].java`
- `src/main/resources/templates/[FileName].html`
- `src/main/resources/static/js/[FileName].js`
- `src/main/java/com/example/repository/[FileName].java`


### 👤 Carlos Marrón Benito

#### Tasks completed:
- [Short and clear bullet point describing a task]
- [Another one, preferably technical or relevant]
- [Can include frontend, backend, database, logic, etc.]

#### Top 5 commits:
1. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
2. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
3. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
4. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
5. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])

#### Top 5 files contributed to:
- `src/main/java/com/example/controller/[FileName].java`
- `src/main/java/com/example/service/[FileName].java`
- `src/main/resources/templates/[FileName].html`
- `src/main/resources/static/js/[FileName].js`
- `src/main/java/com/example/repository/[FileName].java`


### 👤 Héctor Julián Alijas

#### Tasks completed:
- [Short and clear bullet point describing a task]
- [Another one, preferably technical or relevant]
- [Can include frontend, backend, database, logic, etc.]

#### Top 5 commits:
1. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
2. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
3. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
4. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
5. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])

#### Top 5 files contributed to:
- `src/main/java/com/example/controller/[FileName].java`
- `src/main/java/com/example/service/[FileName].java`
- `src/main/resources/templates/[FileName].html`
- `src/main/resources/static/js/[FileName].js`
- `src/main/java/com/example/repository/[FileName].java`


### 👤 Álvaro Mota Lucena

#### Tasks completed:
- [Short and clear bullet point describing a task]
- [Another one, preferably technical or relevant]
- [Can include frontend, backend, database, logic, etc.]

#### Top 5 commits:
1. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
2. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
3. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
4. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])
5. [`[Short description]`](https://gitdiagram.com/DWS-2025/project-grupo-18/commit/[commit_hash])

#### Top 5 files contributed to:
- `src/main/java/com/example/controller/[FileName].java`
- `src/main/java/com/example/service/[FileName].java`
- `src/main/resources/templates/[FileName].html`
- `src/main/resources/static/js/[FileName].js`
- `src/main/java/com/example/repository/[FileName].java`

---

## 📬 Postman Collection

You can test the API using the included Postman collection:  
[`api.postman_collection.json`](./api.postman_collection.json)

This file contains sample requests for:
- Creating, editing, deleting and retrieving users, posts, reviews, companies
- Pagination and dynamic filtering
