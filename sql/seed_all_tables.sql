-- Seed data for TinhNguyenXanh
-- Run on MySQL after schema/tables are created by Hibernate.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Optional cleanup for re-seeding
DELETE FROM event_favorites;
DELETE FROM event_reports;
DELETE FROM event_comments;
DELETE FROM event_registrations;
DELETE FROM reviews;
DELETE FROM donations;
DELETE FROM events;
DELETE FROM volunteers;
DELETE FROM organizations;
DELETE FROM event_categories;
DELETE FROM users;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 1) USERS
-- =====================================================
-- password hash below is BCrypt for sample password, change in production
INSERT INTO users
(id, full_name, email, password, phone_number, address, age, avatar_path, role, registered_date, enabled, account_locked, lockout_end)
VALUES
(1, 'Admin System', 'admin@tinhnguyenxanh.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi6qV5y8bY4xXHzkwmo7aX6ixSeKuuG', '0901000001', 'TP.HCM', '30', '/uploads/avatars/admin.png', 'ADMIN', NOW() - INTERVAL 30 DAY, 1, 0, NULL),
(2, 'Nguyen Van A', 'organizer1@tinhnguyenxanh.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi6qV5y8bY4xXHzkwmo7aX6ixSeKuuG', '0901000002', 'Quan 1, TP.HCM', '28', '/uploads/avatars/org1.png', 'ORGANIZER', NOW() - INTERVAL 20 DAY, 1, 0, NULL),
(3, 'Tran Thi B', 'organizer2@tinhnguyenxanh.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi6qV5y8bY4xXHzkwmo7aX6ixSeKuuG', '0901000003', 'Quan Hai Chau, Da Nang', '29', '/uploads/avatars/org2.png', 'ORGANIZER', NOW() - INTERVAL 18 DAY, 1, 0, NULL),
(4, 'Le Van C', 'volunteer1@tinhnguyenxanh.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi6qV5y8bY4xXHzkwmo7aX6ixSeKuuG', '0901000004', 'Thu Duc, TP.HCM', '22', '/uploads/avatars/v1.png', 'VOLUNTEER', NOW() - INTERVAL 15 DAY, 1, 0, NULL),
(5, 'Pham Thi D', 'volunteer2@tinhnguyenxanh.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi6qV5y8bY4xXHzkwmo7aX6ixSeKuuG', '0901000005', 'Ninh Kieu, Can Tho', '24', '/uploads/avatars/v2.png', 'VOLUNTEER', NOW() - INTERVAL 12 DAY, 1, 0, NULL),
(6, 'Hoang Van E', 'volunteer3@tinhnguyenxanh.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi6qV5y8bY4xXHzkwmo7aX6ixSeKuuG', '0901000006', 'Bien Hoa, Dong Nai', '26', '/uploads/avatars/v3.png', 'VOLUNTEER', NOW() - INTERVAL 10 DAY, 1, 0, NULL);

-- =====================================================
-- 2) EVENT CATEGORIES
-- =====================================================
INSERT INTO event_categories (id, name) VALUES
(1, 'Moi truong'),
(2, 'Giao duc'),
(3, 'Cong dong'),
(4, 'Y te');

-- =====================================================
-- 3) ORGANIZATIONS
-- =====================================================
INSERT INTO organizations
(id, user_id, name, organization_type, description, avatar_url, focus_areas, contact_email, phone_number, website, address, city, district, ward, tax_code, founded_date, legal_representative, verification_docs_url, document_type, verified, verified_date, verification_notes, facebook_url, instagram_url, zalo_number, member_count, events_organized, achievements, joined_date, last_updated, is_active, is_approved, average_rating, total_reviews)
VALUES
(1, 2, 'CLB Song Xanh', 'Non-profit', 'To chuc cac chuong trinh bao ve moi truong va song xanh.', '/uploads/orgs/songxanh.png', 'Moi truong, Tai che, Giao duc', 'contact@songxanh.vn', '02873000001', 'https://songxanh.vn', '12 Nguyen Hue, Quan 1', 'TP.HCM', 'Quan 1', 'Ben Nghe', '0312345678', '2019-05-12', 'Nguyen Van A', '/uploads/docs/songxanh-license.pdf', 'Giay phep hoat dong', 1, NOW() - INTERVAL 14 DAY, 'Ho so hop le', 'https://facebook.com/songxanh', 'https://instagram.com/songxanh', '0909000001', 120, 15, 'Top 10 du an cong dong 2024', NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 1 DAY, 1, 1, 4.60, 2),
(2, 3, 'Tam Long Tre', 'Social Enterprise', 'Ket noi tinh nguyen vien cho cac chuong trinh giao duc va y te cong dong.', '/uploads/orgs/tamlongtre.png', 'Giao duc, Y te, Cong dong', 'hello@tamlongtre.vn', '02367300002', 'https://tamlongtre.vn', '89 Bach Dang, Hai Chau', 'Da Nang', 'Hai Chau', 'Thach Thang', '0401234567', '2020-09-08', 'Tran Thi B', '/uploads/docs/tlt-license.pdf', 'Giay dang ky doanh nghiep', 1, NOW() - INTERVAL 10 DAY, 'Da xac minh ban dau', 'https://facebook.com/tamlongtre', 'https://instagram.com/tamlongtre', '0909000002', 80, 9, 'Hon 3000 gio tinh nguyen trong nam 2025', NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 2 DAY, 1, 1, 4.20, 1);

-- =====================================================
-- 4) VOLUNTEERS
-- =====================================================
INSERT INTO volunteers
(id, user_id, full_name, email, phone, address, skills, bio, avatar_url, joined_date, availability)
VALUES
(1, 4, 'Le Van C', 'volunteer1@tinhnguyenxanh.vn', '0901000004', 'Thu Duc, TP.HCM', 'Truyen thong, to chuc su kien', 'Sinh vien yeu hoat dong moi truong.', '/uploads/volunteers/v1.png', NOW() - INTERVAL 15 DAY, 'Available'),
(2, 5, 'Pham Thi D', 'volunteer2@tinhnguyenxanh.vn', '0901000005', 'Ninh Kieu, Can Tho', 'Cham soc suc khoe cong dong, hau can', 'Da tung tham gia nhieu chuong trinh y te mien phi.', '/uploads/volunteers/v2.png', NOW() - INTERVAL 12 DAY, 'Available'),
(3, 6, 'Hoang Van E', 'volunteer3@tinhnguyenxanh.vn', '0901000006', 'Bien Hoa, Dong Nai', 'Trong cay, thu gom rac, huan luyen doi nhom', 'Yeu thich hoat dong ngoai troi va ket noi cong dong.', '/uploads/volunteers/v3.png', NOW() - INTERVAL 10 DAY, 'Busy');

-- =====================================================
-- 5) EVENTS
-- =====================================================
INSERT INTO events
(id, title, description, start_time, end_time, location, location_coords, max_volunteers, organization_id, category_id, status, images, is_hidden, hidden_reason, hidden_at)
VALUES
(1, 'Ngay Chu Nhat Xanh - Don Rac Kenh Nhieu Loc', 'Chuong trinh thu gom rac va phan loai rac tai nguon.', NOW() + INTERVAL 7 DAY, NOW() + INTERVAL 7 DAY + INTERVAL 4 HOUR, 'Kenh Nhieu Loc, TP.HCM', '10.7869,106.6991', 50, 1, 1, 'approved', '/uploads/events/thugomrac.jpg', 0, NULL, NULL),
(2, 'Lop Hoc Ky Nang Song Cho Tre Em', 'Day ky nang phong tranh nguy co va ky nang giao tiep co ban.', NOW() + INTERVAL 10 DAY, NOW() + INTERVAL 10 DAY + INTERVAL 3 HOUR, 'Nha Van Hoa Thanh Nien Da Nang', '16.0678,108.2208', 30, 2, 2, 'approved', '/uploads/events/Lophockynangchotreem.jpg', 0, NULL, NULL),
(3, 'Hien Mau Nhan Dao Mua He', 'Tuyen tinh nguyen vien ho tro tiep don, huong dan va hau can.', NOW() + INTERVAL 14 DAY, NOW() + INTERVAL 14 DAY + INTERVAL 5 HOUR, 'Trung tam Y te Quan 1', '10.7740,106.7009', 40, 2, 4, 'pending', '/uploads/events/khamsuckhoevatuvanmienphi.jpg', 0, NULL, NULL);

-- =====================================================
-- 6) EVENT REGISTRATIONS
-- =====================================================
INSERT INTO event_registrations
(id, event_id, volunteer_id, full_name, phone, reason, status, registered_date)
VALUES
(1, 1, 1, 'Le Van C', '0901000004', 'Muon dong gop suc cho moi truong thanh pho.', 'Confirmed', NOW() - INTERVAL 2 DAY),
(2, 1, 2, 'Pham Thi D', '0901000005', 'Muon tham gia cung ban be va hoc cach phan loai rac.', 'Pending', NOW() - INTERVAL 1 DAY),
(3, 2, 3, 'Hoang Van E', '0901000006', 'Quan tam giao duc cong dong cho tre em.', 'Confirmed', NOW() - INTERVAL 1 DAY);

-- =====================================================
-- 7) EVENT COMMENTS
-- =====================================================
INSERT INTO event_comments
(id, event_id, user_id, content, created_at, is_visible, is_deleted)
VALUES
(1, 1, 4, 'Su kien rat y nghia, minh da dang ky tham gia.', NOW() - INTERVAL 20 HOUR, 1, 0),
(2, 1, 5, 'Ban to chuc cho minh hoi diem tap trung cu the voi.', NOW() - INTERVAL 12 HOUR, 1, 0),
(3, 2, 6, 'Chuong trinh hay, co ho tro tai lieu cho tinh nguyen vien khong?', NOW() - INTERVAL 8 HOUR, 1, 0);

-- =====================================================
-- 8) EVENT FAVORITES (composite key)
-- =====================================================
INSERT INTO event_favorites (event_id, user_id, favorite_date) VALUES
(1, 4, NOW() - INTERVAL 3 DAY),
(1, 5, NOW() - INTERVAL 2 DAY),
(2, 6, NOW() - INTERVAL 1 DAY);

-- =====================================================
-- 9) EVENT REPORTS
-- =====================================================
INSERT INTO event_reports
(id, event_id, user_id, report_reason, report_date, status)
VALUES
(1, 3, 4, 'Can bo sung thong tin dia diem cu the de de theo doi.', NOW() - INTERVAL 6 HOUR, 'Pending');

-- =====================================================
-- 10) REVIEWS
-- =====================================================
INSERT INTO reviews
(id, user_id, organization_id, rating, comment, created_at)
VALUES
(1, 4, 1, 5, 'To chuc chuyen nghiep, hoat dong rat bai ban.', NOW() - INTERVAL 5 DAY),
(2, 5, 1, 4, 'Thong tin ro rang, tinh nguyen vien duoc ho tro tot.', NOW() - INTERVAL 4 DAY),
(3, 6, 2, 4, 'Noi dung chuong trinh thiet thuc, can them khung gio linh hoat.', NOW() - INTERVAL 3 DAY);

-- =====================================================
-- 11) DONATIONS
-- =====================================================
INSERT INTO donations
(id, donor_name, amount, phone_number, message, is_paid, transaction_code, created_at)
VALUES
(1, 'Nguyen Thi Hoa', 500000, '0912000001', 'Ung ho chuong trinh trong cay', 1, 'TXN000001', NOW() - INTERVAL 7 DAY),
(2, 'Tran Quoc Minh', 1000000, '0912000002', 'Chuc du an thanh cong', 1, 'TXN000002', NOW() - INTERVAL 2 DAY),
(3, 'Le Thanh Tung', 300000, '0912000003', 'Dong hanh cung cong dong', 0, NULL, NOW() - INTERVAL 10 HOUR);

-- reset auto increment safely
ALTER TABLE users AUTO_INCREMENT = 7;
ALTER TABLE event_categories AUTO_INCREMENT = 5;
ALTER TABLE organizations AUTO_INCREMENT = 3;
ALTER TABLE volunteers AUTO_INCREMENT = 4;
ALTER TABLE events AUTO_INCREMENT = 4;
ALTER TABLE event_registrations AUTO_INCREMENT = 4;
ALTER TABLE event_comments AUTO_INCREMENT = 4;
ALTER TABLE event_reports AUTO_INCREMENT = 2;
ALTER TABLE reviews AUTO_INCREMENT = 4;
ALTER TABLE donations AUTO_INCREMENT = 4;

