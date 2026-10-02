-- ============================================================
-- Legacy website PostgreSQL schema
-- Reconstructed from the original Java application
-- ============================================================


-- POSTS
-- Used for the social/news feed.
--
-- Java writes:
--   text, name, time, pinner
--
-- Java reads:
--   id, text, name, time, pinner

CREATE TABLE posts (
    id      SERIAL PRIMARY KEY,
    text    TEXT NOT NULL,
    name    VARCHAR(255) NOT NULL,
    time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pinner  VARCHAR(255)
);


-- COMMENTS
-- Comments belong to posts.
--
-- Note: the original application appears to use "id"
-- as the associated post ID rather than as a unique
-- comment ID.

CREATE TABLE comment (
    name     VARCHAR(255) NOT NULL,
    comment  TEXT NOT NULL,
    id       INTEGER NOT NULL,
    time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ORDERS
-- Each order belongs to a user identified by email.
--
-- The Java code executes:
--
-- INSERT INTO orders (email)
-- RETURNING orderid
--
-- Therefore orderid must be generated automatically.

CREATE TABLE orders (
    orderid  SERIAL PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    order_date  DATE NOT NULL DEFAULT CURRENT_DATE
);


-- ITEMS
-- Individual items belonging to an order.
--
-- Java writes:
--   orderid, name, quantity, price
--
-- Java also performs:
--
-- SELECT *
-- FROM (item NATURAL JOIN orders)
-- WHERE email = ...
--
-- Therefore item.orderid and orders.orderid need
-- the same column name.

CREATE TABLE item (
    orderid   INTEGER NOT NULL,
    name      VARCHAR(255) NOT NULL,
    quantity  INTEGER NOT NULL,
    price     REAL NOT NULL,

    CONSTRAINT fk_item_order
        FOREIGN KEY (orderid)
        REFERENCES orders(orderid)
        ON DELETE CASCADE
);


-- RATINGS
-- Food/item reviews.
--
-- Java writes:
--   score, email, title, comment, reviewer, itemName
--
-- Java reads:
--   score, email, title, comment, reviewer, itemName, date

CREATE TABLE rating (
    score     SMALLINT NOT NULL,
    email     VARCHAR(255),
    title     VARCHAR(255),
    comment   TEXT,
    reviewer  VARCHAR(255),
    itemName  VARCHAR(255) NOT NULL,
    date      DATE DEFAULT CURRENT_DATE
);


-- ============================================================
-- INDEXES
-- Not strictly required by the application, but sensible
-- for the queries used by the original code.
-- ============================================================

CREATE INDEX idx_posts_pinner
    ON posts(pinner);

CREATE INDEX idx_comment_post
    ON comment(id);

CREATE INDEX idx_orders_email
    ON orders(email);

CREATE INDEX idx_item_orderid
    ON item(orderid);

CREATE INDEX idx_rating_itemname
    ON rating(itemName);