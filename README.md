**Hallo Zusammen**



**Erstes Semester**  
1_Semesterarbeit/Ressourcix/ – Hier arbeiten wir alle gemeinsam am eigentlichen Projekt.
Alle Änderungen sollten über Pull‑Requests bzw. Merge‑Requests in diesen Ordner erfolgen, damit wir den Überblick behalten.

**Zweites Semester**    
2_Semesterarbeit/Ressourcix/ – Hier arbeiten wir alle gemeinsam am eigentlichen Projekt.
Alle Änderungen sollten über Pull‑Requests bzw. Merge‑Requests in diesen Ordner erfolgen, damit wir den Überblick behalten.


**🛠️ Arbeitsablauf mit Git**

1.Repository klonen
git clone https://github.com/<euer‑username>/<repo‑name>.git
cd <repo‑name>

2.Neuen Branch erstellen (z. B. für ein Feature oder ein Kapitel der Semesterarbeit)
git checkout -b mein-feature

3.Committen
git add .
git commit -m "Kurze Beschreibung der Änderung"

4.Pushen
Wenn Code lauffähig 
git push origin mein-feature

5.Pull‑Request öffnen – Auf GitHub einen PR vom eigenen Branch zum develop anlegen.
Teammitglieder können den Code reviewen, Kommentare hinterlassen und den PR schliesslich mergen.


**📑Hinweis**

Dieses Repository ist ausschliesslich für den internen Gebrauch im Rahmen unseres Studiums gedacht. 
Bitte respektiert die Urheberrechte aller eingebrachten Materialien und verwendet keine fremden Inhalte ohne Genehmigung.


**Wichtige Aspekte der Branch-Struktur:**

- **Hauptzweige (Main/Develop):** Repräsentieren den stabilen Produktionscode.
- **Feature-Branches:** Werden für neue Funktionen erstellt, um isoliert zu arbeiten.
- **Release-Branches:** Unterstützen die Vorbereitung neuer Produktionsversionen.
- **Hotfix-Branches:** Dienen der schnellen Fehlerbehebung im Produktionscode.
- **Merging:** Zusammenführen der Änderungen aus den Branches zurück in den Hauptzweig. 

**Best Practices für eine saubere Struktur:**
- **Isolierung:** Jedes neue Feature sollte in einem eigenen Branch entwickelt werden.
- **Benennung:** Aussagekräftige Namen verwenden, wie z.B. `feature/login-page` oder `hotfix/fix-header`.
- **Aktualität:** Regelmäßiges Einpflegen der Änderungen aus dem Hauptzweig in den Feature-Branch, um Konflikte beim Merge zu vermeiden. 

**Weitere Informationen:**
- Verstehen Sie den [Gitflow-Workflow](https://www.atlassian.com/de/git/tutorials/comparing-workflows/gitflow-workflow) für strukturierte Entwicklungsabläufe.
- Erfahren Sie mehr über die Grundlagen von [Git Branching](https://git-scm.com/book/de/v2/Git-Branching-Branches-auf-einen-Blick).
- Ein detailliertes Tutorial zum Erstellen und Zusammenführen von [Git Branches](https://www.datacamp.com/de/tutorial/git-branch) finden Sie bei DataCamp. [](https://www.atlassian.com/de/git/tutorials/comparing-workflows/gitflow-workflow)
