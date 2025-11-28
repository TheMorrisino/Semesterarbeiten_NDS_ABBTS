# CSV-Schemata (MVP) – Stand 2025-11-10

## employees.csv
- id (string, unique)
- full_name
- contact (optional)
- qualifications (semicolon-separated SkillIds, optional, z. B. "SRK;WUND")
- qualifications_valid_until (semicolon-separated ISO-Dates, optional, gleiche Anzahl oder leer)
- leaves (semicolon-separated ranges "YYYY-MM-DD..YYYY-MM-DD")

## skills.csv
- skill_id (string, e.g., "SRK", "WUND", "MEDI")
- label_de
- label_en (optional)

## work_types.csv
- id
- label_de
- label_en (optional)
- required_skills (semicolon-separated SkillIds)
- default_code (optional, e.g., "WP")

## clients.csv
- id
- display_name
- number (optional)
- address (optional)
- info_notes (JSON array pro Zeile, optional, z. B. '[{"type":"SAFETY","text":"aggressives Verhalten","visibility":"ICON"}]')

## jobs.csv
- id
- date (YYYY-MM-DD)
- start (HH:MM)
- end (HH:MM)
- client_id
- work_type_id
- priority (P1..P5, default P3)
- status (PLANNED|ASSIGNED|DONE|CANCELLED, default PLANNED)

## codes.csv (optional, überschreibt Defaults)
- kind (WORK_TYPE|STATUS)
- ref_id (WorkType.id oder JobStatus-Name)
- code (frei wählbar, z. B. "GP", "A", "X")
