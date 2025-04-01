[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Jd7ILUgB)

# 🌐 JobMatcher

> JobMatcher es una aplicación web que conecta usuarios con empresas mediante un sistema de 'likes', publicaciones y reseñas. Cada usuario puede explorar compañías y  descubrir oportunidades. Ideal para mejorar la visibilidad empresarial y fomentar el contacto directo entre usuarios y organizaciones.

---

## 👥 Development Team

| Name                  | Email                            | GitHub Username      |
|-----------------------|----------------------------------|----------------------|
| Daniel Martín Muñoz   | d.martinm.2023@alumnos.urjc.es   | @dmartinm2023        |
| Carlos Marrón Benito  | c.marron.2023@alumnos.urjc.es    | @CarlosMarronBenito  |
| Hector Julián Alijas  | h.julian.2023@alumnos.urjc.es    | @h-julian            |
| Álvaro Mota Lucena    | a.mota.2023@alumnos.urjc.es      | @4lvaro18            |

---

## 📦 Main Features

### 📘 Entities

La aplicación gestiona las siguientes entidades principales, las cuales pueden ser creadas, editadas, consultadas y eliminadas:

- **Usuario**
- **Empresa**
- **Post**
- **Review**

Relaciones destacadas:

- Varios usuarios pueden dar 'like' a múltiples empresas (relación N:M).
- Un post puede tener varias reviews (1:N).
- Un usuario puede tener varios posts (1:N).
- Un usuario puede tener varias reviews (1:N).

### 🔐 User Permissions

| Tipo de Usuario  | Permisos                                                                                             |
|------------------|------------------------------------------------------------------------------------------------------|
| Usuario base     | Puede crear, editar y eliminar empresas (si es administrador), posts, reviews y gestionar su perfil. |

> *Nota:* En esta versión el administrador está simulado como un usuario base con capacidad para crear empresas.

### 🖼️ Images

Las siguientes entidades tienen imágenes asociadas:

- **Empresa**: Imagen de la empresa.
- **Usuario**: Foto de perfil.
- **Post**: Imagen destacada del post (opcional).

---

## 🗂️ Database Schema

