# Planner Desktop MVP (Task C – Monatsansicht)

## Features
- CSV-Import (employees, clients, work_types, jobs, codes)
- Monatsansicht (Grid), Filter nach Mitarbeiter/WorkType/Priorität
- INFO-Hinweise am Klienten als Icon/Tooltip
- Vorschlags-Button (Greedy, Skills + Ferien prüfen)

## Start
- Voraussetzung: JDK 17+
- `./gradlew run` (macOS/Linux) oder `gradlew.bat run` (Windows)

## CSV
- Legen Sie CSV-Dateien unter `./data/` ab (siehe Templates, die Sie erhalten haben).

## Architektur
- `core` (leichtgewichtig in derselben Modulstruktur) + UI (Compose)
- Clean Code: KDoc, klare Namen, kleine Funktionen
