CREATE TABLE images (
    id BIGSERIAL PRIMARY KEY,
    content_type VARCHAR(255),
    filename VARCHAR(255),
    data BYTEA
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    genders VARCHAR(255),
    image_id BIGINT,
    email VARCHAR(255) UNIQUE,
    provider VARCHAR(255),
    CONSTRAINT fk_image FOREIGN KEY (image_id) REFERENCES images (id) ON DELETE SET NULL
);

CREATE TABLE user_phone_nos (
    id BIGSERIAL PRIMARY KEY,
    phone_no VARCHAR(255),
    country_code VARCHAR(255),
    userId BIGINT UNIQUE,
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_address (
    address_id BIGSERIAL PRIMARY KEY,
    country VARCHAR(255) NOT NULL,
    latitude PRECISION DOUBLE,
    longitude PRECISION DOUBLE,
    address_details VARCHAR(255),
    userId BIGINT UNIQUE,
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_roles (
    userId BIGINT NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (userId, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    role_type VARCHAR(20)
);

CREATE TABLE confirmation_tokens (
    confirmation_tokens_id BIGSERIAL PRIMARY KEY,
    confirm_token VARCHAR(255) NOT NULL, -- Adjust length as needed
    created_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    userId BIGINT NOT NULL,
    CONSTRAINT fk_confirmation_tokens_user FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
);
