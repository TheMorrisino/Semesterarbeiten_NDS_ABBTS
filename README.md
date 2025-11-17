Hallo Zusammen

/                     ← Root‑Verzeichnis
│
├─ semesterarbeit/    ← Hauptordner für die gemeinsame Semesterarbeit
│   ├─ docs/          ← Dokumentation, Entwürfe, Präsentationen
│   ├─ src/           ← Quellcode, gemeinsam entwickelt
│   └─ ...            ← Weitere Unterordner nach Bedarf
│
├─ fach1/             ← Beispiel: Mathematik, Physik, Informatik …
│   ├─ personA/       ← Code/Material von Person A für Fach 1
│   ├─ personB/       ← Code/Material von Person B für Fach 1
│   └─ ...            ← weitere Personen
│
├─ fach2/
│   ├─ personA/
│   ├─ personC/
│   └─ ...
│
└─ ...                ← weitere Fachordner


semesterarbeit/ – Hier arbeiten wir alle gemeinsam am eigentlichen Projekt.
Alle Änderungen sollten über Pull‑Requests bzw. Merge‑Requests in diesen Ordner erfolgen, damit wir den Überblick behalten.

fachX/ – Für jedes Fach gibt es einen eigenen Ordner.
In jedem Fach‑Ordner legt jede Person einen Unterordner mit ihrem Namen an und speichert dort ihre jeweiligen Dateien (Code, Skripte, Notizen …). So bleibt alles klar getrennt und lässt sich leicht finden.

🛠️ Arbeitsablauf mit Git
Repository klonen

Committen
git clone https://github.com/<euer‑username>/<repo‑name>.git
cd <repo‑name>

Neuen Branch erstellen (z. B. für ein Feature oder ein Kapitel der Semesterarbeit)
git checkout -b mein-feature

git add .
git commit -m "Kurze Beschreibung der Änderung"

Pushen
Wenn Code lauffähig 
git push origin mein-feature

Pull‑Request öffnen – Auf GitHub einen PR vom eigenen Branch zum develop anlegen.
Teammitglieder können den Code reviewen, Kommentare hinterlassen und den PR schließlich mergen.


📑 Lizenz & Hinweis
Dieses Repository ist ausschließlich für den internen Gebrauch im Rahmen unseres Studiums gedacht. 
Bitte respektiert die Urheberrechte aller eingebrachten Materialien und verwendet keine fremden Inhalte ohne Genehmigung.
