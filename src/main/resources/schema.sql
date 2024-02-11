CREATE TABLE IF NOT EXISTS categories (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS restaurants (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,  
	image_name VARCHAR(255),
	description VARCHAR(255) NOT NULL,
	lower_price INT NOT NULL,
	upper_price INT NOT NULL,
	address VARCHAR(255) NOT NULL,
	phone_number VARCHAR(50) NOT NULL,
	opening_time TIME NOT NULL,
	closing_time TIME NOT NULL,
	regular_holiday VARCHAR(10) NOT NULL,
	capacity INT NOT NULL, 
	category_id INT NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (category_id) REFERENCES categories (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
 

CREATE TABLE IF NOT EXISTS roles (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS users (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	postal_code VARCHAR(50) NOT NULL,
	address VARCHAR(255) NOT NULL,
	phone_number VARCHAR(50) NOT NULL,
	email VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
	role_id INT NOT NULL,
	enabled BOOLEAN NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (role_id) REFERENCES roles (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS verification_tokens (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL UNIQUE,
	token VARCHAR(255) NOT NULL,        
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users (id) 
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 
CREATE TABLE IF NOT EXISTS reservations (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	restaurant_id INT NOT NULL,
	user_id INT NOT NULL,
	reservation_date DATE NOT NULL,
	reservation_time TIME NOT NULL,
	number_of_people INT NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
	FOREIGN KEY (user_id) REFERENCES users (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS favorites (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	restaurant_id INT NOT NULL,
	user_id INT NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
	FOREIGN KEY (user_id) REFERENCES users (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS reviews (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	restaurant_id INT NOT NULL,
	user_id INT NOT NULL,
	review_comment VARCHAR(255) NOT NULL,
	review_star INT NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
	FOREIGN KEY (user_id) REFERENCES users (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS subscriptions (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	subscription_id VARCHAR(50) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS company (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	establish VARCHAR(50) NOT NULL,
	postal_code VARCHAR(50) NOT NULL,
	address VARCHAR(255) NOT NULL,
	phone_number VARCHAR(50) NOT NULL,
	president VARCHAR(50) NOT NULL,
	employee INT NOT NULL,
	capital INT NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
