#!/bin/bash

source "$(dirname "${0}")/.env"

cd ${BASE_DIR}

rm -rf ${BASE_DIR}/classes/*
javac \
  -classpath "${CLASS_PATH}" \
  -d "${BASE_DIR}/classes" \
  "${BASE_DIR}/src/chunkedupload/ParallelUpload.java" \
  "${BASE_DIR}/src/UploadSession.java"
