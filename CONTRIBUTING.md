# Contributing

## Development setup

```bash
git clone https://github.com/Zeusse-Neumbi/campus-connect.git
cd campus-connect
mvn clean verify
mvn jetty:run
```

The app runs at <http://localhost:9091>. The first boot creates and seeds
`school.db`; delete that file to reset the local database.

## Checks

```bash
mvn test
```

CI runs `mvn -B verify` on Java 17.

## Pull requests

- Keep changes focused and explain the user-facing effect.
- Add or update tests for service, DAO, or handler changes.
- Update the README when setup, credentials, or behavior changes.
- Do not commit `school.db`, `target/`, IDE settings, or generated reports.
