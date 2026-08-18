##########
# Build stage: use Gradle to produce WAR (respects encoding + junit)
##########
FROM gradle:8.8-jdk17 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle --no-daemon clean war

##########
# Stage to harvest Tomcat 9 + JRE 17 binaries
##########
FROM tomcat:9.0-jre17 AS tomcat

##########
# Final runtime stage: MongoDB 3.4 base with Tomcat 9 + JRE 11 copied in
##########
FROM mongo:3.4

## Default Mongo URL points to the in-container mongod
ENV MONGO_URL="mongodb://127.0.0.1:27017/yourdb"
## NN classifier drop folder (read at startup by VocabularyRegistry).
ENV SOLVER_CLASSIFIERS_DIR="/opt/solver/classifiers"
## Where SituationManager.createSituationFromImage finds predict_board.py +
## the trained .pt checkpoints. Subprocess MVP per Phase D §8.2.
ENV SOLVER_TRAIN_DIR="/opt/solver/Solver_train"
ENV SOLVER_PYTHON="python3"
ENV CATALINA_OPTS="-Dmongo.url=${MONGO_URL} -Dsolver.classifiers.dir=${SOLVER_CLASSIFIERS_DIR}"

## Copy Java and Tomcat from the tomcat image
# Eclipse Temurin images expose JAVA_HOME at /opt/java/openjdk
COPY --from=tomcat /opt/java/openjdk /opt/java/openjdk
COPY --from=tomcat /usr/local/tomcat /usr/local/tomcat
ENV JAVA_HOME="/opt/java/openjdk"
ENV PATH="$JAVA_HOME/bin:/usr/local/tomcat/bin:${PATH}"

## Deploy our app as ROOT.war and remove defaults
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /home/gradle/project/build/libs/ssrgt_solver.war /usr/local/tomcat/webapps/ROOT.war

## Ship the NN classifier drop folder + the trained checkpoints that exist.
## The manifests have checkpointPath relative to the manifest itself (e.g.
## "../../Solver_train/artifacts/neighborhood_detector.pt"), so we mirror that
## layout under /opt/solver to keep relative paths valid in-container.
COPY --from=build /home/gradle/project/classifiers /opt/solver/classifiers
COPY --from=build /home/gradle/project/Solver_train/artifacts/neighborhood_detector.pt /opt/solver/Solver_train/artifacts/neighborhood_detector.pt

## Phase D image-to-situation: install Python + CPU-only torch + Pillow and
## ship the prediction scripts. This adds ~1.7 GB to the image; it is the
## cost of the chosen "Subprocess MVP" path (no separate sidecar container).
##
## We install Miniconda rather than the OS-packaged python because the
## current base image (mongo:3.4, Ubuntu 16.04) ships Python 3.5 which is
## too old for modern torch. Miniconda gives us a self-contained Python
## 3.11+ and dodges PEP-668 / --break-system-packages compatibility issues.
ENV SOLVER_PYTHON="/opt/conda/bin/python3"
ENV PATH="/opt/conda/bin:${PATH}"
RUN apt-get update \
 && apt-get install -y --no-install-recommends wget bzip2 ca-certificates \
 && rm -rf /var/lib/apt/lists/* \
 && wget -q https://repo.anaconda.com/miniconda/Miniconda3-py311_24.7.1-0-Linux-x86_64.sh -O /tmp/miniconda.sh \
 && bash /tmp/miniconda.sh -b -p /opt/conda \
 && rm /tmp/miniconda.sh \
 && /opt/conda/bin/conda clean -afy
RUN /opt/conda/bin/pip install --no-cache-dir \
      --extra-index-url https://download.pytorch.org/whl/cpu \
      "numpy<2" \
      torch==2.4.1+cpu torchvision==0.19.1+cpu Pillow==10.4.0
COPY --from=build /home/gradle/project/Solver_train/predict_board.py /opt/solver/Solver_train/predict_board.py
COPY --from=build /home/gradle/project/Solver_train/chess_piece_classifier.py /opt/solver/Solver_train/chess_piece_classifier.py

# Data directory for MongoDB
RUN mkdir -p /data/db
VOLUME ["/data/db"]

# Add a simple entrypoint that starts mongod then Tomcat
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
