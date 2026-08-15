# Desktop Photo Album

Java desktop application to manage photo albums with different users. Allows importing external photos.

## Contributors

- Karim Smires
- Jude Jacob

## Requirements

- JDK 21

## Run

```sh
./gradlew run
```

## Login

- `admin` — no password (reserved for user administration)
- `stock` — no password (seeded sample account)

## Persistence

Albums and users are stored in `data/data.dat` (gitignored).

- If the file is missing, the first run seeds the `stock` user and sample albums.
- If the file exists but is corrupt or unreadable, the app fails closed and does **not** overwrite it.

## Stock photos

Sample images for the `stock` user live in `data/users/stock/photos/`.
