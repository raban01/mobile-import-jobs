create database emmd;
create table base_aisle_data (store_id int, group_id int, group_nm string, category_id int, category_nm string, aisle string);
create table cop_ttl (id int);
create table cs_offer_ttl (id int);
create table dept_cop_ttl (id int);
create table j4u_ttl (id int);
create table join_cs_offer (id int);
create table join_ycs_offer (id int);
create table open_stores (id int);
create table product_ttl (id int);
create table smic (id int);
create table upc2j4ucatmap (id int);
create table store_status_disabled (id int);
create table store_status (id int);
create table store (id int);
create table upc2j4u_ttl (id int);
create table ycs_offer_ttl (id int);
create table category_offer_ttl (id int);
create table group_offer_ttl (id int);
create table final_upc2j4u (id int);

CREATE TABLE IF NOT EXISTS perimeter_aisle_mapping (department_id int, aisle string);
		
INSERT INTO TABLE perimeter_aisle_mapping SELECT 301, 'Other' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 303, 'Wine, Beer & Spirits' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 304, 'Pharmacy' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 305, 'Wine, Beer & Spirits' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 306, 'Deli' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 309, 'Deli' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 310, 'Front End' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 311, 'Other' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 312, 'Pharmacy' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 314, 'Dairy' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 315, 'Floral' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 316, 'Bakery' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 317, 'Other' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 323, 'Dairy' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 324, 'Dairy' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 325, 'Dairy' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 328, 'Check Stand' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 329, 'Produce' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 330, 'Meat & Seafood' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 331, 'Meat & Seafood' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 333, 'Meat & Seafood' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 336, 'Bakery' FROM store_status LIMIT 1;
INSERT INTO TABLE perimeter_aisle_mapping SELECT 347, 'Check Stand' FROM store_status LIMIT 1;=======

