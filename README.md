# Desktop Photo Album

A JavaFX desktop application for managing photo albums with multiple users.
Import photos, organize them into albums, caption and tag them, search by date
or tag, and browse any album as a slideshow.

## Requirements

- JDK 21+ (only for building — the runnable jar works on any JDK 21+ install)

## Run

Build and run from source:

```sh
./gradlew run
```

Or build once and run the self-contained jar anywhere:

```sh
./gradlew jar
java -jar build/libs/DesktopPhotoAlbum.jar
```

## Demo walkthrough

1. Log in as `stock` (no password) — a sample account seeded on first run with
   a `stock` album containing sample photos.
2. Open the album to browse the photo gallery, set captions, add tags, and
   start a slideshow (arrow keys navigate, space plays/pauses, esc closes).
3. Search across all albums from the homepage — by date range
   (`MM/DD/YYYY-MM/DD/YYYY`) or by tag (`tag=value`, combinable with `AND`/`OR`).
4. Log in as `admin` (no password) to create and delete users.

## Login

- `admin` — no password (reserved for user administration)
- `stock` — no password (seeded sample account)

## Persistence

Albums and users are stored in `data/data.dat` (gitignored).

- If the file is missing, the first run seeds the `stock` user and sample albums.
- If the file exists but is corrupt or unreadable, the app fails closed and does **not** overwrite it.
- Set `-Dphotos.data.dir=/some/dir` to store data elsewhere (useful for demos).

## Stock photos

Sample images for the `stock` user live in `data/users/stock/photos/`.

## Development

- `./gradlew test` — unit tests (JUnit 5)
- CI runs the test suite on every push and pull request
