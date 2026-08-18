# Plan: NN-Backed Nuclear Classifiers for Solver_RG

## 0. One-paragraph intent

Today, a user of Solver_RG has to author nuclear/primitive classifiers with
raw integers — "figure = 6" to mean "king", "figure = 1" to mean "pawn",
"color = 2" to mean "black". The numeric encoding is a hidden dialect baked
into [Situation0.xml](xmls/Situation0.xml), [Nucleus Concepts.json](jsons/db/Nucleus%20Concepts.json),
and hard-coded mappers like [Situation.java:207-238](src/org/ppit/core/percept/Situation.java#L207-L238).
We want to replace that dialect at the authoring surface with the named
output vocabulary of the trained [Solver_train](Solver_train/) classifiers
(`type_classifier.pt`, `color_classifier.pt`, `neighborhood_detector.pt`) —
so the user says "king" or "white" and the system keeps the integer mapping
as an internal implementation detail. The numeric-rule engine underneath
(RuleIn/RuleE/…) stays intact; only the authoring, persistence, rendering,
and (later) situation-ingestion layers learn to speak names.

---

## 1. What the current system looks like (the thing we are replacing)

### 1.1 Concept hierarchy (from code)

- [AbstractBase.java](src/org/ppit/core/concept/AbstractBase.java) — root of all concepts/actions. The kind is carried by `CONCEPT_TYPE`.
- [ConceptType.java](src/org/ppit/core/concept/ConceptType.java) — enum: `NUCLEUS`, `PRIMITIVE`, `COMPOSITE`, `SET`, `ACTION`, `VIRTUAL`, `USAGE`, `AR1`.
- [Concept.java](src/org/ppit/core/concept/Concept.java) — abstract class, handles dependencies.
- [PrimitiveConcept.java](src/org/ppit/core/concept/primitive/PrimitiveConcept.java) — a nucleus *or* a derived primitive. Holds `m_ownRule : IRule`, `m_type : PrimitiveType`, `m_attributeName`. `isNucleus()` is true when `getName().equals(m_type.getName())` ([PrimitiveConcept.java:94-96](src/org/ppit/core/concept/primitive/PrimitiveConcept.java#L94-L96)).
- [PrimitiveType.java](src/org/ppit/core/concept/primitive/PrimitiveType.java) — a lightweight "what family am I" tag (e.g. "FigureType", "cordX").
- [CompositeConcept.java](src/org/ppit/core/concept/composite/CompositeConcept.java) — bundles primitives/composites into higher abstractions (a chess "King" at "x,y" etc.).

So a chess "figure type" lives in the system as:
a **nucleus** named `Figure` with a value attribute `figure : IN [0,6]`, plus
**derived primitives** (Pawn, Knight, …, King) each of which is a
PrimitiveConcept whose own rule narrows the parent range to a single value.

### 1.2 Rules (the numeric vocabulary)

Defined under [src/org/ppit/core/concept/rules/](src/org/ppit/core/concept/rules/):

| File | Operator | Semantics |
|------|----------|-----------|
| [RuleIn.java](src/org/ppit/core/concept/rules/RuleIn.java) | `IN` | closed range `[min, max]` — the bedrock for a nucleus attribute |
| [RuleE.java](src/org/ppit/core/concept/rules/RuleE.java)   | `=`  | exact value |
| [RuleNE.java](src/org/ppit/core/concept/rules/RuleNE.java) | `!=` | exclusion |
| [RuleG.java](src/org/ppit/core/concept/rules/RuleG.java)   | `>`  | strict > |
| [RuleGE.java](src/org/ppit/core/concept/rules/RuleGE.java) | `>=` | ≥ |
| [RuleL.java](src/org/ppit/core/concept/rules/RuleL.java)   | `<`  | strict < |
| [RuleLE.java](src/org/ppit/core/concept/rules/RuleLE.java) | `<=` | ≤ |
| [RuleAbstract.java](src/org/ppit/core/concept/rules/RuleAbstract.java) | `=` (special `*`) | abstract parent placeholder |
| [RuleWildcard.java](src/org/ppit/core/concept/rules/RuleWildcard.java) | wildcard | any |

The interface contract is `boolean check(int value)` ([IRule.java:54](src/org/ppit/core/concept/rules/IRule.java#L54)) — **integer in, boolean out**. That `int` is the hardcoded categorical index.

Registered in [ConceptCreator.createRule()](src/org/ppit/core/concept/ConceptCreator.java#L41-L67); operators surfaced to the UI in [WebContent/concept/ShowOperSymbols.jsp](WebContent/concept/ShowOperSymbols.jsp) and [WebContent/percepts/nucleusSkeleton.html](WebContent/percepts/nucleusSkeleton.html) — nothing here knows about semantic names.

### 1.3 Persistence

Every nucleus/primitive writes a JSON object with a `nucleusConceptValueAttrs` array where each attr has `{name, oper, value}`. Range values are `{minValue, maxValue}` strings. Example — the chess nuclei today ([jsons/db/Nucleus Concepts.json](jsons/db/Nucleus%20Concepts.json#L36-L69)):

```json
{
  "name": "Figure",
  "parent": null,
  "nucleusConceptIndexAttrs": [
    {"name": "x", "oper": "IN", "value": {"minValue": "1", "maxValue": "8"}},
    {"name": "y", "oper": "IN", "value": {"minValue": "1", "maxValue": "8"}}
  ],
  "nucleusConceptValueAttrs": [
    {"name": "figure", "oper": "IN", "value": {"minValue": "0", "maxValue": "6"}}
  ]
}
```

The derived primitive "Pawn" would just carry `{"oper": "=", "value": "1"}` against parent `Figure`. See the generic form in the test JSON at [TestPrimitiveConcept.java:37-60](src/org/ppit/test/concept/primitive/TestPrimitiveConcept.java#L37-L60) and fully-formed primitives in [PrimitiveConcepts.json](jsons/test/PrimitiveConcepts.json).

### 1.4 Situation runtime

- [Situation.java](src/org/ppit/core/percept/Situation.java) carries a map of [IdGroup](src/org/ppit/core/brain/instance/IdGroup.java) objects. Each `IdGroup` = one chess square, containing four [NucleusInstance](src/org/ppit/core/brain/instance/nucleus/NucleusInstance.java)s: `cordX`, `cordY`, `FigureType`, `FigureColor`.
- `NucleusInstance` stores a single `int m_value`. That value is compared to `IRule.check(int)` in the GA pipeline.
- [Situation0.xml](xmls/Situation0.xml) persists those integers literally (`figure value="4"` = rook).
- The only place name↔number is spelled out in code is the FEN emitter [Situation.java:207-238](src/org/ppit/core/percept/Situation.java#L207-L238) — a private switch-case that is *not* reused anywhere else. No canonical vocabulary object.

### 1.5 What Solver_train already gives us (the names we want)

- [Solver_train/chess_piece_classifier.py](Solver_train/chess_piece_classifier.py) — two CNN heads. Checkpoints store `"class_names": [...]` alongside weights (see [TECHNICAL_REFERENCE.md §A.4.1](Solver_train/docs/TECHNICAL_REFERENCE.md)). Canonical order: `["pawn", "knight", "bishop", "rook", "queen", "king"]` for type, `["white", "black"]` for color.
- [Solver_train/chess_neighborhood_detector.py](Solver_train/chess_neighborhood_detector.py) — pair-wise Siamese; labels `{neighbor, not_neighbor}`. Useful later for validating the 8×8 grid that Solver_RG assumes.
- Artifacts live under [Solver_train/artifacts/](Solver_train/artifacts/): `type_classifier.pt`, `color_classifier.pt`, `neighborhood_detector.pt`.

### 1.6 The gap in one table

| Layer | Today | What it should speak |
|-------|-------|----------------------|
| Nucleus JSON | `{oper:"IN", minValue:"0", maxValue:"6"}` | `{oper:"IS", values:["empty","pawn","knight","bishop","rook","queen","king"], classifier:"type_classifier"}` |
| Primitive JSON | `{oper:"=", value:"6"}` for "King" | `{oper:"IS", value:"king"}` |
| UI operators | `= != < > <= >= IN` | add `IS` and `NOT IS` for categorical nuclei |
| UI value input | free integer | dropdown sourced from the classifier's vocabulary |
| Situation XML | `figure="6"` | `figure="king"` (back-compat: still accept integers) |
| Rule check() | `check(int)` | overload or wrap to `check(symbol)` where symbol → int via a registered vocabulary |
| Situation ingestion | manual form | (next step) crop an image, run classifier, emit symbol |

---

## 2. Design

### 2.1 Core idea

Keep the rule engine integer-based. Add one new abstraction — a **Vocabulary** — and one new rule operator — **`IS`** (and its inverse `NOT IS`). A Vocabulary is a name↔index table, optionally bound to a trained classifier's `class_names` list. Nuclei whose value attribute is categorical declare a vocabulary instead of (or in addition to) an `IN [min,max]` range. Primitives under such a nucleus use `IS <name>` instead of `= <int>`. The GA/check pipeline keeps receiving `int`s — the translation happens at the JSON-parse / UI / situation-ingestion boundaries.

This is the smallest change that gets "king" instead of "6" to the user without ripping out the rule system.

### 2.2 New concepts in code

1. **`Vocabulary`** — new class at [src/org/ppit/core/concept/primitive/Vocabulary.java] (new file).
   - Fields: `String name`, `List<String> classNames`, `Optional<String> classifierId` (points at a registered NN model), `Optional<Path> checkpointPath`.
   - Methods: `int indexOf(String name)`, `String nameOf(int idx)`, `int size()`, `List<String> names()`.
   - Immutable after load.

2. **`VocabularyRegistry`** — new singleton at [src/org/ppit/core/concept/primitive/VocabularyRegistry.java].
   - Loaded at startup from a config file — see §2.5.
   - `getByName(String)`, `getByType(PrimitiveType)`.

3. **`PrimitiveType` enrichment** — currently just a name ([PrimitiveType.java](src/org/ppit/core/concept/primitive/PrimitiveType.java)). Add an optional `Vocabulary m_vocabulary` field plus `hasVocabulary()` / `getVocabulary()`. Only set for types that represent a categorical NN output (e.g. `FigureType`, `FigureColor`). For numeric types (`cordX`, `cordY`), it stays null — no behavior change.

4. **`RuleIs`** — new rule at [src/org/ppit/core/concept/rules/RuleIs.java].
   - Stores `String symbolName` plus a back-pointer to the `Vocabulary` it was parsed against.
   - `check(int value)` — delegates to `value == vocabulary.indexOf(symbolName)`.
   - `getExpressionString()` returns the name (for human display) but `getJSON()` emits both the symbolic name and (for debuggability) the resolved integer.
   - Operator string: `"IS"` (add to [Definitions.java](src/org/ppit/util/Definitions.java)).

5. **`RuleNotIs`** — counterpart for exclusion. Cheap: inherit from RuleIs, invert `check`.

6. **(Optional, phase 2) `RuleIsOneOf`** — when a primitive covers multiple symbolic values (e.g. "minor piece" = knight|bishop). Emit internally as `IN` over the vocabulary's covered indices if contiguous, otherwise keep as an explicit list.

### 2.3 Parser / creator changes

Single surgical edit in [ConceptCreator.createRule()](src/org/ppit/core/concept/ConceptCreator.java#L41-L67):

```java
} else if (operator.equals(Definitions.isOperator)) {        // "IS"
    rule = new RuleIs(expression, vocabularyForThisAttribute);
} else if (operator.equals(Definitions.notIsOperator)) {     // "NOT IS"
    rule = new RuleNotIs(expression, vocabularyForThisAttribute);
}
```

To thread the vocabulary in, `createRule()` needs to know the *attribute's* vocabulary, which is the parent nucleus's. That requires `createPrimitiveConcept()` to resolve the parent first (it already does — [ConceptCreator.java:122-139](src/org/ppit/core/concept/ConceptCreator.java#L122-L139)) and to pass `parent.getType().getVocabulary()` into the rule factory. Keep the old integer path untouched for `cordX`/`cordY`/numeric nuclei.

For a nucleus that is itself categorical, the `IN [0,6]` range is now redundant. Allow two JSON shapes:

```json
// categorical nucleus
{"name": "Figure", "parent": null,
 "nucleusConceptValueAttrs":[
   {"name":"figure","oper":"IS_ONE_OF","vocabulary":"chess_figure_type"}
 ]}
```

```json
// or keep the legacy range form and let the loader attach the vocabulary by type-name
{"name": "Figure", "parent": null,
 "nucleusConceptValueAttrs":[{"name":"figure","oper":"IN","value":{"minValue":"0","maxValue":"6"}}]}
// + VocabularyRegistry has "FigureType" → chess_figure_type vocabulary
```

Keeping both shapes makes the migration incremental and back-compat'able (old JSONs still load).

### 2.4 Persistence / JSON emission

In [PrimitiveConcept.getJSONPure()](src/org/ppit/core/concept/primitive/PrimitiveConcept.java#L200-L247) and [IRule.getJSON()](src/org/ppit/core/concept/rules/IRule.java#L74-L89):

- If the type has a vocabulary AND the rule is `=`/`!=`, emit the symbolic form `{oper:"IS", value:"king"}` alongside the legacy integer (dual-write during migration, then drop integer once readers are switched).
- `IN` over a contiguous subset of a vocabulary emits `{oper:"IS_ONE_OF", values:[...]}`.
- Pure numeric rules (no vocabulary) serialize unchanged.

### 2.5 Where vocabularies come from

Two sources, same loader:

1. **Statically declared** in a new file `jsons/db/Vocabularies.json` — for vocabularies not tied to an NN (or bootstrapping before a model exists):

   ```json
   [{"name":"chess_figure_type",
     "classNames":["empty","pawn","knight","bishop","rook","queen","king"],
     "classifier":"type_classifier"},
    {"name":"chess_figure_color",
     "classNames":["none","white","black"],
     "classifier":"color_classifier"}]
   ```
   Note the `"empty"` / `"none"` prefix at index 0 — preserves today's convention that `figure=0` means "empty square", `color=0` means "no color". **Do not drop this** — it is load-bearing for [Situation.java:208-210](src/org/ppit/core/percept/Situation.java#L208-L210) and every Situation XML.

2. **Pulled from a `.pt` checkpoint** — the loader reads the `class_names` field written by Solver_train ([TECHNICAL_REFERENCE.md §A.4.1](Solver_train/docs/TECHNICAL_REFERENCE.md)). This needs a small extraction step because Java can't read torch pickles directly; see §2.7.

Registry load order: Vocabularies.json first, then optionally overlaid with checkpoint-derived vocabularies if a `class_names` field is present. On conflict, log + prefer the checkpoint (the NN model is the source of truth for its own output space).

### 2.6 UI surface (JSP side)

- [ShowOperSymbols.jsp](WebContent/concept/ShowOperSymbols.jsp) and [nucleusSkeleton.html](WebContent/percepts/nucleusSkeleton.html): add `IS`/`NOT IS`/`IS_ONE_OF` to the operator dropdown.
- When the selected operator is `IS` and the enclosing nucleus has a vocabulary, swap the integer `<input>` for a `<select>` populated from the vocabulary's `classNames`. When the operator is numeric or the nucleus is numeric, keep today's behavior.
- A new server endpoint under [src/org/ppit/actions/primitive/](src/org/ppit/actions/primitive/) — `GetVocabulary.java` — returns `{name, classNames[]}` for a given nucleus. The JSP calls this when the user picks the attribute. Minimal new UI machinery.
- No change to `cordX`/`cordY` cells of the nucleus editor.

### 2.7 How the Java side sees Python checkpoints

The `.pt` files are torch pickles — Java should not try to deserialize model weights. Two practical options:

- **Export sidecars from Solver_train.** Add a small CLI command to `chess_piece_classifier.py` / `chess_neighborhood_detector.py` called `export-vocab`: loads the checkpoint, writes `<model>.vocab.json` with `{"classifier": "type_classifier", "classNames": [...]}`. Java reads that JSON. **Preferred** — zero Python-in-Java at the vocabulary layer.
- Alternative: Jep / GraalPython / a subprocess. Too heavy for a vocabulary lookup.

Weights stay in Python-land and are only touched when we actually run inference — see §3.

### 2.8 Situation ingestion (next-step marker)

Today's situation comes in via form / XML with integer values. The goal state we are marking now:

- When a situation is created by capturing a board image, the pipeline should be:
  1. User uploads / captures a board image (new UI).
  2. An **inference service** (Python sidecar, REST-style or file-drop) crops the 8×8 grid and runs `type_classifier` + `color_classifier` on each square → returns a list of 64 `{x, y, figureName, figureColorName, typeConf, colorConf}` records.
  3. Solver_RG turns each record into an `IdGroup` + four `NucleusInstance`s. Names → indices via the `VocabularyRegistry`. From here on, the rest of the system is unchanged.

Mark this with **explicit TODOs** in:
- [SituationCreator.java](src/org/ppit/core/percept/SituationCreator.java) — add a stub `createSituationFromImage(Path)` that for now throws `UnsupportedOperationException("TODO: wire NN inference service")` and a Javadoc explaining the envisioned contract.
- [Situation.java](src/org/ppit/core/percept/Situation.java) — near [`identifyFigure`](src/org/ppit/core/percept/Situation.java#L207-L238): add a comment pointing at the vocabulary registry as the single source of truth once migration is done. Delete that switch-case in a later phase.
- [ProcessSituation.java](src/org/ppit/actions/situation/ProcessSituation.java) — note where an image upload endpoint would hook in.

Also mark:
- Future use of `neighborhood_detector.pt` to **verify** the 8×8 grid extraction before running classifiers (matching step). Drop a TODO in `SituationCreator` stub referencing [Solver_train/chess_neighborhood_detector.py](Solver_train/chess_neighborhood_detector.py).

Scope: do **not** implement inference-service wiring in this PR. Leave the TODOs and the stub.

---

## 3. Implementation phases

### Phase A — Vocabulary plumbing, no behavior change (all additive, all back-compat)
1. Add `Vocabulary`, `VocabularyRegistry`, `Vocabularies.json`, the `"IS"` / `"NOT IS"` operators in [Definitions.java](src/org/ppit/util/Definitions.java).
2. Add `RuleIs` / `RuleNotIs` (extending [IRule](src/org/ppit/core/concept/rules/IRule.java)); wire into [ConceptCreator.createRule()](src/org/ppit/core/concept/ConceptCreator.java#L41-L67).
3. Add the optional `Vocabulary` field to `PrimitiveType`.
4. Unit tests under [src/org/ppit/test/concept/primitive/](src/org/ppit/test/concept/primitive/) mirroring [TestPrimitiveConcept.java](src/org/ppit/test/concept/primitive/TestPrimitiveConcept.java) and [TestRules.java](src/org/ppit/test/concept/primitive/TestRules.java): create a nucleus with a vocabulary, assert `RuleIs("king").check(6) == true` / `check(5) == false`, and that a JSON round-trip preserves the symbolic form.

Existence criterion: all current tests still green; new `IS` tests pass.

### Phase B — Emit symbolic JSON + UI
5. Modify `PrimitiveConcept.getJSONPure()` and rule `getJSON()` to emit symbolic forms when a vocabulary is attached. Old numeric form accepted as input.
6. New Struts action `GetVocabulary` + JSP changes so authoring a primitive uses a dropdown.
7. Convert the seeded chess JSON ([Nucleus Concepts.json](jsons/db/Nucleus%20Concepts.json)) to the new form — but keep an integration test that loads an old-form JSON from fixtures to prove back-compat.

### Phase C — Solver_train vocabulary export
8. Add `export-vocab` subcommand to [Solver_train/chess_piece_classifier.py](Solver_train/chess_piece_classifier.py) that writes `type_classifier.vocab.json` and `color_classifier.vocab.json` next to the `.pt` artifacts.
9. Load them at registry startup; cross-check with `Vocabularies.json`, log on mismatch.

### Phase D — Situation ingestion from images (marked TODOs, not built here)
10. Stub `SituationCreator.createSituationFromImage` + TODO comments pointing at the NN pipeline.
11. Stub a REST-ish contract doc for the Python inference sidecar, including the neighborhood-detector verification step. No implementation yet.

### Phase E — Remove dead code (later, once Phase A–C are adopted)
12. Delete the hardcoded switch in [Situation.java:207-238](src/org/ppit/core/percept/Situation.java#L207-L238) and replace with `VocabularyRegistry.getByName("chess_figure_type").nameOf(figure)`.
13. Drop integer-mode dual-write in JSON emission.

---

## 4. Risks / open questions

- **Parent-chain rule resolution.** `PrimitiveConcept.evaluateDependencies()` ([PrimitiveConcept.java:154-178](src/org/ppit/core/concept/primitive/PrimitiveConcept.java#L154-L178)) threads rules through the parent. `RuleIs` must interoperate — a `RuleIs("king")` child of a `RuleIsOneOf(["king","queen"])` parent should still `equals()` / `clone()` / conjunct correctly. The conjunction logic in [RuleIn.conjunction()](src/org/ppit/core/concept/rules/RuleIn.java#L177-L271) is integer-centric; the cleanest path is to implement `RuleIs.conjunction()` by reducing symbolic forms to integer sets, delegating to existing integer conjunction, and re-symbolizing.
- **The JSON-loading test fixture in [jsons/db/Nucleus Concepts.json](jsons/db/Nucleus%20Concepts.json) is currently malformed** (missing commas, unterminated string on the `"parent"` line). That is orthogonal to this work but worth flagging — any JSON roundtrip test will fail until it's fixed.
- **"empty" / "none" at index 0.** The current system uses `figure=0` as the empty square and `color=0` as absent color. We preserve that convention in the vocabulary — do not reorder to match the NN's 6-class `class_names` list. The type classifier is trained on pieces only (no empty class), so an **occupancy step** is needed upstream of it. The Solver_train scientific doc already calls this out ([SCIENTIFIC_DESCRIPTION.md §7](Solver_train/docs/SCIENTIFIC_DESCRIPTION.md) — "upstream empty-square detector would be needed"). Phase D must address this explicitly.
- **Vocabulary drift.** If somebody retrains the NN with a reordered `class_names`, rule values stored as integers silently become wrong. The symbolic JSON emitted in Phase B makes this detectable and fixable. Storing the integer *only* was always fragile; this plan removes that coupling.
- **No change to the underlying `check(int)` contract** — so no risk to the GA pipeline, which is the most load-bearing part of the system. All novelty is at the edges (parse, serialize, UI, and — later — situation ingress).

---

## 5. Generality & scope (why this spine handles future NNs, and where it stops)

The Vocabulary + `RuleIs` abstraction is deliberately domain-agnostic. It does not mention chess, pieces, or Solver_train. Any new categorical NN in any future problem (chess-or-not) ships a vocabulary manifest and gets picked up by the same registry and the same UI.

**What this handles cleanly:**
- Chess figure type (`type_classifier.pt` → 6 class names) and color (`color_classifier.pt` → 2 class names).
- Coord classifiers, if someone later trains one whose NN output is a symbolic file/rank (`"a".."h"`, `"1".."8"`). The author declares a nucleus with that vocabulary; no rule-engine changes.
- Any future non-chess problem whose NN is a single-head softmax classifier — e.g. a traffic-sign type classifier, an object category classifier, a weather-state classifier. Author ships `Vocabularies.json` + a sidecar manifest, no code changes.
- Multi-head NNs: just declare one nucleus per head. Each head's `class_names` becomes its own vocabulary.

**Known gaps (out of scope for this implementation pass):**
1. **Regression / continuous NN outputs.** The rule engine is `check(int)` ([IRule.java:54](src/org/ppit/core/concept/rules/IRule.java#L54)). A NN that emits a float (angle, bbox coord, probability threshold) doesn't fit Vocabulary. If needed later, add a `RuleRange(float)` + widen the instance value type. Bounded, separate change.
2. **Relational NNs** like [Solver_train/chess_neighborhood_detector.py](Solver_train/chess_neighborhood_detector.py) — pairwise binary output over two crops. These are composite/AR1-level predicates, not nucleus-level `check(int)`. They need a different integration surface (composite rule). Left as a future chapter; `VocabularyRegistry` can still track their existence with `kind: "relation"` metadata so the UI can list them and mark them as "not yet pluggable into a nucleus".
3. **Confidence scores.** NN outputs carry probabilities; `NucleusInstance` stores only the argmax int. If downstream reasoning ever needs confidence, that's a new field on the instance — vocabulary stays the same.

## 6. Dynamic-classifier interface (how new NNs plug in without code changes)

### 6.1 Filesystem layout

A conventions-over-config drop folder, default `./classifiers/` under the project root (configurable via an env var / Java system property `solver.classifiers.dir`). Each NN occupies one subfolder:

```
classifiers/
  type_classifier/
    manifest.json          # <-- the contract (see 6.2)
    type_classifier.pt     # <-- optional; referenced from manifest
  color_classifier/
    manifest.json
    color_classifier.pt
  neighborhood_detector/
    manifest.json          # kind: "relation" — listed but not selectable as nucleus
    neighborhood_detector.pt
```

On startup, `VocabularyRegistry` scans every subfolder for `manifest.json` and registers the ones that parse. No recompilation, no class-loading. Adding a new NN = dropping a folder and restarting the server. A future nicety is a "reload classifiers" Struts action; not in this pass.

### 6.2 `manifest.json` contract (what Solver_train emits)

```json
{
  "schemaVersion": 1,
  "classifierId": "type_classifier",
  "displayName": "Chess Piece Type",
  "kind": "categorical",
  "classNames": ["pawn", "knight", "bishop", "rook", "queen", "king"],
  "indexOffset": 0,
  "suggestedPrimitiveTypeName": "FigureType",
  "checkpointPath": "type_classifier.pt",
  "inputSpec": {
    "modality": "image",
    "channels": 3,
    "height": 128,
    "width": 128,
    "normalization": "imagenet"
  },
  "trainingMetadata": {
    "framework": "pytorch",
    "arch": "resnet18",
    "bestValAcc": 0.97,
    "producedBy": "Solver_train/chess_piece_classifier.py",
    "producedAt": "2026-02-22T21:22:00Z"
  }
}
```

Only `schemaVersion`, `classifierId`, `kind`, and `classNames` are required. The rest is optional metadata that the UI displays but the rule engine ignores.

Key fields:
- **`classifierId`** — stable machine id. Used as the vocabulary's primary key in the registry.
- **`kind`** — `"categorical"` today (RuleIs applies). `"relation"` and `"regression"` reserved for future work; those manifests are listed by the UI but cannot be bound to a primitive classifier yet.
- **`classNames`** — the list drives the indexOf/nameOf lookup. Order matters and is authoritative.
- **`indexOffset`** — lets a vocabulary reserve index 0 for a synthetic "empty"/"none" symbol that isn't in the NN's training classes. E.g. chess `FigureType` wants index 0 = `"empty"` and indices 1-6 = the NN's six piece names. The registry handles this by optionally prepending `"empty"` / `"none"` when `indexOffset > 0` — documented per-vocabulary in a separate `syntheticPrefix` section if used.
- **`suggestedPrimitiveTypeName`** — hint for the authoring UI: "when the user is declaring a nucleus for FigureType, pre-select this manifest". Optional.
- **`inputSpec`** — tells Solver later how to feed the NN when situation ingestion is implemented. We store it now so future work has it.
- **`trainingMetadata`** — provenance for debugging and drift detection.

Solver_train ships an `export-vocab` subcommand on each of `chess_piece_classifier.py` and `chess_neighborhood_detector.py` that emits this manifest from the `.pt`'s `class_names` + training metadata.

### 6.3 How the UI consumes it

- New Struts action `listClassifiers` → returns `[{classifierId, displayName, kind, classNames, suggestedPrimitiveTypeName}]` for every manifest in the registry.
- New Struts action `getVocabulary?classifierId=...` → returns the full vocabulary, including `classNames` (for the dropdown).
- The nucleus editor ([nucleusSkeleton.html](WebContent/percepts/nucleusSkeleton.html)) grows a classifier-picker `<select>` next to the value-attribute row. When a classifier is picked, the operator dropdown adds `IS` / `NOT IS`; the value input swaps from free integer to a `<select>` populated from that classifier's `classNames`.
- The primitive editor ([ViewPrimitiveConcept.jsp](WebContent/concept/ViewPrimitiveConcept.jsp)) inherits the parent nucleus's classifier binding; when authoring a narrower primitive, the user picks a class name from the dropdown rather than typing `= 6`.
- Users who don't want NN binding still get the numeric operators unchanged — the picker is opt-in per value attribute.

### 6.4 What this gets us end-to-end

A user who has just trained a brand-new classifier (say, a knight-only-or-not binary head) for some novel problem can:
1. Drop `classifiers/knight_detector/` with its `manifest.json` + `.pt`.
2. Restart Solver.
3. In the nucleus editor, pick "Knight Detector" from the classifier dropdown, operator `IS`, value `knight`.
4. Save. The primitive is persisted with symbolic JSON, the rule engine compares ints underneath, the GA pipeline is unchanged.

No Java edits, no rule-engine edits, no code generation. That is what "dynamic" means here.

---

## 7. Deliverables for this implementation pass

- Plan document (this file).
- **Core:** `Vocabulary`, `VocabularyRegistry`, `RuleIs` / `RuleNotIs`, updates to `PrimitiveType`, `ConceptCreator`, `Definitions`.
- **Interface:** `ListClassifiers` + `GetVocabulary` Struts actions; JSP updates so the user can pick from available NN classifiers.
- **Solver_train:** `export-vocab` subcommand on both scripts producing the manifest in §6.2; sample manifests checked in for the three existing artifacts.
- **Tests:** unit tests covering `Vocabulary` lookup, `RuleIs.check` / `clone` / `conjunction`, `VocabularyRegistry` manifest load.
- Situation ingestion (the Phase D NN-to-situation pipeline) remains out of scope, as agreed.

---

## 8. Future work

Tracked here so they survive context handoffs. None are in scope for the current branch.

### 8.1 Extend the checkbox picker to the composite editor

The nucleus and primitive editors now use a checkbox-list widget for `IS` / `NOT IS` value attributes (replaced the native `<select multiple>`, which Linux Chromium rendered as a 1-row-tall control regardless of the `size` attribute). The **composite-concept editor** still renders class-name values as plain text inputs.

Scope:
- Mirror the same `f_symbolList` + `f_symbolBox` + `[all] / [none] / [apply]` + preview-span widget into the composite editor's value-attribute rows.
- Wire `populateClassifierPicker()` and `commitSymbolSelection()` (or equivalent helpers) into [comConcept.js](WebContent/static/js/comConcept.js) and the composite skeleton in [conContent.html](WebContent/windows/conContent.html) where composite rows are constructed.
- The save path needs to read the joined value from the same hidden mirror input and emit `{oper, value, classifier}` JSON.
- The load-from-saved-composite path needs to re-tick the saved class names by `value` match, the same way the primitive editor does in [primConcept.js fillPrimitiveWindow](WebContent/static/js/primConcept.js).

Server-side: no changes — `RuleIs` / `RuleNotIs` already work for any concept type whose nucleus has a vocabulary, so composites that reference categorical nuclei attributes already serialize through the same code path.

### 8.2 Image-to-situation pipeline (a.k.a. Phase D)

Today a situation is constructed from a form with explicit integer values. The goal is: **user uploads / captures a chess board image, Solver_RG creates a corresponding Situation with `NucleusInstance` values derived from the NN classifiers, and then runs the normal situation-matching / planning machinery against it**.

End-to-end flow:

1. **UI capture step**
   - Add an "Image situation" tab / dialog under [WebContent/windows/](WebContent/windows/) that accepts a board image (file upload or webcam capture).
   - Optional ROI selection — let the user draw the 8×8 grid bounds if the auto-detector is off.
   - POST the image bytes to a new Struts action `createSituationFromImage` ([SituationCreator](src/org/ppit/core/percept/SituationCreator.java)).

2. **Python inference sidecar**
   - Add a small Flask/FastAPI service under `Solver_train/inference_sidecar/` exposing two HTTP endpoints:
     - `POST /predict-board` — input: board image + optional grid box; output: list of 64 cells, each `{x, y, type:{name,prob}, color:{name,prob}, occupied:{prob}}`. Uses `type_classifier.pt` + `color_classifier.pt`.
     - `POST /verify-grid` — input: board image + grid box; output: `{neighborConsistency: 0..1, mismatchedPairs: [...]}` using the Siamese `neighborhood_detector.pt` to sanity-check the 8×8 grid extraction.
   - Container it (one extra service in [docker-compose.yml](docker-compose.yml)) so it comes up next to `app` and `mongo`. Mount `Solver_train/artifacts/` read-only.

3. **Occupancy step**
   - The type classifier was trained on pieces only — no *empty* class. Either:
     - Add a small thresholded brightness/saturation occupancy filter inside the sidecar that emits `occupied: false` for empty squares, OR
     - Train a binary `occupancy_classifier.pt` head and ship it alongside the others; that lifts the occupancy decision into the same manifest-driven path.
   - Solver_RG's `FigureType` vocabulary already reserves index 0 = `empty`; the sidecar must emit `"empty"` for non-occupied cells.

4. **Server-side bridge**
   - Implement `SituationCreator.createSituationFromImage(image, gridBox)` (currently a stub per [§5 of this plan](NN_CLASSIFIER_INTEGRATION_PLAN.md)).
   - For each of 64 cells, build an `IdGroup(id)` with four `NucleusInstance` records:
     - `cordX` ← cell column (1..8)
     - `cordY` ← cell row (1..8)
     - `FigureType` ← `VocabularyRegistry.getById("type_classifier").indexOf(name)`
     - `FigureColor` ← `VocabularyRegistry.getById("color_classifier").indexOf(name)`
   - Add the resulting `Situation` to [SituationManager](src/org/ppit/core/percept/SituationManager.java) and return its id to the UI.

5. **Reuse existing matching/planning unchanged**
   - From step 4 onward the situation looks identical to one authored by hand — same `IdGroup`s, same `int m_value` per `NucleusInstance`. The existing pipeline ([processSituation.action](src/org/ppit/actions/situation/ProcessSituation.java), GA matching, plan selection) runs against it without modification. That is the design payoff of keeping the rule engine integer-native (see §6).

6. **Confidence handling (optional, deferred)**
   - The sidecar returns per-cell probabilities; Solver_RG currently discards everything except the argmax. If downstream reasoning needs confidence, add a side-channel `confidence: float` field on `NucleusInstance` plus a `RuleConfidenceGE(threshold)` rule. Documented as out-of-scope in §5; this future-work entry just records the natural extension point.

7. **Grid verification gate**
   - After step 4 but before committing the situation, call `/verify-grid` (the Siamese model). If `neighborConsistency` is below a threshold (e.g. 0.8), surface a UI warning ("grid alignment may be off — please review") and let the user accept or re-crop. The neighborhood detector is the canonical relational NN in §6 and exists today; this is the use case that justifies keeping it discoverable in [ListClassifiers](src/org/ppit/actions/primitive/ListClassifiers.java) even though it's marked `pluggable: false` for the nucleus picker.

8. **Auditability**
   - Persist the originating image (or a thumbnail + hash) alongside the `Situation` in MongoDB, plus the per-cell `(name, prob)` tuples. That gives a debugging trail when a classifier misfires and downstream reasoning produces a surprising result.

What this gives the user: instead of clicking 64 cells in the form, they snap a picture of a real board, and Solver_RG produces a situation that drops straight into the existing matching/planning loop — the same loop that runs against today's hand-authored situations.

### 8.3 Smaller cleanup items

- Drop the `[apply]` button now that auto-commit fires on every checkbox change (it was added when the multi-select needed explicit committing).
- Drop the diagnostic `[Solver] classifier "..."` console line once the checkbox widget is confirmed stable across browsers.
- Consider replacing the in-memory `Logger` calls in `VocabularyRegistry` startup with proper SLF4J logging so manifest-load events surface in Tomcat's `localhost.log` consistently.
