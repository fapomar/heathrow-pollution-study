# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
mvn clean compile          # compile only
mvn spring-boot:run        # build and run
```

There are no tests yet.

## Java / Spring Boot compatibility

The project targets **Java 21 bytecode** (compiled with `-source 21 -target 21`) but runs on a **Java 26 JVM**. Do not raise the compiler target above 21 — Spring Boot 3.5.0's bundled ASM library does not support Java 26 class files and will fail at startup when scanning components.

Lombok requires an explicit `<annotationProcessorPaths>` entry in `maven-compiler-plugin` — do not remove it or Lombok-generated getters/setters will silently disappear.

## Architecture

This is a Spring Boot `CommandLineRunner` — it runs once and exits. There is no web layer.

**Two-phase operation**, both controlled by flags in `application.properties`:

| Flag | What it does |
|---|---|
| `app.downloadData=true` | Fetches JSON from external APIs and writes files under `data/` |
| `app.loadJsonFiles=true` | Reads those local files and unmarshals them into POJOs |

Both flags can be true at the same time. The date range (`app.startdate` / `app.enddate`, format `YYYY-MM-DD`) is substituted into URL templates and file path templates via `String.format`.

**Data sources and their JSON shapes are very different:**

- **London Air Quality Network** (`dataSource=LondonAir`): XML converted to JSON, so all field names are prefixed with `@` (e.g. `@MeasurementDateGMT`, `@Data1`). Column names (which species maps to which `DataN` id) are declared in a `Columns.Column` array at the top of the response and vary per station.
- **Open-Meteo** (`dataSource=OpenMeteo`): Standard JSON with parallel arrays — a `time` array and one array per metric (e.g. `wind_direction_180m`), all nested under `hourly`.

**Domain model** (`model/airquality/`): Three-level hierarchy — `AirQualityDataset` (one per station) → `AirQualityDaily` (one per calendar day) → `AirQualityObservation` (one per hour, holds `pm25`, `pm10`, `timestamp`). The raw API JSON does not map directly to this shape and requires a transformation step.

**`DataService.load()`** is currently a stub (returns `null`). The download path (`downloadJson`) is fully implemented.

**`data/` directories** (`aqm-battersea/`, `aqm-richmond/`, `weather-heathrow/`, `weather-battersea/`, `weather-richmond/`) must exist before running with `downloadData=true` — the app does not create them.
