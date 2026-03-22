-- カテゴリのサンプルデータ
INSERT IGNORE INTO categories (id,name,created_at,updated_at)
VALUES
(1,'和食',NOW(),NOW());

--店舗のサンプルデータ
INSERT IGNORE INTO stores (id,name,description,address,min_price,max_price,image_path,category_id,created_at,updated_at)
VALUES
(1,'寿司 花','新鮮な寿司を提供','東京都渋谷区1-1-1',2000,8000,'sushi.jpg',1,NOW(),NOW()),
(2,'ラーメン一番','濃厚豚骨ラーメン','東京都新宿区2-2-2',800,1200,'ramen.jpg',1,NOW(),NOW()),
(3,'焼肉キング','食べ放題焼肉','東京都池袋3-3-3',3000,5000,'yakiniku.jpg',1,NOW(),NOW()),
(4,'イタリアンバル','本格イタリア料理','東京都銀座4-4-4',2500,7000,'italian.jpg',1,NOW(),NOW()),
(5,'カフェブルー','おしゃれカフェ','東京都表参道5-5-5',1000,3000,'cafe.jpg',1,NOW(),NOW()),
(6,'天ぷら匠','職人の天ぷら','東京都浅草6-6-6',4000,9000,'tempura.jpg',1,NOW(),NOW()),
(7,'中華楼','本格中華','東京都上野7-7-7',1500,4000,'chinese.jpg',1,NOW(),NOW()),
(8,'ステーキハウス','極上ステーキ','東京都六本木8-8-8',5000,12000,'steak.jpg',1,NOW(),NOW()),
(9,'居酒屋楽','仕事帰りの一杯','東京都品川9-9-9',2000,4500,'izakaya.jpg',1,NOW(),NOW()),
(10,'うどん丸','手打ちうどん','東京都吉祥寺10-10-10',700,1500,'udon.jpg',1,NOW(),NOW()),
(11,'カレー専門店','スパイスカレー','東京都秋葉原11-11-11',900,2000,'curry.jpg',1,NOW(),NOW());

-- 会員のサンプルデータ
INSERT IGNORE INTO users (name, email, password, role, membership_type, enabled, created_at, updated_at)
VALUES
('山田太郎', 'taro.yamada@example.com', 'password123', 'USER', 'FREE', TRUE, NOW(), NOW()),
('鈴木花子', 'hanako.suzuki@example.com', 'password123', 'USER', 'PREMIUM', TRUE, NOW(), NOW()),
('佐藤次郎', 'jiro.sato@example.com', 'password123', 'USER', 'FREE', TRUE, NOW(), NOW()),
('田中美咲', 'misaki.tanaka@example.com', 'password123', 'USER', 'PREMIUM', TRUE, NOW(), NOW()),
('高橋健一', 'kenichi.takahashi@example.com', 'password123', 'USER', 'FREE', TRUE, NOW(), NOW()),
('伊藤彩', 'aya.ito@example.com', 'password123', 'USER', 'PREMIUM', TRUE, NOW(), NOW()),
('渡辺直樹', 'naoki.watanabe@example.com', 'password123', 'USER', 'FREE', TRUE, NOW(), NOW()),
('中村優子', 'yuko.nakamura@example.com', 'password123', 'USER', 'PREMIUM', TRUE, NOW(), NOW()),
('小林太郎', 'taro.kobayashi@example.com', 'password123', 'USER', 'FREE', TRUE, NOW(), NOW()),
('加藤美香', 'mika.kato@example.com', 'password123', 'ADMIN', 'PREMIUM', TRUE, NOW(), NOW()),
('吉田浩', 'hiroshi.yoshida@example.com', 'password123', 'USER', 'FREE', TRUE, NOW(), NOW());