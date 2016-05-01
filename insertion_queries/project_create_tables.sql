DROP TABLE CHARGES;
DROP TABLE bill;
DROP TABLE TEXT_LOG;
DROP TABLE CALL_LOG;
DROP TABLE INTERNET_USAGE;
DROP TABLE USED_BY;
DROP TABLE PHONE_NUMBER;
DROP TABLE ACCOUNT;
DROP TABLE customer;
DROP TABLE PHONE_PRODUCT;
DROP TABLE STORE;
DROP TABLE PHONE_MODEL;

CREATE TABLE CUSTOMER (
  c_id    NUMBER,
  name    VARCHAR(255) NOT NULL,
  address VARCHAR(255),
  PRIMARY KEY (c_id)
);

CREATE TABLE ACCOUNT (
  a_id     NUMBER(5),
  a_status VARCHAR(50) NOT NULL CHECK (A_STATUS in ('IN_SERVICE', 'RETIRED')),
  c_id     NUMBER(5),
  primary_number NUMBER(10),
  current_plan VARCHAR(25),
  PRIMARY KEY (a_id),
  FOREIGN KEY (c_id) REFERENCES CUSTOMER (c_id),
  FOREIGN KEY (primary_number) REFERENCES PHONE_NUMBER(phone_number),
  FOREIGN KEY (current_plan) REFERENCES PLANS(P_TYPE)
);

CREATE TABLE SUBSCRIBES (
  a_id NUMBER(5),
  c_id NUMBER(5),
  is_owner number(1) DEFAULT (0) NOT NULL,
  PRIMARY KEY (a_id, c_id),
  FOREIGN KEY (a_id) REFERENCES ACCOUNT ON DELETE CASCADE,
  FOREIGN KEY (c_id) REFERENCES CUSTOMER ON DELETE CASCADE,
  CONSTRAINT c_is_owner
  check (is_owner in (0, 1))
);

CREATE TABLE BILL (
  a_id        NUMBER(5),
  bill_id     NUMBER(10),
  bill_period DATE NULL, 
  is_paid     NUMBER(1) DEFAULT 0 NOT NULL CHECK ( is_paid IN (1, 0)),
  plan        VARCHAR(10) NOT NULL,
  PRIMARY KEY (bill_id),
  FOREIGN KEY (a_id) REFERENCES ACCOUNT (a_id) ON DELETE SET NULL,
  FOREIGN KEY (plan) REFERENCES PLANS (P_TYPE) ON DELETE SET NULL
);

--insert into bill(BILL_ID, PLAN_ID, IS_PAID, BILL_PERIOD) VALUES (2, 'CORPORATE_LIMIT', 0, to_date('2000-10-19 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

create table PLANS (
  p_type VARCHAR(55),
  PRIMARY KEY (p_type)
);

insert into PLANS VALUES ('RESIDENT_AS_USED');
insert into PLANS VALUES ('RESIDENT_LIMIT');
insert into PLANS VALUES ('CORPORATE_AS_USED');
insert into PLANS VALUES ('CORPORATE_LIMIT');

CREATE TABLE BILL_DETAILS (
  bill_id             NUMBER(10),
  accumulated_minutes NUMBER(10),
  accumulated_mb      NUMBER(10),
  accumulated_texts   NUMBER(10),
  PRIMARY KEY (bill_id),
  FOREIGN KEY (bill_id) REFERENCES BILL(bill_id) on DELETE CASCADE
);

CREATE TABLE PHONE_NUMBER (
  phone_number      NUMBER(10),
  c_id              NUMBER(5),
  a_id              NUMBER(5) UNIQUE,
  is_in_service     NUMBER(1) DEFAULT 0 NOT NULL CHECK (is_in_service IN (1, 0)),
  is_primary_number NUMBER(1) DEFAULT 0 NOT NULL CHECK (is_primary_number IN (1, 0)),
  PRIMARY KEY (phone_number),
  FOREIGN KEY (c_id)
  REFERENCES CUSTOMER (c_id) ON DELETE SET NULL,
  FOREIGN KEY (a_id)
  REFERENCES ACCOUNT (a_id) ON DELETE SET NULL
);

CREATE TABLE TEXT_LOG (
  text_id       NUMBER(10),
  source_phone  NUMBER(10),
  dest_phone    NUMBER(10),
  time_sent     DATE,
  time_received DATE,
  PRIMARY KEY (text_id),
  FOREIGN KEY (source_phone)
  REFERENCES PHONE_NUMBER (phone_number)
  ON DELETE SET NULL,
  FOREIGN KEY (dest_phone)
  REFERENCES PHONE_NUMBER (phone_number)
  ON DELETE SET NULL
);

CREATE TABLE CALL_LOG (
  call_id      NUMBER(10),
  source_phone NUMBER(10),
  dest_phone   NUMBER(10),
  start_time   DATE,
  end_time     DATE,
  PRIMARY KEY (call_id),
  FOREIGN KEY (source_phone)
  REFERENCES PHONE_NUMBER (phone_number)
  ON DELETE SET NULL,
  FOREIGN KEY (dest_phone)
  REFERENCES PHONE_NUMBER (phone_number)
  ON DELETE SET NULL
);

CREATE TABLE INTERNET_USAGE (
  usage_id     NUMBER(10),
  usage_period DATE,
  source_phone NUMBER(10),
  byte_size    NUMBER(8),
  PRIMARY KEY (usage_id),
  FOREIGN KEY (source_phone)
  REFERENCES PHONE_NUMBER (phone_number)
  ON DELETE SET NULL
);

CREATE TABLE CHARGES (
  bill_id    NUMBER(5),
  text_id    NUMBER(10),
  call_id    NUMBER(10),
  i_usage_id NUMBER(10),
  PRIMARY KEY (bill_id),
  FOREIGN KEY (bill_id)
  REFERENCES BILL (bill_id) ON DELETE CASCADE,
  FOREIGN KEY (text_id)
  REFERENCES TEXT_LOG (text_id) ON DELETE SET NULL,
  FOREIGN KEY (call_id)
  REFERENCES CALL_LOG (call_id) ON DELETE SET NULL,
  FOREIGN KEY (i_usage_id)
  REFERENCES INTERNET_USAGE (usage_id) ON DELETE SET NULL
);

CREATE TABLE STORE (
  store_number NUMBER(8),
  address      VARCHAR(255),
  PRIMARY KEY (store_number)
);

CREATE TABLE PHONE_MODEL (
  phone_id     NUMBER(5),
  manufacturer VARCHAR(50),
  model        VARCHAR(50),
  PRIMARY KEY (phone_id)
);

CREATE TABLE PHONE_PRODUCT (
  meid     NUMBER(10),
  p_status VARCHAR(10) default 'STOCKED' CHECK (P_STATUS in ('STOCKED', 'LOST', 'STOLEN', 'IN_USE')),
  PHONE_id number(5),
  PRIMARY KEY (meid),
  FOREIGN KEY (phone_id) references PHONE_MODEL on DELETE CASCADE
);

CREATE TABLE USED_BY (
  meid         NUMBER(10),
  phone_number NUMBER(10),
  PRIMARY KEY (meid, phone_number),
  FOREIGN KEY (meid)
  REFERENCES PHONE_PRODUCT (meid) ON DELETE CASCADE,
  FOREIGN KEY (phone_number)
  REFERENCES PHONE_NUMBER (phone_number) ON DELETE CASCADE
);

create table stocks (
	meid number(10),
	store_number number(5),
	primary key (meid, store_number),
	  FOREIGN KEY (meid) REFERENCES PHONE_PRODUCT(meid),
  FOREIGN KEY (store_number) REFERENCES store(store_number)
);

create table sold(
  meid  NUMBER(10),
  store_number NUMBER(5),
  PRIMARY KEY (meid, store_number),
  FOREIGN KEY (meid) REFERENCES PHONE_PRODUCT(meid),
  FOREIGN KEY (store_number) REFERENCES store(store_number)
)