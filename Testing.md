# API Testing Checklist

Dokumen ini adalah checklist pengujian end-to-end untuk seluruh API yang tersedia. Fokus utama adalah membuktikan alur bisnis berhasil dan setiap perubahan dapat dibaca kembali melalui API terkait.

## Persiapan

- Jalankan PostgreSQL utama, PostgreSQL audit, dan Kafka dari `docker compose up -d`.
- Jalankan aplikasi pada `http://localhost:8080`.
- Siapkan akun `ADMIN` dan `HR` yang sudah ada di database. Gunakan email employee yang unik pada setiap pengujian, misalnya `employee.test+<timestamp>@example.com`.
- Simpan token hasil login sebagai `ADMIN_TOKEN`, `HR_TOKEN`, dan `EMPLOYEE_TOKEN`. Setiap endpoint selain `POST /auth/login` memakai header `Authorization: Bearer <token>`.
- Simpan ID hasil pembuatan sebagai `DEPARTMENT_ID`, `EMPLOYEE_ID`, dan `NOTIFICATION_ID` untuk langkah berikutnya.

## Alur utama: HR mendaftarkan employee sampai audit log tercatat

| No. | API dan pelaku | Request / aksi | Hasil yang diharapkan | Verifikasi data |
| --- | --- | --- | --- | --- |
| 1 | `POST /auth/login` — HR | Kirim email dan password akun HR. | `200 OK`, respons berisi `accessToken` dan `tokenType: Bearer`. | Panggil `GET /auth/me` dengan `HR_TOKEN`; email dan role harus milik HR. |
| 2 | `POST /departments` — ADMIN | Buat department baru dengan `name` dan `description`. | `201 Created`, respons berisi ID dan data department. | `GET /departments/{DEPARTMENT_ID}` mengembalikan nama dan deskripsi yang sama. |
| 3 | `POST /employees` — HR | Daftarkan employee dengan `employeeNumber`, `firstName`, `lastName`, `email`, `password`, `departmentId`, serta data opsional lain. | **Requirement:** `201 Created`, respons berisi employee dan user yang terhubung. | Catat `EMPLOYEE_ID`; hasil respons harus memakai `departmentId` yang dibuat pada langkah 2. |
| 4 | `POST /auth/login` — employee | Login memakai email dan password dari langkah 3. | `200 OK`, respons berisi token employee. | `GET /auth/me` dengan `EMPLOYEE_TOKEN` mengembalikan email employee dan role `EMPLOYEE`. |
| 5 | `POST /attendance/check-in` — employee | Kirim request tanpa body dengan `EMPLOYEE_TOKEN`. | `200 OK`, respons memiliki `employeeId`, tanggal Jakarta hari ini, `checkIn` terisi, dan `checkOut` kosong. | `GET /attendance/me` harus memuat record yang sama. |
| 6 | `POST /attendance/check-out` — employee | Kirim request tanpa body dengan `EMPLOYEE_TOKEN`. | `200 OK`, record yang sama mempunyai `checkOut` terisi. | `GET /attendance/me?from=<hari-ini>&to=<hari-ini>` mengembalikan satu record dengan `checkIn` dan `checkOut`. |
| 7 | `GET /attendance` — HR | Ambil history attendance dengan `HR_TOKEN`, opsional filter `from` dan `to`. | `200 OK`, daftar memuat attendance employee dengan nomor, nama, department, posisi, tanggal, check-in, dan check-out. | Temukan record employee dari langkah 5 dan pastikan waktu check-in/check-out sama. |
| 8 | `PATCH /employees/me` — employee | Ubah `phone` dan/atau `photoUrl` memakai `EMPLOYEE_TOKEN`. | `200 OK`, respons mengembalikan nilai profil baru. | Nilai `phone`/`photoUrl` pada respons harus sama dengan request. Mengirim nilai yang sama tidak boleh membuat event baru. |
| 9 | Kafka dan audit database | Tunggu consumer memproses event `EMPLOYEE_PROFILE_UPDATED` dari langkah 8. | Event memuat UUID `eventId`, `userId`, `employeeId`, `changedFields`, dan timestamp. | Query database audit: `select * from audit_logs where employee_id = <EMPLOYEE_ID> order by created_at desc;`. Baris terbaru harus berisi payload JSONB dan `changedFields` sesuai field yang diubah. |
| 10 | `GET /notifications` — HR/ADMIN | Ambil notifikasi dengan `HR_TOKEN` atau `ADMIN_TOKEN`. | `200 OK`, terdapat notifikasi bertipe `EMPLOYEE_PROFILE_UPDATED`, judul `Employee Profile Updated`, dan nama employee pada pesan. | Simpan `NOTIFICATION_ID`; notifikasi baru harus `isRead: false`. |
| 11 | `PATCH /notifications/{NOTIFICATION_ID}/read` — penerima | Tandai notifikasi dari langkah 10 sebagai dibaca. | `200 OK`, respons memiliki `isRead: true`. | `GET /notifications` masih memuat ID yang sama dengan `isRead: true`. |
| 12 | `PATCH /notifications/read-all` — penerima | Tandai seluruh notifikasi milik user sebagai dibaca. | `200 OK` dan respons `true`. | `GET /notifications` tidak memiliki notifikasi dengan `isRead: false` untuk user tersebut. |

> Catatan implementasi saat ini: `POST /employees` dibatasi ke role `ADMIN` pada `SecurityConfig`. Karena requirement alur di atas meminta HR berhasil mendaftarkan employee, langkah 3 saat ini akan menghasilkan `403 Forbidden` untuk HR. Jalankan juga langkah yang sama dengan `ADMIN_TOKEN` untuk memvalidasi fungsi pembuatan employee sampai aturan role tersebut diperbaiki.

## Checklist API per modul

### Auth

- [ ] `POST /auth/login` menerima kredensial valid dan mengembalikan token Bearer.
- [ ] `GET /auth/me` dengan token valid menampilkan ID, email, dan role user yang sedang login.

### Department

- [ ] `GET /departments` menampilkan department yang baru dibuat.
- [ ] `GET /departments/{id}` mengembalikan detail department yang benar.
- [ ] `POST /departments` oleh ADMIN membuat department dan mengembalikan `201 Created`.
- [ ] `PATCH /departments/{id}` oleh ADMIN mengembalikan nama/deskripsi terbaru; `GET /departments/{id}` menampilkan perubahan yang sama.
- [ ] `DELETE /departments/{id}` oleh ADMIN mengembalikan `204 No Content`; `GET /departments/{id}` setelahnya tidak menemukan resource tersebut.

### Employee dan profile update

- [ ] `POST /employees` membuat user dan employee yang terhubung, dengan `isActive: true` dan department yang benar.
- [ ] `PATCH /employees/me` hanya mengubah `phone` dan `photoUrl` milik employee yang sedang login.
- [ ] Perubahan profile membuat satu notifikasi untuk setiap ADMIN/HR dan satu event audit dengan `changedFields` yang akurat.
- [ ] Update tanpa perubahan nilai mengembalikan profil saat ini tanpa notifikasi atau event audit baru.

### Attendance

- [ ] `POST /attendance/check-in` membuat satu record untuk tanggal bisnis `Asia/Jakarta` dengan `checkIn` terisi.
- [ ] `POST /attendance/check-out` memperbarui record hari yang sama dan mengisi `checkOut`.
- [ ] `GET /attendance/me` menampilkan history user login urut tanggal terbaru; filter `from` dan `to` membatasi hasil secara inklusif.
- [ ] `GET /attendance` oleh HR/ADMIN menampilkan attendance seluruh employee beserta data employee ringkas.

### Notification

- [ ] `GET /notifications` hanya menampilkan notifikasi milik user login dan urut terbaru.
- [ ] `PATCH /notifications/{id}/read` mengubah status notifikasi milik penerima menjadi read dan mengembalikan data terbaru.
- [ ] `PATCH /notifications/read-all` hanya mengubah notifikasi unread milik user login dan mengembalikan `true`.

### Audit log dan Kafka

- [ ] Kafka berjalan dan topic `employee-audit-events` menerima event saat profile employee berubah.
- [ ] Consumer menyimpan event ke tabel `audit_logs` di database audit dengan `payload` JSONB.
- [ ] Mengirim ulang event dengan `eventId` yang sama tidak menambah baris audit kedua.
- [ ] Audit log menyimpan `event_type`, `user_id`, `employee_id`, payload, dan `created_at` yang benar.
