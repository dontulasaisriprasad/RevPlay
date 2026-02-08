# ERD (Entity Relationship Diagram)

## Database Schema Visualization

### Entity Relationship Diagram

```
┌─────────────────────┐         ┌─────────────────────┐
│        USERS        │         │      ARTISTS        │
├─────────────────────┤         ├─────────────────────┤
│ user_id (PK)        │◄────────┤ artist_id (PK,FK)   │
│ username (UNIQUE)   │         │ stage_name (UNIQUE) │
│ password            │         │ genre               │
│ email (UNIQUE)      │         │ record_label        │
│ full_name           │         │ monthly_listeners   │
│ user_type           │         │ social_media_links  │
│ profile_image       │         └─────────┬───────────┘
│ bio                 │                   │
│ registration_date   │                   │
│ is_active           │                   │
└──────────┬──────────┘                   │
           │                              │
           ▼                              ▼
┌─────────────────────┐         ┌─────────────────────┐
│       SONGS         │         │      ALBUMS         │
├─────────────────────┤         ├─────────────────────┤
│ song_id (PK)        │◄────────┤ album_id (PK)       │
│ title               │         │ title               │
│ artist_id (FK)      │────────►│ artist_id (FK)      │
│ album_id (FK)       │         │ release_date        │
│ duration_seconds    │         │ genre               │
│ genre               │         │ cover_image         │
│ file_path           │         │ total_tracks        │
│ release_date        │         │ duration_minutes    │
│ play_count          │         └─────────────────────┘
│ is_active           │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐         ┌─────────────────────┐
│     PLAYLISTS       │         │  LISTENING_HISTORY  │
├─────────────────────┤         ├─────────────────────┤
│ playlist_id (PK)    │◄────────┤ history_id (PK)     │
│ name                │         │ user_id (FK)        │
│ user_id (FK)        │         │ song_id (FK)        │
│ description         │         │ listened_at         │
│ is_public           │         │ play_duration_seconds
│ created_date        │         └─────────────────────┘
│ total_songs         │
│ total_duration      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐         ┌─────────────────────┐
│   PLAYLIST_SONGS    │         │     FAVORITES       │
├─────────────────────┤         ├─────────────────────┤
│ playlist_id (PK,FK) │◄────────┤ favorite_id (PK)    │
│ song_id (PK,FK)     │────────►│ user_id (FK)        │
│ position_number     │         │ song_id (FK)        │
└─────────────────────┘         │ added_date          │
                                └─────────────────────┘
```

 ## Relationships Summary


```
┌────────────────────────────┬────────┬────────────────────────────────────────────┐
│        RELATIONSHIP        │  TYPE  │                DESCRIPTION                 │
├────────────────────────────┼────────┼────────────────────────────────────────────┤
│ Users → Artists            │  1:1   │ A user can be one artist (extends user).    │
│ Users → Playlists          │  1:N   │ A user can create multiple playlists.       │
│ Users → Favorites          │  1:N   │ A user can favorite multiple songs.         │
│ Users → ListeningHistory   │  1:N   │ A user has multiple listening records.      │
│ Artists → Songs            │  1:N   │ An artist can upload multiple songs.        │
│ Artists → Albums           │  1:N   │ An artist can create multiple albums.       │
│ Albums → Songs             │  1:N   │ An album contains multiple songs.           │
│ Playlists → PlaylistSongs  │  1:N   │ A playlist contains multiple songs.         │
│ Songs → PlaylistSongs      │  1:N   │ A song can be in multiple playlists.        │
│ Songs → Favorites          │  1:N   │ A song can be favorited by many users.      │
│ Songs → ListeningHistory   │  1:N   │ A song can be played by many users.         │
└────────────────────────────┴────────┴────────────────────────────────────────────┘
```
