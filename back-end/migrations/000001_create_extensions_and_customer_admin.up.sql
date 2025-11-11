CREATE TABLE IF NOT EXISTS customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    avatar_url VARCHAR,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS admin (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    avatar_url VARCHAR,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS invalid_token (
    token TEXT PRIMARY KEY,
    expires TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO admin (id, name, password, email)
VALUES (
    gen_random_uuid(),
    'Owner',
    '$2a$12$uWKbKpJVPz65kqb1RIHhHeYr.cuokKHA1lKfNLPyg9MbZlabGrkha',
    'owner@gmail.com'
);
