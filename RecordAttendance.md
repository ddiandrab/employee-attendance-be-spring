# Implement attendance sesuai controller NestJS

## Ringkasan

Implementasikan check-in, check-out, history pribadi, dan daftar attendance untuk HR/ADMIN pada repository `employee-attendance-be-spring`, mengikuti perilaku proyek NestJS.

Referensi implementasi: `employee-attendance-be-nestjs/src/attendance`.

## Requirement

- `POST /attendance/check-in`: mencatat waktu masuk untuk employee milik user yang login. Tolak jika profil employee tidak ditemukan atau sudah check-in pada hari tersebut.
- `POST /attendance/check-out`: mencatat waktu keluar pada attendance hari ini. Tolak jika profil employee tidak ditemukan, belum check-in, atau sudah check-out.
- `GET /attendance/me`: menampilkan history attendance milik user yang login, terbaru terlebih dahulu.
- `GET /attendance`: menampilkan attendance seluruh employee beserta informasi employee sebagaimana respons NestJS, terbaru terlebih dahulu.
- Semua endpoint memerlukan JWT. Check-in, check-out, dan history pribadi dapat diakses `EMPLOYEE`, `HR`, dan `ADMIN`; daftar seluruh attendance hanya `HR` dan `ADMIN`.
- Kedua endpoint history menerima filter `from` dan `to` yang inklusif. Default masing-masing adalah awal bulan berjalan dan hari ini.
- Gunakan zona waktu `Asia/Jakarta` untuk tanggal attendance dan default filter. Identitas employee berasal dari user yang terautentikasi, bukan input client.
- Ikuti struktur respons dan aturan bisnis NestJS, dengan pola controller, service, repository, serta penanganan error yang konsisten dengan proyek Spring. Sesuaikan persistence dengan skema database yang digunakan.

## Acceptance criteria

- User dapat check-in, check-out, lalu melihat record tersebut pada history pribadinya.
- Check-in ganda, check-out tanpa check-in, dan check-out ganda ditolak; profil employee yang tidak ditemukan menghasilkan error yang jelas.
- History pribadi tidak membocorkan attendance user lain.
- Filter tanggal, default periode, urutan hasil, dan pergantian hari mengikuti perilaku NestJS.
- HR/ADMIN dapat melihat seluruh attendance; EMPLOYEE tidak dapat mengakses endpoint tersebut.
- Tersedia pengujian alur sukses, kegagalan bisnis, otorisasi, filter tanggal, dan batas pergantian hari Jakarta.

## Batasan

Tidak mencakup koreksi attendance, shift, atau perhitungan lembur.
