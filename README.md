# Library

Monorepo aplikasi perpustakaan — rebuild modern dari project lama
`warung-makan-bahari` (frontend) & `warung-makan-bahari-api` (backend).

Tahap awal ini berisi **fondasi** saja:

- **Backend** — autentikasi (Spring Boot, JWT access token + refresh token rotasi di Redis,
  akses berbasis peran).
- **Frontend** — manajemen state (Redux Toolkit: slice + thunk auth, axios dengan auto-refresh).

Fitur domain (katalog, anggota, peminjaman) menyusul di atas fondasi ini.

```
library/
├── backend/    # Spring Boot 3.5 · Java 21 · Spring Security · PostgreSQL · Redis
├── frontend/   # React 19 · TypeScript · Vite · Redux Toolkit · Tailwind + daisyUI
└── docker-compose.yml
```

## Menjalankan

```bash
docker compose up --build
# frontend → http://localhost:3000
# backend  → http://localhost:8080  (Swagger UI: /swagger-ui.html)
```

Akun admin awal di-seed otomatis: `admin / admin123` (ubah lewat env).
