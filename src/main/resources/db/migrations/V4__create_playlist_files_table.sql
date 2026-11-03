CREATE TABLE playlist_files (
    playlist_id BIGINT UNSIGNED NOT NULL,
    file_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (playlist_id, file_id),
    CONSTRAINT fk_playlist_files_playlist
        FOREIGN KEY (playlist_id) REFERENCES playlists (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_playlist_files_file
        FOREIGN KEY (file_id) REFERENCES files (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
