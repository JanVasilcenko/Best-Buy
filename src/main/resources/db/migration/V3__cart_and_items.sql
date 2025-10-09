CREATE TABLE IF NOT EXISTS cart (
id VARCHAR(36) NOT NULL,
user_id VARCHAR(36) NOT NULL,
created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
PRIMARY KEY (id),
UNIQUE KEY uk_cart_user (user_id),
FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS cart_item (
id VARCHAR(36) NOT NULL,
cart_id VARCHAR(36) NOT NULL,
product_id VARCHAR(36) NOT NULL,
quantity INT NOT NULL,
unit_price INT NOT NULL,
created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
PRIMARY KEY (id),
UNIQUE KEY uk_cart_item_unique (cart_id, product_id),
KEY idx_cart_item_cart (cart_id),
KEY odx_cart_item_product (product_id),
FOREIGN KEY (cart_id) REFERENCES cart (id),
FOREIGN KEY (product_id) REFERENCES product (id)
)