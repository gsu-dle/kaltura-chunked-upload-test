#!/bin/bash

source "$(dirname "${0}")/.env"

if [ $# -lt 12 ]
then
    echo "Usage: $0  <service URL> <partner ID> <partner admin secret> \
</path/to/file> <file name> <user> <project location> <web dir location> \
<sessionid> <tmpdir> <upload_user_id> <folder name> \
[optional entryId to update]"
    exit 1
fi

SERVICE_URL="${1}"
PARTNER_ID="${2}"
ADMIN_SECRET="${3}"
FILEPATH="${4}"
FILENAME="${5}"
USERNAME="${6}"
APP_LOCATION="${7}"
WEB_LOCATION="${8}"
SESSION_ID="${9}"
TMP_DIR="${10}"
ALT_USERNAME="${11}"
FOLDER_NAME="${12}"
ENTRY_ID_TO_UPDATE=""
if [ -n "${13}" ]
then
  ENTRY_ID_TO_UPDATE="${13}"
fi

cd $BASE_DIR

# echo "CURRENT LOCATION: $(pwd)"
# echo "${PARTNER_ID} -- ${ADMIN_SECRET} -- ${FILEPATH} -- ${FILENAME} -- \
# ${USERNAME} -- ${ALT_USERNAME} -- ${FOLDER_NAME} -- ${TMP_DIR}"

java \
  -cp "${CLASS_PATH}" \
  UploadSession \
  "${SERVICE_URL}" \
  "${PARTNER_ID}" \
  "${ADMIN_SECRET}" \
  "${FILEPATH}" \
  "${FILENAME}" \
  "${USERNAME}" \
  "${SESSION_ID}" \
  "${TMP_DIR}" \
  "${ALT_USERNAME}" \
  "${FOLDER_NAME}" \
  "${ENTRY_ID_TO_UPDATE}"
