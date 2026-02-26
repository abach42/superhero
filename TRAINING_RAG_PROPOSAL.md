# Training-Vorschlag für das Superhero-RAG-Beispiel

## Ausgangslage

Dieses Projekt nutzt aktuell:

- **Ollama Chat-Modell:** `mistral`
- **Ollama Embedding-Modell:** `mxbai-embed-large`
- **RAG mit PGVector/HNSW**
- **Skill-Intensitäten als Zahlen (1..5)**

Die Intensitäten werden im Prompt in Text umgewandelt (`1 -> very low` ... `5 -> very high`), damit das Modell die semantische Bedeutung stabiler erkennt.

## Kurzantworten auf die Kernfragen

### B) Gibt es Spring AI Mittel, um ein Modell zu trainieren?

- **Direktes Modell-Training/Fine-Tuning ist kein Kern-Feature von Spring AI.**
- Spring AI ist vor allem ein **Orchestrierungs-Framework**: Prompting, RAG, Tool Calling, Vector Stores, Structured Output.
- Fine-Tuning/Training erfolgt typischerweise **außerhalb** von Spring AI (z. B. LoRA/QLoRA Pipelines), während Spring AI das resultierende Modell konsumiert.

### C) Welches Modell kommt in Frage und gibt es bei Ollama trainierbare Modelle?

- In Ollama kannst du am einfachsten ein **adaptiertes Modell via Modelfile** erstellen (Prompt-/System-Anpassungen, Parameter, Adapter-Integration).
- Echtes Fine-Tuning (z. B. LoRA) passiert meist mit externen Werkzeugen; das resultierende Modell kann dann in Ollama importiert/serviert werden.
- Für deinen Use Case (deutsche/englische Aufgaben, Team-Selektion, strukturierte Begründung):
  - **Chat/Reasoning:** `mistral`, `llama3.*`, `qwen2.5`-Varianten (je nach Hardware)
  - **Embedding:** `mxbai-embed-large` ist solide; alternativ `nomic-embed-text` testen

### D) Was sollte ich trainieren? (Empfehlung für den Start)

Für den Anfang **kein klassisches Fine-Tuning**. Stattdessen 3 Stufen:

1. **Retrieval verbessern (höchster Hebel)**
2. **Reranking/Scoring einführen**
3. **Optionales Fine-Tuning erst danach**

Warum: Dein aktuelles Problem („`courage = 5` wird nicht immer als sehr gut verstanden“) ist primär ein **Repräsentations-/Retrieval-Problem**, nicht zwingend ein Modellwissensproblem.

## Konkreter Trainings-/Verbesserungsplan

## Stufe 1: Datenrepräsentation für Embeddings verbessern

Anstatt nur `courage: 5` oder nur Freitext zu nutzen, indexiere pro Held ein Dokument mit:

- natürlicher Sprache
- normierter Skala
- expliziten Synonymen

Beispiel für ein Skill-Fragment (englisch):

```text
Skill: courage
Level: 5/5
Interpretation: very high courage
Comparable labels: excellent, outstanding, top-tier
```

Damit lernt das Embedding über den Dokumenttext, dass 5 semantisch zu „very high/excellent“ gehört.

## Stufe 2: Query-Enrichment statt Workaround-Switch allein

Beim User-Task vor Retrieval automatisch Such-Varianten erzeugen:

- „need high courage“
- „need very high courage“
- „need courage level 4 or 5“

Dann hybrid suchen:

- Vector similarity (semantisch)
- plus einfacher strukturierter Boost (z. B. `courage >= 4`)

## Stufe 3: Deterministisches Reranking vor LLM

Nach Top-k Retrieval: Kandidaten mit einer festen Formel scoren, z. B.:

```text
score = 0.45*courage + 0.25*leadership_related + 0.20*peacekeeping + 0.10*reliability
```

Dann gibst du nur Top-N an das Chat-Modell. Dadurch hängt die Kernentscheidung weniger von Modell-Launen ab.


## Konkret: Wie kommt aus einer Aufgabe eine deterministische Formel?

Gute Frage: Die Formel wird **nicht frei vom LLM erfunden**, sondern aus einer kleinen, festen
**Task-to-Skill-Map** erzeugt.

### 1) Definiere Skill-Dimensionen (aus eurer Skill-Liste)

Beispiel-Dimensionen für Ranking (0..5, dann auf 0..1 normalisiert):

- courage
- speed
- strength
- peacekeeping
- empathy
- wisdom
- reliability (z. B. aus `self control` + `loyalty`)

### 2) Lege eine regelbasierte Intent-Matrix an

Pro Intent gibt es feste Gewichte. Beispiel:

```yaml
intents:
  rescue:
    courage: 0.30
    speed: 0.25
    strength: 0.20
    peacekeeping: 0.15
    reliability: 0.10
  negotiation:
    peacekeeping: 0.35
    empathy: 0.25
    wisdom: 0.20
    courage: 0.10
    reliability: 0.10
  logistics:
    speed: 0.35
    reliability: 0.30
    wisdom: 0.20
    courage: 0.15
```

### 3) Extrahiere Intent + Modifier aus der Aufgabe

Für `"Rescue the city from evil robots"`:

- Intent: `rescue`
- Modifier: `robots` => leichter Boost für `strength`
- Modifier: `city` => leichter Boost für `peacekeeping` (Kollateralschäden vermeiden)

Dadurch werden die Gewichte deterministisch angepasst, z. B.:

```text
base(rescue): courage 0.30, speed 0.25, strength 0.20, peacekeeping 0.15, reliability 0.10
+ robots: strength +0.05
+ city: peacekeeping +0.05
renormalize => courage 0.27, speed 0.23, strength 0.23, peacekeeping 0.18, reliability 0.09
```

### 4) Kandidaten-Score berechnen

Für jeden Kandidaten:

```text
score(hero) = Σ weight(skill_i) * normalized_value(hero, skill_i)
```

Bei Team-Bildung zusätzlich Diversity/Abdeckung:

```text
team_score = Σ score(hero_j) + λ * coverage_bonus - μ * redundancy_penalty
```

### 5) LLM erst nach Ranking einsetzen

- Reranker liefert Top-N und nachvollziehbare Teil-Scores
- LLM bekommt diese Top-N + Aufgabe
- LLM macht dann nur noch: Begründung, Risikohinweise, finales JSON

So bleibt die Auswahl **reproduzierbar** und du kannst leicht testen, ob `courage=5` wirklich immer hoch bewertet wird.

### Mini-Pseudocode

```text
intent = classifyIntent(task)               # rule-based / keyword-based
weights = loadBaseWeights(intent)
weights = applyModifiers(weights, task)     # robots, city, fire, flood, ...
weights = renormalize(weights)

for hero in retrievedHeroes:
  hero.score = weightedScore(hero.skills, weights)

ranked = sortByScoreDesc(retrievedHeroes)
topN = ranked[0:N]
```

Hinweis für den Start: beginne mit **5-10 Intents** und einer kleinen Keyword-Liste.
Das ist für ein Lernprojekt völlig ausreichend und zeigt sehr schön den Unterschied zwischen
"Ranking" und "Generierung".

## Stufe 4 (optional): Leichtes Fine-Tuning für Ausgabequalität

Wenn Retrieval + Reranking stabil sind, dann erst Fine-Tuning:

- Ziel: bessere Begründungen und stabiles JSON-Format
- Nicht Ziel: grundlegende numerische Semantik reparieren

Trainingsdaten-Idee:

- Input: Aufgabe + Kandidatenliste (mit Skills)
- Output: Team (IDs) + strukturierte Begründung + Skill-Abdeckung
- 100–500 gute, kuratierte Beispiele reichen oft für deutliche Stil-/Formatverbesserung

## Minimaler Experimentplan (2 Wochen)

1. **Baseline messen**
   - 30 Aufgaben definieren
   - Metriken: Recall@k für „richtige Helden“, Konsistenz bei `courage=5`
2. **Dokumentrepräsentation upgraden**
   - Skill-Skala + Synonyme in Vektor-Dokumente
3. **Reranker einbauen**
   - Einfache gewichtete Formel
4. **A/B Vergleich**
   - alt vs. neu auf denselben 30 Aufgaben
5. **Erst dann Fine-Tuning-Entscheidung**

## Wichtig für dein Lernprojekt

Dein Projekt ist ein Übungs-/Demobeispiel. Genau dafür ist dieser Weg ideal:

- Du lernst den echten Industrie-Workflow (RAG > Ranking > optional Fine-Tuning)
- Du vermeidest frühzeitige Komplexität
- Du bekommst schnell messbare Verbesserungen ohne schweres Training
