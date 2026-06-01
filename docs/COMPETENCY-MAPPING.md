# Pemetaan Kompetensi — 35 Langkah FR.IA.02 → Lokasi di Project

Tabel ini menunjukkan **di mana** setiap langkah kerja dibuktikan di dalam repo, untuk
memudahkan demonstrasi lisan ke asesor. Path backend relatif ke `backend/src/main/java/com/library/api/`
kecuali disebut lain.

| # | Langkah | Bukti / Lokasi |
| --- | --- | --- |
| 1 | Metode pengembangan | Incremental — `README.md` §1 |
| 2 | Diagram (objek & komponen) | Use Case, Class, Component — `README.md` §2 |
| 3 | Penerapan model + IDE | Arsitektur berlapis — `README.md` §3; IDE IntelliJ IDEA + VS Code |
| 4 | Coding-guidelines & tangani galat | `exception/GlobalExceptionHandler.java` (+ `ResourceNotFoundException`, `BusinessRuleException`, `AccessDeniedException`) |
| 5 | Performansi & kemudahan interaksi | `repository/LibraryItemRepository.findAvailable()` (query, bukan ambil-semua), `@Index(idx_item_title)` di `entity/LibraryItem`, envelope seragam `dto/response/WebResponse` |
| 6 | Tipe data & kontrol program | `String`/`int`/`LocalDate`/`boolean`/`List` di `entity/*`; `if`/`for` di `service/impl/LoanServiceImpl` |
| 7 | Program baca-tulis + percabangan/pengulangan | Input via form React (`pages/LoginPage`, `LoanFormPage`, `CatalogPage`) ↔ REST; output tabel; `if/else` di `controller/CatalogController.list`, `for` di `LoanServiceImpl.record` |
| 8 | Prosedur & fungsi (+ keterangan) | `service/impl/LoanServiceImpl`: **fungsi** `calculateDueDate`, **prosedur** `record`; Javadoc tiap method |
| 9 | Array | `service/impl/CatalogServiceImpl.sortedTitles()` → `String[]` + `Arrays.sort`; endpoint `GET /books/titles` |
| 10 | Akses file (tulis & baca) | `LoanServiceImpl.exportReportCsv()` — `Files.newBufferedWriter` + `Files.readAllLines`; endpoint `GET /loans/report.csv` |
| 11 | Kompilasi | `cd backend && mvn clean compile` → BUILD SUCCESS |
| 12 | Class + hak akses (private/protected/public) | `entity/LibraryItem` — `id`/`availableCopies` **private**, `title`/`author` **protected**, method **public** |
| 13 | Tipe data & kontrol pada method/operasi class | method di `service/impl/*` & `entity/LibraryItem.borrowOne()` |
| 14 | Inheritance, polymorphism, overloading | `entity/LibraryItem → Book/Magazine` (inheritance + `describe()`/`itemType()` override = polymorphism); `service/CatalogService.search(...)` (overloading) |
| 15 | Interface & paket | `service/*Service` (interface), impl di `service/impl/`; struktur paket `com.library.api.{entity,repository,service,controller,dto,...}` |
| 16 | Kompilasi (bebas sintaks) | `mvn compile` + `npm run build` bersih |
| 17 | Reuse + lisensi/hak cipta | Tabel lisensi — `README.md` §8 |
| 18 | Integrasi library | `backend/pom.xml`, `frontend/package.json` |
| 19 | Pembaruan library | `mvn versions:display-dependency-updates` / `npm outdated` |
| 20 | Operasi basis data + indeks | `repository/LibraryItemRepository` (derived query + `@Query findAvailable`), `@Index` di `entity/LibraryItem` |
| 21 | Prosedur akses DB | Spring Data JPA — `repository/{LibraryItem,Member,Loan}Repository` |
| 22 | Koneksi DB + hak pengguna | `application.yml` (datasource PostgreSQL) + `@PreAuthorize` per-endpoint + enum `UserRole` |
| 23 | Uji program basis data | `test/.../repository/LibraryItemRepositoryTest` (`@DataJpaTest` + Testcontainers) |
| 24 | Identifikasi kode (modul, parameter, komentar) | Javadoc + struktur layered; komentar di seluruh `entity/`, `service/`, `controller/` |
| 25 | Dokumentasi modul | Javadoc kelas (`@author`, `@version`) — mis. `dto/response/WebResponse`, `constant/UserRole` |
| 26 | Dokumentasi fungsi/method (+ eksepsi) | Javadoc method (`@param`, `@return`, `@throws`) — mis. `service/LoanService`, `CatalogService` |
| 27 | Generate dokumentasi | `mvn javadoc:javadoc` → `target/site/apidocs/`; Swagger UI di `/swagger-ui.html` |
| 28 | Persiapan kode (identifikasi modul) | pemisahan modul controller/service/repository per domain |
| 29 | Debugging | breakpoint IntelliJ di `LoanServiceImpl.record`; Redux DevTools / React DevTools di frontend |
| 30 | Memperbaiki program | contoh: penolakan `@PreAuthorize` 500 → 403 (lihat `docs/TESTING.md` §4) |
| 31 | Kebutuhan uji | `docs/TESTING.md` §1 |
| 32 | Dokumentasi uji (skenario) | `docs/TESTING.md` §2 (TC01–TC13) |
| 33 | Data uji | `docs/TESTING.md` §2 (kolom "Data uji") |
| 34 | Prosedur uji | `docs/TESTING.md` §3 + berkas `*Test` / `*IntegrationTest` |
| 35 | Evaluasi hasil | `docs/TESTING.md` §4 (38 + 24 lulus) |

---

## Ringkasan bukti OOP (paling sering ditanya)

| Konsep OOP | Bukti |
| --- | --- |
| **Inheritance** | `Book extends LibraryItem`, `Magazine extends LibraryItem` |
| **Polymorphism** | `describe()` & `itemType()` abstrak, diimplementasi beda per subclass |
| **Overloading** | `CatalogService.search(title)` vs `search(title, type)` |
| **Encapsulation** | field `private`, stok hanya diubah via `borrowOne()`/`returnOne()` |
| **Abstraction / interface** | `LibraryItem` abstrak; `*Service` interface + `*ServiceImpl` |
| **Hak akses** | `private` (id, availableCopies), `protected` (title, author), `public` (method) |
