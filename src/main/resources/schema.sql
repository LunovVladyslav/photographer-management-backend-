CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS clients
(
    id           UUID NOT NULL ,
    name         VARCHAR(255),
    email        VARCHAR(255),
    phone_number VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_clients PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_email ON clients (email);

CREATE INDEX IF NOT EXISTS idx_name ON clients (name);

CREATE INDEX IF NOT EXISTS idx_phone_number ON clients (phone_number);

-- Create join table
CREATE TABLE session_photos (
                                session_id UUID NOT NULL,
                                photo_id UUID NOT NULL,
                                PRIMARY KEY (session_id, photo_id),
                                FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
                                FOREIGN KEY (photo_id) REFERENCES photos(id) ON DELETE CASCADE
);

-- Migrate existing data (if photos had session_id column)
INSERT INTO session_photos (session_id, photo_id)
SELECT session_id, id FROM photos WHERE session_id IS NOT NULL;

-- Add primary_session_id column
ALTER TABLE photos ADD COLUMN primary_session_id UUID;
UPDATE photos SET primary_session_id = session_id WHERE session_id IS NOT NULL;
ALTER TABLE photos ADD FOREIGN KEY (primary_session_id) REFERENCES sessions(id);

-- Drop old session_id column
ALTER TABLE photos DROP COLUMN session_id;