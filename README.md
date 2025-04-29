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

| User Type   | Permissions                                                                                |
|-------------|--------------------------------------------------------------------------------------------|
| GUEST       | Can navigate through the matches and blog pages.                                           |
| USER        | Can create and edit their own posts, give likes and manage their profile.                  |
| ADMIN       | Can create, edit, and delete companies, users, posts, reviews, and manage their profile.   |

---

### 🧑‍💻 Example Users for Testing

The following users are available for testing purposes:

| Username         | Email                    | Password    | Roles        |
|------------------|--------------------------|-------------|--------------|
| Admin User       | admin@example.com        | password123 | USER, ADMIN  |
| Test User 1      | testuser1@example.com    | password123 | USER         |
| Test User 2      | testuser2@example.com    | password123 | USER         |
| Test User 3      | testuser3@example.com    | password123 | USER         |
| Test User 4      | testuser4@example.com    | password123 | USER         |
| ...              | ...                      | ...         | ...          |
| Test User 20     | testuser20@example.com   | password123 | USER         |


> 🔒 All passwords are set to `password123` for testing convenience.

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

#### DISCLAIMER #####

Most of the commits appear to be done by dmartinm2023, due to the file exchange between web and REST controllers, specially in the top 5 files contributed section. When we wrote this, we selected the files that we felt that were the most significative for each member of the team, not only based in Github but in the time we spent developing the app.

### 👤 Daniel Martín Muñoz

#### Tasks completed:
- DTOs, mappers and REST Controllers created (not all of them finished).
- Special focus on companies and profile section.
- AJAX pagination.

#### Top 5 commits:
1. [`Most of the API operations were fixed and also some web issues`](https://github.com/DWS-2025/project-grupo-18/commit/845a2550b71c6af7eb50795eb381f1bf18d4cff3)
2. [`AJAX pagination totally functional`](https://github.com/DWS-2025/project-grupo-18/commit/d859329db5e5bb68a8dafaff4cdafedd6ddbf435)
3. [`Separation between web and REST controllers + CompanyRestController created`](https://github.com/DWS-2025/project-grupo-18/commit/056adf497d0e66d35a45f2f02c846b3eab585e48)
4. [`DTOs created`](https://github.com/DWS-2025/project-grupo-18/commit/9bff96ac12b3d3eb0899f8be32c0181b6623c477)
5. [`Mappers created + dependencies`](https://github.com/DWS-2025/project-grupo-18/commit/704d9692a074bd2b11f41ecb6cfe58f466ec9937)

#### Top 5 files contributed to:
- `src\main\java\es\grupo18\jobmatcher\controller\web\CompaniesController.java`
- `src\main\java\es\grupo18\jobmatcher\controller\rest\CompanyRestController.java`
- `src\main\java\es\grupo18\jobmatcher\service\CompanyService.java`
- `src\main\resources\static\js\companiesAjax.js`
- `src\main\java\es\grupo18\jobmatcher\controller\web\ProfileController.java`

### 👤 Carlos Marrón Benito

#### Tasks completed:
- Bug fixing.
- Special focus on matches and users.
- UserRestController created.

#### Top 5 commits:
1. [`Match system fixed`](https://github.com/DWS-2025/project-grupo-18/commit/db3e44a0df82a85e5bbe1733aa260f1fca8f6a51)
2. [`Company visualization in match consulting section`](https://github.com/DWS-2025/project-grupo-18/commit/09bc7c891cb837b1280d60ea29151f4e0a51d4c1)
3. [`Review visualization when SQL database is enabled`](https://github.com/DWS-2025/project-grupo-18/commit/5fd9938a8642c9add9ee490d87c17061815ef2f7)
4. [`Match consulting page fixed`](https://github.com/DWS-2025/project-grupo-18/commit/09bc7c891cb837b1280d60ea29151f4e0a51d4c1)
5. [`REST controllers fixed and README.md creation`](https://github.com/DWS-2025/project-grupo-18/commit/ba73988850868c30aa6a321f2118e2f8f4b010fb)

#### Top 5 files contributed to:
- `src\main\java\es\grupo18\jobmatcher\controller\rest\UserRestController.java`
- `src\main\java\es\grupo18\jobmatcher\controller\rest\ImageRestController.java`
- `src\main\java\es\grupo18\jobmatcher\controller\rest\PostRestController.java`
- `src\main\java\es\grupo18\jobmatcher\controller\web\MatchController.java`
- `src\main\java\es\grupo18\jobmatcher\service\UserService.java`


### 👤 Héctor Julián Alijas

#### Tasks completed:
- Major bug fixes.
- Special focus on all of the blog page.
- Database created and configured.
- Separation between MySQL and H2 databases in application.properties.

#### Top 5 commits:
1. [`Blog posts working perfectly with creation, editing, deletion and filtering`](https://github.com/DWS-2025/project-grupo-18/commit/f71220048f0b8b59962ba10db20039e98d8a2b66)
2. [`The database is fully functional with images included.`](https://github.com/DWS-2025/project-grupo-18/commit/5777f2c44d3b44e82f063f00f66bde5046f71ed5)
3. [`H2 & MySQL profile created in application.properties`](https://github.com/DWS-2025/project-grupo-18/commit/ed39cb7ceb67f84091627d428431d4c29678a6a5)
4. [`Major bug fixing`](https://github.com/DWS-2025/project-grupo-18/commit/a65c3a0dfb9490e8c7a33ba9d2d8b41d6b056d1a)
5. [`Early blog creation`](https://github.com/DWS-2025/project-grupo-18/commit/f71220048f0b8b59962ba10db20039e98d8a2b66)

#### Top 5 files contributed to:
- `src\main\java\es\grupo18\jobmatcher\controller\web\BlogController.java`
- `src\main\java\es\grupo18\jobmatcher\service\PostService.java`
- `src\main\resources\application-h2.properties`
- `src\main\resources\application-mysql.properties`
- `db-mysql\run-reset-db-full.bat`


### 👤 Álvaro Mota Lucena

#### Tasks completed:
- Review entity creation.
- All reviews operations implemented.
- Front-end formating.
- Postman collection created. It does not appear as his commit but it was created by him and uploaded by someone else.

#### Top 5 commits:
1. [`REST controller for reviews`](https://github.com/DWS-2025/project-grupo-18/commit/1b7d814500c144958ba11f4076f8f996f2d06dd5)
2. [`Query and operation of reviews, operational and completed.`](https://github.com/DWS-2025/project-grupo-18/commit/5f8183399de2a848e59ddfbad41f5505b4021146)
3. [`Reviews integrated into each post and known issues resolved`](https://github.com/DWS-2025/project-grupo-18/commit/5d9f261b778ce459e13d2cd1f4c6556d9ead5115)
4. [`Postman uploaded. Shared commit`](https://github.com/DWS-2025/project-grupo-18/commit/845a2550b71c6af7eb50795eb381f1bf18d4cff3)
5. [`Posts updated and new showposts.html`](https://github.com/DWS-2025/project-grupo-18/commit/da40b8885586490b4e98f0df50a2bf19f86b0a46)

#### Top 5 files contributed to:
- `src\main\java\es\grupo18\jobmatcher\controller\web\BlogController.java`
- `src\main\java\es\grupo18\jobmatcher\model\Review.java`
- `src\main\java\es\grupo18\jobmatcher\service\ReviewService.java`
- `src\main\resources\static\json\api.postman_collection.json`
- `src\main\java\es\grupo18\jobmatcher\controller\rest\ReviewRestController.java`

---

## 📬 Postman Collection

You can test the API using the included Postman collection included in path: src\main\resources\static\json\api.postman_collection.json:  
[`api.postman_collection.json`]

This file contains sample requests for:
- Creating, editing, deleting and retrieving users, posts, reviews and companies.
- Pagination and dynamic filtering.