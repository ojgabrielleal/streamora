ALTER TABLE playlists
    ADD COLUMN is_base BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN weight INT UNSIGNED NOT NULL DEFAULT 1,
    ADD CONSTRAINT chk_playlists_weight_positive CHECK (weight > 0);
