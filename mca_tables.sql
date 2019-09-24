CREATE TABLE IF NOT EXISTS members (
	member_id VARCHAR(255) NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	mid_name VARCHAR(255) NOT NULL DEFAULT "",
	last_name VARCHAR(255) NOT NULL,
	street_address VARCHAR(255) NOT NULL,
	address_pt2 VARCHAR(255),
	city VARCHAR(255) NOT NULL,
	zip_code VARCHAR(5) NOT NULL,
	email VARCHAR(255) NOT NULL,
	home_num VARCHAR(255) NOT NULL DEFAULT "",
	cell_num VARCHAR(255) NOT NULL DEFAULT "",
	gender VARCHAR(255) NOT NULL DEFAULT "Female",
	admin_lvl SMALLINT NOT NULL DEFAULT 0,
	PRIMARY KEY (member_id)
);

ALTER TABLE members ADD FULLTEXT(member_id,first_name, mid_name, last_name);

ALTER TABLE members ADD FULLTEXT(member_id,first_name, mid_name, last_name, street_address, address_pt2, city, zip_code, email, home_num, cell_num, gender);

CREATE TABLE IF NOT EXISTS member_registration (
	member_id VARCHAR(255) NOT NULL,
	hijri_year VARCHAR(9) NOT NULL,
	membership_date DATETIME NOT NULL,
	ip_address VARCHAR(255) NOT NULL,
	prepaid SMALLINT NOT NULL,
	payment_date DATETIME NOT NULL,
	PRIMARY KEY (member_id, hijri_year)
);

CREATE TABLE IF NOT EXISTS hijri_calendar (
	hijri_year INT NOT NULL,
	hijri_eng VARCHAR(9),
	shawwal_first DATETIME,
	ramadan_last DATETIME,
	PRIMARY KEY (hijri_year)
);

INSERT INTO hijri_calendar (hijri_year, shawwal_first)
VALUES
	(1420, '2000-01-08 00:00:00'),
	(1421, '2000-12-27 00:00:00'),
	(1422, '2001-12-16 00:00:00'),
	(1423, '2002-12-05 00:00:00'),
	(1424, '2003-11-25 00:00:00'),
	(1425, '2004-11-14 00:00:00'),
	(1426, '2005-11-03 00:00:00'),
	(1427, '2006-10-23 00:00:00'),
	(1428, '2007-10-13 00:00:00'),
	(1429, '2008-10-01 00:00:00'),
	(1430, '2009-09-20 00:00:00'),
	(1431, '2010-09-10 00:00:00'),
	(1432, '2011-08-30 00:00:00'),
	(1433, '2012-08-19 00:00:00'),
	(1434, '2013-08-08 00:00:00'),
	(1435, '2014-07-28 00:00:00'),
	(1436, '2015-07-17 00:00:00'),
	(1437, '2016-07-06 00:00:00'),
	(1438, '2017-06-25 00:00:00'),
	(1439, '2018-06-15 00:00:00'),
	(1440, '2019-06-04 00:00:00'),
	(1441, '2020-05-24 00:00:00'),
	(1442, '2021-05-13 00:00:00'),
	(1443, '2022-05-02 00:00:00'),
	(1444, '2023-04-21 00:00:00'),
	(1445, '2024-04-10 00:00:00'),
	(1446, '2025-03-30 00:00:00'),
	(1447, '2026-03-20 00:00:00'),
	(1448, '2027-03-09 00:00:00'),
	(1449, '2028-02-26 00:00:00'),
	(1450, '2029-02-14 00:00:00');

