-- Seed data for TinhNguyenXanh
-- Run on MySQL after schema/tables are created by Hibernate.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Optional cleanup for re-seeding
DELETE FROM event_favorites;
DELETE FROM event_reports;
DELETE FROM event_comments;
DELETE FROM event_registrations;
DELETE FROM newsletter_subscriptions;
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
INSERT INTO users
(id, full_name, email, password, phone_number, address, age, avatar_path, role, registered_date, enabled, account_locked, lockout_end)
VALUES
-- ── existing users (unchanged) ──
(1,  'Admin System',   'admin@tinhnguyenxanh.vn',       '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000001', 'TP.HCM',                 '30', '/uploads/avatars/admin.png',  'ADMIN',     NOW() - INTERVAL 30 DAY, 1, 0, NULL),
(2,  'Nguyen Van A',   'organizer1@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000002', 'Quan 1, TP.HCM',         '28', '/uploads/avatars/org1.png',   'ORGANIZER', NOW() - INTERVAL 20 DAY, 1, 0, NULL),
(3,  'Tran Thi B',     'organizer2@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000003', 'Quan Hai Chau, Da Nang', '29', '/uploads/avatars/org2.png',   'ORGANIZER', NOW() - INTERVAL 18 DAY, 1, 0, NULL),
(4,  'Le Van C',       'volunteer1@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000004', 'Thu Duc, TP.HCM',        '22', '/uploads/avatars/v1.png',     'VOLUNTEER', NOW() - INTERVAL 15 DAY, 1, 0, NULL),
(5,  'Pham Thi D',     'volunteer2@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000005', 'Ninh Kieu, Can Tho',     '24', '/uploads/avatars/v2.png',     'VOLUNTEER', NOW() - INTERVAL 12 DAY, 1, 0, NULL),
(6,  'Hoang Van E',    'volunteer3@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000006', 'Bien Hoa, Dong Nai',     '26', '/uploads/avatars/v3.png',     'VOLUNTEER', NOW() - INTERVAL 10 DAY, 1, 0, NULL),
-- ── new organizers ──
(7,  'Vu Minh F',      'organizer3@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000007', 'Quan 3, TP.HCM',         '35', '/uploads/avatars/org3.png',   'ORGANIZER', NOW() - INTERVAL 16 DAY, 1, 0, NULL),
(8,  'Bui Thi G',      'organizer4@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000008', 'Son Tra, Da Nang',       '31', '/uploads/avatars/org4.png',   'ORGANIZER', NOW() - INTERVAL 13 DAY, 1, 0, NULL),
-- ── new volunteers ──
(9,  'Do Thi H',       'volunteer4@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000009', 'Go Vap, TP.HCM',         '21', '/uploads/avatars/v4.png',     'VOLUNTEER', NOW() - INTERVAL 9 DAY,  1, 0, NULL),
(10, 'Nguyen Van I',   'volunteer5@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000010', 'Cam Le, Da Nang',        '23', '/uploads/avatars/v5.png',     'VOLUNTEER', NOW() - INTERVAL 8 DAY,  1, 0, NULL),
(11, 'Tran Quoc J',    'volunteer6@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000011', 'Long Bien, Ha Noi',      '27', '/uploads/avatars/v6.png',     'VOLUNTEER', NOW() - INTERVAL 7 DAY,  1, 0, NULL),
(12, 'Le Thi K',       'volunteer7@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000012', 'Ninh Kieu, Can Tho',     '20', '/uploads/avatars/v7.png',     'VOLUNTEER', NOW() - INTERVAL 6 DAY,  1, 0, NULL),
(13, 'Phan Van L',     'volunteer8@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000013', 'Hoa Thanh, Tay Ninh',    '25', '/uploads/avatars/v8.png',     'VOLUNTEER', NOW() - INTERVAL 5 DAY,  1, 0, NULL),
(14, 'Hoang Thi M',    'volunteer9@tinhnguyenxanh.vn',  '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000014', 'Phu Nhuan, TP.HCM',      '29', '/uploads/avatars/v9.png',     'VOLUNTEER', NOW() - INTERVAL 4 DAY,  1, 0, NULL),
(15, 'Dang Van N',     'volunteer10@tinhnguyenxanh.vn', '$2a$12$YGM7torrD5osv.eXWniVZOsOhiNA61V29Iz0KvrdvR8DTOZ3MJuhi', '0901000015', 'Thu Duc, TP.HCM',        '32', '/uploads/avatars/v10.png',    'VOLUNTEER', NOW() - INTERVAL 3 DAY,  1, 0, NULL);

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
-- ── existing orgs (unchanged) ──
(1, 2, 'CLB Song Xanh',    'Non-profit',        'To chuc cac chuong trinh bao ve moi truong va song xanh.',               '/uploads/orgs/songxanh.png',     'Moi truong, Tai che, Giao duc',  'contact@songxanh.vn',  '02873000001', 'https://songxanh.vn',     '12 Nguyen Hue, Quan 1',    'TP.HCM',  'Quan 1',   'Ben Nghe',    '0312345678', '2019-05-12', 'Nguyen Van A', '/uploads/docs/songxanh-license.pdf', 'Giay phep hoat dong',       1, NOW() - INTERVAL 14 DAY, 'Ho so hop le',          'https://facebook.com/songxanh',    'https://instagram.com/songxanh',    '0909000001', 120, 15, 'Top 10 du an cong dong 2024',              NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 1 DAY, 1, 1, 4.60, 2),
(2, 3, 'Tam Long Tre',     'Social Enterprise', 'Ket noi tinh nguyen vien cho cac chuong trinh giao duc va y te cong dong.','/uploads/orgs/tamlongtre.png',   'Giao duc, Y te, Cong dong',      'hello@tamlongtre.vn',  '02367300002', 'https://tamlongtre.vn',  '89 Bach Dang, Hai Chau',   'Da Nang', 'Hai Chau', 'Thach Thang', '0401234567', '2020-09-08', 'Tran Thi B',   '/uploads/docs/tlt-license.pdf',      'Giay dang ky doanh nghiep', 1, NOW() - INTERVAL 10 DAY, 'Da xac minh ban dau',   'https://facebook.com/tamlongtre',  'https://instagram.com/tamlongtre',  '0909000002',  80,  9, 'Hon 3000 gio tinh nguyen trong nam 2025', NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 2 DAY, 1, 1, 4.20, 1),
-- ── new orgs ──
(3, 7, 'Hanh Phuc Xanh',  'Non-profit',        'Chuong trinh xay dung vuon rau sach tai cac truong hoc khu vuc noi o.',  '/uploads/orgs/hanhphucxanh.png', 'Moi truong, Giao duc, Cong dong','info@hanhphucxanh.vn', '02873000003', 'https://hanhphucxanh.vn', '45 Le Loi, Quan 3',        'TP.HCM',  'Quan 3',   'Pham Ngu Lao','0312345679', '2021-03-20', 'Vu Minh F',    '/uploads/docs/hpx-license.pdf',      'Giay phep hoat dong',       1, NOW() - INTERVAL 8 DAY,  'Ho so dang ky hop le', 'https://facebook.com/hanhphucxanh','https://instagram.com/hanhphucxanh','0909000003',  60,  6, 'Cai thien chat luong bo bua hoc sinh',    NOW() - INTERVAL 16 DAY, NOW() - INTERVAL 1 DAY, 1, 1, 4.10, 2),
(4, 8, 'Nhan Ai Da Nang', 'Social Enterprise', 'Ho tro cac gia dinh kho khan tai cac xa vung sau Da Nang.',              '/uploads/orgs/nhanai.png',       'Cong dong, Y te, Giao duc',      'info@nhanaidanang.vn', '02367300004', 'https://nhanaidanang.vn', '22 Tran Phu, Son Tra',     'Da Nang', 'Son Tra',  'Man Thai',    '0401234568', '2022-01-15', 'Bui Thi G',    '/uploads/docs/nai-license.pdf',      'Giay dang ky doanh nghiep', 1, NOW() - INTERVAL 5 DAY,  'Dang cho phe duyet',    'https://facebook.com/nhanaidanang','https://instagram.com/nhanaidanang','0909000004',  40,  4, 'Trao 500 phan qua Tet 2025',              NOW() - INTERVAL 13 DAY, NOW() - INTERVAL 1 DAY, 1, 0, 0.00, 0);

-- =====================================================
-- 4) VOLUNTEERS
-- =====================================================
INSERT INTO volunteers
(id, user_id, full_name, email, phone, address, skills, bio, avatar_url, joined_date, availability)
VALUES
-- ── existing volunteers (unchanged) ──
(1,  4,  'Le Van C',     'volunteer1@tinhnguyenxanh.vn',  '0901000004', 'Thu Duc, TP.HCM',     'Truyen thong, to chuc su kien',               'Sinh vien yeu hoat dong moi truong.',                               '/uploads/volunteers/v1.png',  NOW() - INTERVAL 15 DAY, 'Available'),
(2,  5,  'Pham Thi D',   'volunteer2@tinhnguyenxanh.vn',  '0901000005', 'Ninh Kieu, Can Tho',  'Cham soc suc khoe cong dong, hau can',        'Da tung tham gia nhieu chuong trinh y te mien phi.',                '/uploads/volunteers/v2.png',  NOW() - INTERVAL 12 DAY, 'Available'),
(3,  6,  'Hoang Van E',  'volunteer3@tinhnguyenxanh.vn',  '0901000006', 'Bien Hoa, Dong Nai',  'Trong cay, thu gom rac, huan luyen doi nhom',  'Yeu thich hoat dong ngoai troi va ket noi cong dong.',             '/uploads/volunteers/v3.png',  NOW() - INTERVAL 10 DAY, 'Busy'),
-- ── new volunteers ──
(4,  9,  'Do Thi H',     'volunteer4@tinhnguyenxanh.vn',  '0901000009', 'Go Vap, TP.HCM',      'Thiet ke do hoa, mang xa hoi',                'Sinh vien my thuat, muon dung ky nang thiet ke de lan truyen thong diep xanh.','/uploads/volunteers/v4.png', NOW() - INTERVAL 9 DAY,  'Available'),
(5,  10, 'Nguyen Van I', 'volunteer5@tinhnguyenxanh.vn',  '0901000010', 'Cam Le, Da Nang',     'Gia su, day kem, tu van hoc sinh',            'Giao vien tre muon dong hanh cung tre em kho khan.',                '/uploads/volunteers/v5.png',  NOW() - INTERVAL 8 DAY,  'Available'),
(6,  11, 'Tran Quoc J',  'volunteer6@tinhnguyenxanh.vn',  '0901000011', 'Long Bien, Ha Noi',   'Lap trinh, CNTT, ho tro ky thuat',            'Lap trinh vien muon dong gop ki nang cong nghe cho cong dong.',     '/uploads/volunteers/v6.png',  NOW() - INTERVAL 7 DAY,  'Available'),
(7,  12, 'Le Thi K',     'volunteer7@tinhnguyenxanh.vn',  '0901000012', 'Ninh Kieu, Can Tho',  'Nau an, to chuc bep tu thien',                'Tung phu trach bep an cho 200 nguoi trong su kien tu thien.',       '/uploads/volunteers/v7.png',  NOW() - INTERVAL 6 DAY,  'Available'),
(8,  13, 'Phan Van L',   'volunteer8@tinhnguyenxanh.vn',  '0901000013', 'Hoa Thanh, Tay Ninh', 'Lai xe, van chuyen, hau can',                 'Co kinh nghiem van chuyen hang hoa cho cac chuong trinh tu thien.', '/uploads/volunteers/v8.png',  NOW() - INTERVAL 5 DAY,  'Busy'),
(9,  14, 'Hoang Thi M',  'volunteer9@tinhnguyenxanh.vn',  '0901000014', 'Phu Nhuan, TP.HCM',   'Y ta, so cuu, cham soc suc khoe',             'Dieu duong vien muon ho tro cac chuong trinh kham suc khoe mien phi.','/uploads/volunteers/v9.png', NOW() - INTERVAL 4 DAY,  'Available'),
(10, 15, 'Dang Van N',   'volunteer10@tinhnguyenxanh.vn', '0901000015', 'Thu Duc, TP.HCM',     'Chup anh, quay video, dung phim',             'Photographer tu do, muon ghi lai nhung khoang khac y nghia cua su kien.','/uploads/volunteers/v10.png',NOW() - INTERVAL 3 DAY,  'Available');

-- =====================================================
-- 5) EVENTS  (12 total)
-- =====================================================
-- Status mix: approved(8), pending(3), rejected(1)
-- Time mix: upcoming, ongoing area, past
INSERT INTO events
(id, title, description, start_time, end_time, location, location_coords, max_volunteers, organization_id, category_id, status, images, is_hidden, hidden_reason, hidden_at)
VALUES
-- ── Org 1 – CLB Song Xanh ──
(1,  'Ngay Chu Nhat Xanh - Don Rac Kenh Nhieu Loc',
     'Chuong trinh thu gom rac va phan loai rac tai nguon doc tuyen kenh Nhieu Loc - Thi Nghe.',
     NOW() + INTERVAL 7 DAY,  NOW() + INTERVAL 7 DAY  + INTERVAL 4 HOUR,
     'Kenh Nhieu Loc, Quan 3, TP.HCM',         '10.7869,106.6991', 50, 1, 1, 'approved',  '/uploads/events/thugomrac.jpg',                 0, NULL, NULL),

(2,  'Tai Che Sang Tao - Workshop Lam Do Thu Cong Tu Rac',
     'Huong dan tai che chai nhua, lon thiec, bao bia thanh do thu cong va qua tang y nghia.',
     NOW() + INTERVAL 12 DAY, NOW() + INTERVAL 12 DAY + INTERVAL 3 HOUR,
     'Nha Van Hoa Thanh Nien TP.HCM, Quan 1',   '10.7769,106.7009', 30, 1, 1, 'approved',  '/uploads/events/taiche.jpg',                    0, NULL, NULL),

(3,  'Ngay Hoi Trong Cay Xanh - Cong Vien Tao Dan',
     'Trong them 200 cay xanh tai cong vien, cham soc cay cu bi suy yeu, tao bong mat cho cong dong.',
     NOW() + INTERVAL 21 DAY, NOW() + INTERVAL 21 DAY + INTERVAL 5 HOUR,
     'Cong Vien Tao Dan, Quan 1, TP.HCM',       '10.7756,106.6951', 60, 1, 1, 'approved',  '/uploads/events/trongcay.jpg',                  0, NULL, NULL),

-- ── Org 2 – Tam Long Tre ──
(4,  'Lop Hoc Ky Nang Song Cho Tre Em',
     'Day ky nang phong tranh nguy co va ky nang giao tiep co ban cho tre 8-14 tuoi.',
     NOW() + INTERVAL 10 DAY, NOW() + INTERVAL 10 DAY + INTERVAL 3 HOUR,
     'Nha Van Hoa Thanh Nien Da Nang',          '16.0678,108.2208', 30, 2, 2, 'approved',  '/uploads/events/Lophockynangchotreem.jpg',      0, NULL, NULL),

(5,  'Hien Mau Nhan Dao Mua He',
     'Tuyen tinh nguyen vien ho tro tiep don, huong dan nguoi hien va phu trach hau can.',
     NOW() + INTERVAL 14 DAY, NOW() + INTERVAL 14 DAY + INTERVAL 5 HOUR,
     'Trung tam Y te Quan 1, TP.HCM',           '10.7740,106.7009', 40, 2, 4, 'pending',   '/uploads/events/hienmau.jpg',                   0, NULL, NULL),

(6,  'Chuong Trinh Hoc Bong "Uoc Mo Xanh"',
     'Trao 30 suat hoc bong cho hoc sinh ngheo vuot kho hoc gioi tai cac truong THCS Da Nang.',
     NOW() + INTERVAL 30 DAY, NOW() + INTERVAL 30 DAY + INTERVAL 4 HOUR,
     'Truong THCS Nguyen Hue, Hai Chau, Da Nang','16.0544,108.2022', 20, 2, 2, 'approved',  '/uploads/events/hocbong.jpg',                   0, NULL, NULL),

-- ── Org 3 – Hanh Phuc Xanh ──
(7,  'Trong Cay Xanh - Cong Vien Gia Dinh',
     'Trung tu va trong them cay xanh tai cong vien, tao bong mat va khong gian vui choi an toan.',
     NOW() + INTERVAL 5 DAY,  NOW() + INTERVAL 5 DAY  + INTERVAL 3 HOUR,
     'Cong Vien Gia Dinh, Binh Thanh, TP.HCM',  '10.8000,106.7200', 35, 3, 1, 'approved',  '/uploads/events/congviengiading.jpg',           0, NULL, NULL),

(8,  'Day Nghe Mien Phi - Sua Xe Dap',
     'Lop day ky nang sua xe dap co ban cho tre em duong pho va thanh nien kho khan.',
     NOW() + INTERVAL 25 DAY, NOW() + INTERVAL 25 DAY + INTERVAL 4 HOUR,
     'Nha Sinh Hoat Cong Dong Q3, TP.HCM',      '10.7785,106.6900', 20, 3, 2, 'pending',   '/uploads/events/daysuaxe.jpg',                  0, NULL, NULL),

(9,  'Cho Phien Xanh - San Pham Tai Che & Huu Co',
     'Phien cho cong dong mua ban trao doi san pham tai che, rau sach va hang thu cong thu cong.',
     NOW() + INTERVAL 18 DAY, NOW() + INTERVAL 18 DAY + INTERVAL 6 HOUR,
     'San UBND Quan 3, TP.HCM',                 '10.7800,106.6860', 25, 3, 3, 'approved',  '/uploads/events/chophienxanh.jpg',              0, NULL, NULL),

-- ── Org 4 – Nhan Ai Da Nang ──
(10, 'Kham Suc Khoe Mien Phi Cho Nguoi Cao Tuoi',
     'Kham tong quat, tu van suc khoe, do huyet ap, duong huyet va cap phat thuoc mien phi.',
     NOW() + INTERVAL 20 DAY, NOW() + INTERVAL 20 DAY + INTERVAL 6 HOUR,
     'UBND Phuong 5, Quan Phu Nhuan, TP.HCM',   '10.8021,106.6784', 25, 4, 4, 'approved',  '/uploads/events/khammienphi.jpg',               0, NULL, NULL),

(11, 'Phat Com Chay - Bep An Tinh Thuong',
     'Ho tro 300 suat an com chay mien phi cho nguoi ngheo, vo gia cu va benh nhan tai TP.HCM.',
     NOW() + INTERVAL 3 DAY,  NOW() + INTERVAL 3 DAY  + INTERVAL 4 HOUR,
     'Chua Xa Loi, Quan 3, TP.HCM',             '10.7815,106.6940', 30, 4, 3, 'approved',  '/uploads/events/comchay.jpg',                   0, NULL, NULL),

(12, 'Hoi Thao: Bao Ve Moi Truong Bien - Khong Xa Rac Ra Bien',
     'Hoi thao nang cao nhan thuc ve o nhiem moi truong bien, co hoat dong don dep bai bien sau hoi thao.',
     NOW() + INTERVAL 35 DAY, NOW() + INTERVAL 35 DAY + INTERVAL 5 HOUR,
     'Bai Bien My Khe, Son Tra, Da Nang',        '16.0610,108.2470', 45, 4, 1, 'pending',   '/uploads/events/baove_bien.jpg',                0, NULL, NULL);

-- =====================================================
-- 6) EVENT REGISTRATIONS
-- =====================================================
INSERT INTO event_registrations
(id, event_id, volunteer_id, full_name, phone, reason, status, registered_date)
VALUES
-- Event 1 – Don Rac Kenh Nhieu Loc
(1,  1, 1,  'Le Van C',     '0901000004', 'Muon dong gop suc cho moi truong thanh pho.',                     'Confirmed', NOW() - INTERVAL 5 DAY),
(2,  1, 2,  'Pham Thi D',   '0901000005', 'Muon tham gia cung ban be va hoc cach phan loai rac.',            'Pending',   NOW() - INTERVAL 4 DAY),
(3,  1, 4,  'Do Thi H',     '0901000009', 'Muon ho tro truyen thong cho su kien.',                           'Confirmed', NOW() - INTERVAL 3 DAY),
(4,  1, 7,  'Le Thi K',     '0901000012', 'Muon ho tro hau can bep an cho tinh nguyen vien.',                'Confirmed', NOW() - INTERVAL 2 DAY),
-- Event 2 – Workshop Tai Che
(5,  2, 4,  'Do Thi H',     '0901000009', 'Co ky nang thiet ke, muon ho tro huong dan workshop.',            'Confirmed', NOW() - INTERVAL 6 DAY),
(6,  2, 10, 'Dang Van N',   '0901000015', 'Muon ghi lai qua trinh workshop de chia se len mang xa hoi.',     'Confirmed', NOW() - INTERVAL 5 DAY),
(7,  2, 5,  'Nguyen Van I', '0901000010', 'Quan tam tai che sang tao, muon hoc hoi them.',                   'Pending',   NOW() - INTERVAL 3 DAY),
-- Event 3 – Trong Cay Tao Dan
(8,  3, 3,  'Hoang Van E',  '0901000006', 'Co kinh nghiem trong cay va cham soc cay xanh.',                  'Confirmed', NOW() - INTERVAL 4 DAY),
(9,  3, 8,  'Phan Van L',   '0901000013', 'Co the ho tro van chuyen cay giong va dung cu.',                  'Confirmed', NOW() - INTERVAL 3 DAY),
(10, 3, 10, 'Dang Van N',   '0901000015', 'Muon chup anh luu niem ngay trong cay.',                          'Confirmed', NOW() - INTERVAL 2 DAY),
-- Event 4 – Lop Ky Nang Song
(11, 4, 5,  'Nguyen Van I', '0901000010', 'La giao vien, co kinh nghiem day tre em.',                        'Confirmed', NOW() - INTERVAL 3 DAY),
(12, 4, 3,  'Hoang Van E',  '0901000006', 'Quan tam giao duc ky nang song cho tre.',                         'Confirmed', NOW() - INTERVAL 2 DAY),
(13, 4, 6,  'Tran Quoc J',  '0901000011', 'Muon chia se ky nang CNTT co ban cho cac em.',                    'Pending',   NOW() - INTERVAL 1 DAY),
-- Event 5 – Hien Mau
(14, 5, 2,  'Pham Thi D',   '0901000005', 'Co kinh nghiem cham soc y te, muon ho tro nguoi hien mau.',       'Confirmed', NOW() - INTERVAL 2 DAY),
(15, 5, 9,  'Hoang Thi M',  '0901000014', 'La y ta, co the ho tro kiem tra suc khoe truoc khi hien.',        'Confirmed', NOW() - INTERVAL 2 DAY),
-- Event 7 – Trong Cay Cong Vien Gia Dinh
(16, 7, 3,  'Hoang Van E',  '0901000006', 'Rat thich cac hoat dong trong cay xanh.',                         'Confirmed', NOW() - INTERVAL 3 DAY),
(17, 7, 4,  'Do Thi H',     '0901000009', 'Muon giup truyen thong va chup anh su kien.',                     'Confirmed', NOW() - INTERVAL 2 DAY),
(18, 7, 1,  'Le Van C',     '0901000004', 'Muon dong gop cho khong gian xanh cua thanh pho.',                'Pending',   NOW() - INTERVAL 1 DAY),
-- Event 9 – Cho Phien Xanh
(19, 9, 4,  'Do Thi H',     '0901000009', 'Muon ho tro thiet ke bang ron va vat lieu truyen thong cho cho.', 'Confirmed', NOW() - INTERVAL 4 DAY),
(20, 9, 7,  'Le Thi K',     '0901000012', 'Muon ho tro gian hang thuc an tai cho phien.',                    'Confirmed', NOW() - INTERVAL 3 DAY),
(21, 9, 6,  'Tran Quoc J',  '0901000011', 'Muon ho tro quan ly website dang ky gian hang.',                  'Confirmed', NOW() - INTERVAL 2 DAY),
-- Event 10 – Kham Suc Khoe
(22, 10, 9, 'Hoang Thi M',  '0901000014', 'La y ta, phu hop voi cac hoat dong kham suc khoe cong dong.',     'Confirmed', NOW() - INTERVAL 5 DAY),
(23, 10, 2, 'Pham Thi D',   '0901000005', 'Co kinh nghiem cham soc y te, muon ho tro.',                      'Confirmed', NOW() - INTERVAL 4 DAY),
(24, 10, 8, 'Phan Van L',   '0901000013', 'Co the ho tro van chuyen thiet bi y te.',                         'Pending',   NOW() - INTERVAL 2 DAY),
-- Event 11 – Phat Com Chay
(25, 11, 7, 'Le Thi K',     '0901000012', 'Co kinh nghiem bep tu thien, muon ho tro nau an.',                'Confirmed', NOW() - INTERVAL 3 DAY),
(26, 11, 1, 'Le Van C',     '0901000004', 'Muon ho tro phat com va dieu phoi hang doi.',                     'Confirmed', NOW() - INTERVAL 2 DAY),
(27, 11, 2, 'Pham Thi D',   '0901000005', 'Muon dong gop them mot ngay cuoi tuan.',                          'Confirmed', NOW() - INTERVAL 1 DAY),
-- Event 12 – Hoi Thao Bien
(28, 12, 3, 'Hoang Van E',  '0901000006', 'Yeu bao ve moi truong bien, muon tham gia hoi thao.',             'Confirmed', NOW() - INTERVAL 2 DAY),
(29, 12, 10,'Dang Van N',   '0901000015', 'Muon quay phim tai lieu ve hoi thao va don bai bien.',            'Pending',   NOW() - INTERVAL 1 DAY);

-- =====================================================
-- 7) EVENT COMMENTS
-- =====================================================
INSERT INTO event_comments
(id, event_id, user_id, content, created_at, is_visible, is_deleted)
VALUES
-- Event 1
(1,  1, 4,  'Su kien rat y nghia, minh da dang ky tham gia roi!',                               NOW() - INTERVAL 20 HOUR, 1, 0),
(2,  1, 5,  'Ban to chuc cho minh hoi diem tap trung cu the duoc khong?',                       NOW() - INTERVAL 18 HOUR, 1, 0),
(3,  1, 11, 'Su kien nay co thu gom pin cu va do dien tu khong?',                               NOW() - INTERVAL 10 HOUR, 1, 0),
-- Event 2
(4,  2, 9,  'Workshop qua hay! Co the dang ky cho ca nhom ban khong?',                          NOW() - INTERVAL 15 HOUR, 1, 0),
(5,  2, 12, 'Nguyen vat lieu tai che co san hay minh tu mang theo?',                            NOW() - INTERVAL 12 HOUR, 1, 0),
-- Event 3
(6,  3, 6,  'Cho minh hoi co can mang dung cu tu khong hay BTC se chuan bi?',                  NOW() - INTERVAL 14 HOUR, 1, 0),
(7,  3, 13, 'Co the mang cay giong tu nha den tang them khong?',                                NOW() - INTERVAL 9 HOUR,  1, 0),
-- Event 4
(8,  4, 6,  'Chuong trinh hay, co ho tro tai lieu cho tinh nguyen vien day ky nang khong?',    NOW() - INTERVAL 8 HOUR,  1, 0),
(9,  4, 10, 'Tre em mang theo do dung hoc tap hay BTC chuan bi san?',                           NOW() - INTERVAL 5 HOUR,  1, 0),
-- Event 5
(10, 5, 14, 'Chuong trinh hien mau co phat qua luu niem cho nguoi hien khong?',                NOW() - INTERVAL 7 HOUR,  1, 0),
-- Event 6
(11, 6, 5,  'Tieu chi xet hoc bong cu the la gi, co website de xem khong?',                    NOW() - INTERVAL 6 HOUR,  1, 0),
(12, 6, 11, 'Rat vui neu chuong trinh nay co the mo rong ra cac tinh khac.',                   NOW() - INTERVAL 3 HOUR,  1, 0),
-- Event 7
(13, 7, 9,  'Toi co the mang them phan bon huu co khong?',                                     NOW() - INTERVAL 6 HOUR,  1, 0),
(14, 7, 15, 'Minh se co mat som de chup anh tu lieu cho su kien.',                             NOW() - INTERVAL 5 HOUR,  1, 0),
-- Event 8
(15, 8, 12, 'Lop nay co han do tuoi tham gia khong? Con minh muoi tuoi co duoc khong?',        NOW() - INTERVAL 4 HOUR,  1, 0),
-- Event 9
(16, 9, 14, 'Minh muon thue mot gian hang nho de ban rau sach tu vuon nha co duoc khong?',    NOW() - INTERVAL 5 HOUR,  1, 0),
(17, 9, 4,  'Phien cho co nhan trao doi hang khong, khong chi mua ban tien mat?',              NOW() - INTERVAL 3 HOUR,  1, 0),
-- Event 10
(18, 10, 14,'Rat mong den ngay kham suc khoe mien phi, ba ngoai toi rat can duoc tu van.',     NOW() - INTERVAL 4 HOUR,  1, 0),
(19, 10, 13,'Co the dang ky them mot suat cho nguoi than cung di kham khong?',                 NOW() - INTERVAL 2 HOUR,  1, 0),
-- Event 11
(20, 11, 12,'Com chay mien phi rat y nghia, cam on BTC da to chuc!',                           NOW() - INTERVAL 3 HOUR,  1, 0),
(21, 11, 5, 'Hoat dong rat thiet thuc, minh se rut kinh nghiem de lan sau tham gia.',          NOW() - INTERVAL 1 HOUR,  1, 0),
-- Event 12
(22, 12, 6, 'Rat can thiet! Bai bien My Khe gan day nhieu rac lam roi.',                       NOW() - INTERVAL 2 HOUR,  1, 0),
(23, 12, 11,'Minh muon tham gia ca hoi thao lan don bai bien, dang ky o dau?',                 NOW() - INTERVAL 1 HOUR,  1, 0);

-- =====================================================
-- 8) EVENT FAVORITES
-- =====================================================
INSERT INTO event_favorites (event_id, user_id, favorite_date) VALUES
-- Event 1
(1,  4,  NOW() - INTERVAL 5 DAY),
(1,  5,  NOW() - INTERVAL 4 DAY),
(1,  9,  NOW() - INTERVAL 3 DAY),
(1,  13, NOW() - INTERVAL 2 DAY),
-- Event 2
(2,  4,  NOW() - INTERVAL 4 DAY),
(2,  11, NOW() - INTERVAL 3 DAY),
-- Event 3
(3,  6,  NOW() - INTERVAL 3 DAY),
(3,  13, NOW() - INTERVAL 2 DAY),
(3,  15, NOW() - INTERVAL 1 DAY),
-- Event 4
(4,  6,  NOW() - INTERVAL 2 DAY),
(4,  10, NOW() - INTERVAL 1 DAY),
-- Event 5
(5,  5,  NOW() - INTERVAL 3 DAY),
(5,  14, NOW() - INTERVAL 2 DAY),
-- Event 6
(6,  5,  NOW() - INTERVAL 2 DAY),
(6,  11, NOW() - INTERVAL 1 DAY),
-- Event 7
(7,  9,  NOW() - INTERVAL 3 DAY),
(7,  15, NOW() - INTERVAL 2 DAY),
-- Event 8
(8,  12, NOW() - INTERVAL 2 DAY),
-- Event 9
(9,  4,  NOW() - INTERVAL 2 DAY),
(9,  14, NOW() - INTERVAL 1 DAY),
-- Event 10
(10, 14, NOW() - INTERVAL 3 DAY),
(10, 12, NOW() - INTERVAL 2 DAY),
(10, 5,  NOW() - INTERVAL 1 DAY),
-- Event 11
(11, 12, NOW() - INTERVAL 2 DAY),
(11, 5,  NOW() - INTERVAL 1 DAY),
-- Event 12
(12, 6,  NOW() - INTERVAL 2 DAY),
(12, 11, NOW() - INTERVAL 1 DAY),
(12, 15, NOW() - INTERVAL 12 HOUR);

-- =====================================================
-- 9) EVENT REPORTS
-- =====================================================
INSERT INTO event_reports
(id, event_id, user_id, report_reason, report_date, status)
VALUES
(1, 5,  4,  'Can bo sung thong tin dia diem cu the va so luong slot con lai.',     NOW() - INTERVAL 6 HOUR,  'Pending'),
(2, 8,  11, 'Thong tin lop hoc chua ro rang ve so luong hoc vien toi da.',         NOW() - INTERVAL 5 HOUR,  'Pending'),
(3, 10, 13, 'Dia chi to chuc can duoc cap nhat chinh xac hon.',                    NOW() - INTERVAL 3 HOUR,  'Pending'),
(4, 12, 5,  'Mo ta su kien qua ngan, can them thong tin ve chuong trinh cu the.',  NOW() - INTERVAL 1 HOUR,  'Pending');

-- =====================================================
-- 10) REVIEWS
-- =====================================================
INSERT INTO reviews
(id, user_id, organization_id, rating, comment, created_at)
VALUES
-- Org 1 – CLB Song Xanh (existing unchanged)
(1, 4, 1, 5, 'To chuc chuyen nghiep, hoat dong rat bai ban.',                                  NOW() - INTERVAL 5 DAY),
(2, 5, 1, 4, 'Thong tin ro rang, tinh nguyen vien duoc ho tro tot.',                           NOW() - INTERVAL 4 DAY),
-- Org 2 – Tam Long Tre (existing unchanged)
(3, 6, 2, 4, 'Noi dung chuong trinh thiet thuc, can them khung gio linh hoat.',               NOW() - INTERVAL 3 DAY),
-- Org 3 – Hanh Phuc Xanh (new)
(4, 9,  3, 4, 'Du an trong cay rat thiet thuc, mong co them chuong trinh tuong tu.',          NOW() - INTERVAL 2 DAY),
(5, 10, 3, 4, 'Ban to chuc nhiet tinh, chuong trinh duoc chuan bi ky cang.',                  NOW() - INTERVAL 1 DAY),
-- Org 4 – Nhan Ai Da Nang (new)
(6, 14, 4, 5, 'Hoat dong tu thien rat co y nghia, tinh nguyen vien phuc vu tan tam.',        NOW() - INTERVAL 2 DAY),
(7, 12, 4, 4, 'Chuong trinh phat com chay duoc to chuc gon gang, sach se, chat luong tot.',  NOW() - INTERVAL 1 DAY);

-- =====================================================
-- 11) DONATIONS
-- =====================================================
INSERT INTO donations
(id, donor_name, amount, phone_number, message, is_paid, transaction_code, created_at)
VALUES
(1, 'Nguyen Thi Hoa',  500000,  '0912000001', 'Ung ho chuong trinh trong cay',               1, 'TXN000001', NOW() - INTERVAL 7 DAY),
(2, 'Tran Quoc Minh', 1000000, '0912000002', 'Chuc du an thanh cong',                        1, 'TXN000002', NOW() - INTERVAL 6 DAY),
(3, 'Le Thanh Tung',   300000,  '0912000003', 'Dong hanh cung cong dong',                    0, NULL,        NOW() - INTERVAL 5 DAY),
(4, 'Pham Thi Lan',    200000,  '0912000004', 'Ung ho tre em kho khan hoc bong Uoc Mo Xanh', 1, 'TXN000004', NOW() - INTERVAL 4 DAY),
(5, 'Vo Minh Khoa',    750000,  '0912000005', 'Ung ho chuong trinh kham suc khoe mien phi',  1, 'TXN000005', NOW() - INTERVAL 3 DAY),
(6, 'Bui Thi Cam',     150000,  '0912000006', 'Chut long thanh tam gop quy com chay',        1, 'TXN000006', NOW() - INTERVAL 2 DAY),
(7, 'Nguyen Duc Tai',  500000,  '0912000007', 'Ung ho quy hoc bong moi truong',              0, NULL,        NOW() - INTERVAL 1 DAY),
(8, 'Tran Thi My',    2000000,  '0912000008', 'Dong gop cho cac em nho vung sau Da Nang',    1, 'TXN000008', NOW() - INTERVAL 18 HOUR),
(9, 'Ho Van Phuc',     400000,  '0912000009', 'Gop suc bao ve moi truong bien',              1, 'TXN000009', NOW() - INTERVAL 12 HOUR),
(10,'Ly Thi Bich',     600000,  '0912000010', 'Ung ho workshop tai che sang tao',            0, NULL,        NOW() - INTERVAL 6 HOUR);

-- =====================================================
-- 12) NEWSLETTER SUBSCRIPTIONS
-- =====================================================
INSERT INTO newsletter_subscriptions
(id, email, active, subscribed_at)
VALUES
(1, 'updates1@tinhnguyenxanh.vn', 1, NOW() - INTERVAL 10 DAY),
(2, 'updates2@tinhnguyenxanh.vn', 1, NOW() - INTERVAL 8 DAY),
(3, 'updates3@tinhnguyenxanh.vn', 1, NOW() - INTERVAL 6 DAY),
(4, 'oldsubscriber@tinhnguyenxanh.vn', 0, NOW() - INTERVAL 12 DAY);

-- =====================================================
-- RESET AUTO INCREMENT
-- =====================================================
ALTER TABLE users               AUTO_INCREMENT = 16;
ALTER TABLE event_categories    AUTO_INCREMENT = 5;
ALTER TABLE organizations       AUTO_INCREMENT = 5;
ALTER TABLE volunteers          AUTO_INCREMENT = 11;
ALTER TABLE events              AUTO_INCREMENT = 13;
ALTER TABLE event_registrations AUTO_INCREMENT = 30;
ALTER TABLE event_comments      AUTO_INCREMENT = 24;
ALTER TABLE event_reports       AUTO_INCREMENT = 5;
ALTER TABLE reviews             AUTO_INCREMENT = 8;
ALTER TABLE donations           AUTO_INCREMENT = 11;
ALTER TABLE newsletter_subscriptions AUTO_INCREMENT = 5;