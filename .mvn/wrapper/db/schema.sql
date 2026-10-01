-- ============================================================
-- gemal "website" legacy application database
-- Reconstructed from SampleController.java
-- ============================================================


-- ------------------------------------------------------------
-- POSTS
--
-- Application writes:
--   text, name, time, pinner
--
-- Application reads:
--   id, text, name, time, pinner
-- ------------------------------------------------------------

CREATE TABLE posts (
    id      SERIAL PRIMARY KEY,
    text    TEXT NOT NULL,
    name    VARCHAR(255) NOT NULL,
    time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pinner  VARCHAR(255)
);


-- ------------------------------------------------------------
-- COMMENTS
--
-- Application writes:
--   name, comment, id
--
-- Application reads:
--   name, comment, id, time
--
-- IMPORTANT:
-- "id" appears to represent the post ID rather than a unique
-- comment ID. Therefore it is intentionally NOT the PK.
-- ------------------------------------------------------------

CREATE TABLE comment (
    name     VARCHAR(255) NOT NULL,
    comment  TEXT NOT NULL,
    id       INTEGER NOT NULL,
    time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ------------------------------------------------------------
-- ORDERS
--
-- Application inserts only:
--   email
--
-- and immediately executes:
--   RETURNING orderid
--
-- Therefore orderid must be automatically generated.
-- ------------------------------------------------------------

CREATE TABLE orders (
    orderid  SERIAL PRIMARY KEY,
    email    VARCHAR(255) NOT NULL
);


-- ------------------------------------------------------------
-- ITEMS
--
-- Each item belongs to an order.
--
-- Application writes:
--   orderid, name, quantity, price
--
-- Application reads:
--   orderid, name, quantity, price
--
-- The application uses:
--
-- SELECT *
-- FROM (item NATURAL JOIN orders)
-- WHERE email = ...
--
-- Therefore "orderid" MUST have the exact same column name
-- in both tables.
-- ------------------------------------------------------------

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


-- ------------------------------------------------------------
-- RATINGS
--
-- Application writes:
--   score, email, title, comment, reviewer, itemName
--
-- Application reads:
--   score, email, title, comment, reviewer, itemName, date
--
-- ------------------------------------------------------------

CREATE TABLE rating (
    score     SMALLINT NOT NULL,
    email     VARCHAR(255),
    title     VARCHAR(255),
    comment   TEXT,
    reviewer  VARCHAR(255),
    itemName  VARCHAR(255) NOT NULL,
    date      DATE DEFAULT CURRENT_DATE
);


-- ------------------------------------------------------------
-- Helpful indexes
-- Not strictly required by the old application.
-- ------------------------------------------------------------

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