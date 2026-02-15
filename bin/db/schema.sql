PRAGMA foreign_keys = ON;

-- =========================
-- (ADMIN) TABLES
-- =========================
-- Parking spots (used for occupancy reports)
CREATE TABLE IF NOT EXISTS parking_spots (
    spot_id     TEXT PRIMARY KEY,
    floor_id    TEXT NOT NULL,
    row_id      TEXT NOT NULL,
    spot_type   TEXT NOT NULL,
    status      TEXT NOT NULL
);

-- Vehicles master (used in reports)
CREATE TABLE IF NOT EXISTS vehicles (
    plate        TEXT PRIMARY KEY,
    vehicle_type TEXT NOT NULL,
    is_vip       INTEGER NOT NULL DEFAULT 0,
    entry_time   TEXT,
    exit_time    TEXT
);

-- Parking sessions (main report table)
CREATE TABLE IF NOT EXISTS parking_sessions (
    session_id  INTEGER PRIMARY KEY AUTOINCREMENT,
    plate       TEXT NOT NULL,
    spot_id     TEXT NOT NULL,
    entry_time  TEXT NOT NULL,
    exit_time   TEXT,
    fee_amount  REAL NOT NULL DEFAULT 0,
    fine_amount REAL NOT NULL DEFAULT 0,
    is_paid     INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (plate) REFERENCES vehicles(plate),
    FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id)
);

-- Fines (unpaid fines report)
CREATE TABLE IF NOT EXISTS fines (
    fine_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    plate      TEXT NOT NULL,
    fine_type  TEXT NOT NULL,
    amount     REAL NOT NULL,
    is_paid    INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL,
    FOREIGN KEY (plate) REFERENCES vehicles(plate)
);

-- Admin settings (fine policy)
CREATE TABLE IF NOT EXISTS admin_settings (
    key   TEXT PRIMARY KEY,
    value TEXT NOT NULL
);

INSERT OR IGNORE INTO admin_settings(key, value)
VALUES ('fine_policy', 'A');

-- Admin users (login)
CREATE TABLE IF NOT EXISTS admins (
    admin_id TEXT PRIMARY KEY,
    password TEXT NOT NULL,
    name     TEXT
);

INSERT OR IGNORE INTO admins(admin_id, password, name) VALUES ('admin1', '1234', 'Ali');
INSERT OR IGNORE INTO admins(admin_id, password, name) VALUES ('admin2', 'abcd', 'Aisyah');

