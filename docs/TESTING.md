# Dokumentasi Pengujian

Dokumen ini memenuhi **Langkah 23 & 31–35** FR.IA.02: kebutuhan uji, dokumentasi uji (skenario),
data uji, prosedur uji, dan evaluasi hasil. Pengembangan memakai **TDD** — tes ditulis lebih dulu
(gagal), kode dibuat hingga lulus, lalu di-refactor.

---

## 1. Kebutuhan uji coba (Langkah 31)

- **Prosedur uji (SDLC):** uji unit per fungsi → uji repository (akses DB) → uji integrasi endpoint.
- **Tools uji:**
  | Lapis | Tools |
  | --- | --- |
  | Unit (logika service) | JUnit 5 + Mockito + AssertJ |
  | Akses basis data | `@DataJpaTest` + Testcontainers (PostgreSQL nyata) |
  | Integrasi endpoint + keamanan | `@SpringBootTest` + MockMvc + `spring-security-test` (`@WithMockUser`) + Testcontainers (PostgreSQL + Redis) |
  | Frontend | Vitest + Testing Library + jsdom |
- **Standar & kondisi uji:** setiap fungsi inti diuji minimal **satu kondisi normal** dan **satu
  kondisi error** (data tak ditemukan, stok habis, akses tanpa otorisasi).

---

## 2. Skenario & data uji (Langkah 32 & 33)

| ID | Skenario | Data uji | Harapan | Lokasi tes |
| --- | --- | --- | --- | --- |
| TC01 | Hitung tanggal kembali | pinjam `2025-06-01` | `2025-06-08` (+7 hari) | `LoanServiceImplTest` |
| TC02 | Catat peminjaman normal | anggota `M-001`, buku stok 3 | 201, stok → 2, due = +7 hari | `LoanControllerIntegrationTest` |
| TC03 | Stok habis | buku stok 0 | `409 Conflict` | `LoanServiceImplTest`, `LoanControllerIntegrationTest` |
| TC04 | Anggota tak ada | `memberId = "ghost"` | `404 Not Found` | `LoanServiceImplTest`, `LoanControllerIntegrationTest` |
| TC05 | Akses katalog tanpa login | tanpa token | `401 Unauthorized` | `CatalogControllerIntegrationTest` |
| TC06 | MEMBER menambah koleksi / mencatat pinjaman | role `MEMBER` | `403 Forbidden` | Catalog/Loan integration test |
| TC07 | Cari katalog (overload `search`) | `?title=clean` | hanya item judul cocok | `CatalogServiceImplTest`, `CatalogControllerIntegrationTest` |
| TC08 | Urutkan judul (array) | judul `Zebra`, `Clean Code` | terurut alfabet | `CatalogServiceImplTest` |
| TC09 | Registrasi anggota duplikat | `memberNumber` sudah ada | `409 Conflict` | `MemberServiceImplTest` |
| TC10 | Query DB `findAvailable` / `findByTitle…` | 1 buku stok 2, 1 majalah stok 0 | hanya yang stok > 0; cari case-insensitive | `LibraryItemRepositoryTest` |
| TC11 | Login benar / salah | `alice` + password benar/salah | 200 + token / `401` | `AuthControllerIntegrationTest`, `AuthServiceImplTest` |
| TC12 | Login (frontend) sukses/gagal | kredensial valid/invalid | state `authenticated` / pesan error | `authThunks.test.ts` |
| TC13 | Catat pinjaman (frontend) | API di-mock sukses/409 | loan tersimpan / pesan error global | `loansThunks.test.ts` |

---

## 3. Prosedur uji (Langkah 34)

### Backend

```bash
cd backend
mvn test                                   # seluruh tes (butuh Docker daemon)
mvn test -Dtest='*Test' -DfailIfNoTests=false   # hanya unit (tanpa Docker)
```

Pengguna **Colima** (bukan Docker Desktop) set dulu di shell:

```bash
export DOCKER_HOST="unix://$HOME/.colima/default/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
```

Berkas tes (`backend/src/test/java/com/library/api/`):

| Berkas | Jenis | Cakupan |
| --- | --- | --- |
| `service/impl/CatalogServiceImplTest` | unit | search overload, sortedTitles, addBook/addMagazine |
| `service/impl/MemberServiceImplTest` | unit | registrasi, duplikat, not-found |
| `service/impl/LoanServiceImplTest` | unit | due date, decrement stok, stok habis, anggota tak ada |
| `service/impl/{Auth,Jwt,RefreshToken}ServiceImplTest` | unit | autentikasi & token |
| `repository/LibraryItemRepositoryTest` | `@DataJpaTest` | query katalog di PostgreSQL nyata |
| `controller/CatalogControllerIntegrationTest` | integrasi | 401, 403, 201, cari |
| `controller/LoanControllerIntegrationTest` | integrasi | catat (stok turun), 409, 404, 403 |
| `controller/AuthControllerIntegrationTest` | integrasi | login, /me, refresh cookie |

### Frontend

```bash
cd frontend
npm test            # sekali jalan
npm run test:watch  # mode watch (TDD)
```

Berkas tes: `utils/role.test.ts`, `features/auth/{authSlice,authThunks}.test.ts`,
`features/ui/uiSlice.test.ts`, `features/catalog/{catalogSlice,catalogThunks}.test.ts`,
`features/members/membersThunks.test.ts`, `features/loans/loansThunks.test.ts`.

---

## 4. Evaluasi hasil (Langkah 35)

### Backend — `mvn test`

```
Tests run: 38, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Rincian: 22 unit (Catalog 5, Member 3, Loan 5, Auth 3, Jwt 3, RefreshToken 3) +
2 repository (`@DataJpaTest`) + 14 integrasi (Catalog 5, Loan 4, Auth 5).

### Frontend — `npm test`

```
Test Files  8 passed (8)
     Tests  24 passed (24)
```

### Catatan perbaikan selama siklus uji

- **Penolakan `@PreAuthorize` mengembalikan 500, bukan 403.** Penyebab: `AuthorizationDeniedException`
  dilempar saat invokasi controller sehingga ditangkap handler generik `@RestControllerAdvice`
  sebelum mekanisme 403 milik Spring Security. **Perbaikan:** tambah `@ExceptionHandler(AccessDeniedException.class)`
  → 403 di `GlobalExceptionHandler`. Tes TC06 (MEMBER) berubah dari merah ke hijau.
- **Lombok tidak terdeteksi** oleh maven-compiler-plugin 3.14.0 → didaftarkan di `<annotationProcessorPaths>`.
- **Testcontainers di Colima** butuh `DOCKER_HOST` + socket override; versi Docker API dipin ke `1.44`
  di konfigurasi Surefire.

**Kesimpulan:** seluruh 62 tes (38 backend + 24 frontend) lulus; semua skenario normal dan error
(TC01–TC13) terverifikasi.
