# Conclusions: NN Classifiers and the Levels of Chess Meaning Integration in the Solver

1. **The numerical presentation of nuclear knowledge has been overcome.** Chess meanings
   in the Solver were authored against bare integer encodings (`FigureType = 6` for the
   king, `FigureColor = 1` for white) that lived only in the expert's head. With the
   introduced `IS` / `NOT IS` operators, nucleus conditions are now expressed in the class
   names of the neural classifiers themselves (`IS king`, `IS white`,
   `IS knight, bishop` for a minor piece). This removes the three problems of the numeric
   surface: the opacity of the encoding, the coupling of authored meaning to an invisible
   integer layout, and the silent meaning flip after a classifier is retrained with
   reordered classes.

2. **NN classifiers cover the complete nuclear level of chess.** All four chess nucleus
   abstracts can be backed by trained models shipped in the same `Solver_train` folder:
   figure *type* (6-class CNN, 1.00 validation accuracy), figure *color* (2-class CNN,
   1.00), and the *x*/*y* coordinates (axis models of the prototype family; deterministic
   after the 8×8 split, integrable as small categorical vocabularies). Occupancy — the
   Dummy/empty value `0` of the 2016 convention — is covered today by an occupancy
   heuristic and, going forward, by the prototype family's explicit `none` class.

3. **Integration required no change to the reasoning engine.** A classifier plugs in by
   dropping a folder with a `manifest.json` into the classifier drop folder; the
   `VocabularyRegistry` turns its ordered `classNames` into a name↔integer vocabulary, and
   `RuleIs` translates names to integers at rule-construction time. The GA matching,
   conjunction, inheritance and persistence continue to operate on integers — the NN
   vocabulary is a surface adapter, not an engine rewrite, and new classifiers (traffic
   signs, terrain classes, any categorical nucleus) plug in without code changes.

4. **The higher levels of meaning complexity inherit the symbolic surface for free.**
   Because classifiers bind only at the nuclear level, every level of the meaning
   hierarchy builds on them unchanged: *primitive* abstracts strengthen by names
   (`King = FigureType IS king`); *AR1* abstracts compose classifier-backed nuclei into
   positional units (`Figure`); *composite* abstracts and their virtual/polymorphic
   specifications express relations over them (`FieldUnderCheck`); *set* and *action*
   abstracts, goals and plans consume these without ever seeing a class name. The division
   of labor is preserved: NNs answer only nuclear-level questions, while relations and
   strategy remain symbolic.

5. **The perception loop is closed and verified.** A board image is converted into a
   Solver Situation through the same classifiers: per-cell type/color classification →
   name-to-integer mapping → IdGroups of nucleus instances → GA matching, in the same
   shape as the FEN and drag-and-drop paths. The pipeline was demonstrated end-to-end,
   reconstructing the full start position (all 32 pieces, correct FEN) from a board image
   and passing the resulting Situation through the meaning base.

6. **A fifth, relational level of classification is prepared.** The Siamese neighborhood
   detector (`neighbor` / `not_neighbor`, 0.839 validation accuracy) is trained and
   registered as a `relation`-kind classifier. It does not bind to a nucleus — a relation
   judges two instances, not one value — and marks the next integration surface:
   perception-side grid verification in the short term, and NN-backed relational
   predicates at the AR1/composite level as future work.

7. **Retraining drift is now detectable instead of silent.** Saved rules persist the
   symbolic form (`{oper: IS, value: "king", classifier: "type_classifier"}`) together
   with the classifier identity, and the manifest remains authoritative for the current
   index mapping; a reordered retrain therefore produces a resolvable mismatch rather than
   a silently inverted meaning. Legacy numeric abstracts are rescued by unique-vocabulary
   inference at load time.

8. **Remaining steps are refinements, not redesign.** The perception bridge should resolve
   names through the vocabulary registry instead of a hardcoded map; occupancy should
   become a learned class; the recognized Situation should be rendered back onto the board
   interface; the `IS` surface should be extended to the composite editor; and the
   per-request Python subprocess should become a persistent inference sidecar. None of
   these touches the core result: numeric nuclear presentations have been replaced by the
   classifiers' own vocabularies, at every level of the chess meaning hierarchy, with the
   reasoning engine untouched.
