# Export standard variables
export TOKEN_WORKSPACE_FOLDER_NAME="${WORKSPACE_NAME}"
export TOKEN_PROJECT_FOLDER_NAME=$(echo "${PROJECT_NAME}" | cut -d'/' -f2)

export TOKEN_GIT_CREDENTIAL="adop-jenkins-master"
export TOKEN_GERRIT_SERVER="ADOP Gerrit"

# Export other variables
### YOUR TOKENS GO HERE ###

# Replace tokens
for template in `ls xml/*.template`
do
    job_name=$(echo "${template}" | cut -d'/' -f2 | cut -d'.' -f1)
    perl -p -i -e 's/###([^#]+)###/defined $ENV{$1} ? $ENV{$1} : $&/eg' < "${template}" 2> /dev/null 1> "xml/${job_name}.xml"
done

