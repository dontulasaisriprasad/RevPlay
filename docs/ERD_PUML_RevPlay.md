## MODULAR DESIGN EXPLANATION
### RevPlay is organized into well-defined functional modules, each responsible for a single concern.
#### This modular approach improves maintainability, testability, and scalability.


## Module 1: User & Authentication Module
```
com.revplay/
├── controller/
│   └── UserController.java        (Console interface for users)
├── service/
│   └── UserService.java           (Business logic: register/login/profile)
├── dao/
│   ├── UserDAO.java               (Persistence contract)
│   └── impl/UserDAOImpl.java      (JDBC implementation)
├── model/
│   └── User.java                  (User entity: USER/ARTIST types)
└── exception/
    └── CustomException.java       (Error handling)
```

## RESPONSIBILITIES

```
User registration (USER/ARTIST types)

Secure login with credentials

Profile management & updates

Password change functionality

User search and browsing
```

## Module 2: Artist Management Module

```
com.revplay/
├── controller/
│   └── ArtistController.java      (Artist dashboard & management)
├── service/
│   └── ArtistService.java         (Artist-specific business logic)
├── dao/
│   ├── ArtistDAO.java             (Artist persistence contract)
│   └── impl/ArtistDAOImpl.java    (JDBC implementation)
├── model/
│   └── Artist.java                (Artist entity, extends User)
└── Main.java                      (Artist dashboard switching)
```

## RESPONSIBILITIES

```
Artist profile registration

Song upload & management

Monthly listeners tracking

Top artists ranking

Artist search functionality
```

## Module 3: Music Management Module

```
com.revplay/
├── controller/
│   └── SongController.java        (Music player & browsing)
├── service/
│   └── SongService.java           (Song business logic)
├── dao/
│   ├── SongDAO.java               (Song persistence contract)
│   └── impl/SongDAOImpl.java      (JDBC implementation)
├── model/
│   └── Song.java                  (Song entity with metadata)
└── Album.java                     (Album entity)
```

## RESPONSIBILITIES
```
Song browsing & searching

Genre-based filtering

Play count tracking

Top songs ranking

Recently added songs

Music playback simulation
```

## Module 4: Playlist Management Module

```
com.revplay/
├── controller/
│   └── PlaylistController.java    (Playlist creation & management)
├── service/
│   └── PlaylistService.java       (Playlist business logic)
├── dao/
│   ├── PlaylistDAO.java           (Playlist persistence contract)
│   └── impl/PlaylistDAOImpl.java  (JDBC implementation)
├── model/
│   └── Playlist.java              (Playlist entity)
└── Playlist.java                  (Playlist operations)
```

## RESPONSIBILITIES

```
Playlist creation & editing

Song addition/removal

Public/private playlist control

Playlist search functionality

Total duration calculation
```

## Module 5: Utility & Database Module

```
com.revplay/
├── util/
│   ├── DBUtil.java                (Database connection pooling)
│   └── LogUtil.java               (Log4j2 logging wrapper)
├── exception/
│   └── CustomException.java       (Unified error handling)
└── resources/
    └── log4j2.xml                 (Logging configuration)
```

## RESPONSIBILITIES
```
Database connection management

Transaction control (commit/rollback)

Comprehensive logging

Error handling and reporting

Resource cleanup
```
## Module 6: Testing Module
```
test/
├── com.revplay.dao/
│   ├── UserDAOImplTest.java       (User DAO unit tests)
│   ├── ArtistDAOImplTest.java     (Artist DAO unit tests)
│   └── ...                        (Other DAO tests)
└── com.revplay.service/
    ├── UserServiceTest.java       (User service unit tests)
    ├── ArtistServiceTest.java     (Artist service unit tests)
    └── ...                        (Other service tests)
```
## RESPONSIBILITIES
```
Unit testing for DAO layer

Service layer validation testing

Database operation verification

Edge case testing

Test data management
```

## CLASS DIAGRAM

```
┌──────────────────────┐
│    RevPlay Main      │
├──────────────────────┤
│ +main()              │
│ +showLoginMenu()     │
│ +showMusicFeatures() │
│ +handleArtistDashboard()│
└───────────┬──────────┘
            │
┌───────────▼──────────┐
│   UserController     │
├──────────────────────┤
│ -userService         │
│ +showMainMenu()      │
│ +registerUser()      │
│ +loginUser()         │
│ +viewProfile()       │
└───────────┬──────────┘
            │
┌───────────▼──────────┐
│    ArtistController  │
├──────────────────────┤
│ -artistService       │
│ +showArtistDashboard()│
│ +registerArtist()    │
│ +uploadSong()        │
│ +viewArtistSongs()   │
└───────────┬──────────┘
            │
┌───────────▼──────────┐
│    SongController    │
├──────────────────────┤
│ -songService         │
│ +showMusicPlayer()   │
│ +searchSongs()       │
│ +playSong()          │
│ +browseByGenre()     │
└───────────┬──────────┘
            │
┌───────────▼──────────┐
│  PlaylistController  │
├──────────────────────┤
│ -playlistService     │
│ +showPlaylistMenu()  │
│ +createPlaylist()    │
│ +addSongToPlaylist() │
│ +viewPublicPlaylists()│
└───────────┬──────────┘
            │
┌───────────▼──────────┐
│       DAO Layer      │
├──────────────────────┤
│ UserDAO / ArtistDAO  │
│ SongDAO / PlaylistDAO│
└───────────┬──────────┘
            │
┌───────────▼──────────┐
│      DBUtil          │
├──────────────────────┤
│ +getConnection()     │
│ +closeConnection()   │
│ +commitTransaction() │
│ +rollbackTransaction()│
└───────────┬──────────┘
            │
┌───────────▼──────────┘
│   Oracle Database    │
└──────────────────────┘
```

## COMPONENT DIAGRAM
```

┌──────────────────────────────────────────┐
│       RevPlay Console Application        │
├──────────────────────────────────────────┤
│  Main Menu → Controllers → Services      │
│                                          │
│  ┌──────────────┐  ┌──────────────┐     │
│  │  User Module │  │ Artist Module│     │
│  │  • Auth      │  │  • Dashboard │     │
│  │  • Profile   │  │  • Upload    │     │
│  └──────────────┘  └──────────────┘     │
│                                          │
│  ┌──────────────┐  ┌──────────────┐     │
│  │  Song Module │  │ Playlist Mod │     │
│  │  • Player    │  │  • Creation  │     │
│  │  • Search    │  │  • Management│     │
│  └──────────────┘  └──────────────┘     │
│                                          │
│  ┌──────────────────────┐               │
│  │ Utility & Database   │               │
│  │ • Connection Pool    │               │
│  │ • Logging            │               │
│  │ • Error Handling     │               │
│  └──────────────────────┘               │
└───────────────┬──────────────────────────┘
                │
┌───────────────▼──────────────────────────┐
│            JDBC DAO Layer                │
└───────────────┬──────────────────────────┘
                │
┌───────────────▼──────────────────────────┐
│           Oracle Database                │
└──────────────────────────────────────────┘
```
 
## SEQUENCE DIAGRAM – USER REGISTRATION FLOW
```
User → Main → UserController → UserService → UserDAO → Oracle DB
User ← Main ← UserController ← UserService ← UserDAO ← Oracle DB
```

## SEQUENCE DIAGRAM – SONG UPLOAD FLOW
```
Artist → ArtistController → ArtistService → SongDAO → Oracle DB
                                           → ArtistDAO → Oracle DB
Artist ← Notification ← ArtistController ← ArtistService
```
## SEQUENCE DIAGRAM – PLAYLIST CREATION FLOW
```
User → PlaylistController → PlaylistService → PlaylistDAO → Oracle DB
User ← Success Message ← PlaylistController ← PlaylistService
```

## DEPLOYMENT ARCHITECTURE
```
Developer Machine
│
├── IDE (VS Code/IntelliJ/Eclipse)
├── Java Development Kit (JDK 8+)
├── Maven Build System
├── JVM Runtime
│   └── RevPlay Console Application
│       ├── Controllers
│       ├── Services
│       ├── DAO Layer
│       └── Utilities
│
└── Oracle Database Server
    ├── rev_play Schema
    ├── Tables (users, artists, songs, etc.)
    └── JDBC Connection
```

## SECURITY ARCHITECTURE
```
Layer 1: Input Validation (Service Layer)
Layer 2: Authentication (Username/Password)
Layer 3: Role-Based Access (USER/ARTIST)
Layer 4: Secure JDBC (PreparedStatements)
Layer 5: Transaction Management (ACID)
Layer 6: Comprehensive Logging (Log4j2)
Layer 7: Error Handling (CustomException)
```
## PERFORMANCE CONSIDERATIONS
```
1. Database Level:
   • Indexed columns for frequent searches
   • Connection pooling via DBUtil
   • PreparedStatement reuse
   • Batch operations for bulk inserts

2. Application Level:
   • Lazy loading for related entities
   • Caching frequently accessed data (future)
   • Pagination for large result sets
   • Efficient memory management

3. Network Level:
   • Optimized SQL queries
   • Minimal data transfer
   • Connection timeout handling
```
## STATE MANAGEMENT

```
1. User Session:
   • CurrentUser object in UserController
   • CurrentArtist object in ArtistController
   • User ID tracking across sessions

2. In-Memory State:
   • Current playlists during session
   • Search results caching
   • Recently viewed items

3. Database State:
   • User preferences
   • Playlists and favorites
   • Listening history
```
## SCALABILITY PATH
```
Current: Monolithic Console App
    ↓
Phase 1: REST API + Web Interface
    ↓
Phase 2: Microservices Architecture
    ↓
Phase 3: Cloud Deployment
    ↓
Phase 4: Mobile Applications
```
## DEVELOPMENT WORKFLOW
```
1. Database Schema Design
2. Model Classes Creation
3. DAO Layer Implementation
4. Service Layer Development
5. Controller Layer Building
6. Console Interface Design
7. Unit Testing
8. Integration Testing
9. Documentation
10. Deployment
```
