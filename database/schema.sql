
CREATE TABLE users (
    user_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR2(50) UNIQUE NOT NULL,
    password VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    full_name VARCHAR2(100) NOT NULL,
    user_type VARCHAR2(10) CHECK (user_type IN ('USER', 'ARTIST')) NOT NULL,
    profile_image VARCHAR2(200),
    bio VARCHAR2(500),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active CHAR(1) DEFAULT 'Y' CHECK (is_active IN ('Y', 'N'))
);

CREATE TABLE artists (
    artist_id NUMBER PRIMARY KEY,
    stage_name VARCHAR2(100) UNIQUE NOT NULL,
    genre VARCHAR2(50),
    record_label VARCHAR2(100),
    monthly_listeners NUMBER DEFAULT 0,
    social_media_links VARCHAR2(500),
    FOREIGN KEY (artist_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE albums (
    album_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    artist_id NUMBER NOT NULL,
    release_date DATE,
    genre VARCHAR2(50),
    cover_image VARCHAR2(200),
    total_tracks NUMBER DEFAULT 0,
    duration_minutes NUMBER DEFAULT 0,
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id) ON DELETE CASCADE
);

CREATE TABLE songs (
    song_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    artist_id NUMBER NOT NULL,
    album_id NUMBER,
    duration_seconds NUMBER NOT NULL,
    genre VARCHAR2(50),
    file_path VARCHAR2(200) NOT NULL,
    release_date DATE DEFAULT SYSDATE,
    play_count NUMBER DEFAULT 0,
    is_active CHAR(1) DEFAULT 'Y' CHECK (is_active IN ('Y', 'N')),
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES albums(album_id) ON DELETE SET NULL
);

CREATE TABLE playlists (
    playlist_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    user_id NUMBER NOT NULL,
    description VARCHAR2(300),
    is_public CHAR(1) DEFAULT 'N' CHECK (is_public IN ('Y', 'N')),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_songs NUMBER DEFAULT 0,
    total_duration NUMBER DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE playlist_songs (
    playlist_id NUMBER NOT NULL,
    song_id NUMBER NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    position_number NUMBER,
    PRIMARY KEY (playlist_id, song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);

CREATE TABLE favorites (
    favorite_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    song_id NUMBER NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, song_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);

CREATE TABLE listening_history (
    history_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    song_id NUMBER NOT NULL,
    listened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    play_duration_seconds NUMBER,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);

CREATE INDEX idx_songs_title ON songs(title);
CREATE INDEX idx_songs_artist ON songs(artist_id);
CREATE INDEX idx_playlists_user ON playlists(user_id);
CREATE INDEX idx_favorites_user ON favorites(user_id);
CREATE INDEX idx_history_user ON listening_history(user_id);
CREATE INDEX idx_history_listened ON listening_history(listened_at);


CREATE INDEX idx_songs_genre ON songs(genre);
CREATE INDEX idx_playlists_public ON playlists(is_public);
CREATE INDEX idx_songs_active ON songs(is_active);
CREATE INDEX idx_users_active ON users(is_active);

CREATE SEQUENCE seq_users START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE seq_albums START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE seq_songs START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE seq_playlists START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE seq_favorites START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE seq_history START WITH 1000 INCREMENT BY 1;

INSERT INTO users (username, password, email, full_name, user_type) 
VALUES ('admin', 'admin123', 'admin@revplay.com', 'Administrator', 'USER');
COMMIT;

select * from favorites;

INSERT INTO artists (artist_id, stage_name, genre, record_label) 
VALUES (3, 'Taylor Swift', 'Pop', 'Republic Records');

