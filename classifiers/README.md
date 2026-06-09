# NN classifier drop folder

Solver_RG scans this folder at startup (via `VocabularyRegistry`) and registers
every subfolder that contains a `manifest.json`. Each registered classifier
surfaces in the nucleus/primitive editor as a pickable vocabulary — authors
then reference piece names like "king" instead of integer codes like `6`.

Discovery order (first hit wins):
1. `-Dsolver.classifiers.dir=<path>` JVM system property
2. `SOLVER_CLASSIFIERS_DIR` environment variable
3. `./classifiers` under the process working directory (this folder)

## Manifest format

See [NN_CLASSIFIER_INTEGRATION_PLAN.md](../NN_CLASSIFIER_INTEGRATION_PLAN.md)
§6.2 for the full schema.

Minimum viable manifest:

```json
{
  "schemaVersion": 1,
  "classifierId": "my_classifier",
  "kind": "categorical",
  "classNames": ["class_a", "class_b", "class_c"]
}
```

`kind: "categorical"` is pluggable into a nucleus via the `IS` / `NOT IS`
operators. `kind: "relation"` and `kind: "regression"` manifests are listed by
the UI but cannot be bound to a nucleus classifier yet.

## Adding a new classifier

1. Train the model with [Solver_train/](../Solver_train/) (or any pipeline
   that produces a `.pt` checkpoint with a `class_names` field).
2. Run the appropriate `export-vocab` subcommand, e.g.
   ```bash
   python Solver_train/chess_piece_classifier.py export-vocab \
     --checkpoint Solver_train/artifacts/type_classifier.pt \
     --output-dir classifiers/type_classifier \
     --copy-checkpoint
   ```
3. Restart the Solver webapp. The new classifier appears in the nucleus
   editor's classifier dropdown.
