# CLAUDE.md — working notes for Solver_RG

Java/Struts reasoning engine (SSRGT Solver) + a Python NN perception layer that turns a
chess-board image into a Situation. Read this before changing anything; several traps
here cost real debugging time.

## Run it

```bash
./run-solver.sh          # start + sync; idempotent, safe to re-run, keeps the DB
```
Then open <http://localhost:8080/> (ROOT context, not /ssrgt_solver).

**Do not start with a bare `docker compose up`.** The image (`solver_rg-app`) is old and
its baked WAR lags the source tree, so a plain start silently serves stale UI code and
stale classes. `run-solver.sh` starts the stack, mounts `classifiers/` + `Solver_train/`,
pushes the current `WebContent/` assets in, and recompiles/re-pushes the two patched
classes (`Situation`, `SituationManager`) when they differ, restarting Tomcat only then.

`docker compose build` bakes everything properly but re-downloads Miniconda + CPU torch
(~1.7 GB layer), so it is slow. Java-only changes can be hot-swapped:

```bash
javac -cp "<container classes>:<container lib>/*" -d out src/.../X.java
docker cp out/org/.../X.class solver_rg-app-1:/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/org/.../X.class
docker restart solver_rg-app-1
```

## Traps that have already bitten

* **jQuery is 1.5.2.** `$(document).on(...)` does not exist — the handler silently never
  fires. Use `$('#id').live('click', ...)` like the rest of the codebase.
* **Most source files are CRLF.** Editing them with Python `read_text()/write_text()`
  rewrites every line ending and turns a 3-line change into a 700-line diff. Preserve
  CRLF (`read_bytes` + explicit `\r\n`) or use `sed -i`.
* **Chess figure codes are NOT FEN order.** The Solver convention (2016 User Guide
  App. B, the board sprites in `ChessSituation.css`, the legacy abstracts) is
  `1 Pawn, 2 Bishop, 3 Knight, 4 Rook, 5 Queen, 6 King`, `0 = Dummy`. `identifyFigure()`
  and `chessFigureCodeFromName()` were fixed to match; do not "correct" them back to FEN.
* **Vocabularies are a plain 0-based mapping** (`Vocabulary.indexOf` applies no offset).
  Because FigureType/FigureColor reserve `0` for Dummy, the manifests declare a synthetic
  class at index 0 (`"empty"` / `"none"`). Without it `IS king` resolves to 5 (Queen).
* **`export-vocab` will undo both of the above** — it writes the checkpoint's own
  `class_names` verbatim (FEN order, no reserved slot). After regenerating a chess
  manifest, restore index 0 and the Bishop/Knight order by hand, then verify:
  `curl "http://localhost:8080/getVocabulary.action?classifierId=type_classifier"`.
* **Situation processing hardcodes nucleus names**: `cordX`, `cordY`, `FigureType`,
  `FigureColor`. Any other names (e.g. `col`, `type`) produce
  "The element type: cordX ... is not defined" / "no Node registered for NucleusType".
* **The GA goes stale after a meaning-base edit.** Saving a concept or pressing
  "Initialize DB" reloads the graph with fresh `PrimitiveType` objects, so Situations
  created *earlier* fail with "There is no Node registered for the given NucleusType".
  Re-create the situation (re-upload the image); restart the container after Initialize DB.
* **The app image runs its own mongod** (it is `FROM mongo:3.4`), so concepts live in the
  *app* container's anonymous volume, not the `mongo` service. `docker compose down -v`
  (or `docker volume rm`) wipes all abstracts; a plain restart/recreate keeps them.
* **Struts has no `error` result for `ProcessSituation`**, so a failure renders an HTTP 500
  page instead of a message. Read `docker logs solver_rg-app-1` for the real cause.

## Layout

* `src/org/ppit/` — engine. `core/concept/primitive/Vocabulary*` (NN name↔index),
  `core/concept/rules/RuleIs` (`IS` / `NOT IS`), `core/percept/SituationManager`
  (`createSituationFromImage` shells out to `predict_board.py`).
* `classifiers/<id>/manifest.json` — drop folder scanned at startup
  (`solver.classifiers.dir` → `SOLVER_CLASSIFIERS_DIR` → `./classifiers`). Restart to reload.
* `Solver_train/` — training + inference. `chess_piece_classifier.py` (train/predict/
  annotate/export-vocab), `predict_board.py` (image → per-cell JSON), `artifacts/*.pt`
  (tracked), `data/piece_crops/` (the 4557-image set behind the checkpoints, tracked),
  `Solver_new_26/` (prototype JSON classifiers, not integrated), `docs/`.
* `WebContent/` — UI. `windows/chess.html` + `static/js/chessSituation.js` (board,
  image panel, `renderFenOnBoard`), `windows/nucContent.html` + `static/js/nucConcept.js`
  (nucleus editor with the classifier picker).
* `NN_Classifier_User_Guide/` — the user-facing guide (md + PDF + figures) and conclusions;
  §7 is the honest list of what is still missing. Keep it in sync when behaviour changes.

## Quick checks (no UI needed)

```bash
curl http://localhost:8080/listClassifiers.action
curl "http://localhost:8080/getVocabulary.action?classifierId=type_classifier"
curl -X POST http://localhost:8080/initNucleus.action           # nucleus list
curl "http://localhost:8080/processSituation.action?situationName=<name>"   # "" = last
```
`saveNucleus.action` (`nucleusName=<json>`) and `saveComposite.action`
(`compositeName=<json>`) accept the same JSON the UI posts — handy for scripted setup.
UI flows are best verified with headless Chrome (`/usr/bin/google-chrome`) via Playwright.

## Perception pipeline details

* Flow: image → `predict_board.py` (occupancy heuristic, then type + colour CNNs per
  cell) → per-cell JSON → `SituationManager.createSituationFromImage` (names → ints) →
  64 IdGroups × 4 NucleusInstances → GA matching.
* **IdGroups are numbered 1..64 row-major from a8**, which is exactly how
  `showActiveInstance()` indexes `#board > div`. Keep that ordering or instance
  highlighting silently lands on the wrong squares.
* **First call takes ~10-20 s**: every request spawns a fresh Python process that imports
  torch and loads both checkpoints. There is no warm sidecar yet.
* **"Empty" is a brightness-variance heuristic**, not a learned class
  (`--occupancy-var-threshold`, default 120). It works on rendered boards; textured
  photos will fool it. `Solver_new_26` has a real 7-class model incl. `none` if this
  needs fixing properly.
* The board box is a centred-square guess (92 % of the smaller side) — no perspective
  correction. Angled photos need `Solver_new_26/scripts/warp_and_crop.py`.
* The **composite editor has no IS/NOT IS picker** (nucleus and primitive editors do). It
  displays and stores symbolic values but shows a raw comma list; prefer defining a named
  primitive and referencing it, or post the JSON via `saveComposite.action`.

## Verifying UI flows

The UI is jQuery-1.5-era with `.live()` delegation and no test suite, so behaviour is
best confirmed by driving it headlessly:

```python
p.chromium.launch(executable_path="/usr/bin/google-chrome", headless=True)
```
Install Playwright into a throwaway venv (`python3 -m venv pwenv && pwenv/bin/pip install
playwright`); the system Chrome works, no `playwright install` needed. Register
`page.on("pageerror", ...)` — a broken handler shows up as silence, not an error.

To build a test board that the auto board-box detector aligns to exactly, paste a
1024×1024 board (8 × 128 px cells from `Solver_train/data/piece_crops/`) onto a
**1114×1114** canvas at offset (45, 45): `estimate_board_box` takes `int(W * 0.92)`
centred, which is 1024 at that width. Mis-sized canvases shift the grid and the
classifiers read half-squares.

## Repo hygiene

Tracked: engine source, UI, manifests, docs, the three `.pt` checkpoints and
`data/piece_crops/`. Ignored on purpose: `Solver_train/venv/`, the 4.9 GB
`Solver_train/data/mohammedhemed/`, `Solver_new_26/data/raw/` (147 MB),
`neighborhood_detector_epoch*.pt`, and `compress_solver/` (a superseded local copy —
everything of value from it now lives under `Solver_train/`).
